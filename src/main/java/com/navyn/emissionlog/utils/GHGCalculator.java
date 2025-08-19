package com.navyn.emissionlog.utils;

import java.util.Map;

public class GHGCalculator {

    /**
     * General Emission Calculation
     * @param activityData The quantitative measure of activity (e.g., fuel consumed in liters).
     * @param emissionFactor The emission factor (e.g., kg CO2 per liter of fuel).
     * @return Total emissions in kg CO2e.
     */
    public static Double calculateEmissions(Double activityData, Double emissionFactor) {
        return activityData * emissionFactor;
    }

    /**
     * Scope 1: Direct Combustion Emissions
     * @param fuelConsumption The amount of fuel consumed (e.g., liters, kg).
     * @param fuelEmissionFactor The specific emission factor for the fuel type (kg CO2 per unit of fuel).
     * @return Total direct emissions (kg CO2e).
     */
    public static Double calculateScope1Emissions(Double fuelConsumption, Double fuelEmissionFactor) {
        return fuelConsumption * fuelEmissionFactor;
    }

    /**
     * Scope 2: Indirect Emissions from Purchased Electricity
     * @param electricityConsumed Electricity consumed in kWh.
     * @param gridEmissionFactor Emission factor for the electricity grid (kg CO2e per kWh).
     * @return Total indirect emissions from electricity consumption (kg CO2e).
     */
    public static Double calculateScope2Emissions(Double electricityConsumed, Double gridEmissionFactor) {
        return electricityConsumed * gridEmissionFactor;
    }

    /**
     * Scope 3: Emissions from Purchased Goods and Services
     * @param purchasedGoods A map where keys are the quantity of goods and values are their emission factors.
     * @return Total emissions from purchased goods and services (kg CO2e).
     */
    public static Double calculateScope3PurchasedGoods(Map<Double, Double> purchasedGoods) {
        return purchasedGoods.entrySet().stream()
                .mapToDouble(entry -> entry.getKey() * entry.getValue())
                .sum();
    }

    /**
     * Scope 3: Emissions from Business Travel
     * @param travelDistances A map where keys are distances traveled (km) and values are mode-specific emission factors (kg CO2 per km).
     * @return Total emissions from business travel (kg CO2e).
     */
    public static Double calculateScope3BusinessTravel(Map<Double, Double> travelDistances) {
        return travelDistances.entrySet().stream()
                .mapToDouble(entry -> entry.getKey() * entry.getValue())
                .sum();
    }

    /**
     * Emission Intensity per Unit of Product Produced
     * @param totalEmissions Total greenhouse gas emissions (kg CO2e).
     * @param totalUnits Total units of product produced.
     * @return Emission intensity (kg CO2e per unit).
     */
    public static Double calculateEmissionIntensity(Double totalEmissions, Double totalUnits) {
        return totalUnits != 0 ? totalEmissions / totalUnits : 0;
    }
}
