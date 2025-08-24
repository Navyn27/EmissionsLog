package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Enums.Fuel.FuelTypes;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.fuel.dtos.CreateFuelDto;
import com.navyn.emissionlog.modules.fuel.dtos.ExistingFuelDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FuelService {

    Fuel saveFuel(CreateFuelDto fuel);

    Fuel updateFuel(Fuel fuel);

    Optional<Fuel> getFuelById(UUID id);

    List<Fuel> getAllFuels();
    Fuel updateFuel(UUID id, CreateFuelDto fuel);

    void deleteFuel(UUID id);

    Fuel getExistingFuel(ExistingFuelDto existingFuel);

    Optional<Fuel> getExistingFuel(String fuelName);

    List<Fuel> getFuelsByFuelType(FuelTypes fuelType);

    List<Fuel> getStationaryFuelsByFuelType(FuelTypes fuelType);

    List<Fuel> getTransportFuelsByFuelType(FuelTypes fuelType);
}