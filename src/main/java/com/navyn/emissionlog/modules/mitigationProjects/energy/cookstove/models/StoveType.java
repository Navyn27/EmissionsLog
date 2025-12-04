package com.navyn.emissionlog.modules.mitigationProjects.Energy.cookstove.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "stove_type")
@Getter
@Setter
public class StoveType {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;


    @Column(nullable = false)
    private String name;

    @Column(name = "baseline_percentage", nullable = false)
    private double baselinePercentage;
}
