package com.navyn.emissionlog.modules.transportScenarios.enums;

public enum TransportScenarioVehicleCategory {
    MOTORCYCLE,
    PASSENGER_CAR,
    LIGHT_DUTY_VEHICLE,
    BUS,
    TRUCK,
    OTHER;

    /**
     * Helper method to identify if this category is a motorcycle.
     * Useful for mitigation breakdown if needed.
     */
    public boolean isMotorcycle() {
        return this == MOTORCYCLE;
    }

    /**
     * Helper method to identify if this category is a passenger vehicle.
     */
    public boolean isPassengerVehicle() {
        return this == MOTORCYCLE || this == PASSENGER_CAR;
    }

    /**
     * Helper method to identify if this category is a commercial vehicle.
     */
    public boolean isCommercialVehicle() {
        return this == LIGHT_DUTY_VEHICLE || this == BUS || this == TRUCK;
    }
}
