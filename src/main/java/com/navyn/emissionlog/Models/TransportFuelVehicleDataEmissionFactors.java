package com.navyn.emissionlog.Models;

import com.navyn.emissionlog.Enums.RegionGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransportFuelVehicleDataEmissionFactors {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Vehicles vehicle;

    @ManyToOne
    private Fuel fuel;

    private String vehicleYear;
    private String size;
    private String weightLaden;

    @Enumerated(EnumType.STRING)
    private RegionGroup regionGroup;

    private Double CO2EmissionFactor;
    private Double CH4EmissionFactor;
    private Double N2OEmissionFactor;

    @Column(nullable = false, unique = true)
    private String checkSum; // Unique checksum for the emission factor to ensure data integrity and avoid over calculation of emissions

    @PrePersist
    @PreUpdate
    private void generateCheckSum() throws NoSuchAlgorithmException {
        this.checkSum = generateHashValue(this.regionGroup.toString() + this.vehicleYear + this.size + this.weightLaden + this.CO2EmissionFactor + this.CH4EmissionFactor + this.N2OEmissionFactor);
    }

    private String generateHashValue(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashBytes);
    }

}
