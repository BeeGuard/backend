package com.example.pnapibackend.model.login;

import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Data
public class LoginResponse {
    @NonNull
    private String token;

    private String type = "Bearer";

    @NonNull
    private UUID id;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private List<String> roles;
}
