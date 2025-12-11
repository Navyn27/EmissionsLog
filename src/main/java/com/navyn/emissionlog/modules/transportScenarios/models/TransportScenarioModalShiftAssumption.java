package com.navyn.emissionlog.modules.transportScenarios.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "transport_scenario_modal_shift_assumption", uniqueConstraints = {
                @UniqueConstraint(columnNames = { "scenario_id", "year" })
})
@Getter
@Setter
@NoArgsConstructor
public class TransportScenarioModalShiftAssumption {

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
        private Double passengerKmMotorcycleBau;

        @Column(nullable = false)
        private Double passengerKmCarBau;

        @Column(nullable = false)
        private Double passengerKmBusBau;

        @Column(nullable = false)
        private Double emissionFactorMotorcycle_gPerPassKm;

        @Column(nullable = false)
        private Double emissionFactorCar_gPerPassKm;

        @Column(nullable = false)
        private Double emissionFactorBus_gPerPassKm;

        @Column(nullable = false)
        private Double shiftFractionMotorcycleToBus;

        @Column(nullable = false)
        private Double shiftFractionCarToBus;

        /**
         * Validation method
         */
        public boolean isValid() {
                return passengerKmMotorcycleBau != null && passengerKmMotorcycleBau >= 0
                                && passengerKmCarBau != null && passengerKmCarBau >= 0
                                && passengerKmBusBau != null && passengerKmBusBau >= 0
                                && emissionFactorMotorcycle_gPerPassKm != null
                                && emissionFactorMotorcycle_gPerPassKm >= 0
                                && emissionFactorCar_gPerPassKm != null && emissionFactorCar_gPerPassKm >= 0
                                && emissionFactorBus_gPerPassKm != null && emissionFactorBus_gPerPassKm >= 0
                                && shiftFractionMotorcycleToBus != null && shiftFractionMotorcycleToBus >= 0
                                && shiftFractionMotorcycleToBus <= 1
                                && shiftFractionCarToBus != null && shiftFractionCarToBus >= 0
                                && shiftFractionCarToBus <= 1;
        }
}
