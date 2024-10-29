package com.antonroycar.homestay.entity;

public enum RoomType {
    SINGLE("Single Room", 100.00),
    DOUBLE("Double Room", 150.00),
    SUITE("Suite Room", 300.00),
    FAMILY("Family Room", 200.00);

    private final String description;
    private final Double defaultPrice;

    RoomType(String description, Double defaultPrice) {
        this.description = description;
        this.defaultPrice = defaultPrice;
    }

    public String getDescription() {
        return description;
    }

    public Double getDefaultPrice() {
        return defaultPrice;
    }
}
