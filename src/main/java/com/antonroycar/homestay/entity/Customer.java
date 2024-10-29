package com.antonroycar.homestay.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Document(collection = "customers")
public class Customer {

    @Id
    private String customerId;

    private String customerName;

    private int age;

    private String address;

    private String contactNumber;

    private Gender gender;

    private Account account;
}

