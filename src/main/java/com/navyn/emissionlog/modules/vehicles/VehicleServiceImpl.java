package com.navyn.emissionlog.modules.vehicles;

import java.util.Optional;

import com.navyn.emissionlog.Repositories.VehicleRepository;
import com.navyn.emissionlog.Services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicle> getExistingVehicle(String vehicle, String vehicleYear, String size, String weightLaden) {
        Optional<Vehicle> vehicles = vehicleRepository.findByVehicleAndVehicleYearAndSizeAndWeightLaden(
                vehicle,
                vehicleYear,
                size,
                weightLaden
        );
        return vehicles;
    }

    @Override
    public Vehicle createVehicle(Vehicle vehicle) {
        Vehicle existingVehicle = vehicleRepository.findByVehicleAndVehicleYearAndSizeAndWeightLaden(
                vehicle.getVehicle(),
                vehicle.getVehicleYear(),
                vehicle.getSize(),
                vehicle.getWeightLaden()
        ).orElse(null);

        if (existingVehicle != null) {
            return existingVehicle;
        }
        return vehicleRepository.save(vehicle);
    }

    @Override
    public List<Vehicle> getAllVehiclesByVehicle(String vehicle) {
        return vehicleRepository.findAllByVehicle(vehicle);
    }
}
