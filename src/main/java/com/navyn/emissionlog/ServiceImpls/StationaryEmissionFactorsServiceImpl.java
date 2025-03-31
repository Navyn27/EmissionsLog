package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Payload.Requests.EmissionFactors.StationaryEmissionFactorsDto;
import com.navyn.emissionlog.Repositories.EmissionFactorsRepository;
import com.navyn.emissionlog.Services.EmissionFactorsService;
import com.navyn.emissionlog.Repositories.FuelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmissionFactorsServiceImpl implements EmissionFactorsService {

    @Autowired
    FuelRepository fuelRepository;

    @Autowired
    EmissionFactorsRepository emissionFactorsRepository;

    @Override
    public StationaryEmissionFactors createEmissionFactor(StationaryEmissionFactorsDto stationaryEmissionFactorsDto) {
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
        StationaryEmissionFactors savedEmissionFactor = emissionFactorsRepository.save(emissionFactor);
        fuel.get().getStationaryEmissionFactorsList().add(savedEmissionFactor);
        return savedEmissionFactor;
    }

    @Override
    public StationaryEmissionFactors updateEmissionFactor(UUID id, StationaryEmissionFactorsDto stationaryEmissionFactorsDto) {
        Optional<StationaryEmissionFactors> emissionFactor1 = emissionFactorsRepository.findById(id);
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
        return emissionFactorsRepository.save(emissionFactor);
    }

    @Override
    public void deleteEmissionFactorsFactor(UUID id) {
        emissionFactorsRepository.deleteById(id);
    }

    @Override
    public StationaryEmissionFactors getEmissionFactorsFactorById(UUID id) {
        return emissionFactorsRepository.findById(id).get();
    }

    @Override
    public List<StationaryEmissionFactors> getAllEmissionFactorsFactors() {
        return emissionFactorsRepository.findAll();
    }

    @Override
    public StationaryEmissionFactors getEmissionFactorsFactorByFuelId(UUID fuelId) {
        return emissionFactorsRepository.findByFuelId(fuelId);
    }
}
