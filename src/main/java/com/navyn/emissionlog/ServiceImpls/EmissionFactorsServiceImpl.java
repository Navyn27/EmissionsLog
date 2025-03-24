package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Models.StationaryEmissionFactors;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Payload.Requests.EmissionFactorsDto;
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
    public StationaryEmissionFactors createEmissionFactor(EmissionFactorsDto emissionFactorsDto) {
        StationaryEmissionFactors emissionFactor = new StationaryEmissionFactors();
        Optional<Fuel> fuel = fuelRepository.findById(emissionFactorsDto.getFuel());
        if(fuel.isEmpty()) {
            throw new IllegalArgumentException();
        }
        emissionFactor.setEmmission(emissionFactorsDto.getEmission());
        emissionFactor.setEnergyBasis(emissionFactorsDto.getEnergyBasis());
        emissionFactor.setFuel(fuel.get());
        emissionFactor.setGasBasis(emissionFactorsDto.getGasBasis());
        emissionFactor.setLiquidBasis(emissionFactorsDto.getLiquidBasis());
        emissionFactor.setMassBasis(emissionFactorsDto.getMassBasis());
        StationaryEmissionFactors savedEmissionFactor = emissionFactorsRepository.save(emissionFactor);
        fuel.get().getStationaryEmissionFactorsList().add(savedEmissionFactor);
        return savedEmissionFactor;
    }

    @Override
    public StationaryEmissionFactors updateEmissionFactor(UUID id, EmissionFactorsDto emissionFactorsDto) {
        Optional<StationaryEmissionFactors> emissionFactor1 = emissionFactorsRepository.findById(id);
        Optional<Fuel> fuel = fuelRepository.findById(emissionFactorsDto.getFuel());
        if(fuel.isEmpty() || emissionFactor1.isEmpty()) {
            throw new IllegalArgumentException();
        }

        StationaryEmissionFactors emissionFactor = emissionFactor1.get();
        emissionFactor.setEmmission(emissionFactorsDto.getEmission());
        emissionFactor.setEnergyBasis(emissionFactorsDto.getEnergyBasis());
        emissionFactor.setFuel(fuel.get());
        emissionFactor.setGasBasis(emissionFactorsDto.getGasBasis());
        emissionFactor.setLiquidBasis(emissionFactorsDto.getLiquidBasis());
        emissionFactor.setMassBasis(emissionFactorsDto.getMassBasis());
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
