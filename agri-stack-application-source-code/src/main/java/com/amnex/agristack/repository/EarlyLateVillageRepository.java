package com.amnex.agristack.repository;

import com.amnex.agristack.entity.EarlyLateVillage;
import com.amnex.agristack.entity.SurveyActivityConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EarlyLateVillageRepository extends JpaRepository<EarlyLateVillage, Long> {
//    List<SurveyActivityConfiguration> getVillageMapping(Long seasonId,Long year);

    @Query("select d from EarlyLateVillage d where d.isActive = true and d.isDeleted = false and " +
            "d.seasonId = :seasonId and " +
            "d.startYear = :startYear and " +
            "d.endYear = :endYear and " +
            "d.subDistrictLGDCode = :talukLgdCode")
    Optional<EarlyLateVillage> checkEntryExist(Integer startYear, Integer endYear, Long seasonId, Integer talukLgdCode);

    @Query("select d from EarlyLateVillage d where " +
            "d.isActive = true and " +
            "d.isDeleted = false and " +
            "d.seasonId = :seasonId and " +
            "d.startYear = :startYear and " +
            "d.endYear = :endYear and " +
            "((:talukLgdCode) is null  or d.subDistrictLGDCode in (:talukLgdCode)) order by d.id ASC")
    List<EarlyLateVillage> getEarlyLateVillageList(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear,
                                                   @Param("seasonId") Long seasonId, @Param("talukLgdCode") List<Integer> talukLgdCode);
    
    
    @Query("select d from EarlyLateVillage d where " +
            "d.isActive = true and " +
            "d.isDeleted = false and " +
            "d.seasonId = :seasonId and " +
            "d.startYear = :startYear and " +
            "d.endYear = :endYear and " +
            "((:talukLgdCode) is null  or d.villageLGDCodes in (:villageLGDCodes)) order by d.id ASC")
    List<EarlyLateVillage> getEarlyLateByVillageCodes(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear,
                                                   @Param("seasonId") Long seasonId, @Param("villageLGDCodes") List<Integer> villageLGDCodes);
}
