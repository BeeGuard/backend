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
    @Column(name = "name")
    private String name;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "email")
    private Account account;

    @OneToMany(mappedBy = "hive", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TimestampInfo> timestampInfos;

    @Column(name="temperature_lower_threshold")
    private Float tempLowerThreshold;

    @Column(name="temperature_upper_threshold")
    private Float tempUpperThreshold;

    @Column(name="weight_lower_threshold")
    private Float weightLowerThreshold;

    @Column(name="weight_upper_threshold")
    private Float weightUpperThreshold;

    @Column(name="humidity_lower_threshold")
    private Float humidityLowerThreshold;

    @Column(name="humidity_upper_threshold")
    private Float humidityUpperThreshold;
}
