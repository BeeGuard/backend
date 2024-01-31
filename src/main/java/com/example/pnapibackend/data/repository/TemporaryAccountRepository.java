package com.example.pnapibackend.data.repository;

import com.example.pnapibackend.data.entities.TemporaryAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemporaryAccountRepository extends JpaRepository<TemporaryAccount, String> {
    public boolean existsByEmail(String email);

    Optional<TemporaryAccount> findTemporaryAccountByEmail(String email);

    void deleteTemporaryAccountByEmail(String email);
}
