package com.antonroycar.homestay.controller;

import com.antonroycar.homestay.dto.transaction.TransactionRequest;
import com.antonroycar.homestay.dto.transaction.TransactionResponse;
import com.antonroycar.homestay.entity.Transaction;
import com.antonroycar.homestay.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody TransactionRequest transactionRequest, HttpServletRequest request) {
        return transactionService.createTransaction(transactionRequest, request);
    }
}
