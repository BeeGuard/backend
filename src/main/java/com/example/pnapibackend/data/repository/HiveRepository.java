package com.example.pnapibackend.data.repository;

import com.example.pnapibackend.data.entities.Account;
import com.example.pnapibackend.data.entities.Hive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HiveRepository extends JpaRepository<Hive, UUID> {

    List<Hive> findByAccount(Account account);

    List<Hive> findAllByAccount_Email(String accountEmail);
}
