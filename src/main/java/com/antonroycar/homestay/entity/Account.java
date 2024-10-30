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

@Document(collection = "account")
public class Account {

    @Id
    private String accountId;

    private Role role;

    private String username;

    private String password;

    private String token;            // Store the JWT token

    private Long tokenExpiredAt;     // Store token expiration time
}
