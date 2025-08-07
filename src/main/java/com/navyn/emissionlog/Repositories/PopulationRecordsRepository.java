package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.Countries;
import com.navyn.emissionlog.Models.PopulationRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PopulationRecordsRepository extends JpaRepository<PopulationRecords, UUID> {
    PopulationRecords findByYear(int year);

    List<PopulationRecords> findByCountry(Countries country);
}
