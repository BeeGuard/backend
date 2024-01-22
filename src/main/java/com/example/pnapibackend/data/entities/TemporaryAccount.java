package com.example.pnapibackend.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Data
@Table(name = "temporary_account")
@NoArgsConstructor
@RequiredArgsConstructor
public class TemporaryAccount {
    @Id
    @Column(name = "email", unique = true)
    @NonNull
    private String email;

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Column(name = "country_code", length = 2)
    private String countryCode;

    @NonNull
    @Column(name = "auth_code")
    private int authCode;

    @NonNull
    @Column(name = "expiration")
    private LocalDateTime expiration;

    @NonNull
    @Column(name = "usage")
    private int usage;

    @NonNull
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "temp_account_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles;

    public void incrementUsage() {
        this.usage++;
    }
}
