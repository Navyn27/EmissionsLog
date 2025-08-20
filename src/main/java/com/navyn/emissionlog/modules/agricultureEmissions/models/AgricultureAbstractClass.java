package com.navyn.emissionlog.modules.agricultureEmissions.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@MappedSuperclass
@Data
public abstract class AgricultureAbstractClass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private int year;
}
