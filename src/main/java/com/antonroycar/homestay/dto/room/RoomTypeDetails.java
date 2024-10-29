package com.antonroycar.homestay.dto.room;

import com.antonroycar.homestay.entity.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomTypeDetails {

    private String type;

    private String description;

    private Double price;

    private int quantity;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RoomTypeRequest {

        private RoomType roomType;

        private int quantity;
    }
}