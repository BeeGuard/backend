package com.example.pnapibackend.controller;

import com.example.pnapibackend.data.repository.AccountRepository;
import com.example.pnapibackend.model.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/")
public class PnapiController {

    private AccountRepository accountRepository;


    public PnapiController(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok().body("User logged in successfully");
    }

    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok().body("User logged in successfully");
    }

}
