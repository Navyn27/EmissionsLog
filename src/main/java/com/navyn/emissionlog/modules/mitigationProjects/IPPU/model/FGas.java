package com.navyn.emissionlog.modules.mitigationProjects.IPPU.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "f_gases", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class FGas {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "default_gwp")
    private Double defaultGwp;
}
