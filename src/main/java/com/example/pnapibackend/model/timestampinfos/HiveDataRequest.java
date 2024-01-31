package com.example.pnapibackend.model.timestampinfos;

import java.util.List;

public record HiveDataRequest(
        String ID,
        List<TimestampInfosRequest> d
) {}
