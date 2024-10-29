package com.antonroycar.homestay.controller;

import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class LogoutController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountRepository accountRepository;

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Pastikan token memiliki format yang benar
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>("Invalid token format", HttpStatus.BAD_REQUEST);
        }

        // Ambil token tanpa "Bearer "
        String actualToken = token.substring(7);

        // Cari account berdasarkan token
        Account account = accountRepository.findByToken(actualToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token"));

        // Panggil service untuk menghapus token dan logout user
        authService.logout(account);

        return new ResponseEntity<>("Logout successfully", HttpStatus.OK);
    }
}

