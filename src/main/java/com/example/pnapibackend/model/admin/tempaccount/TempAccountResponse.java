package com.example.pnapibackend.model.admin.tempaccount;

import com.example.pnapibackend.data.entities.Role;
import com.example.pnapibackend.data.entities.TemporaryAccount;

import java.time.LocalDateTime;
import java.util.Collection;

public record TempAccountResponse(String email, String name, String countryCode, int authCode,
                                  LocalDateTime expiration, int usage, Collection<Role> role) {
    public static TempAccountResponse getFromTempAccount(TemporaryAccount account){
        return new TempAccountResponse(
                account.getEmail(),
                account.getName(),
                account.getCountryCode(),
                account.getAuthCode(),
                account.getExpiration(),
                account.getUsage(),
                account.getRoles()
        );
    }
}
