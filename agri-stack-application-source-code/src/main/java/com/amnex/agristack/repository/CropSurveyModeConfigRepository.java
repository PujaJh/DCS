package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.amnex.agristack.entity.CropSurveyModeConfig;

@Repository
public interface CropSurveyModeConfigRepository extends JpaRepository<CropSurveyModeConfig, Long> {
    List<CropSurveyModeConfig> findAllByVillageLgdCodeIn(List<Long> codes);

    List<CropSurveyModeConfig> findAllBySubDistrictLgdCodeIn(List<Long> codes);

    List<CropSurveyModeConfig> findAllByDistrictLgdCodeIn(List<Long> codes);

    List<CropSurveyModeConfig> findAllByStateLgdCodeIn(List<Long> codes);

 
    String getAllConfigs = "select " +
            "   smc.id as \"id\",  " +
            "   smc.village_lgd_code as \"villageLgdCode\",  " +
            "   smc.sub_district_lgd_code as \"subDistrictLgdCode\", " +
            "   smc.district_lgd_code as \"districtLgdCode\", " +
            "   smc.state_lgd_code as \"stateLgdCode\", " +
            "   smc.mode as \"mode\", " +
            "   sm.state_name as \"stateName\", " +
            "   dm.district_name as \"districtName\", " +
            "   sdm.sub_district_name as \"subDistrictName\", " +
            "   vm.village_name as \"villageName\", " +
            "   mm.name as \"modeName\"" +
            "   from agri_stack.crop_survey_mode_config smc " +
            "   inner join agri_stack.user_village_mapping uvm" +
            "       on smc.village_lgd_code = uvm.village_lgd_code" +
            "       and uvm.user_id = :userId" +
            "   inner join agri_stack.state_lgd_master sm on  " +
            "    	smc.state_lgd_code = sm.state_lgd_code " +
            "   inner join agri_stack.district_lgd_master dm on  " +
            "   	smc.district_lgd_code = dm.district_lgd_code " +
            "   inner join agri_stack.sub_district_lgd_master sdm on  " +
            "   	smc.sub_district_lgd_code = sdm.sub_district_lgd_code " +
            "   inner join agri_stack.village_lgd_master vm on  " +
            "   	smc.village_lgd_code = vm.village_lgd_code " +
            "   inner join agri_stack.crop_survey_mode_master mm on " +
            "   	smc.mode = mm.id ";

    @Query(value = getAllConfigs, nativeQuery = true)
    List<Object[]> getConfigs(@Param("userId") Long userId);
}
