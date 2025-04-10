package com.navyn.emissionlog.Services;

import com.navyn.emissionlog.Models.TransportVehicleDataEmissionFactors;
import org.springframework.stereotype.Service;

@Service
public interface TransportVehicleEmissionFactorsService {
    TransportVehicleDataEmissionFactors createTransportVehicleEmissionFactors(TransportVehicleDataEmissionFactors transportVehicleDataEmissionFactors);
}
