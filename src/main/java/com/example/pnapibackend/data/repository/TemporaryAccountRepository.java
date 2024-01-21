package com.example.pnapibackend.data.repository;

import com.example.pnapibackend.data.entities.TemporaryAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryAccountRepository extends JpaRepository<TemporaryAccount, String> {
    public boolean existsByEmail(String email);
}
