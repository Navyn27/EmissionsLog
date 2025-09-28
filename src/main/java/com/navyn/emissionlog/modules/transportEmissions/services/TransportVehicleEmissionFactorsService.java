package com.navyn.emissionlog.modules.transportEmissions.services;

import com.navyn.emissionlog.modules.transportEmissions.models.TransportVehicleDataEmissionFactors;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TransportVehicleEmissionFactorsService {
    TransportVehicleDataEmissionFactors createTransportVehicleEmissionFactors(TransportVehicleDataEmissionFactors transportVehicleDataEmissionFactors);

    List<TransportVehicleDataEmissionFactors> findByFuel(UUID id);
}
