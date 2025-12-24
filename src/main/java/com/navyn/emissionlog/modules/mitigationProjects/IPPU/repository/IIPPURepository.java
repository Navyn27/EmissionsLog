package com.navyn.emissionlog.modules.mitigationProjects.IPPU.repository;

import com.navyn.emissionlog.modules.mitigationProjects.IPPU.model.IPPUMitigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IIPPURepository extends JpaRepository<IPPUMitigation, UUID> {
    List<IPPUMitigation> findByYear(int year);

    @Query("""
                SELECT i
                FROM IPPUMitigation i
                WHERE i.year = :year
                  AND i.fGasName = :fGasName
            """)
    Optional<IPPUMitigation> findByYearAndFGasName(
            @Param("year") int year,
            @Param("fGasName") String fGasName
    );
}
