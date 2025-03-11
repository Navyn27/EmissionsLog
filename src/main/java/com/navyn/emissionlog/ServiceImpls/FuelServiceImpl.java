package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Models.EmissionFactors;
import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Payload.Requests.CreateFuelDto;
import com.navyn.emissionlog.Payload.Requests.EmissionFactorsDto;
import com.navyn.emissionlog.Repositories.FuelRepository;
import com.navyn.emissionlog.Services.EmissionFactorsService;
import com.navyn.emissionlog.Services.FuelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FuelServiceImpl implements FuelService {

    @Autowired
    private FuelRepository fuelRepository;

    @Autowired
    private EmissionFactorsService emissionFactorsService;

    @Override
    public Fuel saveFuel(CreateFuelDto fuel) {
        try {
            Fuel fuel1 = new Fuel();
            fuel1.setFuelType(fuel.getFuelType());
            fuel1.setFuel(fuel.getFuel());
            fuel1.setLiquidDensity(fuel.getFuelDensityLiquids());
            fuel1.setGasDensity(fuel.getFuelDensityGases());
            fuel1.setLowerHeatingValue(fuel.getLowerHeatingValue());
            fuel1 = fuelRepository.save(fuel1);

            EmissionFactorsDto emissionFactorsDto = new EmissionFactorsDto();
            emissionFactorsDto.setEmission(fuel.getEmission());
            emissionFactorsDto.setGasBasis(fuel.getGasBasis());
            emissionFactorsDto.setEnergyBasis(fuel.getEnergyBasis());
            emissionFactorsDto.setMassBasis(fuel.getMassBasis());
            emissionFactorsDto.setLiquidBasis(fuel.getLiquidBasis());
            emissionFactorsDto.setFuel(fuel1.getId());
            EmissionFactors emissionFactors = emissionFactorsService.createEmissionFactorsFactor(emissionFactorsDto);
            fuel1.setEmissionFactorsList(List.of(emissionFactors));

            return fuelRepository.save(fuel1);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving fuel: " + e.getMessage());
        }
    }

    @Override
    public Optional<Fuel> getFuelById(UUID id) {
        return fuelRepository.findById(id);
    }

    @Override
    public List<Fuel> getAllFuels() {
        return fuelRepository.findAll();
    }

    @Override
    public Fuel updateFuel(UUID id, CreateFuelDto fuel) {
        return fuelRepository.findById(id)
                .map(existingFuel -> {
                    existingFuel.setFuelType(fuel.getFuelType());
                    existingFuel.setFuel(fuel.getFuel());
                    existingFuel.setLowerHeatingValue(fuel.getLowerHeatingValue());
                    existingFuel.setLiquidDensity(fuel.getFuelDensityLiquids());
                    existingFuel.setGasDensity(fuel.getFuelDensityGases());
                    return fuelRepository.save(existingFuel);
                })
                .orElseThrow(() -> new RuntimeException("Fuel not found with id " + id));
    }

    @Override
    public void deleteFuel(UUID id) {
        fuelRepository.deleteById(id);
    }
}
