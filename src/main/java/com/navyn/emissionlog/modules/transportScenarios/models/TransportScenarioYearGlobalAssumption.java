package com.navyn.emissionlog.modules.transportScenarios.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "transport_scenario_year_global_assumption", uniqueConstraints = {
                @UniqueConstraint(columnNames = { "scenario_id", "year" })
})
@Getter
@Setter
@NoArgsConstructor
public class TransportScenarioYearGlobalAssumption {

        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        @Column(updatable = false, nullable = false)
        private UUID id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "scenario_id", nullable = false)
        @JsonIgnore
        private TransportScenario scenario;

        @Column(nullable = false)
        private Integer year;

        @Column(nullable = false)
        private Double fuelEmissionFactorTco2PerTJ_Gasoline;

        @Column(nullable = false)
        private Double fuelEmissionFactorTco2PerTJ_Diesel;

        @Column(nullable = false)
        private Double fuelEnergyDensityTjPerL_Gasoline;

        @Column(nullable = false)
        private Double fuelEnergyDensityTjPerL_Diesel;

        @Column(nullable = false)
        private Double gridEmissionFactorTco2PerMWh;

        /**
         * Validation method
         */
        public boolean isValid() {
                return fuelEmissionFactorTco2PerTJ_Gasoline != null && fuelEmissionFactorTco2PerTJ_Gasoline > 0
                                && fuelEmissionFactorTco2PerTJ_Diesel != null && fuelEmissionFactorTco2PerTJ_Diesel > 0
                                && fuelEnergyDensityTjPerL_Gasoline != null && fuelEnergyDensityTjPerL_Gasoline > 0
                                && fuelEnergyDensityTjPerL_Diesel != null && fuelEnergyDensityTjPerL_Diesel > 0
                                && gridEmissionFactorTco2PerMWh != null && gridEmissionFactorTco2PerMWh >= 0;
        }
}
