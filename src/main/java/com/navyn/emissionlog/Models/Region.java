package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.Countries;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name="regions")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Region {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull(message = "The country value can't be null")
    @Enumerated(EnumType.STRING)
    private Countries countries;

    @NotNull(message = "The province/state Value can't be null")
    private String province;

    private String city;
}
