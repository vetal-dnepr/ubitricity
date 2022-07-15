package com.vetal.ubitricity.enums;

public enum ChargerStatus {

	OFF(0), LOW(10), FAST(20);

    private int value;

    ChargerStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
