package com.antonroycar.homestay.entity;

public enum AdditionalFacility {
    BREAKFAST("Breakfast", 15.00),
    EXTRA_BED("Extra Bed", 20.00),
    EARLY_CHECK_IN("Early Check-in", 30.00),
    LATE_CHECK_OUT("Late Check-out", 25.00),
    PET_FRIENDLY_ROOM("Pet-friendly Room", 50.00);

    private final String description;
    private final Double defaultPrice;

    AdditionalFacility(String description, Double defaultPrice) {
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
