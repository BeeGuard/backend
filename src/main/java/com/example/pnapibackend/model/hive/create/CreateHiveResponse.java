package com.example.pnapibackend.model.hive.create;

import com.example.pnapibackend.data.entities.Hive;

import java.util.UUID;

public record CreateHiveResponse(UUID hiveId) {
    public static CreateHiveResponse getFromHive(Hive hive) {
        return new CreateHiveResponse(
                hive.getId()
        );
    }
}
