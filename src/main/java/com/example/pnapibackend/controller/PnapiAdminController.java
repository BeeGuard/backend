package com.example.pnapibackend.controller;

import com.example.pnapibackend.data.entities.Account;
import com.example.pnapibackend.data.entities.TemporaryAccount;
import com.example.pnapibackend.data.repository.AccountRepository;
import com.example.pnapibackend.data.repository.RoleRepository;
import com.example.pnapibackend.data.repository.TemporaryAccountRepository;
import com.example.pnapibackend.model.admin.account.AccountResponse;
import com.example.pnapibackend.model.admin.account.GetAccountRequest;
import com.example.pnapibackend.model.admin.account.GetAccountResponse;
import com.example.pnapibackend.model.admin.createaccount.CreateAccountRequest;
import com.example.pnapibackend.model.admin.createaccount.CreateAccountResponse;
import com.example.pnapibackend.model.admin.tempaccount.GetTempAccountResponse;
import com.example.pnapibackend.model.admin.tempaccount.TempAccountResponse;
import com.example.pnapibackend.model.generic.SimpleMessageResponse;
import com.example.pnapibackend.service.TemporaryAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/admin/")
@Slf4j
public class PnapiAdminController {

    private final int PAGE_SIZE = 10;
    private AccountRepository accountRepository;
    private TemporaryAccountService temporaryAccountService;
    private RoleRepository roleRepository;
    private TemporaryAccountRepository temporaryAccountRepository;

    public PnapiAdminController(
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

    @GetMapping(value = "/get-accounts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccounts(@RequestBody GetAccountRequest getAccountRequest) {
        Page<Account> accountPage = accountRepository.findAll(PageRequest.of(getAccountRequest.page(), PAGE_SIZE));
        return ResponseEntity.ok().body(
                new GetAccountResponse(
                        accountPage.stream().map(AccountResponse::getFromAccount).toList(),
                        getAccountRequest.page()
                )
        );
    }

    @GetMapping(value = "/get-temp-accounts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTempAccounts(@RequestBody GetAccountRequest getAccountRequest) {
        Page<TemporaryAccount> accountPage = temporaryAccountRepository.findAll(PageRequest.of(getAccountRequest.page(), PAGE_SIZE));
        return ResponseEntity.ok().body(
                new GetTempAccountResponse(
                        accountPage.stream().map(TempAccountResponse::getFromTempAccount).toList(),
                        getAccountRequest.page()
                )
        );
    }
}
