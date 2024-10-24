package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.amnex.agristack.entity.DepartmentMaster;
import com.amnex.agristack.entity.RoleMaster;
import com.amnex.agristack.entity.RolePatternMapping;

/**
 * @author kinnari.soni
 *
 * 28 Feb 2023 11:32:33 am
 */
public interface RolePatternMappingRepository extends JpaRepository<RolePatternMapping, Long> {

	/**
	 * @param roleList
	 * @return
	 */
	List<RolePatternMapping> findByRoleIn(List<RoleMaster> roleList);

	/**
	 * @param roleId
	 * @return
	 */
	RolePatternMapping findByRole(RoleMaster roleId);

	RolePatternMapping findByRoleAndIsDeletedFalse(RoleMaster roleId);

	/**
	 * @param assignStateLgdCode
	 * @return
	 */
	List<RolePatternMapping> findByAssignStateLgdCode(Long assignStateLgdCode);

	@Modifying
	@Transactional
	@Query(value="Update agri_stack.role_pattern_mapping set is_active=:isActive where role_pattern_mapping_id in (:roleIds)",nativeQuery=true)
	void UpdateSetIsActive(@Param("roleIds") List<Long> roleIds,@Param("isActive") Boolean isActive);

	List<RolePatternMapping> findAllByRole(RoleMaster roleId);

	List<RolePatternMapping> findAllByRoleAndIsDeletedFalse(RoleMaster roleId);

	RolePatternMapping findByRolePatternMappingId(Long rolePatternMappingId);

	@Query(value = "select  distinct(rolePatternMapping.role) from  RolePatternMapping rolePatternMapping where rolePatternMapping.assignStateLgdCode=:stateLgdCode")
	List<RoleMaster>findAllRoleMasterByStateLgdCode(@Param("stateLgdCode") Long stateLgdCode);
	String sd=	"select uvm.villageLgdMaster from UserVillageMapping uvm  where  uvm.userMaster.userId=:userId ";

	@Query(value = "select  distinct(rolePatternMapping.department) from  RolePatternMapping rolePatternMapping where rolePatternMapping.role.roleId=:roleId")
	List<DepartmentMaster>findAllDepartmentMasterByRoleId(@Param("roleId") Long roleId);

	@Query(value = "select  distinct(rolePatternMapping.department) from  RolePatternMapping rolePatternMapping where rolePatternMapping.assignStateLgdCode=:stateLgdCode")
	List<DepartmentMaster>findAllDepartmentMasterByStateLgdCode(@Param("stateLgdCode") Long stateLgdCode);

	@Query(value = "select  distinct(rolePatternMapping.role) from  RolePatternMapping rolePatternMapping where rolePatternMapping.department.departmentId=:departmentId")
	List<RoleMaster>findAllRoleMasterByDeparmentId(@Param("departmentId") Long departmentId);
	//String sd=	"select uvm.villageLgdMaster from UserVillageMapping uvm  where  uvm.userMaster.userId=:userId ";

	String territoryByRole = "SELECT TERRITORY_LEVEL FROM AGRI_STACK.ROLE_PATTERN_MAPPING WHERE ROLE_ID = :roleId "
			+ "AND IS_DELETED = FALSE AND IS_ACTIVE = TRUE GROUP BY TERRITORY_LEVEL" ;
	@Query(value = territoryByRole, nativeQuery= true)
	List<String> findTerritoryByrole(@Param("roleId") Long roleId);

	@Query(value="update agri_stack.role_pattern_mapping set is_deleted = :isDelete,is_active = :isActive,modified_by=:modifiedBy where role_id = :roleId", nativeQuery = true)
	@Modifying
	void updateRolePatternMappingStatusAndDelete(boolean isDelete,boolean isActive,Long roleId,String modifiedBy);

	@Query(value="update agri_stack.role_pattern_mapping set is_deleted = :isDelete,is_active = :isActive,modified_by=:modifiedBy where role_pattern_mapping_id = :rolePatternMappingId", nativeQuery = true)
	@Modifying
	void updateRolePatternMappingStatusByRolePatternMappingIdAndDelete(boolean isDelete,boolean isActive,Long rolePatternMappingId,String modifiedBy);
}
