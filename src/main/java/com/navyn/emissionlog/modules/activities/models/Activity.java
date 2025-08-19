package com.navyn.emissionlog.modules.activities.models;

import com.navyn.emissionlog.Enums.*;
import com.navyn.emissionlog.modules.regions.Region;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Sectors sector;

    @Enumerated(EnumType.STRING)
    private Scopes scope;

    @ManyToOne
    private Region region;

    @OneToOne
    private ActivityData activityData;

    private Double CH4Emissions = 0.0;

    private Double FossilCO2Emissions = 0.0;

    private Double BioCO2Emissions = 0.0;

    private Double N2OEmissions = 0.0;

    @Transient
    private Double CO2EqEmissions = 0.0;

    private LocalDateTime activityYear = LocalDateTime.now();

    public Integer getYear(){
        return activityYear.getYear();
    }


    @PostLoad
    private void setCO2EqEmissions() {
        this.CO2EqEmissions = calculateCO2EqEmissions();
    }

    private Double calculateCO2EqEmissions() {
        return (this.FossilCO2Emissions * 1) + (this.BioCO2Emissions * 1) + (this.N2OEmissions * GWP.N2O.getValue()) + (this.CH4Emissions * GWP.CH4.getValue());
    }
}