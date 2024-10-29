package com.antonroycar.homestay.entity;

import com.antonroycar.homestay.dto.date.DateRange;
import com.antonroycar.homestay.dto.facility.FacilityDetail;
import com.antonroycar.homestay.dto.guest.GuestDetails;
import com.antonroycar.homestay.dto.room.RoomTypeDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Document(collection = "reservation")
public class Reservation {

    @Id
    private String reservationId;

    private String accountId;

    private List<RoomTypeDetails> roomTypeDetails;

    private List<FacilityDetail> additionalFacilities;

    private GuestDetails guestDetails;

    private DateRange dateRange;

}
