/**
 * 
 */
package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.RoleMaster;
import com.amnex.agristack.entity.RoleMenuMasterMapping;

/**
 * @author majid.belim
 *
 */
public interface RoleMenuMasterMappingRepository extends JpaRepository<RoleMenuMasterMapping, String> {
	List<RoleMenuMasterMapping> findByRoleAndIsActiveAndIsDeleted(RoleMaster roleMaster, Boolean isActive,
			Boolean isDeleted);
	
	List<RoleMenuMasterMapping> findByRoleRoleIdAndIsActiveTrueAndIsDeletedFalse(Long userId);	
}
