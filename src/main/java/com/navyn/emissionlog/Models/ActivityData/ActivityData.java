package com.navyn.emissionlog.Models.ActivityData;

import com.navyn.emissionlog.Enums.ActivityTypes;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "activityType")
@Data
public abstract class ActivityData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "activity_type_value")
    @Enumerated(EnumType.STRING)
    private ActivityTypes activityType;

    @OneToOne
    private FuelData fuelData;
}