package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.modules.eicvReports.EICVReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EICVReportRepository extends JpaRepository<EICVReport, UUID> {
    Optional<EICVReport> findByYear(int year);
    Optional<EICVReport> findById(UUID id);

    Optional<EICVReport> findByName(String name);
}
