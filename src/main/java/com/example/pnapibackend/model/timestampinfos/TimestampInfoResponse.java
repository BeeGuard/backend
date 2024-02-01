package com.example.pnapibackend.model.timestampinfos;

import com.example.pnapibackend.data.entities.Hive;
import com.example.pnapibackend.data.entities.TimestampInfo;

import java.time.LocalDateTime;
import java.util.UUID;

public record TimestampInfoResponse(
        UUID id,
        String hiveName,
        LocalDateTime time,
        float interiorHumidity,
        float exteriorHumidity,
        float interiorTemperature,
        float exteriorTemperature,
        float weight,
        int uvIndex
) {
    public static TimestampInfoResponse getInstance(Hive hive, TimestampInfo timestampInfo) {
        if (timestampInfo == null) return null;
        return new TimestampInfoResponse(
                hive.getId(),
                hive.getName(),
                timestampInfo.getTime(),
                timestampInfo.getInteriorHumidity(),
                timestampInfo.getExteriorHumidity(),
                timestampInfo.getInteriorTemperature(),
                timestampInfo.getExteriorTemperature(),
                timestampInfo.getWeight(),
                timestampInfo.getUvIndex()
        );
    }
}
