package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Models.EmissionFactors;
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
    public EmissionFactors createEmissionFactor(EmissionFactorsDto emissionFactorsDto) {
        EmissionFactors emissionFactor = new EmissionFactors();
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
        EmissionFactors savedEmissionFactor = emissionFactorsRepository.save(emissionFactor);
        fuel.get().getEmissionFactorsList().add(savedEmissionFactor);
        return savedEmissionFactor;
    }

    @Override
    public EmissionFactors updateEmissionFactor(UUID id, EmissionFactorsDto emissionFactorsDto) {
        Optional<EmissionFactors> emissionFactor1 = emissionFactorsRepository.findById(id);
        Optional<Fuel> fuel = fuelRepository.findById(emissionFactorsDto.getFuel());
        if(fuel.isEmpty() || emissionFactor1.isEmpty()) {
            throw new IllegalArgumentException();
        }

        EmissionFactors emissionFactor = emissionFactor1.get();
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
    public EmissionFactors getEmissionFactorsFactorById(UUID id) {
        return emissionFactorsRepository.findById(id).get();
    }

    @Override
    public List<EmissionFactors> getAllEmissionFactorsFactors() {
        return emissionFactorsRepository.findAll();
    }

    @Override
    public EmissionFactors getEmissionFactorsFactorByFuelId(UUID fuelId) {
        return emissionFactorsRepository.findByFuelId(fuelId);
    }
}
