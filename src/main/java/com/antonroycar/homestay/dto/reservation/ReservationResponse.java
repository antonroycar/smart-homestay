package com.antonroycar.homestay.dto.reservation;

import com.antonroycar.homestay.dto.date.DateRange;
import com.antonroycar.homestay.dto.facility.FacilityDetail;
import com.antonroycar.homestay.dto.guest.GuestDetails;
import com.antonroycar.homestay.dto.room.RoomTypeDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponse {

    private String reservationId;

    private String accountId;

    private GuestDetails guestDetails;

    private DateRange dateRange;

    private List<RoomTypeDetails> roomTypeDetails;

    private List<FacilityDetail> additionalFacilities;
}
