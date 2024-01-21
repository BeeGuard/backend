package com.example.pnapibackend.model.generic;

import lombok.Data;
import lombok.NonNull;

@Data
public class SimpleMessageResponse {
    @NonNull
    private String message;
}
