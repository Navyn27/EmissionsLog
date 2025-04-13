package com.navyn.emissionlog.ServiceImpls.EmissionFactors;

import com.navyn.emissionlog.Models.TransportVehicleDataEmissionFactors;
import com.navyn.emissionlog.Repositories.TransportVehicleDataEmissionFactorsRepository;
import com.navyn.emissionlog.Services.TransportVehicleEmissionFactorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransportVehicleEmissionFactorsServiceImpl implements TransportVehicleEmissionFactorsService {

    @Autowired
    private TransportVehicleDataEmissionFactorsRepository transportVehicleDataEmissionFactorsRepository;

    @Override
    public TransportVehicleDataEmissionFactors createTransportVehicleEmissionFactors(TransportVehicleDataEmissionFactors transportVehicleDataEmissionFactors) {
        // Implementation for creating transport vehicle emission factors
        return transportVehicleDataEmissionFactorsRepository.save(transportVehicleDataEmissionFactors);
    }
}
