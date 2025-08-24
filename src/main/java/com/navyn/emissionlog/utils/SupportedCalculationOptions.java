package com.navyn.emissionlog.utils;

import com.navyn.emissionlog.Enums.Fuel.FuelStates;
import com.navyn.emissionlog.Enums.Metrics.Metrics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SupportedCalculationOptions {
    List<Metrics> supportedCalculationMetrics = new ArrayList<>();
    List<FuelStates> supportedCalculationFuelStates = new ArrayList<>();
}
