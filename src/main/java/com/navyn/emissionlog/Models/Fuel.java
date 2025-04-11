package com.navyn.emissionlog.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navyn.emissionlog.Enums.FuelSourceType;
import com.navyn.emissionlog.Enums.FuelTypes;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fuel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FuelTypes fuelTypes;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_source_type")
    private List<FuelSourceType> fuelSourceTypes = new ArrayList<>();

    private String fuelDescription; // Description of the fuel

    @Column(nullable = false, unique = true)
    private String fuel; // Fuel type (e.g., Diesel, Natural Gas)

    @Column(nullable = true)
    private Double lowerHeatingValue; // LHV (TJ/Gg) or NCV

    @Column(nullable = true)
    private Double liquidDensity = 0.0; // kg/litre (only for liquid fuels)

    @Column(nullable = true)
    private Double gasDensity = 0.0; // kg/m³ (only for gaseous fuels)

    @OneToMany(mappedBy = "fuel", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    @ToString.Exclude
    private List<StationaryEmissionFactors> stationaryEmissionFactorsList = new ArrayList<>();

    @OneToMany(mappedBy = "fuel", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<TransportFuelEmissionFactors> transportFuelEmissionFactorsList = new ArrayList<>();

    @OneToMany(mappedBy = "fuel", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<TransportFuelVehicleDataEmissionFactors> transportFuelVehicleDataEmissionFactorsList = new ArrayList<>();

    @Override
    public String toString() {
        return "Fuel{" +
                "id=" + id.toString() +
                ", name='" + fuel + '\'';
    }
}
