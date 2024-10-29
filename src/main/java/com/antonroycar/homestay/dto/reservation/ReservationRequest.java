package com.antonroycar.homestay.dto.reservation;

import com.antonroycar.homestay.dto.room.RoomTypeDetails;
import com.antonroycar.homestay.entity.AdditionalFacility;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ReservationRequest {

    @NotNull
    private Date checkInDate;

    @NotNull
    private Date checkOutDate;

    private int adults;

    private int children;

    private int quantity;

    private List<RoomTypeDetails.RoomTypeRequest> roomTypes;

    private List<AdditionalFacility> additionalFacilities;
}