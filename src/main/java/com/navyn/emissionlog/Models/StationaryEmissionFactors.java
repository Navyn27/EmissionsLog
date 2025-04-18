package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.Emissions;
import jakarta.persistence.*;
import lombok.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Entity
@Table(name = "stationary_emission_factors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationaryEmissionFactors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Emissions emmission;

    @ManyToOne
    @JoinColumn(name = "fuel_id", nullable = false)
    @ToString.Exclude
    private Fuel fuel; // Relation to Fuel entity

    @Column(nullable = false)
    private double energyBasis; // kg CO₂/TJ

    @Column(nullable = false)
    private double massBasis; // kg CO₂/tonne

    @Column(nullable = true)
    private Double liquidBasis; // kg CO₂/litre (for liquid fuels)

    @Column(nullable = true)
    private Double gasBasis; // kg CO₂/m³ (for gaseous fuels)

    @Column(nullable = false, unique = true)
    private String checkSum; // Unique checksum for the emission factor to ensure data integrity and avoid over calculation of emissions

    @PrePersist
    @PreUpdate
    private void generateCheckSum() throws NoSuchAlgorithmException {
        this.checkSum = generateHashValue(this.emmission.toString() + this.fuel + this.energyBasis + this.massBasis + this.liquidBasis + this.gasBasis);
    }

    private String generateHashValue(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashBytes);
    }
}
