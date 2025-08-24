package com.navyn.emissionlog.Enums.Transport;

/**
 * Enum representing different types of vehicles for emission calculations.
 */
public enum VehicleType {
    // Passenger vehicles
    PASSENGER_CAR("Passenger Car"),
    MOTORCYCLE("Motorcycles"),

    // Bus category
    BUS("Bus"),

    // Light duty category
    LIGHT_DUTY_VEHICLE("Light Duty Vehicles"),
    LIGHT_DUTY_CAR("Light-Duty Cars"),
    LIGHT_DUTY_TRUCK("Light-Duty Trucks"),

    // Medium duty category
    MEDIUM_DUTY_VEHICLE("Medium-Duty Vehicles"),
    MEDIUM_DUTY_TRUCK("Medium-Duty Trucks"),

    // Heavy duty category
    HEAVY_DUTY_VEHICLE("Heavy Duty Vehicles"),
    HEAVY_DUTY_TRUCK("Heavy-Duty Trucks");

    private final String description;

    VehicleType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns the vehicle category (Light, Medium, Heavy, etc.)
     */
    public String getCategory() {
        if (name().contains("LIGHT")) {
            return "Light Duty";
        } else if (name().contains("MEDIUM")) {
            return "Medium Duty";
        } else if (name().contains("HEAVY")) {
            return "Heavy Duty";
        } else if (this == BUS) {
            return "Bus";
        } else {
            return "Passenger";
        }
    }
}