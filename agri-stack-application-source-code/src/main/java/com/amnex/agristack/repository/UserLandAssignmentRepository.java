package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.amnex.agristack.dao.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.UserLandAssignment;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserLandAssignmentRepository extends JpaRepository<UserLandAssignment, Long> {

	List<UserLandAssignment> findByVillageLgdCode_villageLgdCode(Long villageLgdCode);
	
	List<UserLandAssignment> findByYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeletedOrderByUser_UserFullNameAsc(String year,Long seasonId,Long villageLgdCode,Boolean isActive,Boolean isDeleted);
	
	List<UserLandAssignment> findByYearAndSeason_SeasonIdAndFarmlandIdInAndIsActiveAndIsDeletedOrderByUser_UserFullNameAsc
	(String year,Long seasonId,List<String> farmlandIdList,Boolean isActive,Boolean isDeleted);
	
	
	@Query(value = "select village_lgd_code_village_lgd_code as villageLgdCode, count(*) as totalCount from agri_stack.User_Land_Assignment group by  village_lgd_code_village_lgd_code",nativeQuery = true)
	List<FarmLandCount> getCountDetails();
	
	@Query(value = "select village_lgd_code_village_lgd_code as villageLgdCode, count(*) as totalCount from agri_stack.User_Land_Assignment where season_id=:seasonId and village_lgd_code_village_lgd_code in (:code) and year=:year  and is_active=true and is_deleted=false group by  village_lgd_code_village_lgd_code",nativeQuery = true)
	List<FarmLandCount> getCountDetailsByFilter(Long seasonId,List<Long> code,String year);
	
	@Query(value = "select village_lgd_code_village_lgd_code as villageLgdCode, count(*) as totalCount " + 
			"from agri_stack.User_Land_Assignment " + 
			"inner join agri_stack.village_lgd_master vlm on vlm.sub_district_lgd_code in(:code) and vlm.village_lgd_code=village_lgd_code_village_lgd_code " + 
			"where season_id=:seasonId and year=:year and is_active=true and is_deleted=false " + 
			"group by  village_lgd_code_village_lgd_code",nativeQuery = true)
	List<FarmLandCountReturn> getCountDetailsByFilter1(Long seasonId, List<Long> code, String year);
	
	@Query(value = "select ula.village_lgd_code_village_lgd_code as villageLgdCode, count(*) as totalCount " + 
			" from agri_stack.User_Land_Assignment ula " + 
			" inner join agri_stack.user_village_mapping uvm on uvm.user_id=:userId " + 
			" where ula.season_id=:seasonId and ula.village_lgd_code_village_lgd_code in (uvm.village_lgd_code) and ula.year=:year  and ula.is_active=true and ula.is_deleted=false " + 
			" group by  ula.village_lgd_code_village_lgd_code",nativeQuery = true)
	List<FarmLandCount> getCountDetailsByFilterByUserId(Long seasonId,Long userId,String year);
	
	@Query(value = "select user_id as userId,count(*) as totalCount  from agri_stack.User_Land_Assignment  where season_id=:seasonId and village_lgd_code_village_lgd_code= :code and year=:year and is_active=true and is_deleted=false group by user_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCodeWithfilter(Long seasonId, Long code, String year);
	
	@Query(value = "select user_id as userId,count(*) as totalCount  from agri_stack.User_Land_Assignment  "
			+ "where season_id=:seasonId  and starting_year=:startYear and ending_year=:endYear and user_id in (:userIds) "
			+ "and is_active=true and is_deleted=false group by user_id ",nativeQuery = true)
	List<UserLandCount> getCountDetailsByUserWithfilter(Long seasonId,Integer startYear,Integer endYear,List<Long> userIds);
	
	@Query(value = "select user_id as userId,count(*) as totalCount  from agri_stack.User_Land_Assignment  where season_id=:seasonId and village_lgd_code_village_lgd_code= :code and year=:year and status_id=:statusId and is_active=true and is_deleted=false group by user_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCodeWithfilterPending(Long seasonId,Long code,String year,Long statusId);
	
	
	@Query(value = "select user_id as userId,count(*) as totalCount  from agri_stack.User_Land_Assignment  where season_id=:seasonId and  year=:year and status_id=:statusId and user_id in (:userIds) and is_active=true and is_deleted=false group by user_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCodeWithfilterPendingUserId(Long seasonId,String year,Long statusId,List<Long> userIds);
	
	
	@Query(value = "select user_id as userId,count(*) as totalCount  from agri_stack.User_Land_Assignment where village_lgd_code_village_lgd_code=:code and is_active=true and is_deleted=false group by user_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCode(Long code);
	
	@Query(value = "select user_id as userId,count(*) as totalCount  from agri_stack.User_Land_Assignment where village_lgd_code_village_lgd_code=:code and  season_id=:seasonId and year=:year  and is_active=true and is_deleted=false group by user_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCode(Long code,Long seasonId,String year);
	@Query(value = "select user_id as userId,count(*) as totalCount  from agri_stack.User_Land_Assignment where user_id in (:userIds)  and is_active=true and is_deleted=false "
			+ " and season_id=:seasonId and starting_year=:startYear and ending_year=:endYear group by user_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCode(Set<Long> userIds,Long seasonId,Integer startYear,Integer endYear);
	
	@Query(value = "select village_lgd_code_village_lgd_code as villagelgdcode,count(distinct user_id) as totalCount "
			+ " from agri_stack.User_Land_Assignment where village_lgd_code_village_lgd_code in (:codes) and  "
			+ "season_id=:seasonId and year=:year  and is_active=true and is_deleted=false group by village_lgd_code_village_lgd_code ",nativeQuery = true)
	List<FarmLandCount> getUserCountDetailsByVillageCodeIn(List<Long> codes,Long seasonId,String year);
	
	
	@Query(value = "select user_id as userId,count(*) as totalCount  from agri_stack.User_Land_Assignment where village_lgd_code_village_lgd_code in (:vcodes) and  season_id=:seasonId and year=:year  and is_active=true and is_deleted=false group by user_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCodeIn(List<Long> vcodes,Long seasonId,String year);
	
	List<UserLandAssignment> findByUser_UserIdIn(List<Long> userIds);
	
	@Query(value = "select user_id as userId,count(*) as totalCount  from agri_stack.User_Land_Assignment where user_id in (:userIds)  and is_active=true and is_deleted=false group by user_id",nativeQuery = true)
	List<UserLandCount> getCountDetailsByVillageCode(Set<Long> userIds);

	@Query(value = "select u from UserLandAssignment u where u.user.userId = :userId and u.season.seasonId = :seasonId and year between :startYear and :endYear ")
	List<UserLandAssignment> findUserLandAssignment(@Param("userId") Long userId, @Param("seasonId") Long seasonId, @Param("startYear") String startYear,  @Param("endYear") String endYear);

	@Query(value = "select u from UserLandAssignment u where u.user.userId = :userId and u.season.seasonId = :seasonId and u.farmlandId = :farmlandId and u.startingYear = :startYear and u.endingYear = :endYear and u.isDeleted = false and u.isActive = true")
	Optional<UserLandAssignment> findUserLandAssignmentWithUserIdAndFarmlandId(@Param("userId") Long userId,@Param("farmlandId") String farmlandId, @Param("seasonId") Long seasonId, @Param("startYear") Integer startYear,  @Param("endYear") Integer endYear);

//	@Query(value = "select distinct user_id,village_lgd_code_village_lgd_code from agri_stack.User_Land_Assignment where village_lgd_code_village_lgd_code=:code ",nativeQuery = true)
//	List<UserLandCount> getUserCount(Long code);
	
	
//	List<UserLandAssignment> findByUser_UserIdInAndIsActiveAndIsDeleted(List<Long> ids, Boolean isActive,Boolean isDeleted);
	
	List<UserLandAssignment> findByUser_UserIdInAndIsActiveAndIsDeletedAndStatus_StatusCode(List<Long> ids, Boolean isActive,Boolean isDeleted, Integer statusCode);
	
	List<UserLandAssignment> findByFarmlandIdInAndIsActiveAndIsDeletedAndStatus_StatusCode(List<String> ids, Boolean isActive,Boolean isDeleted, Integer statusCode);
	
	
	List<UserLandAssignment> findByUser_UserIdInAndIsActiveAndIsDeletedAndStatus_StatusCodeAndSeason_SeasonIdAndYear(List<Long> ids, Boolean isActive,Boolean isDeleted, Integer statusCode,Long seasonId,String year);
	
//	List<UserLandAssignment> findByFarmlandIdInAndIsActiveAndIsDeletedAndStatus_StatusCodeAndSeason_SeasonIdAndYear(List<String> ids, Boolean isActive,Boolean isDeleted, Integer statusCode,Long seasonId,String year);
	List<UserLandAssignment> findByFarmlandIdInAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYear(List<String> ids, Boolean isActive,Boolean isDeleted, Long seasonId,Integer startYear,Integer endYear);
	
	
//	@Query(value = "select distinct flpr.FPR_FARMLAND_ID as farmlandId,flpr.FPR_FARMLAND_PLOT_REGISTRY_ID as farmlandPlotRegistryId, " + 
//			"flpr.FPR_LAND_PARCEL_ID as farmlandParcelId, " + 
//			"coalesce(lsm.survey_one_status,105) as surveyOneStatusCode, " + 
//			"COALESCE(statusone.status_name) as surveyOneStatus, " + 
//			"lsm.survey_two_status as surveyTwoStatusCode,  " + 
//			"COALESCE(statustwo.status_name) as surveyTwoStatus, " + 
//			"coalesce(lsm.survey_status,105) as mainStatusCode, " + 
//			"COALESCE(survey_status.status_name) as mainStatus," + 
//			"flpr.fpr_village_lgd_code  " + 
//			"from agri_stack.Land_Parcel_Survey_Master lsm " + 
//			"inner join  agri_stack.farmland_plot_registry flpr on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id " + 
//			"left join agri_stack.status_master as statusone on statusone.status_code = coalesce(lsm.survey_one_status,105)    " + 
//			"left join agri_stack.status_master as statustwo on statustwo.status_code = lsm.survey_two_status " + 
//			"left join agri_stack.status_master as verifierstatus on verifierstatus.status_code = lsm.verifier_status " + 
//			"left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)   " + 
//			"where season_start_year=:startYear and season_end_year=:endYear and season_id=:seasonId and lsm.is_active=true and lsm.is_deleted=false " + 
//			"and flpr.fpr_village_lgd_code= :villageCode",nativeQuery = true)
//	List<SurveyStatusDAO> getfarmLandwithsurveyStatus(Integer startYear,Integer endYear,Long seasonId,Long villageCode);
	
	@Query(value = "select distinct flpr.FPR_FARMLAND_ID as farmlandId,flpr.FPR_FARMLAND_PLOT_REGISTRY_ID as farmlandPlotRegistryId, " + 
			"coalesce(lsm.survey_one_status,105) as surveyOneStatusCode, " + 
			"COALESCE(statusone.status_name) as surveyOneStatus, " + 
			"lsm.survey_two_status as surveyTwoStatusCode, " + 
			"COALESCE(statustwo.status_name) as surveyTwoStatus, " + 
			"coalesce(lsm.survey_status,105) as mainStatusCode, " + 
			"COALESCE(survey_status.status_name) as mainStatus, " + 
			"flpr.fpr_village_lgd_code as village " + 
			"from  agri_stack.farmland_plot_registry flpr " + 
			"inner join agri_stack.User_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " + 
			"left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id  and lsm.season_start_year=:startYear and lsm.season_end_year=:endYear and lsm.season_id=:seasonId " + 
			"left join agri_stack.status_master as statusone on statusone.status_code = coalesce(lsm.survey_one_status,105)    " + 
			"left join agri_stack.status_master as statustwo on statustwo.status_code = lsm.survey_two_status " + 
			"left join agri_stack.status_master as verifierstatus on verifierstatus.status_code = lsm.verifier_status " + 
			"left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)   " + 
			"where ula.starting_year=:startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " + 
			"ula.village_lgd_code_village_lgd_code=:villageCode and ula.farmland_id in (:ids) ",nativeQuery = true)
	List<SurveyStatusDAO> getfarmLandwithsurveyStatus(Integer startYear, Integer endYear, Long seasonId, Long villageCode, List<String> ids);
	
	@Query(value = "select ula.village_lgd_code_village_lgd_code as villageCode,count(ula.user_id) as totalCount " + 
			"			from  agri_stack.farmland_plot_registry flpr " + 
			"			inner join agri_stack.User_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " + 
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.season_start_year=:startYear and lsm.season_end_year=:endYear and lsm.season_id=:seasonId " + 
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  " + 
			"			where ula.starting_year=:startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " + 
			"			ula.village_lgd_code_village_lgd_code in (:ids) " + 
			"			and  ula.is_active=true and ula.is_deleted=false and lsm.survey_status != 105" + 
			"			 group  by ula.village_lgd_code_village_lgd_code 	 ",nativeQuery = true)
	List<UserCountDAO> getAssignVillageCodesByFilter(Integer startYear, Integer endYear, Long seasonId, List<Long> ids);
	
	@Query(value = "select ula.user_id as userId,count(ula.user_id) as totalCount " + 
			"from  agri_stack.farmland_plot_registry flpr " + 
			"inner join agri_stack.User_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " + 
			"left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.season_start_year=:startYear and lsm.season_end_year=:endYear and lsm.season_id=:seasonId " + 
			"left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  " + 
			"where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " + 
			"ula.village_lgd_code_village_lgd_code=:villageCode " + 
			"and  ula.is_active=true and ula.is_deleted=false and lsm.survey_status != 105 " + 
			" group  by ula.user_id  ",nativeQuery = true)
	List<UserLandCount> getAssignLandListByFilter(Integer startYear,Integer endYear,Long seasonId,Long villageCode);
	
	
	@Query(value = "select ula.user_id as userId,count(ula.user_id) as totalCount " + 
			"from  agri_stack.farmland_plot_registry flpr " + 
			"inner join agri_stack.User_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " + 
			"left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id " + 
			"left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  " + 
			"where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " + 
			"user_id in(:userIds) " + 
			"and  ula.is_active=true and ula.is_deleted=false and lsm.survey_status != :statusApprove " + 
			" group  by ula.user_id  ",nativeQuery = true)
	List<UserLandCount> getAssignLandListByUser(Integer startYear,Integer endYear,Long seasonId,List<Long> userIds,int statusApprove);
	
	@Query(value = "select ula.user_id as userId,count(ula.user_id) as totalCount " + 
			"from  agri_stack.farmland_plot_registry flpr " + 
			"inner join agri_stack.User_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " + 
			"left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id " + 
			"left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  " + 
			"where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " + 
			"user_id in(:userIds) " + 
			"and  ula.is_active=true and ula.is_deleted=false and lsm.survey_status = :statusApprove " + 
			" group  by ula.user_id  ",nativeQuery = true)
	List<UserLandCount> getAproveSurveyListByUser(Integer startYear,Integer endYear,Long seasonId,List<Long> userIds,int statusApprove);
	
	
	
	@Query(value = "			select ula.user_id as userId,farmland_id farmLandId " + 
			"			from  agri_stack.farmland_plot_registry flpr  " + 
			"			inner join agri_stack.User_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " + 
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.season_start_year=:startYear and lsm.season_end_year=:endYear and lsm.season_id=:seasonId " + 
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  " + 
			"			where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " + 
			"			ula.village_lgd_code_village_lgd_code=:villageCode and ula.user_id in (:userIds) " + 
			"			and  ula.is_active=true and ula.is_deleted=false and lsm.survey_status is null	  ",nativeQuery = true)
	List<LandUserDAO> getfamlandIdByFilter(Integer startYear,Integer endYear,Long seasonId,Long villageCode,List<Long> userIds);
	
	
	List<UserLandAssignment> findByIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndFarmlandIdIn
	( Boolean isActive,Boolean isDeleted, Long seasonId,Integer startYear,Integer endYear,List<String> ids);

	
	@Query(value = "select distinct ula.created_By from  agri_stack.farmland_plot_registry flpr " + 
			"inner join agri_stack.User_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " + 
			"inner join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id " + 
			"where ula.starting_year=:startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and lsm.parcel_id in (:parcelId) " + 
			"and  ula.is_active=true and ula.is_deleted=false	  ",nativeQuery = true)
	List<String> getUserListByFilter(Integer startYear,Integer endYear,Long seasonId,Long parcelId);
	
	
	
	@Query(value = "select ula.village_lgd_code_village_lgd_code as villageCode,count(ula.verifier_id) as totalCount " + 
			"			from  agri_stack.farmland_plot_registry flpr " + 
			"			inner join agri_stack.Verifier_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " + 
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.season_id =:seasonId" + 
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.verifier_status,105)  " + 
			"			where ula.starting_year=:startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " + 
			"			ula.village_lgd_code_village_lgd_code in (:ids) " + 
			"			and  ula.is_active=true and ula.is_deleted=false and lsm.verifier_status != 105" +
			"			 group  by ula.village_lgd_code_village_lgd_code 	 ",nativeQuery = true)
	List<UserCountDAO> getSurveyorApproveCountByFilter(Integer startYear,Integer endYear,Long seasonId,List<Long> ids);

	@Query(value = "select ula.village_lgd_code_village_lgd_code as villageCode,count(ula.verifier_id) as totalCount " +
			"			from  agri_stack.farmland_plot_registry flpr " +
			"			inner join agri_stack.Verifier_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.season_id =:seasonId" +
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.verifier_status,105)  " +
			"			where ula.starting_year=:startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " +
			"			ula.village_lgd_code_village_lgd_code in (:ids) " +
			"			and  ula.is_active=true and ula.is_deleted=false and lsm.verifier_status != 105 and ula.is_second_time_rejected= true" +
			"			 group  by ula.village_lgd_code_village_lgd_code 	 ",nativeQuery = true)
	List<UserCountDAO> getSurveyorApproveCountByFilterWithSecondTimeRejected(Integer startYear,Integer endYear,Long seasonId,List<Long> ids);

	@Query(value = "select ula.farmland_id " +
			"			from  agri_stack.farmland_plot_registry flpr " +
			"			inner join agri_stack.Verifier_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.season_id =:seasonId" +
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.verifier_status,105)  " +
			"			where ula.starting_year=:startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " +
			"			ula.village_lgd_code_village_lgd_code in (:ids) and lsm.is_active = true and lsm.is_deleted = false" +
			"			and  ula.is_active=true and ula.is_deleted=false and lsm.verifier_status != 105 and ula.is_second_time_rejected= true" +
			"			 group  by ula.farmland_id",nativeQuery = true)
	List<String> getSurveyCompleteFarmlandByFilterWithSecondTimeRejected(Integer startYear,Integer endYear,Long seasonId,List<Long> ids);

	@Query(value = "select ula.village_lgd_code_village_lgd_code as villageCode,count(ula.verifier_id) as totalCount " +
			"			from  agri_stack.farmland_plot_registry flpr " +
			"			inner join agri_stack.Verifier_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.season_id =:seasonId" +
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.inspection_officer_status,105)  " +
			"			where ula.starting_year=:startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " +
			"			ula.village_lgd_code_village_lgd_code in (:ids) AND lsm.is_active = true and lsm.is_deleted = false" +
			"			and  ula.is_active=true and ula.is_deleted=false and lsm.inspection_officer_status != 105 and (ula.is_second_time_rejected!= true or ula.is_second_time_rejected is null)" +
			"			 group  by ula.village_lgd_code_village_lgd_code 	 ",nativeQuery = true)
	List<UserCountDAO> getSurveyorApproveCountByFilterWithoutSecondTimeRejected(Integer startYear,Integer endYear,Long seasonId,List<Long> ids);

	@Query(value = "select ula.farmland_id " +
			"			from  agri_stack.farmland_plot_registry flpr " +
			"			inner join agri_stack.Verifier_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " +
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id and lsm.season_id =:seasonId" +
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.inspection_officer_status,105)  " +
			"			where ula.starting_year=:startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " +
			"			ula.village_lgd_code_village_lgd_code in (:ids) and lsm.is_active = true and lsm.is_deleted = false " +
			"			and  ula.is_active=true and ula.is_deleted=false and lsm.inspection_officer_status != 105 and (ula.is_second_time_rejected!= true or ula.is_second_time_rejected is null)" +
			"			 group  by ula.farmland_id",nativeQuery = true)
	List<String> getSurveyCompleteFarmlandByFilterWithoutSecondTimeRejected(Integer startYear,Integer endYear,Long seasonId,List<Long> ids);

	@Query(value = "select ula.user_id as userId,ula.farmland_id as farmLandId " +
			"from  agri_stack.farmland_plot_registry flpr " +
			"inner join agri_stack.User_Land_Assignment ula on ula.farmland_id=flpr.fpr_farmland_id " +
			"left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id " +
			"left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  " +
			"where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " +
			"ula.user_id in (:userIds) " +
			"and  ula.is_active=true and ula.is_deleted=false and lsm.survey_status is null ",nativeQuery = true)
	List<LandUserDAO> getfamlandAssignmentForSurveyor(Integer startYear,Integer endYear,Long seasonId,List<Long> userIds);

	@Query(value = "SELECT DISTINCT u.villageLgdCode.villageLgdCode FROM UserLandAssignment u WHERE u.isActive = true AND u.isDeleted=false "
			+ " AND u.user.userId = :userId AND u.season.seasonId = :seasonId "
			+ " AND u.startingYear = :startingYear AND u.endingYear = :endingYear ")
	List<Long> getDistinctVillageLgdCodeByUserIdAndSeasonIdAndStartingYearAndEndingYear(Long userId, Long seasonId,
			Integer startingYear, Integer endingYear);
	

	@Query(value = "			select user_Land_Assignment_id " + 
			"			from  agri_stack.farmland_plot_registry flpr  " + 
			"			inner join agri_stack.User_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " + 
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id " + 
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  " + 
			"			where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " + 
			"           ula.user_id = :userId and  ula.is_active=true and ula.is_deleted=false and lsm.survey_status is null and flpr.fpr_farmland_plot_registry_id=:parcelId	  ",nativeQuery = true)
	List<Long> findUserLandAssignmentWithUserIdAndFarmlandIds(Integer startYear,Integer endYear,Long seasonId,Long userId,Long parcelId);

	@Query(nativeQuery = true,value = "select um.user_id userId,lpsm.lpsm_Id lpsdId, " + 
			"sum (case when satm.id = 1 then 1 else 0 end ) as naCount, " + 
			"sum (case when satm.id = 2 then 1 else 0 end ) as fallowCount, " + 
			"sum (case when satm.id = 3 then 1 else 0 end ) as cropCount " + 
			"from agri_stack.land_parcel_survey_master lpsm  " + 
			"inner join agri_stack.land_parcel_survey_detail lpsd on lpsm.lpsm_id = lpsd.lpsm_id " + 
			"inner join agri_Stack.land_parcel_survey_crop_detail cd on cd.lpsd_id= lpsd.lpsd_id " + 
			"LEFT join agri_Stack.crop_registry cr on cr.crop_id = cd.crop_id " + 
			"LEFT JOIN agri_stack.crop_type_master ctm on ctm.crop_type_id = cd.crop_type_id " + 
			"inner join agri_stack.farmland_plot_registry as flp on lpsm.parcel_id = flp.fpr_farmland_plot_registry_id " + 
			"inner join agri_stack.village_lgd_master village on flp.fpr_village_lgd_code = village.village_lgd_code " + 
			"inner join agri_stack.sub_district_lgd_master sub_district on sub_district.sub_district_lgd_code = village.sub_district_lgd_code " + 
			"INNER JOIN agri_stack.district_lgd_master dm on dm.district_lgd_code = sub_district.district_lgd_code " + 
			"INNER JOIN agri_stack.state_lgd_master sm on sm.state_lgd_code = dm.state_lgd_code " + 
			"INNER join agri_stack.user_master um on um.user_id = lpsd.survey_by " + 
			"INNER join agri_stack.survey_area_type_master satm on satm.id = cd.area_type_id " + 
			"where lpsm.is_active = true and lpsm.is_deleted = false " + 
			"AND lpsm.season_id = :seasonId " + 
			"and lpsm.season_start_year = :startYear " + 
			"and lpsm.season_end_year = :endYear " + 
			"and um.user_id in (:userId) " + 
			"and lpsd.status = 101 " + 
			"group by um.user_id,lpsm.lpsm_Id ")
	List<PrCountDAO> getPrPaymentCountsOld(Long seasonId,Integer startYear,Integer endYear,Long userId);
	

	@Query(nativeQuery = true,value = "select um.user_id userId,lpsd.lpsd_id lpsdId, " + 
			"sum(case when satm.id = 1 and cd.is_unutilized_area = true then 1 else 0 end ) as naCount, " + 
			"sum(case when satm.id = 2 and cd.is_waste_area = true then 1 else 0 end ) as fallowCount, " + 
			"sum(case when satm.id = 3 then 1 else 0 end ) as cropCount " + 
			"from agri_stack.land_parcel_survey_master lpsm " + 
			"inner join agri_stack.land_parcel_survey_detail lpsd on lpsm.lpsm_id = lpsd.lpsm_id " + 
			"inner join agri_Stack.land_parcel_survey_crop_detail cd on cd.lpsd_id= lpsd.lpsd_id " + 
			"LEFT join agri_Stack.crop_registry cr on cr.crop_id = cd.crop_id " + 
			"LEFT JOIN agri_stack.crop_type_master ctm on ctm.crop_type_id = cd.crop_type_id " + 
			"inner join agri_stack.farmland_plot_registry as flp on lpsm.parcel_id = flp.fpr_farmland_plot_registry_id " +
			"inner join agri_stack.village_lgd_master village on flp.fpr_village_lgd_code = village.village_lgd_code " + 
			"inner join agri_stack.sub_district_lgd_master sub_district on sub_district.sub_district_lgd_code = village.sub_district_lgd_code " + 
			"INNER JOIN agri_stack.district_lgd_master dm on dm.district_lgd_code = sub_district.district_lgd_code " + 
			"INNER JOIN agri_stack.state_lgd_master sm on sm.state_lgd_code = dm.state_lgd_code " +
			"INNER join agri_stack.user_master um on um.user_id = lpsd.survey_by " + 
			"INNER join agri_stack.survey_area_type_master satm on satm.id = cd.area_type_id " +
			"where lpsm.is_active = true and lpsm.is_deleted = false " + 
			"AND lpsm.season_id = :seasonId " + 
			"and lpsm.season_start_year = :startYear " + 
			"and lpsm.season_end_year = :endYear " + 
			"and um.user_id in (:userIds) " + 
			"and lpsd.status = 101 " + 
			"group by um.user_id,lpsd.lpsd_id  ")
	List<PrCountDAO> getPrPaymentCounts(Long seasonId,Integer startYear,Integer endYear,Set<Long> userIds);
	
	
	@Query(value = "			select user_Land_Assignment_id " + 
			"			from  agri_stack.farmland_plot_registry flpr  " + 
			"			inner join agri_stack.User_Land_Assignment ula on ula.farmland_id=fpr_farmland_id " + 
			"			left join agri_stack.Land_Parcel_Survey_Master lsm on flpr.fpr_farmland_plot_registry_id=lsm.parcel_id "
			+ " and lsm.season_start_year=:startYear and lsm.season_end_year=:endYear and lsm.season_id=:seasonId " + 
			"			left join agri_stack.status_master as survey_status on survey_status.status_code = coalesce(lsm.survey_status,105)  " + 
			"			where ula.starting_year= :startYear and ula.ending_year=:endYear and ula. season_id=:seasonId and " + 
			"           ula.user_id = :userId and  ula.is_active=true and ula.is_deleted=false and lsm.survey_status is null and flpr.fpr_farmland_plot_registry_id=:parcelId	  ",nativeQuery = true)
	List<Long> findUserLandAssignmentWithUserIdAndFarmlandIdV2(Integer startYear,Integer endYear,Long seasonId,Long userId,Long parcelId);

	@Modifying
	@Transactional
	@Query(value = "DELETE from agri_stack.user_Land_Assignment  ula where ula.village_lgd_code_village_lgd_code = :villageLgdCode and ula.farmland_id IN (:farmLandList)", nativeQuery = true)
	int deleteUserLandAssignmentByVillageAndFarmlandId(Long villageLgdCode, List<String> farmLandList);
	
	@Query(value = "select agri_stack.fn_calculate_pr_payment_details(:userId,:seasonId,:startyear,:endYear) ", nativeQuery = true)
	String getCalculatePrPaymentDetails(String userId, Integer seasonId, Integer startyear,Integer endYear);
	
	@Query(value = "select agri_stack.fn_get_unable_to_survey_details(:userId,:startyear,:endYear,:seasonId,:villageList,:page,:limit,:sortField,:sortOrder,:search) ", nativeQuery = true)
	String getUnableToSurveyDetails(Long userId, Integer startyear,Integer endYear, Integer seasonId, String villageList, Integer page, Integer limit,String sortField,String sortOrder,String search);
	
	@Modifying
	@Transactional
	@Query(value = "update agri_stack.unable_to_survey_details set is_active = false, is_deleted = true where lpsm_id = :lpsm_id", nativeQuery = true)
	void unableToSurveyEnable(Long lpsm_id);
	
//	UnableToSurveyDetails findByParcelIdAndSeasonStartYearAndSeasonEndYearAndSeasonId(Long parcelId,Integer seasonStartYear,Integer seasonEndYear,Long seasonId);
	
	@Query(value = "select agri_stack.fn_get_assign_data_by_village_lgd_code( :villageLgdCode, :seasonId,:startYear,:endYear) ", nativeQuery = true)
	String getAssignDataByvillagelgdcode(Long villageLgdCode,Long seasonId,Integer startYear,Integer endYear );
	
	@Query(value = "select agri_stack.get_Assign_Surveyor_data_by_village_lgd_code( :villageLgdCode, :seasonId,:startYear,:endYear) ", nativeQuery = true)
	String getAssignSurveyorDataByvillagelgdcode(Long villageLgdCode,Long seasonId,Integer startYear,Integer endYear );
}
