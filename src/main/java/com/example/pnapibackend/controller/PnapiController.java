package com.example.pnapibackend.controller;

import com.example.pnapibackend.data.entities.Account;
import com.example.pnapibackend.data.entities.Role;
import com.example.pnapibackend.data.entities.TemporaryAccount;
import com.example.pnapibackend.data.repository.AccountRepository;
import com.example.pnapibackend.data.repository.RoleRepository;
import com.example.pnapibackend.data.repository.TemporaryAccountRepository;
import com.example.pnapibackend.model.createaccount.CreateAccountRequest;
import com.example.pnapibackend.model.createaccount.CreateAccountResponse;
import com.example.pnapibackend.model.generic.SimpleMessageResponse;
import com.example.pnapibackend.model.login.LoginRequest;
import com.example.pnapibackend.service.TemporaryAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDateTime;

@RestController
@RequestMapping("api/")
public class PnapiController {

    private AccountRepository accountRepository;
    private TemporaryAccountRepository temporaryAccountRepository;
    private TemporaryAccountService temporaryAccountService;
    private RoleRepository roleRepository;


    public PnapiController(
            AccountRepository accountRepository,
            TemporaryAccountRepository temporaryAccountRepository,
            TemporaryAccountService temporaryAccountService,
            RoleRepository roleRepository
    ){
        this.accountRepository = accountRepository;
        this.temporaryAccountRepository = temporaryAccountRepository;
        this.temporaryAccountService = temporaryAccountService;
        this.roleRepository = roleRepository;
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
                validity,
                authCode,
                createAccountRequest.getName()
        ));
    }

}
