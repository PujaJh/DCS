/**
 *
 */
package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amnex.agristack.entity.BoundaryMaster;

/**
 * @author kinnari.soni
 *
 */

public interface BoundaryMasterRepository extends JpaRepository<BoundaryMaster, Long> {

	/**
	 * @param parentBoundaryLevelCode
	 * @return
	 */
	@Query(value="select * from agri_stack.boundary_master where parent_boundary_level_code = :parentBoundaryLevelCode ", nativeQuery=true)
	List<BoundaryMaster> findByParentBoundaryLevelCode(@Param("parentBoundaryLevelCode") Long parentBoundaryLevelCode);

}
