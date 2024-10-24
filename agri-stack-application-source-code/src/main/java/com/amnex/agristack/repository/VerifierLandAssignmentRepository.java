package com.amnex.agristack.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.amnex.agristack.dao.FarmLandCount;
import com.amnex.agristack.dao.LandUserDAO;
import com.amnex.agristack.dao.UserLandCount;
import com.amnex.agristack.entity.FarmlandPlotRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.entity.VerifierLandAssignment;

public interface VerifierLandAssignmentRepository extends JpaRepository<VerifierLandAssignment, Long> {

	@Modifying
	@Transactional
	@Query(value = "UPDATE agri_stack.verifier_land_assignment SET is_deleted = true, is_active = false "
			+ " WHERE verifier_id = :userId "
			+ " and is_active = true and is_deleted = false ", nativeQuery = true)
	void UpdateByUserId( Long userId);
	
	List<VerifierLandAssignment> findByVillageLgdCode_villageLgdCode(Long villageLgdCode);

	List<VerifierLandAssignment> findByStartingYearAndEndingYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeletedAndIsSecondTimeRejected(
			Integer startingYear, Integer endingYear, Long seasonId, Long villageLgdCode, Boolean isActive,
			Boolean isDeleted, Boolean isSecondTimeRejected);

	@Query(value = "select * from agri_stack.verifier_land_assignment where starting_year = :startingYear and ending_year = :endingYear and season_id = :seasonId and village_lgd_code_village_lgd_code = :villageLgdCode" +
			" and is_active = true and is_deleted = false and (is_second_time_rejected != true or is_second_time_rejected is null)", nativeQuery = true)
	List<VerifierLandAssignment> findByStartingYearAndEndingYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeleted(
			Integer startingYear, Integer endingYear, Long seasonId, Long villageLgdCode);

	@Query(value = "SELECT vla FROM VerifierLandAssignment vla WHERE vla.startingYear = :startingYear AND vla.endingYear = :endingYear" +
			" AND vla.season.seasonId = :seasonId AND vla.isActive = :isActive AND vla.isDeleted = :isDeleted AND" +
			" vla.isSecondTimeRejected = true AND vla.villageLgdCode.villageLgdCode = :villageLgdCode")
	List<VerifierLandAssignment> findLandAssignmentWithSecondRejected(
			Integer startingYear, Integer endingYear, Long seasonId, Long villageLgdCode, Boolean isActive,
			Boolean isDeleted);

	@Query(value = "SELECT vla FROM VerifierLandAssignment vla WHERE vla.startingYear = :startingYear AND vla.endingYear = :endingYear" +
			" AND vla.season.seasonId = :seasonId AND vla.isActive = :isActive AND vla.isDeleted = :isDeleted AND" +
			" (vla.isSecondTimeRejected != true OR vla.isSecondTimeRejected IS null) AND vla.villageLgdCode.villageLgdCode = :villageLgdCode")
	List<VerifierLandAssignment> findLandAssignmentWithoutSecondRejected(
			Integer startingYear, Integer endingYear, Long seasonId, Long villageLgdCode, Boolean isActive,
			Boolean isDeleted);

	List<VerifierLandAssignment> findByStartingYearAndEndingYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeletedAndVerifier_UserId(
			Integer startingYear, Integer endingYear, Long seasonId, Long villageLgdCode, Boolean isActive,
			Boolean isDeleted, Long userId);

	@Query(value = "select verifier_id as userId, count(*) as totalCount "
			+ " from agri_stack.verifier_land_assignment "
			+ " where village_lgd_code_village_lgd_code = :code and is_active = true and is_deleted = false "
			+ " group by verifier_id", nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCode(Long code);

	@Query(value = "select verifier_id as userId, count(*) as totalCount "
			+ " from agri_stack.verifier_land_assignment where village_lgd_code_village_lgd_code = :code "
			+ " and season_id = :seasonId and starting_year = :startingYear and ending_year = :endingYear "
			+ " and is_active = true and is_deleted = false group by verifier_id", nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCode(Long code, Long seasonId, Integer startingYear,
			Integer endingYear);

	@Query(value = "select village_lgd_code_village_lgd_code as villageLgdCode, "
			+ " count(*) as totalCount from agri_stack.verifier_land_assignment "
			+ " where season_id = :seasonId and village_lgd_code_village_lgd_code in (:code) "
			+ " and starting_year = :startingYear and ending_year = :endingYear  "
			+ " and is_active = true and is_deleted = false "
			+ " group by village_lgd_code_village_lgd_code", nativeQuery = true)
	List<FarmLandCount> getCountDetailsByFilter(Long seasonId, List<Long> code, Integer startingYear,
			Integer endingYear);

	@Query(value = "select village_lgd_code_village_lgd_code as villageLgdCode, "
			+ " count(*) as totalCount from agri_stack.verifier_land_assignment "
			+ " where season_id = :seasonId and village_lgd_code_village_lgd_code in (:code) "
			+ " and starting_year = :startingYear and ending_year = :endingYear  "
			+ " and is_active = true and is_deleted = false and is_second_time_rejected = true "
			+ " group by village_lgd_code_village_lgd_code", nativeQuery = true)
	List<FarmLandCount> getCountDetailsByFilterWithSecondTimeRejected(Long seasonId, List<Long> code, Integer startingYear,
																	  Integer endingYear);

	@Query(value = "select village_lgd_code_village_lgd_code as villageLgdCode, "
			+ " count(*) as totalCount from agri_stack.verifier_land_assignment "
			+ " where season_id = :seasonId and village_lgd_code_village_lgd_code in (:code) "
			+ " and starting_year = :startingYear and ending_year = :endingYear  "
			+ " and is_active = true and is_deleted = false and (is_second_time_rejected != true or is_second_time_rejected is null)"
			+ " group by village_lgd_code_village_lgd_code", nativeQuery = true)
	List<FarmLandCount> getCountDetailsByFilterWithoutSecondTimeRejected(Long seasonId, List<Long> code, Integer startingYear,
												Integer endingYear);
	
	
	@Query(value = "select village_lgd_code_village_lgd_code as villageLgdCode, "
			+ " count(*) as totalCount from agri_stack.verifier_land_assignment "
			+ " where season_id = :seasonId and village_lgd_code_village_lgd_code in (:code) "
			+ " and starting_year = :startingYear and ending_year = :endingYear  "
			+ " and is_active = true and is_deleted = false and reason_of_assignment=:verifierReasonOfAssignment "
			+ " group by village_lgd_code_village_lgd_code", nativeQuery = true)
	List<FarmLandCount> getCountDetailsByFilter(Long seasonId, List<Long> code, Integer startingYear,
			Integer endingYear,String verifierReasonOfAssignment);

	@Query(value = "SELECT distinct v.verifier.userId FROM VerifierLandAssignment v "
			+ " WHERE v.startingYear = :startingYear AND v.endingYear = :endingYear "
			+ " AND v.season.seasonId = :seasonId and v.isActive IS TRUE AND v.isDeleted IS FALSE ")
	List<Long> findByYearAndSeason(Integer startingYear, Integer endingYear, Long seasonId);

	@Query(value = "select verifier_id as userId, count(*) as totalCount "
			+ " from agri_stack.verifier_land_assignment where village_lgd_code_village_lgd_code = :villageCode "
			+ " and season_id = :seasonId and starting_year = :startingYear and ending_year = :endingYear "
			+ " and is_active = true and is_deleted = false and verifier_id = :verifierId", nativeQuery = true)
	List<UserLandCount> getCountDetailsByVerifier(Long villageCode, Long seasonId, Integer startingYear,
			Integer endingYear, Long verifierId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE agri_stack.verifier_land_assignment SET is_deleted = true, is_active = false "
			+ " WHERE village_lgd_code_village_lgd_code = :villageCode "
			+ " and season_id = :seasonId and starting_year = :startingYear and ending_year = :endingYear "
			+ " and is_active = true and is_deleted = false and verifier_id = :verifierId and farmland_id in (:ids) ", nativeQuery = true)
	void unassignVerifierTaskAllocation(Long villageCode, Long seasonId, Integer startingYear,
			Integer endingYear, Long verifierId, List<String> ids);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE agri_stack.verifier_land_assignment SET is_deleted = true, is_active = false "
			+ " WHERE village_lgd_code_village_lgd_code = :villageCode "
			+ " and season_id = :seasonId and starting_year = :startingYear and ending_year = :endingYear "
			+ " and is_active = true and is_deleted = false and farmland_id in (:ids) ", nativeQuery = true)
	void unassignVerifierTaskAllocationWithoutUserId(Long villageCode, Long seasonId, Integer startingYear,
			Integer endingYear,  List<String> ids);
	
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE agri_stack.verifier_land_assignment SET is_deleted = true, is_active = false "
			+ " WHERE verifier_id = :userId "
			+ " and season_id = :seasonId and starting_year = :startingYear and ending_year = :endingYear "
			+ " and is_active = true and is_deleted = false and farmland_id in (:ids) ", nativeQuery = true)
	void unassignVerifierTaskAllocationWithoutUserId( Long seasonId, Integer startingYear,
			Integer endingYear,  List<String> ids,Long userId);
	

	@Query(value = "			select ula.verifier_id as userId,farmland_id farmLandId " +
			"			from  agri_stack.farmland_plot_registry flpr  " +
			"			inner join agri_stack.verifier_land_assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id "
			+
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  "
			+
			"			where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and "
			+
			"			ula.village_lgd_code_village_lgd_code=:villageCode and ula.verifier_id = :userId " +
			"			and  ula.is_active=true and ula.is_deleted=false and lsm.survey_status is null	  ", nativeQuery = true)
	List<LandUserDAO> getfamlandIdByFilter(Integer startYear, Integer endYear, Long seasonId, Long villageCode,
										   Long userId);
	
	
	@Query(value = "			select ula.verifier_id as userId,farmland_id farmLandId " +
			"			from  agri_stack.farmland_plot_registry flpr  " +
			"			inner join agri_stack.verifier_land_assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.survey_status is null  "
			+
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  "
			+
			"			where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and "
			+
			"			ula.village_lgd_code_village_lgd_code=:villageCode and " +
			"			  ula.is_active=true and ula.is_deleted=false 	  ", nativeQuery = true)
//	and lsm.survey_status is null
	List<LandUserDAO> getfamlandIdByFilterWithOutUserId(Integer startYear, Integer endYear, Long seasonId, Long villageCode);

	@Query(value = "			select ula.verifier_id as userId,farmland_id farmLandId " +
			"			from  agri_stack.farmland_plot_registry flpr  " +
			"			inner join agri_stack.verifier_land_assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.survey_status is null  "
			+
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  "
			+
			"			where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and "
			+
			"			ula.village_lgd_code_village_lgd_code=:villageCode and " +
			"			  ula.is_active=true and ula.is_deleted=false and is_second_time_rejected = true  ", nativeQuery = true)
//	and lsm.survey_status is null
	List<LandUserDAO> getfamlandIdByFilterWithOutUserIdWithSecondRejected(Integer startYear, Integer endYear, Long seasonId, Long villageCode);

	@Query(value = "			select ula.verifier_id as userId,farmland_id farmLandId " +
			"			from  agri_stack.farmland_plot_registry flpr  " +
			"			inner join agri_stack.verifier_land_assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.survey_status is null  "
			+
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  "
			+
			"			where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and "
			+
			"			ula.village_lgd_code_village_lgd_code=:villageCode and " +
			"			ula.is_active=true and ula.is_deleted=false and is_second_time_rejected = true" +
			"			and farmland_id not in (:farmlandIds)", nativeQuery = true)
//	and lsm.survey_status is null
	List<LandUserDAO> getFarmlandIdByFilterWithOutUserIdWithSecondRejectedFarmlandNotIn(Integer startYear, Integer endYear, Long seasonId, Long villageCode,
																						List<String> farmlandIds);

	@Query(value = "			select ula.verifier_id as userId,farmland_id farmLandId " +
			"			from  agri_stack.farmland_plot_registry flpr  " +
			"			inner join agri_stack.verifier_land_assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.survey_status is null  "
			+
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  "
			+
			"			where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and "
			+
			"			ula.village_lgd_code_village_lgd_code=:villageCode and " +
			"			  ula.is_active=true and ula.is_deleted=false and (is_second_time_rejected != true or is_second_time_rejected is null)  ", nativeQuery = true)
//	and lsm.survey_status is null
	List<LandUserDAO> getfamlandIdByFilterWithOutUserIdWithoutSecondRejected(Integer startYear, Integer endYear, Long seasonId, Long villageCode);

	@Query(value = "			select ula.verifier_id as userId,farmland_id farmLandId " +
			"			from  agri_stack.farmland_plot_registry flpr  " +
			"			inner join agri_stack.verifier_land_assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.survey_status is null  "
			+
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  "
			+
			"			where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and "
			+
			"			ula.village_lgd_code_village_lgd_code=:villageCode and " +
			"			ula.is_active=true and ula.is_deleted=false and (is_second_time_rejected != true or is_second_time_rejected is null)" +
			"			and farmland_id not in (:farmlandIds)", nativeQuery = true)
//	and lsm.survey_status is null
	List<LandUserDAO> getFarmlandIdByFilterWithOutUserIdWithoutSecondRejectedFarmlandNotIn(Integer startYear, Integer endYear, Long seasonId, Long villageCode,
																						  List<String> farmlandIds);
	
	@Query(value = "			select ula.verifier_id as userId,farmland_id farmLandId " +
			"			from  agri_stack.farmland_plot_registry flpr  " +
			"			inner join agri_stack.verifier_land_assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id "
			+
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  "
			+
			"			where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and "
			+
			"  verifier_id =:userId and " +
			"			  ula.is_active=true and ula.is_deleted=false and lsm.survey_status is null	  ", nativeQuery = true)
	List<LandUserDAO> getfamlandIds(Integer startYear, Integer endYear, Long seasonId,Long userId);
	

	@Query(value = "SELECT distinct village_lgd_code_village_lgd_code FROM agri_stack.verifier_land_assignment "
			+ " WHERE ending_year BETWEEN (:endingYear - 4) AND :endingYear "
			+ " AND is_active is true and is_deleted is false ", nativeQuery = true)
	List<Long> findPreviouslyAllocatedVillages(Integer endingYear);

	@Query(value = "SELECT distinct farmland_id FROM agri_stack.verifier_land_assignment "
			+ " WHERE ending_year BETWEEN (:endingYear - 4) AND :endingYear "
			+ " AND is_active is true and is_deleted is false", nativeQuery = true)
	List<String> findPreviouslyAllocatedPlots(Integer endingYear);

	String findVerifierForAutoAssignmentQuery =

			" Select \"userId\",\"flag\" from " +
					" (Select distinct(um.user_id) as \"userId\",1 as \"flag\" from agri_stack.user_master um " +
					" inner join agri_stack.user_village_mapping uvm on um.user_id = uvm.user_id  " +
					" where uvm.village_lgd_code  = :villageCode   " +
					" and um.is_deleted = false and um.is_active = true " +
					"  and um.role_id in (Select role_id from agri_Stack.role_master where UPPER(role_name) = 'VERIFIER' and is_default = true) "
					+
					"  union all " +
					" Select distinct(um.user_id) as \"userId\",2 as \"flag\" from agri_stack.user_master um " +
					" inner join agri_stack.user_village_mapping uvm on um.user_id = uvm.user_id  " +
					" where uvm.village_lgd_code in  " +
					"   (Select village_lgd_code from agri_stack.village_lgd_master  " +
					" 	where sub_district_lgd_code = :subDistrictCode)  " +
					" 	and um.role_id in (Select role_id from agri_Stack.role_master where UPPER(role_name) = 'VERIFIER' and is_default = true) "
					+
					" and um.is_deleted = false and um.is_active = true " +
					" ) as subQuery  " +
					" order by \"flag\" ; ";

	@Query(value = findVerifierForAutoAssignmentQuery, nativeQuery = true)

	List<Object[]> findVerifierForAutoAssignment(Long villageCode, Long subDistrictCode);

	@Query("Select v from VerifierLandAssignment v where verifier = :verfierId and farmlandId = :farmlandId")
	public VerifierLandAssignment findByVerifierAndFarmlandId(UserMaster verfierId, String farmlandId);

	@Query("Select case when (count(v) > 0)  then true else false end from VerifierLandAssignment v where "
			+ " v.farmlandId = :farmlandId and v.endingYear = :endingYear"
			+ " and v.startingYear = :startingYear and v.season.seasonId = :seasonId "
			+ " and v.isActive = true and v.isDeleted = false")
	public Boolean findByFarmlandIdAndSeasonAndStartYearAndEndYear(String farmlandId,
			Long seasonId, Integer startingYear, Integer endingYear);
	

//	and ending_year=:year and season_id=:seasonId 
	@Query(value="select  village_lgd_code_village_lgd_code villageLgdCode, count(*) totalCount from agri_stack.Verifier_Land_Assignment" + 
			" where is_active=true and is_deleted=false " + 
			" group by village_lgd_code_village_lgd_code " + 
			" order by village_lgd_code_village_lgd_code asc",nativeQuery = true)
	public 	List<FarmLandCount> getVerifierLandAssigmentVillageCount();

	@Query(value = "select  village_lgd_code_village_lgd_code villageLgdCode, count(*) totalCount from agri_stack.Verifier_Land_Assignment" +
			" where is_active=true and is_deleted=false and is_second_time_rejected = true" +
			" group by village_lgd_code_village_lgd_code" +
			" order by village_lgd_code_village_lgd_code asc", nativeQuery = true)
	public 	List<FarmLandCount> getVerifierLandAssignmentVillageCountWithSecondRejected();

	@Query(value = "select  village_lgd_code_village_lgd_code villageLgdCode, count(*) totalCount from agri_stack.Verifier_Land_Assignment" +
			" where is_active=true and is_deleted=false and (is_second_time_rejected != true or is_second_time_rejected is null)" +
			" group by village_lgd_code_village_lgd_code" +
			" order by village_lgd_code_village_lgd_code asc", nativeQuery = true)
	public 	List<FarmLandCount> getVerifierLandAssignmentVillageCountWithoutSecondRejected();
//	Integer year,Long seasonId
	
	@Query(value="select fpr_village_lgd_code villageLgdCode,count(*) totalCount from agri_stack.farmland_Plot_Registry" + 
			" where fpr_village_lgd_code in(:codes) and is_active=true and is_deleted=false" + 
			" group by fpr_village_lgd_code " + 
			" order by fpr_village_lgd_code asc",nativeQuery = true)
	public 	List<FarmLandCount> getFarmLandPlotRegistry(List<Long> codes);
	
	
	@Query(value="select  village_lgd_code_village_lgd_code villageLgdCode, count(*) totalCount from agri_stack.Verifier_Land_Assignment" + 
			" where is_active=true and is_deleted=false and village_lgd_code_village_lgd_code in codes " + 
			" group by village_lgd_code_village_lgd_code " + 
			" order by village_lgd_code_village_lgd_code asc",nativeQuery = true)
	public 	List<FarmLandCount> getVerifierLandAssigmentVillageCountByVillageCodes(List<Long> codes);
	
	
	List<VerifierLandAssignment> findByVillageLgdCode_villageLgdCodeInAndIsActiveAndIsDeleted(List<Long> codes,Boolean isActive,Boolean isDeleted);
	
	List<VerifierLandAssignment> findByVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeleted(Long code,Boolean isActive,Boolean isDeleted);
	

	@Query(value = "select verifier_id as userId,count(*) as totalCount " + 
			"				from agri_stack.verifier_land_assignment  " + 
			"				where season_id=:seasonId and village_lgd_code_village_lgd_code =:code " + 
			"				  and starting_year=:startYear and ending_year=:endYear  " + 
			"				  and is_active=true and is_deleted=false and farmland_id not in (:farmLandIds) " + 
			"				and is_deleted=false group by verifier_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCodeWithfilterNAPlots(Long seasonId,Long code,Integer startYear, Integer endYear,List<String> farmLandIds);


	@Query(value = "select verifier_id as userId,count(*) as totalCount " +
			"				from agri_stack.verifier_land_assignment  " +
			"				where season_id=:seasonId and village_lgd_code_village_lgd_code =:code " +
			"				  and starting_year=:startYear and ending_year=:endYear and is_second_time_rejected = true " +
			"				  and is_active=true and is_deleted=false and farmland_id not in (:farmLandIds) " +
			"				and is_deleted=false group by verifier_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCodeWithFilterNAPlotsWithSecondRejected(Long seasonId,Long code,Integer startYear, Integer endYear,List<String> farmLandIds);


	@Query(value = "select verifier_id as userId,count(*) as totalCount " +
			"				from agri_stack.verifier_land_assignment  " +
			"				where season_id=:seasonId and village_lgd_code_village_lgd_code =:code " +
			"				  and starting_year=:startYear and ending_year=:endYear and (is_second_time_rejected != true or is_second_time_rejected is null) " +
			"				  and is_active=true and is_deleted=false and farmland_id not in (:farmLandIds) " +
			"				and is_deleted=false group by verifier_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCodeWithFilterNAPlotsWithoutSecondRejected(Long seasonId,Long code,Integer startYear, Integer endYear,List<String> farmLandIds);
	
	@Query(value = "select verifier_id as userId,count(*) as totalCount " + 
			"				from agri_stack.verifier_land_assignment  " + 
			"				where season_id=:seasonId and village_lgd_code_village_lgd_code =:code " + 
			"				  and starting_year=:startYear and ending_year=:endYear  " + 
			"				  and is_active=true and is_deleted=false  " + 
			"				and is_deleted=false group by verifier_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCodeWithfilter(Long seasonId,Long code,Integer startYear, Integer endYear);
	
	@Query(value = "select agri_stack.fn_get_village_list(:userId,:stateLgdCode,:subDistrictLgdCode)", nativeQuery = true)
	String getVillageList(@Param("userId") Long userId,Long stateLgdCode, Long subDistrictLgdCode);

	@Query(value = "select verifier_id as userId,count(*) as totalCount " +
			"				from agri_stack.verifier_land_assignment  " +
			"				where season_id=:seasonId and village_lgd_code_village_lgd_code =:code " +
			"				  and starting_year=:startYear and ending_year=:endYear  " +
			"				  and is_active=true and is_deleted=false and is_second_time_rejected = true " +
			"				and is_deleted=false group by verifier_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCodeWithFilterWithSecondRejected(Long seasonId,Long code,Integer startYear, Integer endYear);

	@Query(value = "select verifier_id as userId,count(*) as totalCount " +
			"				from agri_stack.verifier_land_assignment  " +
			"				where season_id=:seasonId and village_lgd_code_village_lgd_code =:code " +
			"				  and starting_year=:startYear and ending_year=:endYear and (is_second_time_rejected != true or is_second_time_rejected is null) " +
			"				  and is_active=true and is_deleted=false  " +
			"				and is_deleted=false group by verifier_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCodeWithFilterWithoutSecondRejected(Long seasonId,Long code,Integer startYear, Integer endYear);

	@Query(value = "SELECT fpr FROM LandParcelSurveyMaster lpsm" +
			"            inner join LandParcelSurveyDetail lpsd on lpsd.lpsmId = lpsm.lpsmId" +
			"            inner join FarmlandPlotRegistry fpr on fpr.farmlandPlotRegistryId = lpsm.parcelId" +
			"            inner join VillageLgdMaster vlm on vlm.villageLgdCode = fpr.villageLgdMaster.villageLgdCode" +
			"            where lpsd.reviewNo = 2 and lpsm.surveyOneStatus = 102 and lpsm.surveyTwoStatus = 102 and lpsm.isActive = true and lpsm.isDeleted = false" +
			" 			 and fpr.isActive = true and fpr.isDeleted = false" +
			"            and lpsm.seasonId = :seasonId and lpsm.seasonStartYear = :startYear and lpsm.seasonEndYear = :endYear and vlm.subDistrictLgdCode.subDistrictLgdCode in (:subDistrictLgdCodes)")
	List<FarmlandPlotRegistry> findSecondTImeRejectedSurveyLandDetailsBySubDistrictLgdCodeV2(Long seasonId, Integer startYear, Integer endYear, List<Long> subDistrictLgdCodes);

	@Query(value = "SELECT fpr FROM LandParcelSurveyMaster lpsm" +
			"            inner join LandParcelSurveyDetail lpsd on lpsd.lpsmId = lpsm.lpsmId" +
			"            inner join FarmlandPlotRegistry fpr on fpr.farmlandPlotRegistryId = lpsm.parcelId" +
			"            inner join VillageLgdMaster vlm on vlm.villageLgdCode = fpr.villageLgdMaster.villageLgdCode" +
			"            where lpsd.reviewNo = 2 and lpsm.surveyOneStatus = 102 and lpsm.surveyTwoStatus = 102 and lpsm.isActive = true and lpsm.isDeleted = false" +
			" 			 and fpr.isActive = true and fpr.isDeleted = false" +
			"            and lpsm.seasonId = :seasonId and lpsm.seasonStartYear = :startYear and lpsm.seasonEndYear = :endYear and vlm.subDistrictLgdCode.subDistrictLgdCode in (:subDistrictLgdCodes)" +
			"			 and lower(vlm.villageName) like lower(CONCAT('%',:search ,'%'))")
	List<FarmlandPlotRegistry> findSecondTImeRejectedSurveyLandDetailsBySubDistrictLgdCodeWithSearchV2(Long seasonId, Integer startYear, Integer endYear, List<Long> subDistrictLgdCodes, String search);

	@Query(value = "SELECT fpr FROM LandParcelSurveyMaster lpsm" +
			"            inner join LandParcelSurveyDetail lpsd on lpsd.lpsmId = lpsm.lpsmId" +
			"            inner join FarmlandPlotRegistry fpr on fpr.farmlandPlotRegistryId = lpsm.parcelId" +
			"            inner join VillageLgdMaster vlm on vlm.villageLgdCode = fpr.villageLgdMaster.villageLgdCode" +
			"            where lpsd.reviewNo = 2 and lpsm.surveyOneStatus = 102 and lpsm.surveyTwoStatus = 102 and lpsm.isActive = true and lpsm.isDeleted = false" +
			" 			 and fpr.isActive = true and fpr.isDeleted = false" +
			"            and lpsm.seasonId = :seasonId and lpsm.seasonStartYear = :startYear and lpsm.seasonEndYear = :endYear and vlm.villageLgdCode = :villageLgdCode")
	List<FarmlandPlotRegistry> findSecondTImeRejectedSurveyLandDetailsByVillageLgdCodeV2(Long seasonId, Integer startYear, Integer endYear,Long villageLgdCode);

	@Query(value = "SELECT fpr FROM LandParcelSurveyMaster lpsm" +
			"            inner join LandParcelSurveyDetail lpsd on lpsd.lpsmId = lpsm.lpsmId" +
			"            inner join FarmlandPlotRegistry fpr on fpr.farmlandPlotRegistryId = lpsm.parcelId" +
			"            inner join VillageLgdMaster vlm on vlm.villageLgdCode = fpr.villageLgdMaster.villageLgdCode" +
			"            where lpsd.reviewNo = 2 and lpsm.surveyOneStatus = 102 and lpsm.surveyTwoStatus = 102 and lpsm.isActive = true and lpsm.isDeleted = false" +
			" 			 and fpr.isActive = true and fpr.isDeleted = false" +
			"            and lpsm.seasonId = :seasonId and lpsm.seasonStartYear = :startYear and lpsm.seasonEndYear = :endYear and vlm.villageLgdCode = :villageLgdCode" +
			"			 and fpr.farmlandPlotRegistryId not in (:fprIds)")
	List<FarmlandPlotRegistry> findSecondTimeRejectedSurveyLandDetailsByVillageLgdCodeWithPlotExcludeV2(Long seasonId, Integer startYear, Integer endYear,Long villageLgdCode, List<Long> fprIds);

	@Query(value = "SELECT fpr.* FROM agri_stack.land_parcel_survey_master lpsm" +
			"            inner join agri_stack.land_parcel_survey_detail lpsd on lpsd.lpsm_id = lpsm.lpsm_id" +
			"            inner join agri_stack.farmland_plot_registry fpr on fpr.fpr_farmland_plot_registry_id = lpsm.parcel_id" +
			"            inner join agri_stack.village_lgd_master vlm on vlm.village_lgd_code = fpr.fpr_village_lgd_code" +
			"            where lpsd.review_no = 2 and lpsm.survey_one_status = 102 and lpsm.survey_two_status = 102 and lpsm.is_active = true and lpsm.is_deleted = false" +
			" 			 and fpr.is_active = true and fpr.is_deleted = false" +
			"            and lpsm.season_id = :seasonId and lpsm.season_start_year = :startYear and lpsm.season_end_year = :endYear and vlm.sub_district_lgd_code in (:subDistrictLgdCodes)" +
			"			 and vlm.village_name like CONCAT('%', :search, '%')", nativeQuery = true)
	Page<FarmlandPlotRegistry> findSecondTImeRejectedSurveyLandDetailsBySubDistrictLgdCodeWithSearch(Long seasonId, Integer startYear, Integer endYear, List<Long> subDistrictLgdCodes, String search, Pageable pageable);
}
