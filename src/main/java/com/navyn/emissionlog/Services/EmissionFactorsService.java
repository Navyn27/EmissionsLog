package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.EmissionFactors;
import com.navyn.emissionlog.Payload.Requests.EmissionFactorsDto;

import java.util.List;
import java.util.UUID;

public interface EmissionFactorsService {

    EmissionFactors createEmissionFactorsFactor(EmissionFactorsDto emissionFactorsDto);

    EmissionFactors updateEmissionFactorsFactor(UUID id, EmissionFactorsDto EmissionFactors);

    void deleteEmissionFactorsFactor(UUID id);

    EmissionFactors getEmissionFactorsFactorById(UUID id);

    List<EmissionFactors> getAllEmissionFactorsFactors();

    EmissionFactors getEmissionFactorsFactorByFuelId(UUID fuelId);
}
