package com.example.pnapibackend.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "hive")
public class Hive {
    @Id
    @GeneratedValue
    @Column(name = "hive_id")
    private UUID id;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "email")
    private Account account;

    @OneToMany(mappedBy = "hive", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TimestampInfo> timestampInfos;
}
