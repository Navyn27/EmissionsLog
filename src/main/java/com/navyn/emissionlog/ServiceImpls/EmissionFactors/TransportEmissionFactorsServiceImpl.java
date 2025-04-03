package com.navyn.emissionlog.ServiceImpls.EmissionFactors;

import com.navyn.emissionlog.Enums.RegionGroup;
import com.navyn.emissionlog.Enums.TransportType;
import com.navyn.emissionlog.Enums.VehicleEngineType;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Models.TransportFuelEmissionFactors;
import com.navyn.emissionlog.Repositories.FuelRepository;
import com.navyn.emissionlog.Repositories.TransportFuelEmissionFactorsRepository;
import com.navyn.emissionlog.Services.TransportFuelEmissionFactorsService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransportEmissionFactorsServiceImpl implements TransportFuelEmissionFactorsService {

    @Autowired
    private TransportFuelEmissionFactorsRepository transportFuelEmissionFactorsRepository;

    @Autowired
    private FuelRepository fuelRepository;

    @Override
    public TransportFuelEmissionFactors saveTransportFuelEmissionFactors(TransportFuelEmissionFactors transportFuelEmissionFactors) {
       return transportFuelEmissionFactorsRepository.save(transportFuelEmissionFactors);
    }

    @Override
    public Optional<TransportFuelEmissionFactors> findById(UUID id) {
        return transportFuelEmissionFactorsRepository.findById(id);
    }

    @Override
    public List<TransportFuelEmissionFactors> findAll() {
        return transportFuelEmissionFactorsRepository.findAll();
    }

    @Override
    public List<TransportFuelEmissionFactors> findByFuel(UUID fuel) throws BadRequestException {
        Optional<Fuel> fuel1 = fuelRepository.findById(fuel);
        if(fuel1.isEmpty()){
            throw new BadRequestException("Fuel not found");
        }
        return transportFuelEmissionFactorsRepository.findByFuel(fuel1.get());
    }

    @Override
    public List<TransportFuelEmissionFactors> findByRegionGroup(RegionGroup regionGroup) {
        return transportFuelEmissionFactorsRepository.findByRegionGroup(regionGroup);
    }

    @Override
    public List<TransportFuelEmissionFactors> findByTransportType(TransportType transportType) {
        return transportFuelEmissionFactorsRepository.findByTransportType(transportType);
    }

    @Override
    public List<TransportFuelEmissionFactors> findByVehicleEngineType(VehicleEngineType vehicleEngineType) {
        return transportFuelEmissionFactorsRepository.findByVehicleEngineType(vehicleEngineType);
    }

    @Override
    public Optional<TransportFuelEmissionFactors> findByFuelAndRegionGroupAndTransportTypeAndVehicleEngineType(Fuel fuel, RegionGroup regionGroup, TransportType transportType, VehicleEngineType vehicleEngineType) {
        return transportFuelEmissionFactorsRepository.findByFuelAndRegionGroupAndTransportTypeAndVehicleEngineType(fuel, regionGroup, transportType, vehicleEngineType);
    }

    @Override
    public TransportFuelEmissionFactors updateTransportFuelEmissionFactors(TransportFuelEmissionFactors transportFuelEmissionFactors) {
        return transportFuelEmissionFactorsRepository.save(transportFuelEmissionFactors);
    }

    @Override
    public void deleteById(UUID id) {
        transportFuelEmissionFactorsRepository.deleteById(id);
    }

    @Override
    public void delete(TransportFuelEmissionFactors transportFuelEmissionFactors) {
        transportFuelEmissionFactorsRepository.delete(transportFuelEmissionFactors);
    }

    @Override
    public void deleteAll() {
        transportFuelEmissionFactorsRepository.deleteAll();
    }

    @Override
    public List<Fuel> findAllFuelsByRegionGroup(RegionGroup regionGroup) {
        return fuelRepository.findByRegionGroup(regionGroup);
    }

    @Override
    public List<Fuel> findAllFuelsByTransportType(TransportType transportType) {
        return fuelRepository.findByTransportType(transportType);
    }

    @Override
    public List<Fuel> findAllFuelsByVehicleEngineType(VehicleEngineType vehicleEngineType) {
        return fuelRepository.findByVehicleEngineType(vehicleEngineType);
    }
}
