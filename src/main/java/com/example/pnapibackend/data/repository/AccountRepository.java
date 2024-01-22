package com.example.pnapibackend.data.repository;

import com.example.pnapibackend.data.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByName(String username);

    Optional<Account> findAccountById(UUID uuid);

    Boolean existsAccountByEmail(String email);
}