package com.antonroycar.homestay.dto.facility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityDetail {

    private String type;

    private String description;

    private double price;

    private int quantity;
}

