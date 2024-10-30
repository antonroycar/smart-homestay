package com.antonroycar.homestay.controller;

import com.antonroycar.homestay.dto.payment.PaymentRequest;
import com.antonroycar.homestay.dto.transaction.TransactionResponse;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.entity.Role;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.security.JwtUtil;
import com.antonroycar.homestay.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponse processPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        return paymentService.processPayment(paymentRequest, request);
    }

    @GetMapping("/completed")
    public List<TransactionResponse> getCompletedPayments(HttpServletRequest request) {
        // Ambil token dari header Authorization
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is required");
        }

        String actualToken = token.substring(7);

        // Ambil username dari token JWT
        String username = jwtUtil.extractUsername(actualToken);
        // Validasi token JWT
        if (!jwtUtil.validateToken(actualToken, username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }


        // Cari account berdasarkan username
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid account"));

        // Pastikan hanya role CREW yang bisa mengakses
        if (account.getRole() != Role.CREW) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Kembalikan daftar transaksi yang sudah dibayar (status COMPLETED)
        return paymentService.getCompletedPayments();
    }
}

