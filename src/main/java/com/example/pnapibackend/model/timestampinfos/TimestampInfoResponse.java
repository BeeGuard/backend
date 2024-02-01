package com.example.pnapibackend.model.timestampinfos;

import java.time.LocalDateTime;
import java.util.UUID;

public record TimestampInfoResponse(UUID id,
                                    String hiveName,
                                    LocalDateTime time,
                                    float interiorHumidity,
                                    float exteriorHumidity,
                                    float interiorTemperature,
                                    float exteriorTemperature,
                                    float weight,
                                    int uvIndex) {
}
