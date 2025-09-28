package com.navyn.emissionlog.modules.vehicles;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface VehicleService {
    List<Vehicle> getAllVehicles();

    List<Vehicle> getAllVehiclesByVehicle(String vehicle);

    Optional<Vehicle> getExistingVehicle(String vehicle, String vehicleYear, String size, String weightLaden);

    Vehicle createVehicle(Vehicle vehicle);
}
