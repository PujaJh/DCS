package com.amnex.agristack.repository;

import com.amnex.agristack.entity.LandParcelSurveyMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExternalAPIRepository extends JpaRepository<LandParcelSurveyMaster, Long>{

	@Query(value = "select agri_stack.fn_get_village_census_crop_detail(:villageLgdCode, :year, :season)", nativeQuery = true)
	String getVillageWisePlotAndCropDetail(Long villageLgdCode,String year, Integer season);
	
	@Query(value = "select agri_stack.fn_get_village_census_crop_detailV2(:villageLgdCode, :year, :season,:startDate,:endDate)", nativeQuery = true)
	String getVillageWisePlotAndCropDetailByDate(Long villageLgdCode,String year, Integer season,String startDate,String endDate);
	
	@Query(value = "select agri_stack.fn_get_village_and_survey_number_wise_sub_survey_number(:villageLgdCode, :surveyNumber)", nativeQuery = true)
	String getSubSurveyNumberDetail(Integer villageLgdCode,String surveyNumber);
	
	@Query(value = "select agri_stack.fn_get_village_suvrey_and_sub_survey_wise_land_owner_list(:villageLgdCode, :surveyNumber, :subSurveyNumber)", nativeQuery = true)
	String landOwnerShipDetail(Integer villageLgdCode,String surveyNumber, String subSurveyNumber);



	@Query(value = "select agri_stack.fn_get_village_wise_farmland_plot_detail(:villageLgdCode)", nativeQuery = true)
	String getVillageWiseFarmlandPlotDetail(Long villageLgdCode);

	@Query(value = "select agri_stack.fn_get_dcs_survey_table_for_gces(:villageLgdCode, :year,:seasonId)", nativeQuery = true)
	String getVillageLevelSurveyDetailForGCES(Long villageLgdCode, String year, Long seasonId);
	
	@Query(value = "select agri_stack.fn_get_village_census_crop_and_media_detail(:villageLgdCode,:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getVillageWiseCropAndMediaDetails(Long villageLgdCode, Integer seasonId, Integer startYear, Integer endYear);

	@Query(value = "select agri_stack.fn_get_survey_number_by_village(:villageLgdCode,:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getSurveyNumberByVillage(Long villageLgdCode, Long seasonId, Integer startYear, Integer endYear);

	@Query(value = "select agri_stack.fn_check_survey_done_by_village(:villageLgdCode,:seasonId,:startYear,:endYear)", nativeQuery = true)
	String checkSurveyDoneByVillage(Long villageLgdCode, Long seasonId, Integer startYear, Integer endYear);

	@Query(value = "select agri_stack.fn_get_approved_crop_detail_by_village(:villageLgdCode,:seasonId,:startYear,:endYear," +
			":farmlandId,:cropCode,:surveyNumber)", nativeQuery = true)
	String getApprovedSurveyDetailsByVillage(Long villageLgdCode, Long seasonId, Integer startYear, Integer endYear,
											 String farmlandId, String cropCode, String surveyNumber);
	
	@Query(value = "select agri_stack.fn_get_village_crop_identification_detail(:villageLgdCode,:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getVillageCropIdentificationDetails(Long villageLgdCode, Integer seasonId, Integer startYear, Integer endYear);

	
}
