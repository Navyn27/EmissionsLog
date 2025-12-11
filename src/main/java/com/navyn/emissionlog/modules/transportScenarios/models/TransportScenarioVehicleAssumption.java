package com.navyn.emissionlog.modules.transportScenarios.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navyn.emissionlog.modules.transportScenarios.enums.TransportScenarioFuelType;
import com.navyn.emissionlog.modules.transportScenarios.enums.TransportScenarioVehicleCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "transport_scenario_vehicle_assumption", uniqueConstraints = {
                @UniqueConstraint(columnNames = { "scenario_id", "year", "vehicle_category" })
})
@Getter
@Setter
@NoArgsConstructor
public class TransportScenarioVehicleAssumption {

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

        @Enumerated(EnumType.STRING)
        @Column(name = "vehicle_category", nullable = false)
        private TransportScenarioVehicleCategory vehicleCategory;

        @Enumerated(EnumType.STRING)
        @Column(name = "fuel_type", nullable = false)
        private TransportScenarioFuelType fuelType;

        @Column(nullable = false)
        private Double fleetSize;

        @Column(nullable = false)
        private Double averageKmPerVehicle;

        @Column(nullable = false)
        private Double fuelEconomyLPer100Km;

        @Column(nullable = false)
        private Double evShare;

        @Column(nullable = false)
        private Double evKWhPer100Km;

        /**
         * Validation method
         */
        public boolean isValid() {
                return fleetSize != null && fleetSize >= 0
                                && averageKmPerVehicle != null && averageKmPerVehicle >= 0
                                && fuelEconomyLPer100Km != null && fuelEconomyLPer100Km > 0
                                && evShare != null && evShare >= 0 && evShare <= 1
                                && evKWhPer100Km != null && evKWhPer100Km >= 0;
        }
}
