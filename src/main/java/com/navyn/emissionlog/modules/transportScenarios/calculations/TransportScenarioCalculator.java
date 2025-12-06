package com.navyn.emissionlog.modules.transportScenarios.calculations;

import com.navyn.emissionlog.modules.transportScenarios.dtos.TransportScenarioRunResponseDto;
import com.navyn.emissionlog.modules.transportScenarios.dtos.TransportScenarioYearResultDto;
import com.navyn.emissionlog.modules.transportScenarios.enums.TransportScenarioFuelType;
import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenario;
import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenarioModalShiftAssumption;
import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenarioVehicleAssumption;
import com.navyn.emissionlog.modules.transportScenarios.models.TransportScenarioYearGlobalAssumption;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TransportScenarioCalculator {

    /**
     * Calculate emissions scenario results for all years
     *
     * @param scenario The transport scenario
     * @param vehicleAssumptions List of vehicle assumptions
     * @param globalAssumptions List of global assumptions per year
     * @return TransportScenarioRunResponseDto with yearly results
     */
    public TransportScenarioRunResponseDto calculate(
            TransportScenario scenario,
            List<TransportScenarioVehicleAssumption> vehicleAssumptions,
            List<TransportScenarioYearGlobalAssumption> globalAssumptions) {
        return calculate(scenario, vehicleAssumptions, globalAssumptions, List.of());
    }

    /**
     * Calculate emissions scenario results for all years with modal shift
     *
     * @param scenario The transport scenario
     * @param vehicleAssumptions List of vehicle assumptions
     * @param globalAssumptions List of global assumptions per year
     * @param modalShiftAssumptions List of modal shift assumptions per year
     * @return TransportScenarioRunResponseDto with yearly results
     */
    public TransportScenarioRunResponseDto calculate(
            TransportScenario scenario,
            List<TransportScenarioVehicleAssumption> vehicleAssumptions,
            List<TransportScenarioYearGlobalAssumption> globalAssumptions,
            List<TransportScenarioModalShiftAssumption> modalShiftAssumptions) {

        // Group assumptions by year for easier lookup
        Map<Integer, List<TransportScenarioVehicleAssumption>> vehicleByYear = vehicleAssumptions.stream()
                .collect(Collectors.groupingBy(TransportScenarioVehicleAssumption::getYear));

        Map<Integer, TransportScenarioYearGlobalAssumption> globalByYear = globalAssumptions.stream()
                .collect(Collectors.toMap(
                        TransportScenarioYearGlobalAssumption::getYear,
                        g -> g
                ));

        Map<Integer, TransportScenarioModalShiftAssumption> modalShiftByYear = modalShiftAssumptions.stream()
                .collect(Collectors.toMap(
                        TransportScenarioModalShiftAssumption::getYear,
                        m -> m,
                        (existing, replacement) -> existing
                ));

        List<TransportScenarioYearResultDto> results = new ArrayList<>();

        // Calculate for each year in the scenario range
        for (int year = scenario.getBaseYear(); year <= scenario.getEndYear(); year++) {
            TransportScenarioYearResultDto yearResult = calculateYearResult(
                    year,
                    vehicleByYear.getOrDefault(year, List.of()),
                    globalByYear.get(year),
                    modalShiftByYear.get(year)
            );
            results.add(yearResult);
        }

        return new TransportScenarioRunResponseDto(
                scenario.getId(),
                scenario.getName(),
                scenario.getBaseYear(),
                scenario.getEndYear(),
                results
        );
    }

    /**
     * Calculate emissions for a single year with optional modal shift
     */
    private TransportScenarioYearResultDto calculateYearResult(
            int year,
            List<TransportScenarioVehicleAssumption> vehicleAssumptions,
            TransportScenarioYearGlobalAssumption globalAssumption,
            TransportScenarioModalShiftAssumption modalShiftAssumption) {

        if (globalAssumption == null || vehicleAssumptions.isEmpty()) {
            // No data for this year, return zeros
            return new TransportScenarioYearResultDto(year, 0.0, 0.0, 0.0, 0.0);
        }

        double bauTotalCO2 = 0.0;
        double altTotalCO2 = 0.0;

        // Process each vehicle category
        for (TransportScenarioVehicleAssumption assumption : vehicleAssumptions) {
            VehicleCategoryResult result = calculateVehicleCategoryEmissions(assumption, globalAssumption);
            bauTotalCO2 += result.bauCO2;
            altTotalCO2 += result.altCO2;
        }

        // Add modal shift emissions and mitigation if present
        if (modalShiftAssumption != null) {
            ModalShiftResult modalResult = calculateModalShiftEmissions(modalShiftAssumption);
            bauTotalCO2 += modalResult.bauModalCO2;
            altTotalCO2 += modalResult.altModalCO2;
        }

        // Calculate mitigation and reduction percentage
        double totalMitigation = bauTotalCO2 - altTotalCO2;
        double reductionPercent = bauTotalCO2 > 0 ? (totalMitigation / bauTotalCO2) * 100.0 : 0.0;

        TransportScenarioYearResultDto result = new TransportScenarioYearResultDto(
                year,
                bauTotalCO2,
                altTotalCO2,
                totalMitigation,
                reductionPercent
        );

        // Round values to 3 decimal places
        result.roundValues();

        return result;
    }

    /**
     * Calculate emissions for a single vehicle category
     */
    private VehicleCategoryResult calculateVehicleCategoryEmissions(
            TransportScenarioVehicleAssumption assumption,
            TransportScenarioYearGlobalAssumption globalAssumption) {

        // Step 1: Vehicle-km total
        double vktTotal = assumption.getFleetSize() * assumption.getAverageKmPerVehicle();

        // Step 2: Split into ICE vs EV
        double evShare = clamp(assumption.getEvShare(), 0.0, 1.0);
        double vktEV = vktTotal * evShare;
        double vktICE = vktTotal * (1.0 - evShare);

        // For BAU: no EVs
        double vktICE_BAU = vktTotal;

        // Get fuel-specific emission factors and energy densities
        double fuelEmissionFactor;
        double fuelEnergyDensity;

        if (assumption.getFuelType() == TransportScenarioFuelType.GASOLINE) {
            fuelEmissionFactor = globalAssumption.getFuelEmissionFactorTco2PerTJ_Gasoline();
            fuelEnergyDensity = globalAssumption.getFuelEnergyDensityTjPerL_Gasoline();
        } else if (assumption.getFuelType() == TransportScenarioFuelType.DIESEL) {
            fuelEmissionFactor = globalAssumption.getFuelEmissionFactorTco2PerTJ_Diesel();
            fuelEnergyDensity = globalAssumption.getFuelEnergyDensityTjPerL_Diesel();
        } else {
            // OTHER fuel type - treat as zero emissions for now
            fuelEmissionFactor = 0.0;
            fuelEnergyDensity = 0.0;
        }

        // Step 3: Fuel consumption for ICE (BAU)
        double fuelLitres_BAU = vktICE_BAU * assumption.getFuelEconomyLPer100Km() / 100.0;
        double fuelTJ_BAU = fuelLitres_BAU * fuelEnergyDensity;
        double CO2_ICE_BAU = fuelTJ_BAU * fuelEmissionFactor;

        // Step 3: Fuel consumption for ICE (Alternative)
        double fuelLitres_Alt = vktICE * assumption.getFuelEconomyLPer100Km() / 100.0;
        double fuelTJ_Alt = fuelLitres_Alt * fuelEnergyDensity;
        double CO2_ICE_Alt = fuelTJ_Alt * fuelEmissionFactor;

        // Step 4: EV electricity demand and emissions
        double EV_kWh = vktEV * (assumption.getEvKWhPer100Km() / 100.0);
        double EV_MWh = EV_kWh / 1000.0;
        double CO2_EV = EV_MWh * globalAssumption.getGridEmissionFactorTco2PerMWh();

        // Total emissions
        double bauCO2 = CO2_ICE_BAU;
        double altCO2 = CO2_ICE_Alt + CO2_EV;

        return new VehicleCategoryResult(bauCO2, altCO2);
    }

    /**
     * Clamp value between min and max
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Calculate modal shift emissions and mitigation
     *
     * @param modalShift Modal shift assumption for the year
     * @return ModalShiftResult with BAU and alternative emissions
     */
    private ModalShiftResult calculateModalShiftEmissions(TransportScenarioModalShiftAssumption modalShift) {
        // Step 1: Compute BAU modal emissions (g CO2)
        double CO2_modal_BAU_g =
                modalShift.getPassengerKmMotorcycleBau() * modalShift.getEmissionFactorMotorcycle_gPerPassKm()
                        + modalShift.getPassengerKmCarBau() * modalShift.getEmissionFactorCar_gPerPassKm()
                        + modalShift.getPassengerKmBusBau() * modalShift.getEmissionFactorBus_gPerPassKm();

        // Convert g to tonnes CO2
        double CO2_modal_BAU_tonnes = CO2_modal_BAU_g / 1_000_000.0;

        // Step 2: Apply shift
        double shiftMToB = clamp(modalShift.getShiftFractionMotorcycleToBus(), 0.0, 1.0);
        double shiftCToB = clamp(modalShift.getShiftFractionCarToBus(), 0.0, 1.0);

        double passengerKmMotorcycle_new = modalShift.getPassengerKmMotorcycleBau() * (1.0 - shiftMToB);
        double passengerKmCar_new = modalShift.getPassengerKmCarBau() * (1.0 - shiftCToB);
        double passengerKmBus_new = modalShift.getPassengerKmBusBau()
                + modalShift.getPassengerKmMotorcycleBau() * shiftMToB
                + modalShift.getPassengerKmCarBau() * shiftCToB;

        // Step 3: Recompute emissions with new passenger-km
        double CO2_modal_Alt_g =
                passengerKmMotorcycle_new * modalShift.getEmissionFactorMotorcycle_gPerPassKm()
                        + passengerKmCar_new * modalShift.getEmissionFactorCar_gPerPassKm()
                        + passengerKmBus_new * modalShift.getEmissionFactorBus_gPerPassKm();

        // Convert g to tonnes CO2
        double CO2_modal_Alt_tonnes = CO2_modal_Alt_g / 1_000_000.0;

        return new ModalShiftResult(CO2_modal_BAU_tonnes, CO2_modal_Alt_tonnes);
    }

    /**
     * Internal result holder for a single vehicle category
     */
    private static class VehicleCategoryResult {
        final double bauCO2;
        final double altCO2;

        VehicleCategoryResult(double bauCO2, double altCO2) {
            this.bauCO2 = bauCO2;
            this.altCO2 = altCO2;
        }
    }

    /**
     * Internal result holder for modal shift
     */
    private static class ModalShiftResult {
        final double bauModalCO2;
        final double altModalCO2;

        ModalShiftResult(double bauModalCO2, double altModalCO2) {
            this.bauModalCO2 = bauModalCO2;
            this.altModalCO2 = altModalCO2;
        }
    }
}
