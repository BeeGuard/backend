package com.example.pnapibackend.data.repository;

import com.example.pnapibackend.data.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByName(String username);

    Optional<Account> findByEmail(String email);

    Optional<Account> findAccountByEmail(String email);

    Boolean existsAccountByEmail(String email);

    void deleteAccountByEmail(String email);
}
