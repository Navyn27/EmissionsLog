package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Enums.FuelTypes;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Payload.Requests.Fuel.CreateFuelDto;
import com.navyn.emissionlog.Payload.Requests.Fuel.ExistingFuelDto;

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