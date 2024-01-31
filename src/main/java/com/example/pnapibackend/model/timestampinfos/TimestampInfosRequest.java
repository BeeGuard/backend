package com.example.pnapibackend.model.timestampinfos;

import java.time.LocalDateTime;

public record TimestampInfosRequest(
        LocalDateTime t,    //format iso
        float a_eh,         //humidité extérieure moyenne
        float a_et,         //température extérieure moyenne
        float a_ih,         //humidité intérieure moyenne
        float a_it,         //température intérieure moyenne
        float a_w,          //poids moyen
        int a_u           //indice UV
) {}
