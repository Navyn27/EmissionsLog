package com.navyn.emissionlog.modules.wasteEmissions.models;

import com.navyn.emissionlog.Enums.GWP;
import com.navyn.emissionlog.Enums.Scopes;
import com.navyn.emissionlog.Enums.Waste.WasteType;
import com.navyn.emissionlog.modules.regions.Region;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "waste_type")
@Data
@Table(name = "waste_data_abstract")
public abstract class WasteDataAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "waste_type_value")
    private WasteType wasteType;

    @Enumerated(EnumType.STRING)
    private Scopes scope;

    @ManyToOne
    private Region region;

    private Double CH4Emissions = 0.0;

    private Double FossilCO2Emissions = 0.0;

    private Double BioCO2Emissions = 0.0;

    private Double N2OEmissions = 0.0;

    private Double CO2EqEmissions = 0.0;

    private LocalDateTime activityYear = LocalDateTime.now();

    private Integer year;

    @PrePersist
    private void setCO2EqEmissions() {
        this.CO2EqEmissions = calculateCO2EqEmissions();
        // Always update year from activityYear to ensure consistency
        if (this.activityYear != null) {
            this.year = this.activityYear.getYear();
        }
    }

    @PostLoad
    private void updateYearFromActivityYear() {
        // Ensure year is always in sync with activityYear after loading from DB
        if (this.activityYear != null && this.year == null) {
            this.year = this.activityYear.getYear();
        }
    }

    private Double calculateCO2EqEmissions() {
        return (this.FossilCO2Emissions * 1) + (this.BioCO2Emissions * 1) + (this.N2OEmissions * GWP.N2O.getValue()) + (this.CH4Emissions * GWP.CH4.getValue());
    }

    private LocalDateTime createdAt = LocalDateTime.now();

    public Integer getYear() {
        return activityYear.getYear();
    }
}
