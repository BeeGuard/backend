package com.example.pnapibackend.controller;

import com.example.pnapibackend.configuration.SecurityConfig;
import com.example.pnapibackend.data.entities.*;
import com.example.pnapibackend.data.repository.AccountRepository;
import com.example.pnapibackend.data.repository.HiveRepository;
import com.example.pnapibackend.data.repository.TemporaryAccountRepository;
import com.example.pnapibackend.data.repository.TimestampInfoRepository;
import com.example.pnapibackend.exceptions.AccountDoesNotExistsException;
import com.example.pnapibackend.exceptions.HiveNotFoundException;
import com.example.pnapibackend.exceptions.InvalidAuthCodeException;
import com.example.pnapibackend.model.hive.GetHiveResponse;
import com.example.pnapibackend.model.hive.create.CreateHiveRequest;
import com.example.pnapibackend.model.hive.create.CreateHiveResponse;
import com.example.pnapibackend.model.hive.threshold.SetThresholdRequest;
import com.example.pnapibackend.model.login.LoginRequest;
import com.example.pnapibackend.model.login.LoginResponse;
import com.example.pnapibackend.model.register.RegisterRequest;
import com.example.pnapibackend.security.jwt.JwtTokenProvider;
import com.example.pnapibackend.security.service.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/app/")
@Slf4j
public class PnapiController {

    private AccountRepository accountRepository;
    private TemporaryAccountRepository temporaryAccountRepository;
    private HiveRepository hiveRepository;
    private final int MAX_USAGE;

    private ApplicationContext context;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private TimestampInfoRepository timestampInfoRepository;


    public PnapiController(
            AccountRepository accountRepository,
            TemporaryAccountRepository temporaryAccountRepository,
            @Value("${app.temporary_account.MAX_USAGE}") int maxUsage,
            ApplicationContext applicationContext,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            HiveRepository hiveRepository,
            TimestampInfoRepository timestampInfoRepository
    ){
        this.accountRepository = accountRepository;
        this.temporaryAccountRepository = temporaryAccountRepository;
        this.MAX_USAGE = maxUsage;
        this.context = applicationContext;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.hiveRepository = hiveRepository;
        this.timestampInfoRepository = timestampInfoRepository;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new LoginResponse(jwt,
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerAccount(@RequestBody RegisterRequest registerRequest) {
        try {
            TemporaryAccount temporaryAccount = temporaryAccountRepository
                    .findTemporaryAccountByEmail(registerRequest.getEmail()).orElseThrow();

            if(temporaryAccount.getExpiration().isBefore(LocalDateTime.now())
                    || temporaryAccount.getUsage() >= MAX_USAGE) {
                temporaryAccountRepository.delete(temporaryAccount);
                throw new NoSuchElementException();
            }
            if(temporaryAccount.getAuthCode() != registerRequest.getAuthCode()) {
                temporaryAccount.incrementUsage();
                temporaryAccountRepository.save(temporaryAccount);
                throw new InvalidAuthCodeException();
            }

            PasswordEncoder encoder = context.getBean(SecurityConfig.class).passwordEncoder();
            Account account = new Account(
                    temporaryAccount.getEmail(),
                    encoder.encode(registerRequest.getPassword()),
                    temporaryAccount.getName(),
                    temporaryAccount.getCountryCode(),
                    new HashSet<>(temporaryAccount.getRoles())
            );
            accountRepository.save(account);
            temporaryAccountRepository.delete(temporaryAccount);

            String jwt = jwtTokenProvider.generateToken(account);

            log.info("Temporary account successfully deleted for " + account.getEmail());
            return ResponseEntity.ok().body(new LoginResponse(jwt,
                    account.getName(),
                    account.getEmail(),
                    account.getRoles().stream().map(Role::getName).toList()));
        } catch (NoSuchElementException | InvalidAuthCodeException e) {
            return ResponseEntity.badRequest().body("No such account was found");
        }
    }

    @PostMapping("/create-hive")
    public ResponseEntity<?> createHive(@RequestBody CreateHiveRequest createHiveRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        if(authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            try{
                Account account = accountRepository.findByEmail(userDetails.getEmail()).orElseThrow(
                        AccountDoesNotExistsException::new
                );

                Hive hive = new Hive(createHiveRequest.name(), account);
                hiveRepository.save(hive);

                //generate response
                return ResponseEntity.ok(CreateHiveResponse.getFromHive(hive));
            }catch (AccountDoesNotExistsException e) {
                return ResponseEntity.internalServerError().body("");
            }

        }
        return ResponseEntity.badRequest().body("");
    }

    @GetMapping(value = "/test")
    public ResponseEntity<?> testingToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return ResponseEntity.ok("Welcome " + userDetails.getUsername() + ". Your email is : " + userDetails.getEmail());
        }
        return ResponseEntity.badRequest().body("");
    }

    @PostMapping(value = "/set-thresholds/{hive-id}")
    public ResponseEntity<?> setThreshold(@PathVariable(name="hive-id") String hiveId, @RequestBody SetThresholdRequest setThresholdRequest) {
        try {
            Hive hive = hiveRepository.getReferenceById(UUID.fromString(hiveId));
            hive.setTempLowerThreshold(setThresholdRequest.lowerTemp());
            hive.setTempUpperThreshold(setThresholdRequest.upperTemp());
            hive.setHumidityLowerThreshold(setThresholdRequest.lowerHumidity());
            hive.setHumidityUpperThreshold(setThresholdRequest.upperHumidity());
            hive.setWeightLowerThreshold(setThresholdRequest.lowerWeight());
            hive.setWeightUpperThreshold(setThresholdRequest.upperWeight());
            hiveRepository.save(hive);
            return ResponseEntity.ok("Thresholds updated.");
        } catch(EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Hive not found.");
        }
    }

    @GetMapping(value = "/hive/{hiveId}")
    public ResponseEntity<?> getHiveFromID(@PathVariable("hiveId") String hiveId) {
        Hive hive = null;
        try {
            hive = hiveRepository.getReferenceById(UUID.fromString(hiveId));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
            }
            if(authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
                if (!hive.getAccount().getEmail().equals(userDetails.getEmail()) &&
                        userDetails.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().toUpperCase().contains("ADMIN"))) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You didn't own this hive");
                }
            }

            TimestampInfo latest = timestampInfoRepository.findTopByHiveOrderByTimeDesc(hive)
                    .orElseThrow(HiveNotFoundException::new);
            return ResponseEntity.ok(GetHiveResponse.getInstance(hive, latest));
        } catch(EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Hive not found.");
        }catch (HiveNotFoundException e) {
            return ResponseEntity.ok(GetHiveResponse.getInstance(hive, null));
        }
    }

    @GetMapping(value = "/hives")
    public ResponseEntity<?> getHivesFromAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        if(authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            List<Hive> hives = hiveRepository.findAllByAccount_Email(userDetails.getEmail());
            List<GetHiveResponse> getHiveResponses = hives.stream()
                    .map(hive -> {
                        TimestampInfo latest = null;
                        try {
                            latest = timestampInfoRepository.findTopByHiveOrderByTimeDesc(hive)
                                    .orElseThrow(HiveNotFoundException::new);
                        } catch (HiveNotFoundException ignored) {}
                        return GetHiveResponse.getInstance(hive, latest);
                    })
                    .toList();
            return ResponseEntity.ok(getHiveResponses);
        }
        return ResponseEntity.badRequest().body("No account found");
    }
}
