package com.navyn.emissionlog.modules.stationaryEmissions.serviceImpls;

import com.navyn.emissionlog.modules.stationaryEmissions.StationaryEmissionFactors;
import com.navyn.emissionlog.modules.fuel.Fuel;
import com.navyn.emissionlog.modules.stationaryEmissions.dtos.StationaryEmissionFactorsDto;
import com.navyn.emissionlog.Repositories.StationaryEmissionFactorsRepository;
import com.navyn.emissionlog.modules.stationaryEmissions.services.StationaryEmissionFactorsService;
import com.navyn.emissionlog.Repositories.FuelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StationaryEmissionFactorsServiceImpl implements StationaryEmissionFactorsService {

    @Autowired
    FuelRepository fuelRepository;

    @Autowired
    StationaryEmissionFactorsRepository stationaryEmissionFactorsRepository;

    @Override
    public StationaryEmissionFactors createStationaryEmissionFactor(StationaryEmissionFactorsDto stationaryEmissionFactorsDto) {
        StationaryEmissionFactors emissionFactor = new StationaryEmissionFactors();
        Optional<Fuel> fuel = fuelRepository.findById(stationaryEmissionFactorsDto.getFuel());
        if(fuel.isEmpty()) {
            throw new IllegalArgumentException();
        }
        emissionFactor.setEmmission(stationaryEmissionFactorsDto.getEmission());
        emissionFactor.setEnergyBasis(stationaryEmissionFactorsDto.getEnergyBasis());
        emissionFactor.setFuel(fuel.get());
        emissionFactor.setGasBasis(stationaryEmissionFactorsDto.getGasBasis());
        emissionFactor.setLiquidBasis(stationaryEmissionFactorsDto.getLiquidBasis());
        emissionFactor.setMassBasis(stationaryEmissionFactorsDto.getMassBasis());
        StationaryEmissionFactors savedEmissionFactor = stationaryEmissionFactorsRepository.save(emissionFactor);
        fuel.get().getStationaryEmissionFactorsList().add(savedEmissionFactor);
        return savedEmissionFactor;
    }

    @Override
    public StationaryEmissionFactors updateStationaryEmissionFactor(UUID id, StationaryEmissionFactorsDto stationaryEmissionFactorsDto) {
        Optional<StationaryEmissionFactors> emissionFactor1 = stationaryEmissionFactorsRepository.findById(id);
        Optional<Fuel> fuel = fuelRepository.findById(stationaryEmissionFactorsDto.getFuel());
        if(fuel.isEmpty() || emissionFactor1.isEmpty()) {
            throw new IllegalArgumentException();
        }

        StationaryEmissionFactors emissionFactor = emissionFactor1.get();
        emissionFactor.setEmmission(stationaryEmissionFactorsDto.getEmission());
        emissionFactor.setEnergyBasis(stationaryEmissionFactorsDto.getEnergyBasis());
        emissionFactor.setFuel(fuel.get());
        emissionFactor.setGasBasis(stationaryEmissionFactorsDto.getGasBasis());
        emissionFactor.setLiquidBasis(stationaryEmissionFactorsDto.getLiquidBasis());
        emissionFactor.setMassBasis(stationaryEmissionFactorsDto.getMassBasis());
        return stationaryEmissionFactorsRepository.save(emissionFactor);
    }

    @Override
    public void deleteStationaryEmissionFactors(UUID id) {
        stationaryEmissionFactorsRepository.deleteById(id);
    }

    @Override
    public StationaryEmissionFactors getStationaryEmissionFactorsById(UUID id) {
        return stationaryEmissionFactorsRepository.findById(id).get();
    }

    @Override
    public List<StationaryEmissionFactors> getAllStationaryEmissionFactors() {
        return stationaryEmissionFactorsRepository.findAll();
    }

    @Override
    public List<StationaryEmissionFactors> getStationaryEmissionFactorsByFuelId(UUID fuelId) {
        Optional<Fuel> fuel = fuelRepository.findById(fuelId);

        if(fuel.isEmpty()){
            throw new IllegalArgumentException("Fuel not found");
        }
        return stationaryEmissionFactorsRepository.findByFuel(fuel.get());
    }

    @Override
    public StationaryEmissionFactors createStationaryEmissionFactorFromExcel(StationaryEmissionFactors stationaryEmissionFactors) {
        return stationaryEmissionFactorsRepository.save(stationaryEmissionFactors);
    }
}
