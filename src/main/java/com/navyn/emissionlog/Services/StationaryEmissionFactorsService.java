package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import com.navyn.emissionlog.Payload.Requests.EmissionFactors.StationaryEmissionFactorsDto;

import java.util.List;
import java.util.UUID;

public interface EmissionFactorsService {

    StationaryEmissionFactors createEmissionFactor(StationaryEmissionFactorsDto stationaryEmissionFactorsDto);

    StationaryEmissionFactors updateEmissionFactor(UUID id, StationaryEmissionFactorsDto EmissionFactors);

    void deleteEmissionFactorsFactor(UUID id);

    StationaryEmissionFactors getEmissionFactorsFactorById(UUID id);

    List<StationaryEmissionFactors> getAllEmissionFactorsFactors();

    StationaryEmissionFactors getEmissionFactorsFactorByFuelId(UUID fuelId);
}
