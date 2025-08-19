package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.modules.vehicles.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicle> getAllVehicles();

    List<Vehicle> getAllVehiclesByVehicle(String vehicle);

    Optional<Vehicle> getExistingVehicle(String vehicle, String vehicleYear, String size, String weightLaden);

    Vehicle createVehicle(Vehicle vehicle);
}
