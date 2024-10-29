package com.antonroycar.homestay.controller;

import com.antonroycar.homestay.dto.payment.PaymentRequest;
import com.antonroycar.homestay.dto.transaction.TransactionResponse;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.entity.Role;
import com.antonroycar.homestay.entity.Transaction;
import com.antonroycar.homestay.repository.AccountRepository;
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

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public TransactionResponse processPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        return paymentService.processPayment(paymentRequest, request);
    }

    @GetMapping("/completed")
    public List<TransactionResponse> getCompletedPayments(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is required");
        }

        token = token.substring(7);

        // Cari akun berdasarkan token
        Account account = accountRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));

        // Pastikan hanya role CREW yang bisa mengakses
        if (account.getRole() != Role.CREW) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Kembalikan daftar transaksi yang sudah dibayar (status COMPLETED)
        return paymentService.getCompletedPayments();
    }
}
