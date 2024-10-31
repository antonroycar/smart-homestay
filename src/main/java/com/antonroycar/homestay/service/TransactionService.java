package com.antonroycar.homestay.service;

import com.antonroycar.homestay.dto.facility.FacilityDetail;
import com.antonroycar.homestay.dto.reservation.ReservationResponse;
import com.antonroycar.homestay.dto.transaction.TransactionRequest;
import com.antonroycar.homestay.dto.transaction.TransactionResponse;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.entity.Reservation;
import com.antonroycar.homestay.entity.Transaction;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.repository.ReservationRepository;
import com.antonroycar.homestay.repository.TransactionRepository;
import com.antonroycar.homestay.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest transactionRequest, HttpServletRequest request) {

        // Ambil token dari header Authorization
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is required");
        }
        String actualToken = token.substring(7);  // Hapus "Bearer " dari awal token

        String username = jwtUtil.extractUsername(actualToken);

        // Validasi token JWT dan ambil username
        if (!jwtUtil.validateToken(actualToken, username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // Cari account berdasarkan username
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Customer must be logged in to make a transaction"));

        // Check if the account token matches the provided token and if the token is expired
        if (!actualToken.equals(account.getToken()) || account.getTokenExpiredAt() < System.currentTimeMillis()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token has expired, please log in again");
        }

        // Cari reservasi terkait
        Reservation reservation = reservationRepository.findById(transactionRequest.getReservationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        // Validasi: Pastikan tidak ada transaksi lain dengan reservationId yang sama
        if (transactionRepository.existsByReservation(reservation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction for this reservation already exists");
        }

        // Pastikan reservasi milik customer yang sedang login
        if (!reservation.getAccountId().equals(account.getAccountId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only make transactions for your own reservations");
        }

        // Calculate total room cost
        double roomCost = reservation.getRoomTypeDetails().stream()
                .mapToDouble(roomType -> roomType.getPrice() * roomType.getQuantity())
                .sum();

        // Calculate additional facility costs
        int totalGuests = reservation.getGuestDetails().getQuantity();
        int totalRooms = reservation.getRoomTypeDetails().stream()
                .mapToInt(room -> room.getQuantity())
                .sum();

        // Get facility details with calculated quantities and prices
        List<FacilityDetail> facilityDetails = reservationService.calculateFacilityDetails(
                reservation.getAdditionalFacilities(), totalGuests, totalRooms);

        // Calculate total cost for additional facilities
        double additionalFacilityCost = facilityDetails.stream()
                .mapToDouble(facility -> facility.getPrice() * facility.getQuantity())
                .sum();

        // Hitung durasi menginap dalam hari
        long stayDuration = ChronoUnit.DAYS.between(
                reservation.getDateRange().getCheckInDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                reservation.getDateRange().getCheckOutDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());

        if (stayDuration < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-out date must be at least one day after check-in date.");
        }

        // Calculate final total amount (roomCost + additionalFacilityCost) * stayDuration
        double totalAmount = (roomCost + additionalFacilityCost) * stayDuration;

        // Generate a unique payment code
        String paymentCode;
        do {
            paymentCode = UUID.randomUUID().toString();
        } while (transactionRepository.findByPaymentCode(paymentCode).isPresent());  // Check for uniqueness

        // Buat objek Transaction
        Transaction transaction = Transaction.builder()
                .reservation(reservation)
                .account(account)  // Simpan referensi ke customer
                .totalAmount(totalAmount)  // Hasil dari perhitungan otomatis
                .status("PENDING")  // Status awal transaksi
                .transactionDate(new Date())
                .paymentCode(paymentCode)
                .build();

        // Simpan transaksi ke database
        transactionRepository.save(transaction);

        // Buat objek ReservationResponse yang hanya menampilkan accountId
        ReservationResponse reservationResponse = ReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .accountId(reservation.getAccountId())  // Hanya accountId
                .roomTypeDetails(reservation.getRoomTypeDetails())
                .guestDetails(reservation.getGuestDetails())
                .dateRange(reservation.getDateRange())
                .additionalFacilities(facilityDetails)
                .build();

        // Buat dan kembalikan objek TransactionResponse
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .reservation(reservationResponse)  // Reservation tanpa detail account
                .totalAmount(totalAmount)
                .status(transaction.getStatus())
                .transactionDate(transaction.getTransactionDate())
                .paymentCode(paymentCode)
                .build();
    }
}
