package com.antonroycar.homestay.controller;

import com.antonroycar.homestay.dto.reservation.ReservationRequest;
import com.antonroycar.homestay.dto.reservation.ReservationResponse;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.security.JwtUtil;
import com.antonroycar.homestay.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@RequestBody ReservationRequest reservationRequest, HttpServletRequest request) {
        // Ambil token dari header Authorization
        String token = request.getHeader("Authorization");

        // Periksa apakah token ada
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is required");
        }


        // Hapus "Bearer " dari awal token
        String actualToken = token.substring(7);
        String username = jwtUtil.extractUsername(actualToken);

        // Validasi token JWT
        if (!jwtUtil.validateToken(actualToken, username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // Panggil service untuk membuat reservasi
        return reservationService.reservation(reservationRequest, actualToken);
    }

    @GetMapping("/all")
    public List<ReservationResponse> getAllReservations(HttpServletRequest request) {
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

        // Periksa apakah pengguna memiliki role CREW untuk mengakses semua reservasi
        if (!account.getRole().name().equalsIgnoreCase("CREW")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Kembalikan semua reservasi
        return reservationService.getAllReservations();
    }
}
