package com.antonroycar.homestay.service;

import com.antonroycar.homestay.dto.login.TokenResponse;
import com.antonroycar.homestay.dto.login.LoginRequest;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.security.BCrypt;
import com.antonroycar.homestay.service.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginRequest login) {
        validationService.validate(login);

        // Cari account berdasarkan username
        Account account = accountRepository.findByUsername(login.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Username or password is incorrect"));

        // Verifikasi password menggunakan BCrypt
        if (!BCrypt.checkpw(login.getPassword(), account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Username or password is incorrect");
        }

        // Generate token
        account.setToken(UUID.randomUUID().toString());
        account.setTokenExpiredAt(System.currentTimeMillis() + (1000 * 60 * 24 * 30));  // Token expires in 30 days
        accountRepository.save(account);

        // Return TokenResponse
        return TokenResponse.builder()
                .token(account.getToken())
                .expiredAt(account.getTokenExpiredAt())
                .build();
    }

    @Transactional
    public void logout(Account account) {
        account.setToken(null);
        account.setTokenExpiredAt(null);

        accountRepository.save(account);
    }
}

