package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.RoleMaster;

public interface RoleMasterRepository extends JpaRepository<RoleMaster, String> {

	List<RoleMaster> findByIsDefaultAndRoleNameInIgnoreCase(Boolean isDefault,List<String> roleNames);
	RoleMaster findByIsDefaultAndRoleName(Boolean isDefault,String roleNames);
	List<RoleMaster> findByIsDeletedAndIsDefault(Boolean isDeleted,Boolean isDefault);
	List<RoleMaster> findByIsDeleted(Boolean isDeleted);
	List<RoleMaster> findByIsDeletedAndIsDefaultAndCreatedBy(Boolean isDeleted,Boolean isDefault,String createdBy);

	Optional<RoleMaster> findByIsDeletedAndRoleId(Boolean isDeleted, Long roleId);

	Optional<RoleMaster> findByIsDeletedAndIsActiveAndRoleId(Boolean isDeleted, Boolean isActive, Long roleId);

	Optional<RoleMaster> findByCode(String code);

	List<RoleMaster> findByIsDeletedAndIsActiveAndIsDefaultOrderByRoleIdAsc(Boolean isDeleted,Boolean IsActive,Boolean isDefault);

	List<RoleMaster> findByPrefixIgnoreCaseAndStateLgdCodeAndIsActiveTrueAndIsDeletedFalse(String findByPrefix,Long stateLgdCode);

	Optional<RoleMaster> findByRoleNameIgnoreCaseAndStateLgdCodeAndIsActiveTrueAndIsDeletedFalse(String roleName,Long stateLgdCode);

	List<RoleMaster> findByIsDeletedAndStateLgdCode(Boolean isDeleted,Long stateLgdCode);

	/**
	 * @param assignStateLgdCode
	 * @return
	 */
	List<RoleMaster> findByStateLgdCode(Long assignStateLgdCode);

	/**
	 * @param roleId
	 * @return
	 */
	RoleMaster findByRoleId(Long roleId);
	/**
	 * @param role
	 * @return
	 */
	RoleMaster findByRoleIdAndIsDeletedFalse(Long role);

	RoleMaster findByCodeAndIsDefaultTrue(String code);
	/**
	 * @param prefix
	 * @return
	 */
	List<RoleMaster> findByPrefixIgnoreCaseAndIsDefaultTrueAndIsActiveTrueAndIsDeletedFalse(String prefix);
	/**
	 * @param roleName
	 * @return
	 */
	Optional<RoleMaster> findByRoleNameIgnoreCaseAndIsDefaultTrueAndIsActiveTrueAndIsDeletedFalse(String roleName);

	List<RoleMaster> findByIsDeletedAndStateLgdCodeInOrderByRoleIdDesc(Boolean isDeleted,List<Long> stateLgdCodes);
	
	List<RoleMaster> findByIsDeletedAndStateLgdCodeInAndCreatedByOrderByRoleIdDesc(Boolean isDeleted,List<Long> stateLgdCodes,String createdBy);

	List<RoleMaster> findByIsDeletedAndIsActiveAndStateLgdCodeIn(Boolean isDeleted,Boolean isActive,List<Long> stateLgdCodes);

	@Query(value="update agri_stack.role_master set is_deleted = :isDelete,is_active = :isActive,modified_by=:modifiedBy where role_id = :roleId", nativeQuery = true)
	@Modifying
	void updateRoleMasterStatusAndDelete(boolean isDelete,boolean isActive,Long roleId,String modifiedBy);
	
	@Query(value="SELECT * FROM agri_stack.role_master r WHERE r.code NOT IN ('SURVEYOR', 'VERIFIER') and is_active = true and is_deleted = false", nativeQuery = true)
    List<RoleMaster> findRolesExcludingSurveyorAndVerifierAndSuperAdmin();
}
