package com.example.pnapibackend.model.admin.createaccount;

import lombok.Data;

import java.util.Set;

@Data
public class CreateAccountRequest {
    private String email;
    private String name;
    private String countryCode;
    private Set<String> roles;
}
