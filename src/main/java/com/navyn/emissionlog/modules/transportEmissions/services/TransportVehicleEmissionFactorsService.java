package com.navyn.emissionlog.modules.transportEmissions.services;

import com.navyn.emissionlog.modules.transportEmissions.models.TransportVehicleDataEmissionFactors;

import java.util.List;
import java.util.UUID;

public interface TransportVehicleEmissionFactorsService {
    TransportVehicleDataEmissionFactors createTransportVehicleEmissionFactors(TransportVehicleDataEmissionFactors transportVehicleDataEmissionFactors);

    List<TransportVehicleDataEmissionFactors> findByFuel(UUID id);
}
