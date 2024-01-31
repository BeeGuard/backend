package com.example.pnapibackend.data.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "timestamp_info", indexes = {
        @Index(name = "idx_hive_time", columnList = "hive_id, time")
})
public class TimestampInfo {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @NonNull
    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "interior_humidity")
    private float interiorHumidity;

    @Column(name = "exterior_humidity")
    private float exteriorHumidity;

    @Column(name = "interior_temperature")
    private float interiorTemperature;

    @Column(name = "exterior_temperature")
    private float exteriorTemperature;

    @Column(name = "weight")
    private float weight;

    @Column(name = "uv_index")
    private int uvIndex;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hive_id")
    private Hive hive;
}
