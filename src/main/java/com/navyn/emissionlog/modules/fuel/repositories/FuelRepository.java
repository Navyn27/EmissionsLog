package com.navyn.emissionlog.modules.fuel.repositories;

import com.navyn.emissionlog.Enums.Fuel.FuelSourceType;
import com.navyn.emissionlog.Enums.Fuel.FuelTypes;
import com.navyn.emissionlog.modules.fuel.Fuel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

@Repository
public interface FuelRepository extends JpaRepository<Fuel, UUID> {
    Optional<Fuel> findByFuelTypeAndFuelAndLowerHeatingValueAndLiquidDensityAndGasDensity(
            FuelTypes fuelTypes,
            String fuel,
            Double lowerHeatingValue,
            Double liquidDensity,
            Double gasDensity
    );

    Optional<Fuel> findByFuel(String fuelName);

    List<Fuel> findByFuelType(FuelTypes fuelType);

    List<Fuel> findByFuelTypeAndFuelSourceTypesContaining(FuelTypes fuelType, FuelSourceType fuelSourceType);

    Optional<Fuel> findByCheckSum(String checkSum);
}
