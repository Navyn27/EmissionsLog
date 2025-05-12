package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.SolidWasteType;
import com.navyn.emissionlog.Enums.WasteType;
import com.navyn.emissionlog.Models.WasteData.SolidWasteData;
import com.navyn.emissionlog.Models.WasteData.WasteDataAbstract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WasteDataRepository extends JpaRepository<WasteDataAbstract, UUID> {
    List<WasteDataAbstract> findAllByWasteType(WasteType type);
    //findLatestByWasteType
    WasteDataAbstract findFirstByWasteTypeOrderByCreatedAtDesc(WasteType type);

    List<SolidWasteData> findAllBySolidWasteType(SolidWasteType solidWasteType);
}
