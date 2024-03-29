package com.example.pnapibackend.security.service;

import com.example.pnapibackend.data.entities.Account;
import com.example.pnapibackend.data.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private AccountRepository accountRepository;

    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with uuid : " + username));

        return UserDetailsImpl.build(account);
    }
}
