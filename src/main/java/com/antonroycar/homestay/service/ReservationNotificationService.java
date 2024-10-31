package com.antonroycar.homestay.service;

import com.antonroycar.homestay.dto.facility.FacilityDetail;
import com.antonroycar.homestay.dto.reservation.ReservationResponse;
import com.antonroycar.homestay.dto.room.RoomTypeDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ReservationNotificationService {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "reservation-created", groupId = "reservation-notification-group")
    public void handleReservationCreated(ReservationResponse reservation) {

        // Construct email content from reservation details
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("Dear Customer,\n\n");
        emailContent.append("Thank you for your reservation. Here are your reservation details:\n\n");

        // Room Type Details
        emailContent.append("Room Type Details:\n");
        for (RoomTypeDetails roomType : reservation.getRoomTypeDetails()) {
            emailContent.append("- Type: ").append(roomType.getType())
                    .append(", Description: ").append(roomType.getDescription())
                    .append(", Price: $").append(roomType.getPrice())
                    .append(", Quantity: ").append(roomType.getQuantity())
                    .append("\n");
        }

        // Additional Facilities (if any)
        if (reservation.getAdditionalFacilities() != null && !reservation.getAdditionalFacilities().isEmpty()) {
            emailContent.append("\nAdditional Facilities:\n");
            for (FacilityDetail facility : reservation.getAdditionalFacilities()) {
                emailContent.append("- Facility: ").append(facility.getType())
                        .append(", Description: ").append(facility.getDescription())
                        .append(", Price: $").append(facility.getPrice())
                        .append(", Quantity: ").append(facility.getQuantity())
                        .append("\n");
            }
        }

        // Guest Details
        emailContent.append("\nGuest Details:\n");
        emailContent.append("- Adults: ").append(reservation.getGuestDetails().getAdults()).append("\n");
        emailContent.append("- Children: ").append(reservation.getGuestDetails().getChildren()).append("\n");
        emailContent.append("- Total Guests: ").append(reservation.getGuestDetails().getQuantity()).append("\n");

        // Date Range
        emailContent.append("\nReservation Dates:\n");
        emailContent.append("- Check-In Date: ").append(reservation.getDateRange().getCheckInDate()).append("\n");
        emailContent.append("- Check-Out Date: ").append(reservation.getDateRange().getCheckOutDate()).append("\n");

        emailContent.append("\nWe look forward to hosting you!\n\nBest Regards,\nHomestay Team");

        // Set email subject
        String emailSubject = "Your Homestay Reservation Confirmation";

        // Send email
        emailService.sendEmail("rrqneutron@gmail.com", emailSubject, emailContent.toString());
    }
}

