package com.amnex.agristack.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.amnex.agristack.entity.DepartmentMaster;
import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.entity.RoleMaster;
import com.amnex.agristack.entity.RolePatternMapping;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.SubDistrictLgdMaster;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.entity.VillageLgdMaster;

public interface UserMasterRepository extends JpaRepository<UserMaster, String> {

	List<UserMaster> findByUserToken(String token);

	Optional<UserMaster> findByUserIdAndIsDeletedAndIsActive(Long userId, Boolean isDelete, Boolean isActive);

	Optional<UserMaster> findByUserIdAndIsDeleted(Long userId, Boolean isDelete);

	List<UserMaster> findByUserIdInAndIsDeletedAndIsActive(List<Long> userIds, Boolean isDelete, Boolean isActive);

	List<UserMaster> findByUserIdInAndIsDeletedAndIsActive(Set<Long> userIds, Boolean isDelete, Boolean isActive);

	Optional<UserMaster> findByUserNameAndIsDeletedAndIsActive(String userName, Boolean isDelete, Boolean isActive);

	Optional<UserMaster> findByUserMobileNumberAndIsDeletedAndIsActive(String userName, Boolean isDelete,
			Boolean isActive);

	Optional<UserMaster> findByRoleId_CodeAndIsDeletedAndIsActive(String code, Boolean isDelete, Boolean isActive);

	Optional<UserMaster> findByUserNameAndIsDeletedAndIsActiveAndRoleId_RoleId(String userName, Boolean isDelete,
			Boolean isActive, Long roleId);

	List<UserMaster> findByIsDeletedAndIsActiveAndUserStateLGDCodeAndRoleId_RoleIdOrderByUserIdDesc(Boolean isDelete,
			Boolean isActive, int stateLgdCode, Long roleId);

	Optional<UserMaster> findByUserName(String userName);

	Optional<UserMaster> findByUserFullName(String userFullName);

	Optional<UserMaster> findByUserMobileNumberAndUserAadhaarHash(String userMobileNumber, String userAadhaar);

	Optional<UserMaster> findByUserEmailAddress(String userEmailAddress);

	List<UserMaster> findByIsDeletedAndRoleId_IsDefaultOrderByCreatedOnDesc(Boolean isDelete, Boolean isDefault);

	public Page<UserMaster> findByIsDeletedAndRoleId_IsDefaultOrderByCreatedOnDesc(Boolean isDelete, Boolean isDefault,
			Pageable pageable);


	public Page<UserMaster> findByIsDeletedAndRoleId_RoleIdOrderByCreatedOnDesc(Boolean isDelete, Long roleId,
			Pageable pageable);
	
	public Page<UserMaster> findByIsDeletedAndRoleId_RoleIdAndUserNameContainsOrderByCreatedOnDesc(Boolean isDelete, Long roleId,String userName,
			Pageable pageable);

	public Page<UserMaster> findByIsDeletedAndRolePatternMappingId_TerritoryLevelOrderByCreatedOnDesc(Boolean isDelete,
			String territoryLevel, Pageable pageable);
	
	public Page<UserMaster> findByIsDeletedAndRolePatternMappingId_TerritoryLevelAndCreatedByOrderByCreatedOnDesc(Boolean isDelete,
			String territoryLevel,String createdBy, Pageable pageable);

	public Page<UserMaster> findByIsDeletedAndRoleId_RoleIdAndRolePatternMappingId_TerritoryLevelOrderByCreatedOnDesc(
			Boolean isDelete, Long roleId, String territoryLevel, Pageable pageable);
	
	public Page<UserMaster> findByIsDeletedAndRoleId_RoleIdAndRolePatternMappingId_TerritoryLevelAndCreatedByOrderByCreatedOnDesc(
			Boolean isDelete, Long roleId, String territoryLevel,String createdBy, Pageable pageable);
	
	

	public Page<UserMaster> findByIsDeletedAndRoleId_IsDefaultAndCreatedByOrderByCreatedOnDesc(Boolean isDelete,
			Boolean isDefault, String createdBy, Pageable pageable);

	public Page<UserMaster> findByIsDeletedAndRoleId_IsDefaultAndCreatedBy(Boolean isDelete, Boolean isDefault,
			String createdBy, Pageable pageable);

	public Page<UserMaster> findByIsDeletedAndRoleId_IsDefaultAndCreatedByAndUserNameContainsOrderByCreatedOnDesc(
			Boolean isDelete, Boolean isDefault, String createdBy, String userName, Pageable pageable);

	Optional<UserMaster> findByUserId(Long userId);

	Optional<UserMaster> findByIsDeletedAndUserId(Boolean isDelete, Long userId);

	@Query(value = "select u from UserMaster u where u.isActive = true and u.isDeleted = false and u.roleId= :role and u.roleId.isDefault = :isDefault")
	List<UserMaster> findAllActiveUserByRole(@Param("role") RoleMaster role, @Param("isDefault") Boolean isDefault);

	@Query(value = "select u from UserMaster u where u.isActive = true and u.isDeleted = false and (u.userMobileNumber=:source or u.userName= :source or u.userEmailAddress=:source) ")
	Optional<UserMaster> getByUser(@Param("source") String source);

	@Query(value = "select u from UserMaster u where u.isDeleted = false and (u.userMobileNumber=:source or u.userName= :source or u.userEmailAddress=:source or u.userFullName=:source) ")
	Optional<UserMaster> getUserWithoutStatus(@Param("source") String source);

	Optional<UserMaster> findByUserMobileNumber(String userMobileNumber);

	Optional<UserMaster> findByGovernmentId(String governmentId);

	List<UserMaster> findByUserIdInOrderByUserFullNameAsc(Set<Long> userids);

	List<UserMaster> findByUserAadhaarHash(String userAadhaarHash);

	@Query(value = "select count(u.userId) from UserMaster u" + " where u.departmentId.departmentId = :departmentId"
			+ " and u.userVillageLGDCode = :userVillageLGDCode")
	Integer getUserCountForDepartmentAndVillage(@Param("departmentId") Long departmentId,
			@Param("userVillageLGDCode") Integer villageLGDCode);

	@Query(value = " SELECT COALESCE(max((string_to_array (user_name,'_',''))[3]\\:\\:integer),0) as count "
			+ "from agri_stack.user_master um "
			+ "inner join agri_stack.department_master dm on dm.department_id = um.department_id "
			+ "where user_name ilike %:departmentCode% and user_name ilike %:userVillageLGDCode% and um.role_id = :roleId ", nativeQuery = true)
	Integer getUserCountForDepartmentAndVillageV2(@Param("departmentCode") String departmentCode,
			@Param("userVillageLGDCode") Integer villageLGDCode, @Param("roleId") Long roleId);

	UserMaster findByUserEmailAddressAndIsDeletedAndIsActive(String verificationSource, boolean b, boolean b1);
	
	UserMaster findByUserEmailAddressOrUserMobileNumberOrUserNameAndIsDeletedAndIsActive(String verificationSource, String userMobileNumber, String userId, boolean b, boolean b1);

	UserMaster findByUserMobileNumberAndIsDeletedAndIsActive(String verificationSource, boolean b, boolean b1);

	Optional<UserMaster> findByIsDeletedAndIsActiveAndUserMobileNumberAndRoleId_Code(boolean b, boolean b1,
			String verificationSource, String code);

	Optional<UserMaster> findByUserIdAndIsDeletedAndIsActive(Long userId, boolean b, boolean b1);

	UserMaster findByUserMobileNumberAndIsDeletedAndIsActiveAndRoleId_Code(String verificationSource, boolean b,
			boolean b1, String code);

	List<UserMaster> findAllByIsDeletedAndIsActiveAndRoleId_RoleIdAndUserPrIdNotNullOrderByCreatedOnDesc(
			Boolean isDelete, Boolean isActive, Long roleId);

	/// added below function to check Aadhar , email and Mobile number avaibility
	/// except mobile number for given user Id

	/**
	 *
	 * @param state
	 * @return
	 */
	UserMaster findByUserStateLGDCode(StateLgdMaster state);

	/**
	 *
	 * @param districtList
	 * @return
	 */
	List<UserMaster> findByUserDistrictLGDCodeIn(List<DistrictLgdMaster> districtList);

	/**
	 *
	 * @param subDistrictList
	 * @return
	 */
	List<UserMaster> findByUserTalukaLGDCodeIn(List<SubDistrictLgdMaster> subDistrictList);

	/**
	 *
	 * @param villageList
	 * @return
	 */
	List<UserMaster> findByUserVillageLGDCodeIn(List<VillageLgdMaster> villageList);

	@Modifying
	@Transactional
	@Query(value = "Update agri_stack.user_master set is_active=:isActive where user_id in (:userIds)", nativeQuery = true)
	void UpdateUsersSetIsActive(@Param("userIds") List<Long> userIds, @Param("isActive") Boolean isActive);

	/**
	 * @param state
	 * @return
	 */
	List<UserMaster> findByDepartmentIdAndRoleIdAndCreatedByAndUserStateLGDCodeInOrderByUserIdDesc(
			DepartmentMaster departmentId, RoleMaster role, String createdBy, List<Integer> state);

	/**
	 *
	 * @param createdBy
	 * @return
	 */
	List<UserMaster> findByCreatedByOrderByUserIdDesc(String createdBy);

	/**
	 * @param department
	 * @param role
	 * @param string
	 * @param assignDistrictLGDCodes
	 * @return
	 */
	List<UserMaster> findByDepartmentIdAndRoleIdAndCreatedByAndUserDistrictLGDCodeInOrderByUserIdDesc(
			DepartmentMaster department, RoleMaster role, String string, List<Integer> assignDistrictLGDCodes);

	/**
	 * @param department
	 * @param string
	 * @param assignDistrictLGDCodes
	 * @return
	 */
	List<UserMaster> findByDepartmentIdAndCreatedByAndUserDistrictLGDCodeNotInOrderByUserIdDesc(
			DepartmentMaster department, String string, List<Integer> assignDistrictLGDCodes);

	/**
	 * @param department
	 * @param role
	 * @param string
	 * @param assignSubDistrictLGDCodes
	 * @return
	 */
	List<UserMaster> findByDepartmentIdAndRoleIdAndCreatedByAndUserTalukaLGDCodeInOrderByUserIdDesc(
			DepartmentMaster department, RoleMaster role, String string, List<Integer> assignSubDistrictLGDCodes);

	/**
	 * @param department
	 * @param string
	 * @param assignSubDistrictLGDCodes
	 * @return
	 */
	List<UserMaster> findByDepartmentIdAndCreatedByAndUserTalukaLGDCodeNotInOrderByUserIdDesc(
			DepartmentMaster department, String string, List<Integer> assignSubDistrictLGDCodes);

	/**
	 * @param department
	 * @param role
	 * @param string
	 * @param assignVillageLGDCodes
	 * @return
	 */
	List<UserMaster> findByDepartmentIdAndRoleIdAndCreatedByAndUserVillageLGDCodeInOrderByUserIdDesc(
			DepartmentMaster department, RoleMaster role, String string, List<Integer> assignVillageLGDCodes);

	/**
	 * @param department
	 * @param string
	 * @param assignVillageLGDCodes
	 * @return
	 */
	List<UserMaster> findByDepartmentIdAndCreatedByAndUserVillageLGDCodeNotInOrderByUserIdDesc(
			DepartmentMaster department, String string, List<Integer> assignVillageLGDCodes);

	/**
	 * @param role
	 * @return
	 */
	List<UserMaster> findByRoleId(RoleMaster role);

	/**
	 * @param department
	 * @param role
	 * @return
	 */
	List<UserMaster> findByDepartmentIdAndRoleIdOrderByUserIdDesc(DepartmentMaster department, RoleMaster role);

	/// added below function to check Aadhar , email and Mobile number avaibility
	/// except mobile number for given user Id

	@Query(value = "select u from UserMaster u where u.userAadhaarHash = :userAadhaarHash and "
			+ " (:userId is null or cast(:userId as integer) = 0 or u.userId <> :userId)")
	Optional<UserMaster> findByUserAadhaarHashByUserId(String userAadhaarHash, Long userId);

	@Query(value = "select u from UserMaster u where u.userMobileNumber = :userMobileNumber and "
			+ " (:userId is null or cast(:userId as integer) = 0 or u.userId <> :userId)")
	Optional<UserMaster> findByUserMobileNumberByUserId(String userMobileNumber, Long userId);

	@Query(value = "select u from UserMaster u where u.userEmailAddress = :userEmailAddress and "
			+ " (:userId is null or cast(:userId as integer) = 0 or u.userId <> :userId)")
	Optional<UserMaster> findByUserEmailAddressByUserId(String userEmailAddress, Long userId);

	@Query(value = "select u from UserMaster u where u.governmentId = :governmentId and "
			+ " (:userId is null or cast(:userId as integer) = 0 or u.userId <> :userId)")
	Optional<UserMaster> findByGovernmentIdByUserId(String governmentId, Long userId);

	@Query(value = "select u from UserMaster u where u.isActive = true and u.isDeleted = false and "
			+ " ((:departmentId) is null or u.departmentId.departmentId =:departmentId) and "
			+ "((:userStateLGDCode) is null OR u.userStateLGDCode in (:userStateLGDCode) ) and "
			+ "((:userDistrictLGDCode) is null OR u.userDistrictLGDCode in (:userDistrictLGDCode) ) and "
			+ "((:userTalukaLGDCode) is null OR u.userTalukaLGDCode in ( :userTalukaLGDCode) ) and "
			+ "((:userVillageLGDCode) is null OR u.userVillageLGDCode in (:userVillageLGDCode) ) AND u.roleId = :roleId AND u.createdOn > :createdOn  order by u.userId Asc")

	List<UserMaster> findAllByBoundarySelection(@Param("departmentId") Long departmentId,
			@Param("userVillageLGDCode") List<Integer> villageLGDCode,
			@Param("userTalukaLGDCode") List<Integer> userTalukaLGDCode,
			@Param("userDistrictLGDCode") List<Integer> userDistrictLGDCode,
			@Param("userStateLGDCode") List<Integer> userStateLGDCode,
			@Param("roleId") RoleMaster roleId,
			@Param("createdOn") Date createdOn);

	public static final String FIND_PROJECTS = "select userId, userName from UserMaster u where u.isActive = true and u.isDeleted = false and userName != null";

	@Query(value = FIND_PROJECTS)
	public List<Object[]> getAllActiveUserPlainList();

	@Query(value = "SELECT u.user_id, u.user_first_name, u.user_last_name, CASE WHEN u.user_pr_id IS NULL THEN u.user_name ELSE u.user_pr_id END "
			+ " FROM agri_stack.user_master u LEFT JOIN agri_stack.role_master r ON r.role_id = u.role_id "
			+ " WHERE u.is_active = true and u.is_deleted = false and r.code = :roleCode "
			+ " and u.user_id NOT IN (:userIds) and u.user_village_lgd_code IN "
			+ " (SELECT DISTINCT m1.village_lgd_code FROM agri_stack.user_village_mapping m1 "
			+ "		WHERE m1.user_id = :loggedInUserId)"
			+ " ORDER BY u.user_first_name, u.user_last_name ", nativeQuery = true)
	public List<Object[]> findAllVerifiers(String roleCode, List<Long> userIds, Long loggedInUserId);

	/**
	 * @param roles
	 * @return
	 */
	List<UserMaster> findByRoleIdIn(List<RoleMaster> roles);

	/**
	 * @param roleId
	 * @return
	 */
	List<UserMaster> findByRoleIdAndIsDeletedFalse(RoleMaster roleId);

	/**
	 *
	 * @param rolePatternMappingId
	 * @return
	 */
	List<UserMaster> findByRolePatternMappingIdOrderByUserIdDesc(RolePatternMapping rolePatternMappingId);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "Update agri_stack.user_master set is_active=:isActive, is_deleted=:isDelete where user_id in (:userIds)", nativeQuery = true)
	void UpdateUsersSetIsActiveAndIsDelete(@Param("userIds") List<Long> userIds, @Param("isActive") Boolean isActive,
			@Param("isDelete") Boolean iseDelete);

	List<UserMaster> findByRolePatternMappingIdAndIsActiveTrueOrderByUserIdDesc(
			RolePatternMapping rolePatternMappingId);

	List<UserMaster> findByDepartmentIdAndRoleIdAndRolePatternMappingId_TerritoryLevelOrderByUserIdDesc(
			DepartmentMaster department, RoleMaster role, String territorylevel);

	List<UserMaster> findByDepartmentIdAndRoleIdAndRolePatternMappingId_TerritoryLevelAndIsActiveTrueOrderByUserIdDesc(
			DepartmentMaster department, RoleMaster role, String territorylevel);

	List<UserMaster> findByDepartmentIdAndRoleIdAndIsActiveTrueAndIsDeletedFalse(DepartmentMaster department,
			RoleMaster role);
	
	List<UserMaster> findByDepartmentId_departmentIdInAndRoleIdAndIsActiveTrueAndIsDeletedFalse(List<Long> ids,
			RoleMaster role);

	Optional<UserMaster> findByUserMobileNumberAndRoleIdRoleId(String mobileNo, Long roleId);

	List<UserMaster> findByRoleId_RoleIdAndUserStateLGDCodeInAndIsDeletedFalseAndIsActiveTrue(Long roleId,
			List<Integer> stateLgdCodes);

	List<UserMaster> findByRoleId_RoleIdAndUserDistrictLGDCodeInAndIsDeletedFalseAndIsActiveTrue(Long roleId,
			List<Integer> stateLgdCodes);

	List<UserMaster> findByRoleId_RoleIdAndUserTalukaLGDCodeInAndIsDeletedFalseAndIsActiveTrue(Long roleId,
			List<Integer> stateLgdCodes);

	List<UserMaster> findByRoleId_RoleIdAndUserVillageLGDCodeInAndIsDeletedFalseAndIsActiveTrue(Long roleId,
			List<Integer> stateLgdCodes);

	Page<UserMaster> findByRoleId_RoleNameAndIsDeletedAndRoleId_IsDefaultAndCreatedByAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrderByCreatedOnDesc(
			String rolename, Boolean isDelete, Boolean isDefault, String createdBy, String userName,
			String userFullName, String userEmailAddress, String userMobileNumber, Pageable pageable);

	Page<UserMaster> findByRoleId_RoleNameAndRolePatternMappingId_TerritoryLevelAndIsDeletedAndRoleId_IsDefaultAndCreatedByAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrderByCreatedOnDesc(
			String rolename,String boundaryType, Boolean isDelete, Boolean isDefault, String createdBy, String userName,
			String userFullName, String userEmailAddress, String userMobileNumber, Pageable pageable);

	Page<UserMaster> findByIsDeletedAndRoleId_IsDefaultAndCreatedByAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrRoleId_RoleNameIgnoreCaseContainsOrderByCreatedOnDesc(
			Boolean isDelete, Boolean isDefault, String createdBy, String userName, String userFullName,
			String userEmailAddress, String userMobileNumber, String rolename, Pageable pageable);

	List<UserMaster> findByImeiNumberAndUserIdNot(String imeiNumber, Long userId);

	@Query(value = "select u from UserMaster u where (u.userName = :source or u.userMobileNumber = :source or u.userEmailAddress = :source) ")
	Optional<UserMaster> getByUserCredentials(@Param("source") String source);

	// Page<UserMaster>
	// findByIsDeletedAndRoleId_IsDefaultAndCreatedByAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrRoleId_RoleNameIgnoreCaseContainsOrderByCreatedOnDesc
	// (Boolean isDelete, Boolean isDefault,String createdBy,
	// String userName,String userFullName,String userEmailAddress,String
	// userMobileNumber,String rolename,Pageable pageable);

	@Query(value = "SELECT user_id from agri_stack.user_master where role_id = :roleId", nativeQuery = true)
	List<Long> findUserIdByRoleId(Long roleId);

	@Query(value = "SELECT user_id from agri_stack.user_master where role_pattern_mapping_id = :rolePatterMappingId", nativeQuery = true)
	List<Long> findUserIdByRolePatternMappingId(Long rolePatterMappingId);

	@Query(value = "UPDATE agri_stack.user_master set is_deleted = :isDelete,is_active = :isActive,modified_by=:modifiedBy where user_id in (:userIds)", nativeQuery = true)
	@Modifying
	void updateUserStatusAndDelete(boolean isDelete, boolean isActive, List<Long> userIds, String modifiedBy);

	List<UserMaster> findByIsDeletedAndRoleId_IsDefaultAndRoleId_RoleIdAndDepartmentId_DepartmentTypeIsNot(
			Boolean aFalse, Boolean aTrue, Long roleId, Integer value);
	
	public Page<UserMaster> findByIsDeletedAndRoleId_RoleId(Boolean isDelete, Long roleId,
			Pageable pageable);
	public Page<UserMaster> findByIsDeletedAndRoleId_RoleIdAndCreatedBy(Boolean isDelete, Long roleId,String createdBy,
			Pageable pageable);
	
	
	 @Query(value ="SELECT u FROM UserMaster u join u.roleId r where u.isDeleted = :isDelete and r.roleId = :roleId and (LOWER(u.userName) LIKE LOWER(CONCAT('%', :userName, '%')) or LOWER(u.userFullName) LIKE LOWER(CONCAT('%', :userFullName, '%')) or LOWER(u.userEmailAddress) LIKE LOWER(CONCAT('%', :userEmailAddress, '%'))  or LOWER(u.userMobileNumber) LIKE LOWER(CONCAT('%', :userMobileNumber, '%')) or LOWER(r.roleName) LIKE LOWER(CONCAT('%', :roleName, '%'))) ")
	public Page<UserMaster> findByIsDeletedAndRoleId_RoleIdAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrRoleId_RoleNameIgnoreCaseContains(@Param("isDelete") Boolean isDelete, @Param("roleId") Long roleId,@Param("userName") String userName, @Param("userFullName") String userFullName,@Param("userEmailAddress") String userEmailAddress, @Param("userMobileNumber") String userMobileNumber, @Param("roleName") String roleName,
			Pageable pageable);
	 
	 
	 
	 @Query(value ="SELECT u FROM UserMaster u join u.roleId r where u.isDeleted = :isDelete and r.roleId = :roleId and u.createdBy=:createdBy and (LOWER(u.userName) LIKE LOWER(CONCAT('%', :userName, '%')) or LOWER(u.userFullName) LIKE LOWER(CONCAT('%', :userFullName, '%')) or LOWER(u.userEmailAddress) LIKE LOWER(CONCAT('%', :userEmailAddress, '%'))  or LOWER(u.userMobileNumber) LIKE LOWER(CONCAT('%', :userMobileNumber, '%')) or LOWER(r.roleName) LIKE LOWER(CONCAT('%', :roleName, '%'))) ")
		public Page<UserMaster> findByIsDeletedAndRoleId_RoleIdAndCreatedByAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrRoleId_RoleNameIgnoreCaseContains(@Param("isDelete") Boolean isDelete, @Param("roleId") Long roleId,@Param("createdBy") String createdBy,@Param("userName") String userName, @Param("userFullName") String userFullName,@Param("userEmailAddress") String userEmailAddress, @Param("userMobileNumber") String userMobileNumber, @Param("roleName") String roleName,
				Pageable pageable);
	 
		@Query(value = "SELECT u FROM UserMaster u " +
			       "JOIN u.roleId r " +
			       "WHERE u.isDeleted = :isDelete " +
			       "AND r.isDefault = :isDefault " +
			       "AND u.createdBy = :createdBy " +
			       "AND (LOWER(u.userName) LIKE LOWER(CONCAT('%', :userName, '%')) " +
			       "OR LOWER(u.userFullName) LIKE LOWER(CONCAT('%', :userFullName, '%')) " +
			       "OR LOWER(u.userEmailAddress) LIKE LOWER(CONCAT('%', :userEmailAddress, '%')) " +
			       "OR LOWER(u.userMobileNumber) LIKE LOWER(CONCAT('%', :userMobileNumber, '%')) " +
			       "OR LOWER(r.roleName) LIKE LOWER(CONCAT('%', :roleName, '%')))")
		Page<UserMaster> findByIsDeletedAndRoleId_IsDefaultAndCreatedByAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrRoleId_RoleNameIgnoreCaseContainsOrderByCreatedOnDescs(
				@Param("isDelete") Boolean isDelete, @Param("isDefault") Boolean isDefault, @Param("createdBy") String createdBy, @Param("userName") String userName, @Param("userFullName") String userFullName,
				@Param("userEmailAddress") String userEmailAddress, @Param("userMobileNumber") String userMobileNumber, @Param("roleName") String roleName, Pageable pageable);
	
	public Page<UserMaster> findByIsDeletedAndRoleId_RoleIdAndUserNameIgnoreCaseContainingAndUserFullNameIgnoreCaseContainingAndUserEmailAddressIgnoreCaseContainingAndUserMobileNumberIgnoreCaseContainingAndRoleId_RoleNameIgnoreCaseContaining(Boolean isDelete, Long roleId,String userName, String userFullName, String userEmailAddress, String userMobileNumber, String roleName,
			Pageable pageable);
	
	public Page<UserMaster> findByIsDeletedAndRoleId_RoleIdAndUserNameContains(Boolean isDelete, Long roleId,String userName,
			Pageable pageable);
	@Query(value = "select agri_stack.fn_user_assign_village_by_territory(:data)", nativeQuery = true)
	String userAssignVillageByTerritory(String data);
	
	
	@Query(value = "select agri_stack.fn_get_user_count_filter_v3(:userId, :userType, :startDate, :endDate)", nativeQuery = true)
	String getUserCount(Long userId, String userType, Date startDate, Date endDate );
	
	
	@Query(value = "select agri_stack.fn_get_user_details_filter(:userId, :userType,:page,:limit,:sortField,:sortOrder,:search,:roleId)", nativeQuery = true)
	String getUserDetailsFilter(Long userId,String userType,Integer page, Integer limit,String sortField,String sortOrder,String search,Long roleId);
	

	@Query(value = "select agri_stack.fn_get_user_details_count_filter(:userId, :userType,:search,:roleId)", nativeQuery = true)
	String getUserDetailsCountFilter(Long userId,String userType,String search,Long roleId);

	@Query(value = "select agri_stack.FN_UPDATE_ROLE_PATTERN_STATUS(:isDeleted,:isActive,:userId,:userIds,:roleId)", nativeQuery = true)
	String updateRolePatternStatus(Boolean isDeleted,Boolean isActive,Long userId,String userIds,Long roleId);


	@Query(value = "select agri_stack.fn_mis_get_all_surveyor_list(:userId,:departmentIds,:seasonId,:startYear,:endYear) ", nativeQuery = true)
	String getAllSurveyorV2(Long userId, String departmentIds, Long seasonId, Integer startYear, Integer endYear);

	@Query(value = "SELECT agri_stack.fn_get_all_surveyor_with_filter(:userId,:pageNo,:pageSize,:pageSortField,:pageSortOrder,:pageSearch,:stateLgdCode,:districtLgdCode,:talukLgdCode,:villageLgdCode,:startYear,:endYear,:seasonId,:departmentIds,:status)", nativeQuery = true)
	public String getAllSurveyorWithFilter(Long userId,Integer pageNo,Integer pageSize,String pageSortField,String pageSortOrder,String pageSearch,@Param("stateLgdCode") String stateLgdCode,  @Param("districtLgdCode") String districtLgdCode,
			@Param("talukLgdCode") String talukLgdCode, @Param("villageLgdCode") String villageLgdCode,String startYear, String endYear, Integer seasonId, String departmentIds, Integer status
			);
}
