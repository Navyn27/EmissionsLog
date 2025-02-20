package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Payload.Requests.CreateFuelDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FuelService {

    Fuel saveFuel(CreateFuelDto fuel);

    Optional<Fuel> getFuelById(UUID id);

    List<Fuel> getAllFuels();
    Fuel updateFuel(UUID id, CreateFuelDto fuel);

    void deleteFuel(UUID id);
}