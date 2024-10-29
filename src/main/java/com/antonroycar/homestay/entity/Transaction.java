package com.antonroycar.homestay.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Document(collection = "transaction")
public class Transaction {

    @Id
    private String transactionId;

    private Reservation reservation;

    private Account account;

    private Double totalAmount;

    private String status;            // Status transaksi (e.g. PENDING, COMPLETED)

    private Date transactionDate;
}
