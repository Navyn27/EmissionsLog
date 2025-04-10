package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.VehicleType;
import com.navyn.emissionlog.Models.Vehicles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicles, UUID> {
    List<Vehicles> findAll();
    List<Vehicles> findAllByVehicle(String vehicle);
    Optional<Vehicles> findByVehicleAndVehicleYearAndSizeAndWeightLaden(String vehicle, String vehicleYear, String size, String weightLaden);
}
