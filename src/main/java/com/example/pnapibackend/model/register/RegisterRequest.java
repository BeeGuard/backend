package com.example.pnapibackend.model.register;

import lombok.Data;

@Data
public class RegisterRequest {

    private String email;
    private int authCode;
    private String password;
}
