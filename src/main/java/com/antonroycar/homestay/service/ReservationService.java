package com.antonroycar.homestay.service;

import com.antonroycar.homestay.dto.date.DateRange;
import com.antonroycar.homestay.dto.facility.FacilityDetail;
import com.antonroycar.homestay.dto.guest.GuestDetails;
import com.antonroycar.homestay.dto.reservation.ReservationRequest;
import com.antonroycar.homestay.dto.reservation.ReservationResponse;
import com.antonroycar.homestay.dto.room.RoomTypeDetails;
import com.antonroycar.homestay.entity.Account;
import com.antonroycar.homestay.entity.AdditionalFacility;
import com.antonroycar.homestay.entity.Reservation;
import com.antonroycar.homestay.repository.AccountRepository;
import com.antonroycar.homestay.repository.ReservationRepository;
import com.antonroycar.homestay.security.JwtUtil;
import com.antonroycar.homestay.service.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KafkaTemplate<String, ReservationResponse> kafkaTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String TOPIC = "reservation-created";

    @Transactional
    public ReservationResponse reservation(ReservationRequest reservationRequest, String token) {
        validationService.validate(reservationRequest);

        String username = jwtUtil.extractUsername(token);
        // Validasi token JWT dan ambil username
        if (!jwtUtil.validateToken(token, username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // Cari account berdasarkan username
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account"));

        // Hitung jumlah total tamu
        int totalGuests = reservationRequest.getAdults() + reservationRequest.getChildren();

        // Hitung total jumlah bed yang dipesan
        int totalBeds = reservationRequest.getRoomTypes().stream().mapToInt(RoomTypeDetails.RoomTypeRequest::getQuantity).sum();

        // Validasi: Jumlah bed tidak boleh lebih banyak dari jumlah guest
        if (totalBeds > totalGuests) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total beds cannot exceed total guests");
        }

        // Buat list RoomTypeDetails berdasarkan request
        List<RoomTypeDetails> roomTypeDetailsList = reservationRequest.getRoomTypes().stream()
                .map(roomTypeRequest -> RoomTypeDetails.builder()
                        .type(roomTypeRequest.getRoomType().name())
                        .description(roomTypeRequest.getRoomType().getDescription())
                        .price(roomTypeRequest.getRoomType().getDefaultPrice())
                        .quantity(roomTypeRequest.getQuantity())
                        .build())
                .toList();

        // Buat objek GuestDetails berdasarkan request
        GuestDetails guestDetails = GuestDetails.builder()
                .adults(reservationRequest.getAdults())
                .children(reservationRequest.getChildren())
                .quantity(totalGuests)
                .build();

        // Buat objek DateRange berdasarkan request
        DateRange dateRange = DateRange.builder()
                .checkInDate(reservationRequest.getCheckInDate())
                .checkOutDate(reservationRequest.getCheckOutDate())
                .build();

        List<AdditionalFacility> facilities = reservationRequest.getAdditionalFacilities() != null
                ? reservationRequest.getAdditionalFacilities()
                : List.of();

        // Convert List<AdditionalFacility> to List<FacilityDetail>
        List<FacilityDetail> facilityDetailsInput = facilities.stream()
                .map(facility -> FacilityDetail.builder()
                        .type(facility.name())
                        .description(facility.getDescription())
                        .price(facility.getDefaultPrice())
                        .quantity(0)  // Set initial quantity to 0; it will be updated in calculateFacilityDetails
                        .build())
                .collect(Collectors.toList());

        // Now call calculateFacilityDetails with List<FacilityDetail>
        List<FacilityDetail> facilityDetails = calculateFacilityDetails(facilityDetailsInput, totalGuests, totalBeds);

        // Buat objek Reservation berdasarkan ReservationRequest
        Reservation reservation = Reservation.builder()
                .accountId(account.getAccountId())
                .roomTypeDetails(roomTypeDetailsList)
                .additionalFacilities(facilityDetails)
                .guestDetails(guestDetails)
                .dateRange(dateRange)
                .build();

        // Simpan reservasi ke database
        reservationRepository.save(reservation);

        // Create ReservationResponse object
        ReservationResponse reservationResponse = ReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .accountId(account.getAccountId())
                .roomTypeDetails(roomTypeDetailsList)
                .guestDetails(guestDetails)
                .dateRange(dateRange)
                .additionalFacilities(facilityDetails)
                .build();

        // Publish ReservationResponse object to Kafka as JSON
        kafkaTemplate.send(TOPIC, reservationResponse);

        // Buat dan kembalikan objek ReservationResponse
        return reservationResponse;
    }

    @Transactional
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(reservation -> {
                    int totalGuests = reservation.getGuestDetails().getQuantity();
                    int totalRooms = reservation.getRoomTypeDetails().stream()
                            .mapToInt(RoomTypeDetails::getQuantity)
                            .sum();

                    // Convert additional facilities to FacilityDetail list
                    List<FacilityDetail> facilityDetails = calculateFacilityDetails(
                            reservation.getAdditionalFacilities(), totalGuests, totalRooms);

                    return ReservationResponse.builder()
                            .reservationId(reservation.getReservationId())
                            .accountId(reservation.getAccountId())
                            .roomTypeDetails(reservation.getRoomTypeDetails())
                            .guestDetails(reservation.getGuestDetails())
                            .dateRange(reservation.getDateRange())
                            .additionalFacilities(facilityDetails)  // Use calculated FacilityDetail list
                            .build();
                })
                .toList();
    }

    @Transactional
    public List<FacilityDetail> calculateFacilityDetails(List<FacilityDetail> facilities, int totalGuests, int totalRooms) {
        return facilities.stream().map(facility -> {
            int quantity = 0;
            switch (facility.getType()) {
                case "BREAKFAST":
                    quantity = totalGuests;
                    break;
                case "EXTRA_BED":
                case "EARLY_CHECK_IN":
                case "LATE_CHECK_OUT":
                case "PET_FRIENDLY_ROOM":
                    quantity = totalRooms;
                    break;
            }

            return FacilityDetail.builder()
                    .type(facility.getType())
                    .description(facility.getDescription())
                    .price(facility.getPrice())
                    .quantity(quantity)
                    .build();
        }).collect(Collectors.toList());
    }
}
