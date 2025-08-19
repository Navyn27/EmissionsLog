package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.modules.fuel.Fuel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

@Repository
public interface FuelRepository extends JpaRepository<Fuel, UUID> {
    Optional<Fuel> findByFuelTypesAndFuelAndLowerHeatingValueAndLiquidDensityAndGasDensity(
            FuelTypes fuelTypes,
            String fuel,
            Double lowerHeatingValue,
            Double liquidDensity,
            Double gasDensity
    );

    Optional<Fuel> findByFuel(String fuelName);

    List<Fuel> findByFuelTypes(FuelTypes fuelType);

    List<Fuel> findByFuelTypesAndFuelSourceTypesContaining(FuelTypes fuelType, FuelSourceType fuelSourceType);
}
