package com.example.pnapibackend.data.repository;

import com.example.pnapibackend.data.entities.Hive;
import com.example.pnapibackend.data.entities.TimestampInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimestampInfoRepository extends JpaRepository<TimestampInfo, UUID> {
    // Method to find the latest 10 records for a specific hive
    List<TimestampInfo> findTop10ByHiveOrderByTimeDesc(Hive hive);

    Page<TimestampInfo> findByHiveAndTimeBefore(Hive hive, LocalDateTime time, Pageable pageable);

    Optional<TimestampInfo> findTopByHiveOrderByTime(Hive hive);
}
