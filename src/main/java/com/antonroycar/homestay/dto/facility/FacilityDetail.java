package com.antonroycar.homestay.dto.facility;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacilityDetail {

    private String type;

    private String description;

    private double price;

    private int quantity;
}

