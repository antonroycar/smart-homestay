package com.antonroycar.homestay.controller;

import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.antonroycar.homestay.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/auth")
public class LogoutController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AccountRepository accountRepository;

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>("Invalid token format", HttpStatus.BAD_REQUEST);
        }

        String actualToken = token.substring(7);

        String username = jwtUtil.extractUsername(actualToken);

        // Validasi token JWT
        if (!jwtUtil.validateToken(actualToken, username)) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        // Panggil AuthService untuk logout (Anda dapat menambahkan logika tambahan di sini jika perlu)
        authService.logout(account);

        return new ResponseEntity<>("Logout successfully", HttpStatus.OK);
    }
}


