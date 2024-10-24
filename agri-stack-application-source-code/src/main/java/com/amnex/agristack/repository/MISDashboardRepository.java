package com.amnex.agristack.repository;

import java.sql.Timestamp;

import com.amnex.agristack.dao.ReviewSurveyCountDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.LandParcelSurveyMaster;

public interface MISDashboardRepository extends JpaRepository<LandParcelSurveyMaster, Long> {

	@Query(value = "select agri_stack.fn_mis_get_village_level_crop_details_by_filter(:userId,:stateList,:districtList,:subDistrictList,:villageList,:seasonId,:startYear,:endYear,:statusList,:startDate,:endDate)", nativeQuery = true)
	String getVillageLevelCropDetails(Integer userId, String stateList, String districtList, String subDistrictList,
			String villageList, Integer seasonId, Integer startYear, Integer endYear, String statusList, String startDate, String endDate);

	@Query(value = "select agri_stack.fn_mis_get_survey_summary_report(:userId,:stateList,:districtList,:subDistrictList,:villageList,:seasonId,:startYear,:endYear,:statusList)", nativeQuery = true)
	String getSurveySummaryDetails(Integer userId, String stateList, String districtList, String subDistrictList,
			String villageList, Integer seasonId, Integer startYear, Integer endYear, String statusList);

	@Query(value = "select agri_stack.fn_mis_get_center_cultivated_summary_report_v2(:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getCultivatedSummaryDetails_v2(Integer seasonId, Integer startYear, Integer endYear);

	@Query(value = "select agri_stack.fn_mis_get_surveyor_survey_report_by_filter(:userId,:stateList,:districtList,:subDistrictList,:villageList,:seasonId,:startYear,:endYear,:statusList,:startDate,:endDate)", nativeQuery = true)
	String getSurveyorSurveyDetails(Integer userId, String stateList, String districtList, String subDistrictList,
			String villageList, Integer seasonId, Integer startYear, Integer endYear, String statusList, String startDate, String endDate);

	@Query(value = "select agri_stack.fn_mis_get_surveyor_activity_report_by_filter(:userId,:stateList,:districtList,:subDistrictList,:villageList,:seasonId,:startYear,:endYear,:statusList,:startDate,:endDate)", nativeQuery = true)
	String getSurveyorActivityDetails(Integer userId, String stateList, String districtList, String subDistrictList,
			String villageList, Integer seasonId, Integer startYear, Integer endYear, String statusList, String startDate, String endDate);

	@Query(value = "select agri_stack.fn_mis_get_cadastral_map_survey_details(:userId,:stateList,:districtList,:subDistrictList,:villageList,:startDate,:endDate)", nativeQuery = true)
	String getCadastralMapSurveyDetails(Integer userId, String stateList, String districtList, String subDistrictList,
			String villageList,  String startDate, String endDate);
	
	@Query(value = "select agri_stack.fn_get_review_survey_summary_counts_by_user_id(:userId)", nativeQuery = true)
	public String getSurveySummaryCountByUser(Long userId);

//	@Query(value = "WITH AggregatedCounts AS ( SELECT survey_status, verifier_status FROM agri_stack.Land_Parcel_Survey_Master lpsm inner join agri_stack.farmland_plot_registry flpr on flpr.fpr_farmland_plot_registry_id = lpsm.parcel_id and flpr.is_active=true and flpr.is_deleted=false inner join agri_stack.user_village_mapping uvm on uvm.village_lgd_code = flpr.fpr_village_lgd_code and user_id=:userId WHERE season_id = :seasonId AND season_start_year = :startYear AND season_end_year = :endYear ) SELECT COUNT(*) FILTER (WHERE survey_status NOT IN (105) OR survey_status IS NULL) AS total_survey, COUNT(*) FILTER (WHERE survey_status = 101) AS approved_count, COUNT(*) FILTER (WHERE survey_status = 102) AS rejectedCount, COUNT(*) FILTER (WHERE survey_status = 106) AS reassigned_Count, COUNT(*) FILTER (WHERE survey_status = 103) AS under_approval_Count, COUNT(*) FILTER (WHERE verifier_status IS NOT NULL AND verifier_status NOT IN (105)) AS verifier_Count FROM AggregatedCounts", nativeQuery = true)
//	public ReviewSurveyCountDAO getSurveySummaryCountByUserV2(Long userId,Long seasonId,Integer startYear,Integer endYear);
	
	@Query(value = "WITH AggregatedCounts AS ( SELECT survey_status, verifier_status FROM agri_stack.Land_Parcel_Survey_Master lpsm  inner join agri_stack.user_village_mapping uvm on uvm.village_lgd_code = lpsm.village_lgd_code and user_id=:userId WHERE season_id = :seasonId AND season_start_year = :startYear AND season_end_year = :endYear ) SELECT COUNT(*) FILTER (WHERE survey_status NOT IN (105) OR survey_status IS NULL) AS total_survey, COUNT(*) FILTER (WHERE survey_status = 101) AS approved_count, COUNT(*) FILTER (WHERE survey_status = 102) AS rejectedCount, COUNT(*) FILTER (WHERE survey_status = 106) AS reassigned_Count, COUNT(*) FILTER (WHERE survey_status = 103) AS under_approval_Count, COUNT(*) FILTER (WHERE verifier_status IS NOT NULL AND verifier_status NOT IN (105)) AS verifier_Count FROM AggregatedCounts", nativeQuery = true)
	public ReviewSurveyCountDAO getSurveySummaryCountByUserV2(Long userId,Long seasonId,Integer startYear,Integer endYear);
	
	@Query(value = "select agri_stack.fn_mis_get_survey_data_details_of_crop_and_media(:districtList,:subDistrictList,:villageList)", nativeQuery = true)
	String getsureyDataDetailsOfCropMedia(String districtList, String subDistrictList,String villageList);			
	
	@Query(value = "select agri_stack.fn_mis_get_survey_summary_report_date_wise(:userId,:stateList,:districtList,:subDistrictList,:villageList,:seasonId,:startYear,:endYear,:statusList,:startDate,:endDate)", nativeQuery = true)
	String getSurveySummaryDetailsDateWise(Integer userId, String stateList, String districtList, String subDistrictList,
			String villageList, Integer seasonId, Integer startYear, Integer endYear, String statusList,String startDate,String endDate);
	
//	@Query(value = "select agri_stack.fn_mis_get_cultivated_aggregated_summary_report(:userId,:stateList,:districtList,:subDistrictList,:villageList,:seasonId,:startYear,:endYear,:page,:limit,:sortField,:sortOrder,:search)", nativeQuery = true)
//	String getCultivatedAggregatedDetails(String userId, String stateList, String districtList, String subDistrictList,
//			String villageList, Integer seasonId, Integer startYear, Integer endYear, 
//			Long page, Long limit, String sortField, String sortOrder, String search);
	
	@Query(value = "select agri_stack.fn_mis_get_cultivated_aggregated_summary_report_v2(:userId,:territoryLevel,:seasonId,:startYear,:endYear,:code)", nativeQuery = true)
	String getCultivatedAggregatedDetails(String userId, String territoryLevel, Integer seasonId, Integer startYear, Integer endYear, 
			Long code);

	//last_sync_date
	@Query(value = "select agri_stack.fn_mis_get_cultivated_aggregated_summary_report_new(:territoryLevel,:code,:page,:limit)", nativeQuery = true)
	String getCultivatedAggregatedDetails_v2(String territoryLevel, Long code, Integer page, Integer limit);//, Timestamp last_sync_date);

	@Query(value = "select agri_stack.fn_mis_get_cultivated_total_record_count(:territoryLevel,:code)", nativeQuery = true)
	String getCultivatedAggregatedTotalCount(String territoryLevel, Long code);



	@Query(value = "select agri_stack.fn_mis_get_cultivated_summary_report(:userId,:territoryLevel,:seasonId,:startYear,:endYear,:code)", nativeQuery = true)
	String getCultivatedSummaryDetails(String userId, String territoryLevel, Integer seasonId, Integer startYear, Integer endYear, 
			Long code);

	@Query(value = "select agri_stack.fn_mis_get_cultivated_summary_report_v2(:userId,:territoryLevel,:seasonId,:startYear,:endYear,:code)", nativeQuery = true)
	String getCultivatedSummaryDetails_v2(String userId, String territoryLevel, Integer seasonId, Integer startYear, Integer endYear,
									   Long code);

//	@Query(value = "select agri_stack.fn_mis_get_cultivated_summary_report_v3(:userId,:territoryLevel,:seasonId,:startYear,:endYear,:code,:page,:limit,:createdOn)", nativeQuery = true)
//	String getCultivatedSummaryDetails_v3(String userId, String territoryLevel, Integer seasonId, Integer startYear, Integer endYear,
//										  Long code, Integer page, Integer limit, Timestamp createdOn);

	@Query(value = "select agri_stack.fn_mis_get_cultivated_summary_report_new(:territoryLevel,:seasonId,:startYear,:endYear,:code,:page,:limit)", nativeQuery = true)
	String getCultivatedSummaryDetails_v3(String territoryLevel, Integer seasonId, Integer startYear, Integer endYear,
										  Long code, Integer page, Integer limit);

	@Query(value = "select agri_stack.fn_mis_get_cultivated_summary_total_records_v3(:userId,:territoryLevel,:seasonId,:startYear,:endYear,:code,:createdOn)", nativeQuery = true)
	String getCultivatedSummaryDetailsCount(String userId, String territoryLevel, Integer seasonId, Integer startYear, Integer endYear,
										  Long code, Timestamp createdOn);

	@Query(value = "select agri_stack.fn_mis_get_cultivated_crop_category_detail(:userId,:territoryLevel,:seasonId,:startYear,:endYear,:code,:typeId)", nativeQuery = true)
	String getCultivatedCropCategoryDetails(String userId, String territoryLevel, Integer seasonId, Integer startYear, Integer endYear, 
			Long code, Integer typeId);


	/**
	 *
	 * @param userId
	 * @param stateLgdCode
	 * @param districtLgdCode
	 * @param subDistrictLgdCode
	 * @param villageLgdCode
	 * @param statusCode
	 * @param seasonId
	 * @param StartYear
	 * @param endYear
	 * @param page
	 * @param limit
	 * @param sortField
	 * @param sortOrder
	 * @param search
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Query(value = "select agri_stack.fn_web_get_land_survey_summary_details_paginationV2(:userId, :stateLgdCode, :districtLgdCode,:subDistrictLgdCode, :villageLgdCode, :statusCode , " + 
			"			:seasonId,:StartYear,:endYear,:page,:limit,:sortField, " + 
			"			:sortOrder,:search,:startDate,:endDate) ",nativeQuery = true)
	String getSurveySummaryPagination(String userId, String stateLgdCode, String districtLgdCode,String subDistrictLgdCode, String villageLgdCode,
			String statusCode,Integer seasonId,Integer StartYear,Integer endYear,Integer page,Integer limit,String sortField,
			String sortOrder,String search,String startDate,String endDate);


	/**
	 * 
	 * @param userId
	 * @param stateLgdCode
	 * @param districtLgdCode
	 * @param subDistrictLgdCode
	 * @param villageLgdCode
	 * @param statusCode
	 * @param seasonId
	 * @param StartYear
	 * @param endYear
	 * @param page
	 * @param limit
	 * @param sortField
	 * @param sortOrder
	 * @param search
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@Query(value = "select agri_stack.fn_web_get_land_survey_summary_details_pagination_count(:userId, :stateLgdCode, :districtLgdCode,:subDistrictLgdCode, :villageLgdCode, :statusCode , " + 
			"			:seasonId,:StartYear,:endYear,:page,:limit,:sortField, " + 
			"			:sortOrder,:search,:startDate,:endDate) ",nativeQuery = true)
	String getSurveySummaryPaginationCount(String userId, String stateLgdCode, String districtLgdCode,String subDistrictLgdCode, String villageLgdCode,
			String statusCode,Integer seasonId,Integer StartYear,Integer endYear,Integer page,Integer limit,String sortField,
			String sortOrder,String search,String startDate,String endDate);

	@Query(value = "select agri_stack.fn_mis_get_user_management_report(:userId,:territoryLevel,:code)", nativeQuery = true)
	String getUsermanagementReport(String userId, String territoryLevel,Long code);
	
	@Query(value = "select agri_stack.fn_mis_get_surveyor_progress_reportV2(:userId,:seasonId,:startYear,:endYear,:districtLgdCode,:subDistrictLgdCode,:villageLgdCode)", nativeQuery = true)
	String getSurveyorProgressDetails(Integer userId, Integer seasonId, Integer startYear, Integer endYear,String districtLgdCode,String subDistrictLgdCode,String villageLgdCode);


	@Query(value = "SELECT * FROM agri_stack.get_crop_area_data(:stateLgdCode, :seasonName, :seasonStartYear,:seasonEndYear,:pageSize,:pageNumber,:lastSyncDate)", nativeQuery = true)
	String getCropAreaData(int stateLgdCode, String seasonName, int seasonStartYear, int seasonEndYear, int pageSize, int pageNumber,String lastSyncDate);

	@Query(value = "SELECT * FROM agri_stack.get_crop_area_total_count(:stateLgdCode, :seasonName, :seasonStartYear,:seasonEndYear)", nativeQuery = true)
	String getCropTotalCount(int stateLgdCode, String seasonName, int seasonStartYear, int seasonEndYear);


}
