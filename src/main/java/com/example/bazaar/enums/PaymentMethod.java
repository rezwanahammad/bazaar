package com.example.bazaar.enums;

public enum PaymentMethod {
    BKASH("bKash"),
    NAGAD("Nagad"),
    COD("Cash on Delivery");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isManualGateway() {
        return this == BKASH || this == NAGAD;
    }
}
