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

@Document(collection = "crew")
public class Crew {

    @Id
    private String crewId;

    private Account account;

    private String crewName;

    private String jobTitle;

}
