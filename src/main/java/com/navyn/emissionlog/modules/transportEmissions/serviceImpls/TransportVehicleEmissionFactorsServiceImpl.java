package com.navyn.emissionlog.modules.transportEmissions.serviceImpls;

import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.transportEmissions.models.TransportVehicleDataEmissionFactors;
import com.navyn.emissionlog.modules.transportEmissions.repositories.TransportVehicleDataEmissionFactorsRepository;
import com.navyn.emissionlog.modules.fuel.repositories.FuelRepository;
import com.navyn.emissionlog.Services.TransportVehicleEmissionFactorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransportVehicleEmissionFactorsServiceImpl implements TransportVehicleEmissionFactorsService {

    private final TransportVehicleDataEmissionFactorsRepository transportVehicleDataEmissionFactorsRepository;
    private final FuelRepository fuelRepository;

    @Override
    public TransportVehicleDataEmissionFactors createTransportVehicleEmissionFactors(TransportVehicleDataEmissionFactors transportVehicleDataEmissionFactors) {
        // Implementation for creating transport vehicle emission factors
        System.out.println(transportVehicleDataEmissionFactors);
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
