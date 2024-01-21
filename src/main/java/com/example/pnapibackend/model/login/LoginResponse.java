package com.example.pnapibackend.model.login;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class LoginResponse {
    @NonNull
    private String token;

    private String type = "Bearer";

    @NonNull
    private Long id;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private List<String> roles;
}
