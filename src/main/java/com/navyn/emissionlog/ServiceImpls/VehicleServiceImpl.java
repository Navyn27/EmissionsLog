package com.navyn.emissionlog.ServiceImpls;

import java.util.Optional;
import com.navyn.emissionlog.Models.Vehicles;
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
    public List<Vehicles> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicles> getExistingVehicle(String vehicle, String vehicleYear, String size, String weightLaden) {
        Optional<Vehicles> vehicles = vehicleRepository.findByVehicleAndVehicleYearAndSizeAndWeightLaden(
                vehicle,
                vehicleYear,
                size,
                weightLaden
        );
        return vehicles;
    }

    @Override
    public Vehicles createVehicle(Vehicles vehicle) {
        Vehicles existingVehicle = vehicleRepository.findByVehicleAndVehicleYearAndSizeAndWeightLaden(
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
    public List<Vehicles> getAllVehiclesByVehicle(String vehicle) {
        return vehicleRepository.findAllByVehicle(vehicle);
    }
}
