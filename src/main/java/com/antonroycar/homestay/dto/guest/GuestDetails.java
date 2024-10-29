package com.antonroycar.homestay.dto.guest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestDetails {

    private int adults;

    private int children;

    private int quantity;
}
