package com.antonroycar.homestay.service;

import com.antonroycar.homestay.dto.login.TokenResponse;
import com.antonroycar.homestay.dto.login.LoginRequest;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.security.BCrypt;
import com.antonroycar.homestay.security.JwtUtil;
import com.antonroycar.homestay.service.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public TokenResponse login(LoginRequest login) {

        // Validate login request
        validationService.validate(login);

        // Find account by username
        Account account = accountRepository.findByUsername(login.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Username or password is incorrect"));

        // Verify password using BCrypt
        if (!BCrypt.checkpw(login.getPassword(), account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Username or password is incorrect");
        }

        // Generate JWT token with role
        String jwtToken = jwtUtil.generateToken(account.getUsername(), account.getRole().name());

        // Set token and expiration time in account
        account.setToken(jwtToken);
        account.setTokenExpiredAt(jwtUtil.extractExpiration(jwtToken).getTime());

        // Save updated account with new token and expiration
        accountRepository.save(account);

        // Return TokenResponse
        return TokenResponse.builder()
                .token(jwtToken)
                .expiredAt(account.getTokenExpiredAt())
                .build();
    }

    @Transactional
    public String logout(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        account.setToken(null);
        account.setTokenExpiredAt(null);
        accountRepository.save(account);
        return "logout successfully";
    }


    public void validateToken(String token) {
        String username = jwtUtil.extractUsername(token);
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token or user logged out"));

        if (!account.getToken().equals(token) || account.getTokenExpiredAt() < System.currentTimeMillis()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired or user logged out");
        }
    }

}

