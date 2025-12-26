package com.navyn.emissionlog.modules.mitigationProjects.BAU.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navyn.emissionlog.modules.mitigationProjects.BAU.enums.ESector;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "baus", uniqueConstraints = @UniqueConstraint(columnNames = { "year", "sector" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BAU {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotNull(message = "BAU value is required")
    @PositiveOrZero(message = "BAU value must be a positive number or zero")
    @Column(nullable = false)
    private Double value;

    @NotNull(message = "Sector is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ESector sector;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Column(nullable = false)
    private Integer year;
}
