package com.navyn.emissionlog.modules.wasteEmissions;

import com.navyn.emissionlog.Enums.Waste.SolidWasteType;
import com.navyn.emissionlog.Enums.Waste.WasteType;
import com.navyn.emissionlog.modules.wasteEmissions.models.SolidWasteData;
import com.navyn.emissionlog.modules.wasteEmissions.models.WasteDataAbstract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WasteDataRepository extends JpaRepository<WasteDataAbstract, UUID>, JpaSpecificationExecutor<WasteDataAbstract> {
    List<WasteDataAbstract> findAllByWasteTypeOrderByYearDesc(WasteType type);
    //findLatestByWasteType
    WasteDataAbstract findFirstByWasteTypeOrderByCreatedAtDesc(WasteType type);

    List<SolidWasteData> findAllBySolidWasteTypeOrderByYearDesc(SolidWasteType solidWasteType);

    List<WasteDataAbstract> findByActivityYearBetweenOrderByYearDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    List<WasteDataAbstract> findAllByOrderByYearDesc();
}
