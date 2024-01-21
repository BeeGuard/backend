package com.example.pnapibackend.service.impl;

import com.example.pnapibackend.service.TemporaryAccountService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Service;

import java.util.Random;

@Log4j2
@Service
public class TemporaryAccountServiceImpl implements TemporaryAccountService {
    @Override
    public int generateAuthNumber() {
        log.log(Level.DEBUG, "Generating new auth number");
        Random random = new Random();
        return random.nextInt(100000,1000000);
    }
}
