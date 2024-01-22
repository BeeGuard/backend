package com.example.pnapibackend.model.createaccount;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter@Setter@AllArgsConstructor
public class CreateAccountResponse {
    private String email;
    private LocalDateTime validity;
    private int authCode;
    private String name;
}
