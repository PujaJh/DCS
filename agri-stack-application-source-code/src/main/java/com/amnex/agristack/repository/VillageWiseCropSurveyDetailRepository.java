package com.amnex.agristack.repository;

import com.amnex.agristack.dao.VillageWiseCropSurveyDetailDAO;
import com.amnex.agristack.entity.VillageWiseCropSurveyDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface VillageWiseCropSurveyDetailRepository extends JpaRepository<VillageWiseCropSurveyDetail, Long>{

	List<VillageWiseCropSurveyDetail> findByIsAcknowledgedAndIsShared(Integer value, Object o);

    @Query (value = "select count(*) from VillageWiseCropSurveyDetail where isAcknowledged = :value and isShared is null",nativeQuery = false)
    Integer findCountOfIsAcknowledgedAndIsShared(Integer value);

    public String getNativeData="select vc.id, vc.action, vc.created_on, vc.crop_code, vc.crop_id, vc.crop_name, vc.irrigation_source, vc.irrigation_type, vc.is_acknowledged, vc.is_active, vc.is_deleted, vc.is_shared, vc.modified_on, vc.no_of_farmers, vc.no_of_plots, vc.previous_reference_id, vc.reference_id, vc.season_id, vc.season_name, vc.sown_area, vc.sown_area_unit, vc.state_lgd_code, vc.state_name, vc.status, vc.village_lgd_code, vc.village_name, vc.year,'123456' as sown_area_str from agri_stack.village_wise_crop_survey_detail vc where vc.id between :startingNumber and :endingNumber order by vc.id asc";
    @Query(value=getNativeData,nativeQuery=true)
    List<VillageWiseCropSurveyDetail> getNativeData(@Param("startingNumber") Long startingNumber, @Param("endingNumber") Long endingNumber);
}
