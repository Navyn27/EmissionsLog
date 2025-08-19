package com.navyn.emissionlog.modules.fuel;

import com.navyn.emissionlog.Enums.FuelSourceType;
import com.navyn.emissionlog.Enums.FuelTypes;
import com.navyn.emissionlog.modules.fuel.dtos.CreateFuelDto;
import com.navyn.emissionlog.modules.fuel.dtos.ExistingFuelDto;
import com.navyn.emissionlog.modules.fuel.repositories.FuelRepository;
import com.navyn.emissionlog.Services.FuelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FuelServiceImpl implements FuelService {

    private final FuelRepository fuelRepository;

    @Override
    public Fuel saveFuel(CreateFuelDto fuel) {
        try {
            Optional<Fuel> existingFuel = fuelRepository.findByFuel(fuel.getFuel());

            if(existingFuel.isPresent()) {

                Fuel existingFuel1 = existingFuel.get();

                if (existingFuel1.getLiquidDensity() == null || existingFuel1.getLiquidDensity() == 0.0) {
                    fuel.setFuelDensityLiquids(existingFuel.get().getLiquidDensity());
                }
                if (existingFuel1.getGasDensity() == null || existingFuel1.getGasDensity() == 0.0) {
                    fuel.setFuelDensityGases(existingFuel.get().getGasDensity());
                }
                if (existingFuel1.getLowerHeatingValue() == null || existingFuel.get().getLowerHeatingValue() == 0.0) {
                    fuel.setLowerHeatingValue(existingFuel.get().getLowerHeatingValue());
                }
                if(existingFuel1.getFuelDescription() == null || existingFuel.get().getFuelDescription().isEmpty()) {
                    fuel.setFuelDescription(existingFuel.get().getFuelDescription());
                }
                if(existingFuel1.getFuelTypes() == null || existingFuel.get().getFuelTypes() == null) {
                    fuel.setFuelTypes(existingFuel.get().getFuelTypes());
                }

                return fuelRepository.save(existingFuel1);
            }

            Fuel fuel1 = new Fuel();
            fuel1.setFuelTypes(fuel.getFuelTypes());
            fuel1.setFuel(fuel.getFuel());
            fuel1.setFuelDescription(fuel.getFuelDescription());
            fuel1.setLiquidDensity(fuel.getFuelDensityLiquids());
            fuel1.getFuelSourceTypes().add(fuel.getFuelSourceType());
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
    public Fuel updateFuel(Fuel fuel){
        return fuelRepository.save(fuel);
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

    @Override
    public List<Fuel> getFuelsByFuelType(FuelTypes fuelType) {
        return fuelRepository.findByFuelTypes(fuelType);
    }

    @Override
    public List<Fuel> getStationaryFuelsByFuelType(FuelTypes fuelType) {
        return fuelRepository.findByFuelTypesAndFuelSourceTypesContaining(fuelType, FuelSourceType.STATIONARY);
    }

    @Override
    public List<Fuel> getTransportFuelsByFuelType(FuelTypes fuelType) {
        return fuelRepository.findByFuelTypesAndFuelSourceTypesContaining(fuelType, FuelSourceType.TRANSPORT);
    }
}
