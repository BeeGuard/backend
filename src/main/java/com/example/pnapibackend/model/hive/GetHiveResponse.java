package com.example.pnapibackend.model.hive;

import com.example.pnapibackend.data.entities.Hive;
import com.example.pnapibackend.data.entities.TimestampInfo;
import com.example.pnapibackend.model.timestampinfos.TimestampInfoResponse;

public record GetHiveResponse(
        String name,
        String id,
        TimestampInfoResponse latestTimestampInfo
) {
    public static GetHiveResponse getInstance(Hive hive, TimestampInfo timestampInfo) {
        return new GetHiveResponse(
                hive.getName(),
                hive.getId().toString(),
                TimestampInfoResponse.getInstance(hive, timestampInfo)
        );
    }
}
