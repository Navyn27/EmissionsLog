package com.navyn.emissionlog.ServiceImpls.EmissionFactors;

import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.TransportVehicleDataEmissionFactors;
import com.navyn.emissionlog.Repositories.FuelRepository;
import com.navyn.emissionlog.Repositories.TransportVehicleDataEmissionFactorsRepository;
import com.navyn.emissionlog.Services.TransportVehicleEmissionFactorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class TransportVehicleEmissionFactorsServiceImpl implements TransportVehicleEmissionFactorsService {

    @Autowired
    private TransportVehicleDataEmissionFactorsRepository transportVehicleDataEmissionFactorsRepository;
    @Autowired
    private FuelRepository fuelRepository;

    @Override
    public TransportVehicleDataEmissionFactors createTransportVehicleEmissionFactors(TransportVehicleDataEmissionFactors transportVehicleDataEmissionFactors) {
        // Implementation for creating transport vehicle emission factors
        return transportVehicleDataEmissionFactorsRepository.save(transportVehicleDataEmissionFactors);
    }

    @Override
    public List<TransportVehicleDataEmissionFactors> findByFuel(UUID id) {
        Optional<Fuel> fuel = fuelRepository.findById(id);
        if(fuel.isPresent()) {
            return transportVehicleDataEmissionFactorsRepository.findByFuel(fuel.get());
        } else {
            throw new RuntimeException("Fuel not found");
        }
    }
}
