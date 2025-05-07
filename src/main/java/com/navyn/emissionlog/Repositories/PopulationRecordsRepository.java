package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Models.PopulationRecords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PopulationRecordsRepository extends JpaRepository<PopulationRecords, UUID> {
    PopulationRecords findByYear(int year);
}
