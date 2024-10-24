/**
 *
 */
package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.amnex.agristack.dao.*;
import com.amnex.agristack.entity.FarmlandPlotRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import feign.Param;

/**
 * @author kinnari.soni
 *
 */

public interface FarmlandPlotRegistryRepository extends JpaRepository<FarmlandPlotRegistry, Long> {

	List<FarmlandPlotRegistry> findByVillageLgdMaster_VillageLgdCode(Long villageLgdCode);

	List<FarmlandPlotRegistry> findByVillageLgdMaster_VillageLgdCodeIn(List<Long> villageLgdCode);

	List<FarmlandPlotRegistry> findByVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeIn(
			List<Long> subDistrictLgdCodes);

	List<FarmlandPlotRegistry> findByVillageLgdMaster_VillageLgdCodeInAndFarmlandIdNotIn(List<Long> villageLgdCode,
			List<String> ids);

	List<FarmlandPlotRegistry> findByVillageLgdMasterVillageLgdCode(Long villageLgdCode);

	List<FarmlandPlotRegistry> findByVillageLgdMasterVillageLgdCodeOrderBySurveyNumberAsc(Long villageLgdCode);

	Page<FarmlandPlotRegistry> findByVillageLgdMasterVillageLgdCode(Long villageLgdCode, Pageable pageable);

	List<FarmlandPlotRegistry> findByVillageLgdMasterVillageLgdCodeAndSurveyNumberAndSubSurveyNumber(
			Long villageLgdCode, String surveyNumber, String subSurveyNumber);

	List<FarmlandPlotRegistry> findByVillageLgdMasterVillageLgdCodeAndPlotGeometryIsNotNull(Long villageLgdCode);

	@Query(value = "select fpr_farmland_plot_registry_id as id,fpr_village_lgd_code as villageLgdCode,st_asText(fpr_plot_geometry) as geometry  from agri_stack.farmland_plot_registry "
			+ "where fpr_village_lgd_code =:villageLgdCode and fpr_plot_geometry is not null ", nativeQuery = true)
	List<FarmGeometry> getGeometryDetails(Long villageLgdCode);

	List<FarmlandPlotRegistry> findByVillageLgdMasterVillageLgdCodeAndFarmlandIdNotIn(Long villageLgdCode,
			Set<String> ids);

	List<FarmlandPlotRegistry> findByFarmlandIdIn(List<String> ids);

	List<FarmlandPlotRegistry> findByVillageLgdMasterVillageLgdCodeAndFarmlandIdNotInOrderBySurveyNumberAsc(
			Long villageLgdCode, Set<String> ids);

	@Query(value = "select fpr_village_lgd_code as villageLgdCode, count(*) as totalCount from agri_stack.farmland_plot_registry group by  fpr_village_lgd_code", nativeQuery = true)
	List<FarmLandCount> getCountDetails();

	@Query(value = "select fpr_village_lgd_code as villageLgdCode, count(*) as totalCount from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCode) group by  fpr_village_lgd_code", nativeQuery = true)
	List<FarmLandCount> getCountDetailsByVillageCode(List<Long> villageLgdCode);

	@Query(value = "select fpr_village_lgd_code as villageLgdCode, count(*) as totalCount from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCode) group by  fpr_village_lgd_code", nativeQuery = true)
	Page<FarmLandCount> getCountDetailsByVillageCodePagination(List<Long> villageLgdCode, Pageable pageable);

//	@Query(value = "select uvm.village_lgd_code as villageLgdCode, count(*) as totalCount from agri_stack.user_village_mapping uvm "
//			+ "inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uvm.village_lgd_code  and  vld.sub_District_Lgd_Code in(:codes) and uvm.user_id=:userId "
//			+ "inner join agri_stack.farmland_plot_registry fpr on uvm.village_lgd_code = fpr.fpr_village_lgd_code and uvm.user_id=:userId "
//			+ " group by  uvm.village_lgd_code ", nativeQuery = true)
//	Page<FarmLandCount> getElse(Long userId,List<Long> codes,Pageable pageable);

	@Query(value = "select * from agri_stack.fn_get_FarmLandCount(:data) ", nativeQuery = true)
	String getElse(String data);

	@Query(value = "select * from agri_stack.fn_get_survey_task_count_filter_v2(:userId, :startYear, :endYear, :seasonId, :subDistrictCodes) ", nativeQuery = true)
	String getSurveyTaskCountFilter(Integer userId, Integer startYear, Integer endYear, Integer seasonId,
			String subDistrictCodes);

	@Query(value = "select * from agri_stack.fn_get_farmlandcount_without_plots(:villageData,:plotsData) ", nativeQuery = true)
	String getIf(String villageData, String plotsData);

	@Query(countQuery = "select count(*) from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCode) group by  fpr_village_lgd_code", value = "select fpr_village_lgd_code as villageLgdCode, count(*) as totalCount from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCode) group by  fpr_village_lgd_code", nativeQuery = true)
	Page<FarmLandCount> getCountDetailsByVillageCodeByPagination(List<Long> villageLgdCode, Pageable pageable);

	@Query(value = "select fpr_village_lgd_code code,count(lpsm.lpsm_id) survey "
			+ "from  agri_stack.land_parcel_survey_master lpsm "
			+ "inner join agri_stack.farmland_plot_registry fpr on lpsm.parcel_id = fpr.fpr_farmland_plot_registry_id "
			+ "							and lpsm.is_active = true and lpsm.is_deleted = false "
			+ "							and lpsm.season_id =:seasonId "
			+ "							and lpsm.season_start_year = :startYear "
			+ "							and lpsm.season_end_year = :endYear "
			+ "							and lpsm.survey_status is not null "
			+ "							and fpr.fpr_village_lgd_code in (:vCodes ) "
			+ "group by fpr_village_lgd_code", nativeQuery = true)
	List<CountReturnDAO> getCountVillageWise(List<Long> vCodes, Integer startYear, Integer endYear, Long seasonId);

	@Query(value = "select fpr_village_lgd_code as villageLgdCode, count(*) as totalCount from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCode) and fpr_farmland_id not in( :ids) group by  fpr_village_lgd_code", nativeQuery = true)
	List<FarmLandCount> getCountDetailsByVillageCode(List<Long> villageLgdCode, List<String> ids);

	@Query(value = "select fpr_village_lgd_code as villageLgdCode, count(*) as totalCount from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCode) and fpr_farmland_id not in( :ids) group by  fpr_village_lgd_code", nativeQuery = true)
	Page<FarmLandCount> getCountDetailsByVillageCodePagination(List<Long> villageLgdCode, List<String> ids,
			Pageable pageable);

	@Query(value = "select uvm.village_lgd_code as villageLgdCode, count(*) as totalCount from agri_stack.user_village_mapping uvm  "
			+ "inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uvm.village_lgd_code  and  vld.sub_District_Lgd_Code in(:codes) "
			+ "inner join agri_stack.agri_stack.farmland_plot_registry fpr on uvm.village_lgd_code = fpr.fpr_village_lgd_code and uvm.user_id=:userId  "
			+ "where  fpr_farmland_id not in( :ids) group by  uvm.village_lgd_code ", nativeQuery = true)
	Page<FarmLandCount> getIf(Long userId, List<String> ids, List<Long> codes, Pageable pageable);

	@Query(countQuery = "select count(fpr_village_lgd_code) from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCode) group by  fpr_village_lgd_code", value = "select fpr_village_lgd_code as villageLgdCode, count(fpr_village_lgd_code) as totalCount from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCode) group by  fpr_village_lgd_code", nativeQuery = true)
	Page<FarmLandCount> getCountDetailsByVillageCode(List<Long> villageLgdCode, Pageable pageable);

	@Query(countQuery = "select count(fpr_village_lgd_code) from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCode) group by  fpr_village_lgd_code ", value = "select fpr_village_lgd_code as villageLgdCode, count(fpr_village_lgd_code) as totalCount,vlm.village_name  from agri_stack.farmland_plot_registry flr "
			+ "inner join agri_stack.village_lgd_master vlm on vlm.village_lgd_code=flr.fpr_village_lgd_code "
			+ "where flr.fpr_village_lgd_code in (:villageLgdCode)  group by  fpr_village_lgd_code,vlm.village_name "
			+ " ", nativeQuery = true)
	Page<FarmVillageCount> getCountDetailsByVillageCodeWithName(List<Long> villageLgdCode, Pageable pageable);

	@Query("SELECT f FROM FarmlandPlotRegistry f where f.farmlandId in (:landParcelIds)")
	List<FarmlandPlotRegistry> findAllByFarmLandPlotIds(@Param("landParcelIds") List<String> landParcelIds);

	@Query(value = "select ST_AsText(st_transform(ST_SetSRID(st_extent(fpr_plot_geometry),4326),3857)) from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCodeList) and fpr_plot_geometry is not null", nativeQuery = true)
	List<Object> getVillageGeometryByVillageLgdCodeList(List<Long> villageLgdCodeList);

	Optional<FarmlandPlotRegistry> findByFarmlandPlotRegistryId(Long farmLandPlotId);

	@Modifying
	@Transactional
	@Query(value = "DELETE from agri_stack.farmland_plot_registry where fpr_village_lgd_code IN (:villageLgdCode)", nativeQuery = true)
	void deleteAllByVillageLgdCode(Long villageLgdCode);

	/**
	 * @param villageLgdCode
	 * @return
	 */

	@Query(value = "SELECT f.fpr_farmland_plot_registry_id as farmlandPlotRegistryId, f.fpr_farmland_id as farmlandId, f.fpr_land_parcel_id as landParcelId FROM agri_stack.farmland_plot_registry f where f.fpr_village_lgd_code = :villageLgdCode", nativeQuery = true)
	List<FarmlandPlotVerifierDAO> getFarmLandIdsVillageLgdMaster_VillageLgdCode(Long villageLgdCode);

	@Query(value = "SELECT new com.amnex.agristack.dao.FarmlandPlotDAO(f.farmlandPlotRegistryId, f.farmlandId, f.landParcelId,"
			+ " f.surveyNumber, f.subSurveyNumber) FROM FarmlandPlotRegistry f "
			+ "where f.villageLgdMaster.villageLgdCode = :villageLgdCode")
	List<FarmlandPlotDAO> findPlotRegistryForVillage(Long villageLgdCode);

//	@Query(value="select ST_AsText(st_extent(fpr_plot_geometry )) from agri_stack.farmland_plot_registry where fpr_village_lgd_code in (:villageLgdCodeList)", nativeQuery = true)
//	List<Object> getVillageGeometryByVillageLgdCodeList(List<Long> villageLgdCodeList);

	@Query(value = "SELECT f.fpr_survey_number FROM agri_stack.farmland_plot_registry f where f.fpr_village_lgd_code = :villageLgdCode ", nativeQuery = true)
	List<String> findAllByFprSurveyNumberByFprVillageLgdCode(@Param("villageLgdCode") Long villageLgdCode);

	@Query(value = "SELECT f.fpr_unique_code FROM agri_stack.farmland_plot_registry f where f.fpr_village_lgd_code = :villageLgdCode ", nativeQuery = true)
	List<String> findAllByUniqueCodeByFprVillageLgdCode(@Param("villageLgdCode") Long villageLgdCode);

	@Query(value = "SELECT f.fpr_farmland_id FROM agri_stack.farmland_plot_registry f where f.fpr_village_lgd_code = :villageLgdCode ", nativeQuery = true)
	List<String> findAllByFprFarmlandIdByFprVillageLgdCode(@Param("villageLgdCode") Long villageLgdCode);

	@Modifying
	@Transactional
	@Query(value = "DELETE from agri_stack.farmland_plot_registry where fpr_farmland_plot_registry_id IN (:fprPrimaryIds)", nativeQuery = true)
	int deleteAllByFarmlandPlotRegistryId(List<Long> fprPrimaryIds);

	@Query(value = "select agri_stack.fn_get_review_survey_summary_counts_by_user_id(:userId)", nativeQuery = true)
	public String getSurveyCounts(Long userId);

	@Modifying
	@Transactional
	@Query(value = "update agri_stack.user_master set user_first_name = null, user_last_name = null, user_full_name = null, user_email_address = null, user_mobile_number = null, user_password = null, is_active = false, is_deleted = true, user_aadhaar_hash = null where user_id = :user_id", nativeQuery = true)
	void clearUserDetailsById(Long user_id);

	@Query(value = "SELECT f.fpr_farmland_plot_registry_id as farmlandPlotRegistryId, f.fpr_farmland_id as farmlandId, f.fpr_land_parcel_id as landParcelId FROM agri_stack.farmland_plot_registry f where f.fpr_farmland_plot_registry_id in (:ids) ", nativeQuery = true)
	List<FarmlandPlotVerifierDAO> getFarmLandIdsDetailsByids(List<Long> ids);

	Long countBy();

	@Query(value = "select agri_stack.get_Unassign_Plot_data_by_village_lgd_code( :villageLgdCode, :seasonId,:startYear,:endYear) ", nativeQuery = true)
	String getUnassignDataByvillagelgdcode(Long villageLgdCode, Long seasonId, Integer startYear, Integer endYear);

	@Query(value = "select agri_stack.fn_get_plot_details_by_village_lgd_code(:village_lgd_code, :startYear, :endYear, :seasonId, :statusCode) ", nativeQuery = true)
	String getPlotDetailByVillageLgdCode(Long village_lgd_code, Integer startYear, Integer endYear, Integer seasonId,
			Integer statusCode);

	@Query(value = "select flor.flor_owner_name_per_ror from agri_stack.farmer_land_ownership_registry flor "
			+ "inner join agri_stack.farmland_plot_registry fpr on fpr.fpr_farmland_plot_registry_id = flor.flor_fpr_farmland_plot_registry_id "
			+ "where fpr.fpr_village_lgd_code = :villageLgdCode", nativeQuery = true)
	List<String> getFarmerDetailByVillageLgdCode(Integer villageLgdCode);

	@Query(value = " select u from FarmlandPlotRegistry  u "
			+ "	where u.villageLgdMaster.villageLgdCode = :villageLgdCode "
			+ " and u.surveyNumber = :surveyNo and u.subSurveyNumber= :subSurveyNo ")
	List<FarmlandPlotRegistry> findByVillageLgdCodeAndSurveyNumberAndSubSurveyNumber(Long villageLgdCode,
			String surveyNo, String subSurveyNo);

	@Query(value = " select count(u) from FarmlandPlotRegistry  u "
			+ "	where u.isActive = true and u.isDeleted = false")
	Long geTotalPLotCountForState();


	@Query(value = "select agri_stack.get_geo_map_shared_data_by_reference_id(:startNumber,:endNumber)", nativeQuery = true)
	String findLandDetailInRange(@Param("startNumber") Long startNumber, @Param("endNumber") Long endNumber);



	@Query(value = " select new com.amnex.agristack.dao.FarmerOutputDAO(f.farmlandPlotRegistryId,f.surveyNumber,f.subSurveyNumber,f.farmlandId,f.plotArea,f.villageLgdMaster.villageLgdCode) from FarmlandPlotRegistry f "
			+ "	where f.isActive = true and f.isDeleted = false and f.villageLgdMaster.villageLgdCode = :villageLgdCode " +
			  " and f.surveyNumber = :surveyNumber and f.subSurveyNumber = :subSurveyNumber")
	FarmerOutputDAO getFarmLandIPlotDetails(Long villageLgdCode, String surveyNumber, String subSurveyNumber);

	@Query(value = " select f.* from " +
			" agri_stack.Farmland_Plot_Registry f " +
			" where " +
			" CASE WHEN 1 = :seasonId and '2023-2024' = :year THEN  f.is_Active = false and f.is_Deleted = true\n" +
			" ELSE  f.is_Active = true and f.is_Deleted = false END " +
			" and f.fpr_village_Lgd_Code = :villageLgdCode " +
			" and (f.fpr_survey_number = :surveyNumber and f.fpr_sub_Survey_Number = :subSurveyNumber)", nativeQuery = true)
	List<FarmlandPlotRegistry> getFarmLandIPlotDetailsWithSeasonYear(Long seasonId, String year,Long villageLgdCode, String surveyNumber, String subSurveyNumber );

	@Query(value = "select agri_stack.fn_gis_get_plot_data_by_village_land(:isOwnerShipData, :villageLgdCode,:landUsageType,:ownershipType,:plotId)", nativeQuery = true)
	public String getFarmlandPlotSurvey(Integer isOwnerShipData, Integer villageLgdCode,String landUsageType,String ownershipType, String plotId);

}
