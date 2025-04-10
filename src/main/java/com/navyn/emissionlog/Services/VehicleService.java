package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.VehicleType;
import com.navyn.emissionlog.Models.Vehicles;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicles> getAllVehicles();

    List<Vehicles> getAllVehiclesByVehicle(String vehicle);

    Optional<Vehicles> getExistingVehicle( String vehicle, String vehicleYear, String size, String weightLaden);

    Vehicles createVehicle(Vehicles vehicle);
}
