package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.EICVReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EICVReportRepository extends JpaRepository<EICVReport, UUID> {
    EICVReport findByYear(int year);
    Optional<EICVReport> findById(UUID id);
}
