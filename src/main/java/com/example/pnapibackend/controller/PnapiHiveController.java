package com.example.pnapibackend.controller;

import com.example.pnapibackend.data.entities.Account;
import com.example.pnapibackend.data.entities.Hive;
import com.example.pnapibackend.data.entities.TimestampInfo;
import com.example.pnapibackend.data.repository.AccountRepository;
import com.example.pnapibackend.data.repository.HiveRepository;
import com.example.pnapibackend.data.repository.TimestampInfoRepository;
import com.example.pnapibackend.exceptions.AccountDoesNotExists;
import com.example.pnapibackend.exceptions.UnauthorizeHiveRights;
import com.example.pnapibackend.model.timestampinfos.HiveDataRequest;
import com.example.pnapibackend.model.timestampinfos.TimestampInfosRequest;
import com.example.pnapibackend.security.service.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/hive/")
@Slf4j
public class PnapiHiveController {
    private AccountRepository accountRepository;
    private HiveRepository hiveRepository;
    private TimestampInfoRepository timestampInfoRepository;

    public PnapiHiveController(
            AccountRepository accountRepository,
            HiveRepository hiveRepository,
            TimestampInfoRepository timestampInfoRepository
    ) {
        this.accountRepository = accountRepository;
        this.hiveRepository = hiveRepository;
        this.timestampInfoRepository = timestampInfoRepository;
    }

    @PostMapping("/data")
    public ResponseEntity<?> pushDatas(@RequestBody HiveDataRequest dataRequest) {
        //check if user have right on this hive
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        if(authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            try {
                Account account = accountRepository.findAccountByEmail(userDetails.getEmail())
                        .orElseThrow(AccountDoesNotExists::new);

                Hive hive = hiveRepository.getReferenceById(UUID.fromString(dataRequest.ID()));
                if (!hive.getAccount().equals(account)) {
                    throw new UnauthorizeHiveRights();
                }

                if (!dataRequest.d().isEmpty()) {
                    for (TimestampInfosRequest h: dataRequest.d()) {
                        TimestampInfo timestampInfo = new TimestampInfo(h.t(), hive);
                        timestampInfo.setExteriorHumidity(h.a_eh());
                        timestampInfo.setInteriorHumidity(h.a_ih());
                        timestampInfo.setExteriorTemperature(h.a_et());
                        timestampInfo.setInteriorTemperature(h.a_it());
                        timestampInfo.setWeight(h.a_w());
                        timestampInfo.setUvIndex(h.a_u());

                        timestampInfoRepository.save(timestampInfo);
                    }
                }
                return ResponseEntity.ok("Data added");
            } catch (AccountDoesNotExists e) {
                return ResponseEntity.internalServerError().body("");
            } catch (EntityNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        "No hive with this id"
                );
            } catch (UnauthorizeHiveRights e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        "This user cannot push info on this hive"
                );
            }
        }
        return ResponseEntity.badRequest().body("");
    }
}
