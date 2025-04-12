package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.TransportVehicleDataEmissionFactors;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface TransportVehicleEmissionFactorsService {
    TransportVehicleDataEmissionFactors createTransportVehicleEmissionFactors(TransportVehicleDataEmissionFactors transportVehicleDataEmissionFactors);

    List<TransportVehicleDataEmissionFactors> findByFuel(UUID id);
}
