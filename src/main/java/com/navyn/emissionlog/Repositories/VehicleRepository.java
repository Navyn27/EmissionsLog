package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    List<Vehicle> findAll();
    List<Vehicle> findAllByVehicle(String vehicle);
    Optional<Vehicle> findByVehicleAndVehicleYearAndSizeAndWeightLaden(String vehicle, String vehicleYear, String size, String weightLaden);
}
