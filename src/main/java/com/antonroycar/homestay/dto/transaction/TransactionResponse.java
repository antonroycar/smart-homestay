package com.antonroycar.homestay.dto.transaction;

import com.antonroycar.homestay.dto.reservation.ReservationResponse;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TransactionResponse {

    private String transactionId;

    private ReservationResponse reservation;

    private double totalAmount;

    private String status;

    private Date transactionDate;

    private String paymentCode;
}
