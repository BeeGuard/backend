package com.example.pnapibackend.model.admin.account;

import com.example.pnapibackend.data.entities.Account;
import com.example.pnapibackend.data.entities.Role;

import java.util.Collection;

public record AccountResponse(String email, String name, String countryCode, Collection<Role> role) {
    public static AccountResponse getFromAccount(Account account){
        return new AccountResponse(
                account.getEmail(),
                account.getName(),
                account.getCountryCode(),
                account.getRoles()
        );
    }
}
