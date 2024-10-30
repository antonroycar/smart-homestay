package com.antonroycar.homestay.service;

import com.antonroycar.homestay.dto.payment.PaymentRequest;
import com.antonroycar.homestay.dto.reservation.ReservationResponse;
import com.antonroycar.homestay.dto.transaction.TransactionResponse;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.entity.Transaction;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.repository.TransactionRepository;
import com.antonroycar.homestay.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PaymentService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Transactional
    public TransactionResponse processPayment(PaymentRequest paymentRequest, HttpServletRequest request) {
        // Ambil token dari header Authorization
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is required");
        }
        String actualToken = token.substring(7);

        String username = jwtUtil.extractUsername(actualToken);
        // Validasi token JWT dan ambil username
        if (!jwtUtil.validateToken(actualToken, username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // Cari account berdasarkan username
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Customer must be logged in to make a payment"));

        // Check if the account token matches the provided token and if the token is expired
        if (!actualToken.equals(account.getToken()) || account.getTokenExpiredAt() < System.currentTimeMillis()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired, please log in again");
        }

        // Cari transaksi terkait
        Transaction transaction = transactionRepository.findByPaymentCode(paymentRequest.getPaymentCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        // Validasi: Pastikan transaksi milik customer yang sedang login
        if (!transaction.getAccount().getAccountId().equals(account.getAccountId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only make payments for your own transactions");
        }

        // Validasi: Pastikan status transaksi masih PENDING
        if (!transaction.getStatus().equals("PENDING")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction is not pending, cannot proceed with payment");
        }

        // Set transaction status to COMPLETED setelah pembayaran berhasil
        transaction.setStatus("COMPLETED");

        // Simpan perubahan ke database
        transactionRepository.save(transaction);

        // Buat objek ReservationResponse yang hanya menyertakan accountId
        ReservationResponse reservationResponse = ReservationResponse.builder()
                .reservationId(transaction.getReservation().getReservationId())
                .accountId(transaction.getReservation().getAccountId())  // Hanya accountId
                .roomTypeDetails(transaction.getReservation().getRoomTypeDetails())
                .guestDetails(transaction.getReservation().getGuestDetails())
                .dateRange(transaction.getReservation().getDateRange())
                .additionalFacilities(transaction.getReservation().getAdditionalFacilities())
                .build();

        // Buat dan kembalikan objek TransactionResponse yang hanya memiliki accountId
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .reservation(reservationResponse)
                .totalAmount(transaction.getTotalAmount())
                .status(transaction.getStatus())
                .transactionDate(transaction.getTransactionDate())
                .paymentCode(transaction.getPaymentCode())
                .build();
    }

    public List<TransactionResponse> getCompletedPayments(HttpServletRequest request) {

        // Retrieve token from Authorization header
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is required");
        }

        String actualToken = token.substring(7); // Remove "Bearer " prefix

        // Validate the token
        authService.validateToken(actualToken);

        // Extract username from token
        String username = jwtUtil.extractUsername(actualToken);

        // Find the account by username
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found or logged out"));

        // Validate that the token in the account matches and hasn't expired
        if (account.getToken() == null || !account.getToken().equals(actualToken) || account.getTokenExpiredAt() < System.currentTimeMillis()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired or user logged out, please log in again");
        }

        return transactionRepository.findByStatus("COMPLETED").stream()
                .map(transaction -> {
                    // Bangun kembali TransactionResponse dengan reservationId yang tersedia melalui metode baru
                    return TransactionResponse.builder()
                            .transactionId(transaction.getTransactionId())
                            .reservation(ReservationResponse.builder()
                                    .reservationId(transaction.getReservation().getReservationId())
                                    .accountId(transaction.getAccount().getAccountId())  // Hanya accountId
                                    .roomTypeDetails(transaction.getReservation().getRoomTypeDetails())
                                    .guestDetails(transaction.getReservation().getGuestDetails())
                                    .dateRange(transaction.getReservation().getDateRange())
                                    .additionalFacilities(transaction.getReservation().getAdditionalFacilities())
                                    .build())
                            .totalAmount(transaction.getTotalAmount())
                            .status(transaction.getStatus())
                            .transactionDate(transaction.getTransactionDate())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
