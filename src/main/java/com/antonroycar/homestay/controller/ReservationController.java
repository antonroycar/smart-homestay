package com.antonroycar.homestay.controller;

import com.antonroycar.homestay.dto.reservation.ReservationResponse;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.entity.Reservation;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antonroycar.homestay.dto.reservation.ReservationRequest;
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
        token = token.substring(7);

        // Panggil service untuk membuat reservasi
        return reservationService.reservation(reservationRequest, token);
    }

    @GetMapping("/all")
    public List<ReservationResponse> getAllReservations(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is required");
        }

        token = token.substring(7);

        // Misalkan ada metode untuk memverifikasi apakah pengguna adalah Crew
        Account account = accountRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));

        if (!account.getRole().name().equalsIgnoreCase("CREW")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }


        // Kembalikan semua reservasi
        return reservationService.getAllReservations();
    }
}