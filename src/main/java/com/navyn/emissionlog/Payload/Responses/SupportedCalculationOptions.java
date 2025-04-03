package com.navyn.emissionlog.Payload.Responses;

import com.navyn.emissionlog.Enums.FuelStates;
import com.navyn.emissionlog.Enums.Metrics;
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
