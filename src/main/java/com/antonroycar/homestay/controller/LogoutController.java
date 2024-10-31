package com.antonroycar.homestay.controller;

import com.antonroycar.homestay.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.antonroycar.homestay.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class LogoutController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtUtil.extractUsername(token);
        String logoutMessage = authService.logout(username);
        return ResponseEntity.ok(logoutMessage);
    }
}


