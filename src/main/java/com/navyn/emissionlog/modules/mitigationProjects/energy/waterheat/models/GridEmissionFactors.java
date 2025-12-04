package com.navyn.emissionlog.modules.mitigationProjects.Energy.waterheat.models;

import java.util.Map;

public class GridEmissionFactors {

    public static final Map<Integer, Double> FACTORS = Map.ofEntries(
            Map.entry(2025, 0.138),
            Map.entry(2026, 0.138),
            Map.entry(2027, 0.128),
            Map.entry(2028, 0.127),
            Map.entry(2029, 0.127),
            Map.entry(2030, 0.127),
            Map.entry(2031, 0.114),
            Map.entry(2032, 0.114),
            Map.entry(2033, 0.112),
            Map.entry(2034, 0.112),
            Map.entry(2035, 0.113),
            Map.entry(2036, 0.113),
            Map.entry(2037, 0.113),
            Map.entry(2038, 0.113),
            Map.entry(2039, 0.113),
            Map.entry(2040, 0.114),
            Map.entry(2041, 0.114),
            Map.entry(2042, 0.114),
            Map.entry(2043, 0.114),
            Map.entry(2044, 0.114),
            Map.entry(2045, 0.114),
            Map.entry(2046, 0.115),
            Map.entry(2047, 0.115),
            Map.entry(2048, 0.115),
            Map.entry(2049, 0.115),
            Map.entry(2050, 0.115)
    );

    public static Double get(int year) {
        return FACTORS.get(year);
    }
}
