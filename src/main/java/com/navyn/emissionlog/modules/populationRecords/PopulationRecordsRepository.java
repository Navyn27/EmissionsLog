package com.navyn.emissionlog.modules.populationRecords;

import com.navyn.emissionlog.Enums.Countries;
import com.navyn.emissionlog.modules.populationRecords.PopulationRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PopulationRecordsRepository extends JpaRepository<PopulationRecords, UUID> {
    PopulationRecords findByYear(int year);

    boolean existsByYear(int year);

    List<PopulationRecords> findByCountryOrderByYearDesc(Countries country);

    List<PopulationRecords> findByCountryAndYearOrderByYearDesc(Countries country, Integer year);

    List<PopulationRecords> findAllByOrderByYearDesc();
}
