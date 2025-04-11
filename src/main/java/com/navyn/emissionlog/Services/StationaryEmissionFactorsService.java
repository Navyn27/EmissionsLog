package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import com.navyn.emissionlog.Payload.Requests.EmissionFactors.StationaryEmissionFactorsDto;

import java.util.List;
import java.util.UUID;

public interface StationaryEmissionFactorsService {

    StationaryEmissionFactors createStationaryEmissionFactor(StationaryEmissionFactorsDto stationaryEmissionFactorsDto);

    StationaryEmissionFactors updateStationaryEmissionFactor(UUID id, StationaryEmissionFactorsDto EmissionFactors);

    void deleteStationaryEmissionFactors(UUID id);

    StationaryEmissionFactors getStationaryEmissionFactorsById(UUID id);

    List<StationaryEmissionFactors> getAllStationaryEmissionFactors();

    List<StationaryEmissionFactors> getStationaryEmissionFactorsByFuelId(UUID fuelId);

    StationaryEmissionFactors createStationaryEmissionFactorFromExcel(StationaryEmissionFactors stationaryEmissionFactors);
}
