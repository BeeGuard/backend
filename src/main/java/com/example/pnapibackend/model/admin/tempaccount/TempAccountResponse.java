package com.example.pnapibackend.model.admin.tempaccount;

import com.example.pnapibackend.data.entities.Role;
import com.example.pnapibackend.data.entities.TemporaryAccount;

import java.util.Collection;

public record TempAccountResponse(String email, String name, String countryCode, int authCode, Collection<Role> role) {
    public static TempAccountResponse getFromTempAccount(TemporaryAccount account){
        return new TempAccountResponse(
                account.getEmail(),
                account.getName(),
                account.getCountryCode(),
                account.getAuthCode(),
                account.getRoles()
        );
    }
}
