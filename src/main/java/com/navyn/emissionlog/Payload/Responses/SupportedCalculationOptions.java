package com.navyn.emissionlog.Payload.Responses;

import com.navyn.emissionlog.Enums.FuelStates;
import com.navyn.emissionlog.Enums.Metrics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SupportedCalculationOptions {
    List<Metrics> supportedCalculationMetrics;
    List<FuelStates> supportedCalculationFuelStates;
}
