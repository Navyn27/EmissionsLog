package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.modules.vehicles.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    List<Vehicle> findAll();
    List<Vehicle> findAllByVehicle(String vehicle);
    Optional<Vehicle> findByVehicleAndVehicleYearAndSizeAndWeightLaden(String vehicle, String vehicleYear, String size, String weightLaden);
}
