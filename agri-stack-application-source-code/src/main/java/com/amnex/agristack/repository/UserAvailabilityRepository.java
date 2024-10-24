package com.amnex.agristack.repository;

import com.amnex.agristack.entity.DepartmentMaster;
import com.amnex.agristack.entity.UserAvailability;
import com.fasterxml.jackson.annotation.JacksonInject.Value;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserAvailabilityRepository extends JpaRepository<UserAvailability, Long> {

	List<UserAvailability> findByIsAvailable(Boolean issAvailable);
	
	List<UserAvailability> findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonEndYearAndSeasonIdAndUserId_UserVillageLGDCodeOrderByUserId_UserFullNameAsc(Long roleId,Integer status,Boolean issAvailable, String year, Long userId, Integer villageLgdCode);
//	List<UserAvailability> findByUserId_UserStatusAndIsAvailableAndSeasonEndYearAndSeasonIdAndUserId_UserVillageLGDCodeOrderByUserId_UserFullNameAsc(Integer status,Boolean issAvailable, String year, Long userId, Integer villageLgdCode);
	
//	List<UserAvailability> findByIsAvailableAndSeasonEndYearAndSeasonIdAndUserId_UserVillageLGDCodeOrderByUserId_UserFullNameAsc(Boolean issAvailable, String year, Long userId, Integer villageLgdCode);
	
	
	
//	List<UserAvailability> findByIsAvailableAndSeasonEndYearAndSeasonIdAndUserId_UserTalukaLGDCodeOrderByUserId_UserFullNameAsc(Boolean issAvailable, String year, Long userId, Integer talukaLgdCode);
	
//	List<UserAvailability> findByUserId_UserStatusAndIsAvailableAndSeasonEndYearAndSeasonIdAndUserId_UserTalukaLGDCodeOrderByUserId_UserFullNameAsc(Integer status,Boolean issAvailable, String year, Long userId, Integer talukaLgdCode);
	
	List<UserAvailability> findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonEndYearAndSeasonIdAndUserId_UserTalukaLGDCodeOrderByUserId_UserFullNameAsc(Long roleId,Integer status,Boolean issAvailable, String year, Long userId, Integer talukaLgdCode);
	
	List<UserAvailability> findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonStartYearAndSeasonEndYearAndSeasonIdAndUserId_UserTalukaLGDCodeInOrderByUserId_UserFullNameAsc(Long roleId,Integer status,Boolean issAvailable,String seasonStartYear, String seasonEndYear, Long seasonId, List<Integer> talukaLgdCode);
	
	

	@Query("select s from UserAvailability s where s.seasonId = :seasonId and " +
			"s.userId.roleId.roleId = :roleId  and " +
			"s.seasonStartYear >= :startYear and  s.seasonEndYear <= :endYear")
	List<UserAvailability> findBySeasonStartYearAndSeasonEndYearAndSeasonId(@Param("startYear") String startYear,
                                                                            @Param("endYear") String endYear, @Param("seasonId") Long seasonId,  @Param("roleId") Long roleId);

	Optional<UserAvailability> findByUserId(Long userId);

	@Query("select s from UserAvailability s where s.seasonId = :seasonId and s.seasonStartYear = :startYear and  s.seasonEndYear = :endYear and s.userId.userId =:userId order by s.Id desc")
	List<UserAvailability> findUserAvailability(@Param("startYear") String startYear,
                                                    @Param("endYear") String endYear, @Param("seasonId") Long seasonId, @Param("userId") Long userId);
	
	
	@Query("select s from UserAvailability s where s.seasonId = :seasonId and s.seasonStartYear >= :startYear and  s.seasonEndYear <= :endYear "
			+ "and s.userId.roleId.roleId = :roleId")
	List<UserAvailability> findAllVerifierSeasonStartYearAndSeasonEndYearAndSeasonId(@Param("startYear") String startYear,
                                                                            @Param("endYear") String endYear, @Param("seasonId") Long seasonId, @Param("roleId") Long roleId);


	@Query("select s from UserAvailability s where s.seasonId = :seasonId and s.seasonStartYear >= :startYear and  s.seasonEndYear <= :endYear "
			+ " and s.userId.roleId.roleId = :roleId "
			+ "and ((:departmentIds) is null or s.userId.departmentId.departmentId in (:departmentIds)) order by s.createdOn desc")
	List<UserAvailability> findAllVerifierSeasonStartYearAndSeasonEndYearAndSeasonIdAndDepartmentIds(
			@Param("startYear") String startYear,
            @Param("endYear") String endYear, @Param("seasonId") Long seasonId, @Param("roleId") Long roleId,@Param("departmentIds") List<Long> departmentIds);
	
	@Query("select s from UserAvailability s where s.seasonId = :seasonId and s.seasonStartYear >= :startYear and  s.seasonEndYear <= :endYear "
			+ " and s.userId.roleId.roleId in (:roleIds) "
			+ "and ((:departmentIds) is null or s.userId.departmentId.departmentId in (:departmentIds)) order by s.createdOn desc")
	List<UserAvailability> findAllVerifierSeasonStartYearAndSeasonEndYearAndRoleIdInAndSeasonIdAndDepartmentIds(
			@Param("startYear") String startYear,
            @Param("endYear") String endYear, @Param("seasonId") Long seasonId, @Param("roleIds") List<Long> roleIds,@Param("departmentIds") List<Long> departmentIds);

	@Query("select s from UserAvailability s where s.seasonStartYear = :startYear and  s.seasonEndYear = :endYear and s.userId.userId =:userId")
	List<UserAvailability> findAllUserAvailability(@Param("startYear") String startYear,
												@Param("endYear") String endYear, @Param("userId") Long userId);

	@Query("select s from UserAvailability s where s.seasonStartYear = :startYear and  s.seasonEndYear = :endYear and s.userId.userId =:userId and s.seasonId= :seasonId order by Id DESC")
	List<UserAvailability> findUserAvailabilityWithSeason(@Param("startYear") String startYear,
														  @Param("endYear") String endYear, @Param("userId") Long userId, @Param("seasonId") Long seasonId);

	@Query("select s from UserAvailability s where s.seasonStartYear = :startYear and  s.seasonEndYear = :endYear and s.userId.userId =:userId and s.isActive =:isActive and s.isDeleted = :isDeleted and s.isAvailable = :isAvailable order by s.Id desc")
	List<UserAvailability> findUserAvailabilityForUser(@Param("startYear") String startYear,
														  @Param("endYear") String endYear,@Param("isAvailable") Boolean isAvailable,@Param("userId") Long userId, @Param("isActive") Boolean isActive,  @Param("isDeleted") Boolean isDeleted);
	@Query("select s from UserAvailability s where s.seasonStartYear = :startYear and  s.seasonEndYear = :endYear and s.seasonId = :seasonId and s.userId.userId in (:userIds) and s.isActive =:isActive and s.isDeleted = :isDeleted order by s.Id desc")
	List<UserAvailability> findUserAvailabilityInSeason(@Param("startYear") String startYear,
														@Param("endYear") String endYear,@Param("seasonId") Long seasonId,@Param("userIds") List<Long> userIds, @Param("isActive") Boolean isActive,  @Param("isDeleted") Boolean isDeleted);
	@Query("select s from UserAvailability s where s.seasonId = :seasonId and s.seasonStartYear >= :startYear and  s.seasonEndYear <= :endYear "
			+ " and s.userId.roleId.roleId = :roleId order by s.createdOn desc")
	List<UserAvailability> findAllVerifierSeasonStartYearAndSeasonEndYearAndSeasonIdAndDepartmentIds(
            @Param("endYear") String endYear, @Param("seasonId") Long seasonId, @Param("roleId") Long roleId);
	
	
	List<UserAvailability> findByIsAvailableAndSeasonEndYearInAndIsActiveAndIsDeletedAndUserId_RoleId_RoleId(Boolean issAvailable,List<String> yearList,Boolean isActive,Boolean isDeleted,Long roleId);

	/**
	 * @param roleId
	 * @param value
	 * @param b
	 * @param string
	 * @param string2
	 * @param seasonId
	 * @param intValue
	 * @return
	 */
	List<UserAvailability> findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonStartYearAndSeasonEndYearAndSeasonIdAndUserId_VerificationVillageLGDCodeOrderByUserId_UserFullNameAsc(
			Long roleId, Integer value, boolean b, String string, String string2, Long seasonId, int intValue);
	
	
	List<UserAvailability> findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonStartYearAndSeasonEndYearAndSeasonIdAndUserId_UserIdInOrderByUserId_UserFullNameAsc(
			Long roleId, Integer value, boolean b, String string, String string2, Long seasonId, List<Long> userIds);
	
	
	
	List<UserAvailability> findByIsAvailableAndSeasonStartYearAndSeasonEndYearAndSeasonIdAndIsActiveAndIsDeletedAndUserId_RoleId_RoleIdAndUserId_UserStatusAndUserId_VerificationVillageLGDCodeOrderByUserId_UserFullNameAsc(
			 Boolean isAvailable,String startYear,String endYear,Long seasonId, Boolean isActive, Boolean isDeleted,Long roleId ,Integer userStatus,Integer verificationVillageLGDCode);

	List<UserAvailability> findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonStartYearAndSeasonEndYearAndSeasonIdAndUserId_UserTalukaLGDCodeInAndUserId_DepartmentIdOrderByUserId_UserFullNameAsc(
			Long roleId,Integer status,Boolean issAvailable,String seasonStartYear, String seasonEndYear, Long seasonId, List<Integer> talukaLgdCode, DepartmentMaster departmentMaster);
	
	
	List<UserAvailability> findByUserId_UserIdAndIsActiveAndIsDeleted(Long id,Boolean isActive,Boolean isDeleted);
	
	@Modifying
	@Transactional
	@Query(value = "update agri_stack.User_Availability set is_active = false, is_deleted = true where user_id=:user_id", nativeQuery = true)
	void clearUserDetailsById(Long user_id);
	
	@Query(value = "select agri_stack.fn_get_pr_surveyor_Detail(:seasonId,:startYear,:endYear,:codes)", nativeQuery = true)
	String getPrSurveyorDetail(Long seasonId, String startYear, String endYear,String codes);







	@Query(value = "select agri_stack.fn_get_all_verifier(:seasonId, :startYear, :endYear, :userId, :roleIds, :departmentIds)", nativeQuery = true)
	String getAllVerifierBySeasonIdAndSeasonYear(Long seasonId, String startYear, String endYear, Long userId, String roleIds, String departmentIds);






}
