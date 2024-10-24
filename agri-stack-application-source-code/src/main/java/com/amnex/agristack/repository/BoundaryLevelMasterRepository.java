/**
 *
 */
package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.BoundaryLevelMaster;

/**
 * @author kinnari.soni
 *
 */
public interface BoundaryLevelMasterRepository extends JpaRepository<BoundaryLevelMaster, Long>{

	/**
	 * @param boundaryLevelCode
	 * @return
	 */
	BoundaryLevelMaster findByBoundaryLevelCode(Long boundaryLevelCode);

}
