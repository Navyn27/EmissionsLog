package com.navyn.emissionlog.Repositories;

import com.navyn.emissionlog.Enums.WasteType;
import com.navyn.emissionlog.Models.WasteData.WasteDataAbstract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WasteDataRepository extends JpaRepository<WasteDataAbstract, UUID> {
    List<WasteDataAbstract> findAllByWasteType(WasteType type);
    //findLatestByWasteType
    WasteDataAbstract findFirstByWasteTypeOrderByCreatedAtDesc(WasteType type);
}
