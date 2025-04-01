package com.navyn.emissionlog.ServiceImpls;

import com.navyn.emissionlog.Models.Fuel;
import com.navyn.emissionlog.Payload.Requests.CreateFuelDto;
import com.navyn.emissionlog.Payload.Requests.ExistingFuelDto;
import com.navyn.emissionlog.Repositories.FuelRepository;
import com.navyn.emissionlog.Services.StationaryEmissionFactorsService;
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

    @Override
    public Fuel saveFuel(CreateFuelDto fuel) {
        try {
            Optional<Fuel> existingFuel = fuelRepository.findByFuelTypesAndFuelAndLowerHeatingValueAndLiquidDensityAndGasDensity(
                    fuel.getFuelTypes(),
                    fuel.getFuel(),
                    fuel.getLowerHeatingValue(),
                    fuel.getFuelDensityLiquids(),
                    fuel.getFuelDensityGases()
            );

            if(existingFuel.isPresent()) {
                return existingFuel.get();
            }

            Fuel fuel1 = new Fuel();
            fuel1.setFuelTypes(fuel.getFuelTypes());
            fuel1.setFuel(fuel.getFuel());
            fuel1.setFuelDescription(fuel.getFuelDescription());
            fuel1.setLiquidDensity(fuel.getFuelDensityLiquids());
            fuel1.setGasDensity(fuel.getFuelDensityGases());
            fuel1.setLowerHeatingValue(fuel.getLowerHeatingValue());
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
                    existingFuel.setFuelTypes(fuel.getFuelTypes());
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

    @Override
    public Fuel getExistingFuel(ExistingFuelDto existingFuel){
        return fuelRepository.findByFuelTypesAndFuelAndLowerHeatingValueAndLiquidDensityAndGasDensity(
                existingFuel.getFuelType(),
                existingFuel.getFuel(),
                existingFuel.getLowerHeatingValue(),
                existingFuel.getLiquidDensity(),
                existingFuel.getGasDensity()
        ).orElseThrow(() -> new RuntimeException("Fuel not found"));
    }

    @Override
    public Optional<Fuel> getExistingFuel(String fuelName) {
        return fuelRepository.findByFuel(fuelName);
    }
}
