package com.example.pnapibackend.data.repository;

import com.example.pnapibackend.data.entities.Account;
import com.example.pnapibackend.data.entities.Hive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HiveRepository extends JpaRepository<Hive, UUID> {

    List<Hive> findByAccount(Account account);
}
