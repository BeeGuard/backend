package com.example.pnapibackend.model.hive.threshold;

import com.example.pnapibackend.data.entities.Hive;

public record ThresholdResponse(
        Float lowerTemp,
        Float upperTemp,
        Float lowerHumidity,
        Float upperHumidity,
        Float lowerWeight,
        Float upperWeight
) {
    public static ThresholdResponse getInstance(Hive hive) {
        return new ThresholdResponse(
                hive.getTempLowerThreshold(),
                hive.getTempUpperThreshold(),
                hive.getHumidityLowerThreshold(),
                hive.getHumidityUpperThreshold(),
                hive.getWeightLowerThreshold(),
                hive.getWeightUpperThreshold()
        );
    }
}
