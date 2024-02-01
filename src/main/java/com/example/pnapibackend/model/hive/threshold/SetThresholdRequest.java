package com.example.pnapibackend.model.hive.threshold;

public record SetThresholdRequest(String id,
                                  String name,
                                  float lowerTemp,
                                  float upperTemp,
                                  float lowerHumidity,
                                  float upperHumidity,
                                  float lowerWeight,
                                  float upperWeight) {
}
