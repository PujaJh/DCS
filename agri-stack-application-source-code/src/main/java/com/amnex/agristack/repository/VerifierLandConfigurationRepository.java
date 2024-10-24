/**
 * 
 */
package com.amnex.agristack.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amnex.agristack.dao.YearDistinctDAO;
import com.amnex.agristack.entity.VerifierLandConfiguration;

/**
 * @author majid.belim
 *
 */
public interface VerifierLandConfigurationRepository extends JpaRepository<VerifierLandConfiguration, String> {

	List<VerifierLandConfiguration> findByConfigId_SurveyVerificationConfigurationMasterIdAndIsActiveAndIsDeleted(
			Integer configId, Boolean isActive, Boolean isDeleted);
	
	Page<VerifierLandConfiguration> findByConfigId_SurveyVerificationConfigurationMasterIdAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_SubDistrictLgdCode_SubDistrictLgdCodeInOrderByVillageLgdCode_VillageName(
			Integer configId, Boolean isActive, Boolean isDeleted,Long seasonId,Integer startingYear, Integer endingYear,List<Long> subDistrictLgdCodeList, Pageable pageable);

	Page<VerifierLandConfiguration> findByConfigId_SurveyVerificationConfigurationMasterIdAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_SubDistrictLgdCode_SubDistrictLgdCodeIn(
			Integer configId, Boolean isActive, Boolean isDeleted,Long seasonId,Integer startingYear, Integer endingYear,List<Long> subDistrictLgdCodeList, Pageable pageable);

//	OrderByVillageLgdCode_VillageName
	Page<VerifierLandConfiguration> findByConfigId_StateLgdMaster_StateLgdCodeInAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_SubDistrictLgdCode_SubDistrictLgdCodeIn(
			List<Long> stateCodes , Boolean configIsActive, Boolean configIsDeleted, Boolean isActive, Boolean isDeleted,Long seasonId,Integer startingYear, Integer endingYear,List<Long> subDistrictLgdCodeList, Pageable pageable);
//	OrderByVillageLgdCode_VillageName
	Page<VerifierLandConfiguration> findByConfigId_StateLgdMaster_StateLgdCodeInAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_SubDistrictLgdCode_SubDistrictLgdCodeInAndVillageLgdCode_VillageNameContaining(
			List<Long> stateCodes , Boolean configIsActive, Boolean configIsDeleted, Boolean isActive, Boolean isDeleted,Long seasonId,Integer startingYear, Integer endingYear,List<Long> subDistrictLgdCodeList,String villageName, Pageable pageable);
	
	
	List<VerifierLandConfiguration> findByConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndConfigId_StateLgdMaster_StateLgdCodeInAndEndingYearAndSeason_SeasonIdAndVillageLgdCode_VillageLgdCodeIn(
			 Boolean configIsActive, Boolean configIsDeleted,Boolean isActive, Boolean isDeleted,List<Long> codes,Integer year,Long seasonId,List<Long> code);
	
	
	
	List<VerifierLandConfiguration> findByConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndConfigId_StateLgdMaster_StateLgdCodeInAndVillageLgdCode_VillageLgdCodeIn(
			 Boolean configIsActive, Boolean configIsDeleted,Boolean isActive, Boolean isDeleted,List<Long> codes,List<Long> code);
	
	 @Query(value = "SELECT * FROM agri_Stack.verifier_land_configuration vlc " +
             "JOIN agri_Stack.survey_verification_configuration_master svm ON vlc.config_Id = svm.survey_verification_configuration_master_id " +
             "JOIN agri_Stack.village_lgd_master vlm ON vlc.village_lgd_code = vlm.village_lgd_code " +
             "WHERE vlc.is_active = :configIsActive " +
             "AND vlc.is_deleted = :configIsDeleted " +
             "AND vlc.is_active = :isActive " +
             "AND vlc.is_deleted = :isDeleted " +
             "AND vlc.state_lgd_code IN :codes " +
             "AND vlc.village_lgd_code IN :code",
     nativeQuery = true)
List<VerifierLandConfiguration> findByConfigIdIsActiveAndConfigIdIsDeletedAndIsActiveAndIsDeletedAndStateLgdMasterStateLgdCodeInAndVillageLgdCodeVillageLgdCodeIn(
      @Param("configIsActive") Boolean configIsActive,
      @Param("configIsDeleted") Boolean configIsDeleted,
      @Param("isActive") Boolean isActive,
      @Param("isDeleted") Boolean isDeleted,
      @Param("codes") List<Long> codes,
      @Param("code") List<Long> code);
	
	
	List<VerifierLandConfiguration> findByConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndConfigId_StateLgdMaster_StateLgdCodeAndVillageLgdCode_VillageLgdCodeIn(
			 Boolean configIsActive, Boolean configIsDeleted,Boolean isActive, Boolean isDeleted,Long codes,List<Long> code);
	
	
	
	VerifierLandConfiguration findByConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndConfigId_StateLgdMaster_StateLgdCodeAndVillageLgdCode_VillageLgdCodeAndLandPlot_LandParcelIdNotInAndEndingYearAndSeason_SeasonId(
			 Boolean configIsActive, Boolean configIsDeleted,Boolean isActive, Boolean isDeleted,Long scodes,Long vcode,Set<String> ids,Integer year,Long seasonId);
	
	
	VerifierLandConfiguration findByConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndConfigId_StateLgdMaster_StateLgdCodeAndVillageLgdCode_VillageLgdCodeAndEndingYearAndSeason_SeasonId(
			 Boolean configIsActive, Boolean configIsDeleted,Boolean isActive, Boolean isDeleted,Long scodes,Long vcode,Integer year,Long seasonId);
	
	@Query(value = "select distinct starting_Year startYear, ending_year endYear,season_id seasonId from agri_stack.Verifier_Land_Configuration where verifier_Land_Configuration_Id in (:ids) ",nativeQuery = true)
	List<YearDistinctDAO> getYearAndSeasonDistinct(List<Long> ids);
	
	
	
	@Query(value = "select village_lgd_code from agri_stack.Verifier_Land_Configuration " + 
			"			where  is_active=true and is_deleted=false and config_id= :configId and ending_year=:endingYear and season_id=:seasonId ",nativeQuery = true)
	List<Long> getVillagesByFilter(Integer configId,Integer endingYear,Long seasonId);
	
	
	List<VerifierLandConfiguration>  findByConfigId_StateLgdMaster_StateLgdCodeAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_VillageLgdCode(
			Long stateCode , Boolean configIsActive, Boolean configIsDeleted, Boolean isActive, Boolean isDeleted,Long seasonId,Integer startingYear, Integer endingYear,Long vcode);
	
	List<VerifierLandConfiguration>  findByConfigId_StateLgdMaster_StateLgdCodeAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_VillageLgdCodeAndLandPlot_FarmlandIdNotIn(
			Long stateCode , Boolean configIsActive, Boolean configIsDeleted, Boolean isActive, Boolean isDeleted,Long seasonId,Integer startingYear, Integer endingYear,Long vcode,List<String> farmLandIds);

	List<VerifierLandConfiguration>  findByConfigId_StateLgdMaster_StateLgdCodeAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_VillageLgdCodeAndLandPlotsNotIn(
			Long stateCode , Boolean configIsActive, Boolean configIsDeleted, Boolean isActive, Boolean isDeleted,Long seasonId,Integer startingYear, Integer endingYear,Long vcode,List<Long> farmLandIds);

	@Query(value = "select vlm.land_plots from agri_stack.verifier_land_configuration vlc" +
			" inner join agri_stack.survey_verification_configuration_master vcm on vcm.survey_verification_configuration_master_id = vlc.config_id" +
			" inner join agri_stack.verifier_land_plots_mapping vlm on vlm.verifier_land_configuration_verifier_land_configuration_id = vlc.verifier_land_configuration_id" +
			" inner join agri_stack.farmland_plot_registry fpr on fpr.fpr_farmland_plot_registry_id = vlm.land_plots" +
			" where vlc.state_lgd_code = :stateCode and vcm.is_active = true and vcm.is_deleted = false and" +
			" vlc.is_active = true and vlc.is_deleted = false and vlc.season_id = :seasonId and vlc.village_lgd_code = :vcode" +
			" and vlc.starting_year = :startingYear and vlc.ending_year = :endingYear", nativeQuery = true)
	List<Long> findByConfigId_StateLgdMaster_StateLgdCodeAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_VillageLgdCodeV2(
			Long stateCode, Long seasonId,Integer startingYear, Integer endingYear,Long vcode);

	@Query(value = "select vlm.land_plots from agri_stack.verifier_land_configuration vlc" +
			" inner join agri_stack.survey_verification_configuration_master vcm on vcm.survey_verification_configuration_master_id = vlc.config_id" +
			" inner join agri_stack.verifier_land_plots_mapping vlm on vlm.verifier_land_configuration_verifier_land_configuration_id = vlc.verifier_land_configuration_id" +
			" inner join agri_stack.farmland_plot_registry fpr on fpr.fpr_farmland_plot_registry_id = vlm.land_plots" +
			" where vlc.state_lgd_code = :stateCode and vcm.is_active = true and vcm.is_deleted = false and" +
			" vlc.is_active = true and vlc.is_deleted = false and vlc.season_id = :seasonId and vlc.village_lgd_code = :vcode" +
			" and vlc.starting_year = :startingYear and vlc.ending_year = :endingYear" +
			" and fpr.fpr_farmland_plot_registry_id not in (:farmLandIds)", nativeQuery = true)
	List<Long> findByConfigId_StateLgdMaster_StateLgdCodeAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_VillageLgdCodeAndFarmlandNotInV2(
			Long stateCode, Long seasonId,Integer startingYear, Integer endingYear,Long vcode, List<Long> farmLandIds);

	@Query(value = "select agri_stack.fn_survey_verifier_configuration_add(:surveyVerificationConfigurationMasterId,:userId,:villageAssignmentPercentage," +
			":plotAssignmentPercentage,:plotAssignmentCount,:createdModifiedIP,:startYear,:endYear,:seasonId)", nativeQuery = true)
	Object addSurveyVerifierConfiguration(Integer surveyVerificationConfigurationMasterId, Long userId,Double villageAssignmentPercentage,
										  Long plotAssignmentPercentage,Integer plotAssignmentCount,String createdModifiedIP,
										  Integer startYear, Integer endYear, Long seasonId);


	@Query(value = "select agri_stack.fn_get_user_count_filter_verifier_inspection(:userId)", nativeQuery = true)
	String getUserCountVerifierInspection(Long userId);



}
