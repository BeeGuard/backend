package com.example.pnapibackend.controller;

import com.example.pnapibackend.configuration.SecurityConfig;
import com.example.pnapibackend.data.entities.Account;
import com.example.pnapibackend.data.entities.Role;
import com.example.pnapibackend.data.entities.TemporaryAccount;
import com.example.pnapibackend.data.repository.AccountRepository;
import com.example.pnapibackend.data.repository.RoleRepository;
import com.example.pnapibackend.data.repository.TemporaryAccountRepository;
import com.example.pnapibackend.exceptions.InvalidAuthCodeException;
import com.example.pnapibackend.model.createaccount.CreateAccountRequest;
import com.example.pnapibackend.model.createaccount.CreateAccountResponse;
import com.example.pnapibackend.model.generic.SimpleMessageResponse;
import com.example.pnapibackend.model.login.LoginRequest;
import com.example.pnapibackend.model.register.RegisterRequest;
import com.example.pnapibackend.service.TemporaryAccountService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/")
@Slf4j
public class PnapiController {

    private AccountRepository accountRepository;
    private TemporaryAccountRepository temporaryAccountRepository;
    private TemporaryAccountService temporaryAccountService;
    private RoleRepository roleRepository;
    private final int MAX_USAGE;

    private ApplicationContext context;


    public PnapiController(
            AccountRepository accountRepository,
            TemporaryAccountRepository temporaryAccountRepository,
            TemporaryAccountService temporaryAccountService,
            RoleRepository roleRepository,
            @Value("${app.temporary_account.MAX_USAGE}") int maxUsage,
            ApplicationContext applicationContext

    ){
        this.accountRepository = accountRepository;
        this.temporaryAccountRepository = temporaryAccountRepository;
        this.temporaryAccountService = temporaryAccountService;
        this.roleRepository = roleRepository;
        this.MAX_USAGE = maxUsage;
        this.context = applicationContext;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok().body("User logged in successfully");
    }

    @PostMapping(value = "/create-account", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest createAccountRequest) {
        if (accountRepository.existsAccountByEmail(createAccountRequest.getEmail())
                || temporaryAccountRepository.existsByEmail(createAccountRequest.getEmail())){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new SimpleMessageResponse("User already have an account or a temporary account"));
        }

        LocalDateTime validity = LocalDateTime.now().plusDays(7);
        int authCode = temporaryAccountService.generateAuthNumber();

        TemporaryAccount temporaryAccount = new TemporaryAccount(
                createAccountRequest.getEmail(),
                createAccountRequest.getName(),
                createAccountRequest.getCountryCode(),
                authCode,
                validity,
                0,
                createAccountRequest.getRoles().stream().map(e -> roleRepository.findByName(e).orElseThrow()).toList()
        );

        temporaryAccountRepository.save(temporaryAccount);

        return ResponseEntity.ok().body(new CreateAccountResponse(
                createAccountRequest.getEmail(),
                validity,                authCode,
                createAccountRequest.getName()
        ));
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerAccount(@RequestBody RegisterRequest registerRequest) {
        log.info("aaaaaaaaaaaaaaaaaaaaaa");
        try {
            TemporaryAccount temporaryAccount = temporaryAccountRepository
                    .findTemporaryAccountByEmail(registerRequest.getEmail()).orElseThrow();
            log.info("User " + registerRequest.getEmail() + " successfully retrieve");

            if(temporaryAccount.getExpiration().isBefore(LocalDateTime.now())
                    || temporaryAccount.getUsage() >= MAX_USAGE) {
                log.info("The account is no longer valid");
                temporaryAccountRepository.delete(temporaryAccount);
                throw new NoSuchElementException();
            }
            if(temporaryAccount.getAuthCode() != registerRequest.getAuthCode()) {
                log.info("expected " + temporaryAccount.getAuthCode() + " received : " + registerRequest.getAuthCode());
                log.info("Bad authCode enter for account " + temporaryAccount.getEmail());
                temporaryAccount.incrementUsage();
                temporaryAccountRepository.save(temporaryAccount);
                throw new InvalidAuthCodeException();
            }

            log.info("Creating account for " + temporaryAccount.getEmail());
            PasswordEncoder encoder = context.getBean(SecurityConfig.class).passwordEncoder();
            Account account = new Account(
                    temporaryAccount.getEmail(),
                    encoder.encode(registerRequest.getPassword()),
                    temporaryAccount.getName(),
                    temporaryAccount.getCountryCode(),
                    new HashSet<>(temporaryAccount.getRoles())
            );
            log.info("Creating account");
            accountRepository.save(account);
            log.info("Account successfully created for " + account.getEmail());
            temporaryAccountRepository.delete(temporaryAccount);
            log.info("Temporary account successfully deleted for " + account.getEmail());
            return ResponseEntity.ok("Account created");
        } catch (NoSuchElementException | InvalidAuthCodeException e) {
            return ResponseEntity.badRequest().body("No such account was found");
        }
    }
}
