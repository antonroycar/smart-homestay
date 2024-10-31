package com.antonroycar.homestay.controller;

import com.antonroycar.homestay.dto.login.TokenResponse;
import com.antonroycar.homestay.dto.login.LoginRequest;
import com.antonroycar.homestay.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest login) {

        // Gunakan authService untuk login dan dapatkan TokenResponse
        TokenResponse tokenResponse = authService.login(login);

        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }
}

