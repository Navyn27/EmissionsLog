package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.SolidWasteType;
import com.navyn.emissionlog.Enums.WasteType;
import com.navyn.emissionlog.modules.wasteEmissions.models.SolidWasteData;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WasteDataRepository extends JpaRepository<WasteDataAbstract, UUID> {
    List<WasteDataAbstract> findAllByWasteType(WasteType type);
    //findLatestByWasteType
    WasteDataAbstract findFirstByWasteTypeOrderByCreatedAtDesc(WasteType type);

    List<SolidWasteData> findAllBySolidWasteType(SolidWasteType solidWasteType);

    List<WasteDataAbstract> findByActivityYearBetween(LocalDateTime startDate, LocalDateTime endDate);
}
