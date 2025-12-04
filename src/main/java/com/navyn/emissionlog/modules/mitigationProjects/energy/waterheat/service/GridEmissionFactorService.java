package com.navyn.emissionlog.modules.mitigationProjects.Energy.waterheat.service;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GridEmissionFactorService {

    private static final Map<Integer, Double> FACTORS = Map.ofEntries(
            Map.entry(2025, 0.138013616882613),
            Map.entry(2026, 0.138013616882613),
            Map.entry(2027, 0.127912185675897),
            Map.entry(2028, 0.127140490741303),
            Map.entry(2029, 0.127140490741303),
            Map.entry(2030, 0.127140490741303),
            Map.entry(2031, 0.114346013979138),
            Map.entry(2032, 0.114346013979138),
            Map.entry(2033, 0.114346013979138),
            Map.entry(2034, 0.114346013979138),
            Map.entry(2035, 0.112656088252988),
            Map.entry(2036, 0.112656088252988),
            Map.entry(2037, 0.112656088252988),
            Map.entry(2038, 0.112656088252988),
            Map.entry(2039, 0.112656088252988),
            Map.entry(2040, 0.113500843805477),
            Map.entry(2041, 0.113500843805477),
            Map.entry(2042, 0.113500843805477),
            Map.entry(2043, 0.113500843805477),
            Map.entry(2044, 0.113500843805477),
            Map.entry(2045, 0.113500843805477),
            Map.entry(2046, 0.114565124623546),
            Map.entry(2047, 0.114565124623546),
            Map.entry(2048, 0.114565124623546),
            Map.entry(2049, 0.114565124623546),
            Map.entry(2050, 0.114565124623546)
    );

    public Double getFactor(int year) {
        return FACTORS.get(year);
    }

    public Map<Integer, Double> getAllFactors() {
        return FACTORS;
    }
}
