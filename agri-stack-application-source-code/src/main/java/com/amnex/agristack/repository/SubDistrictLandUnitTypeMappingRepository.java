/**
 *
 */
package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.SubDistrictLandUnitTypeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.SubDistrictLgdMaster;

/**
 * @author kinnari.soni
 *
 *         22 Feb 2023 3:23:06 pm
 */

public interface SubDistrictLandUnitTypeMappingRepository extends JpaRepository<SubDistrictLandUnitTypeMapping, Long> {

	/**
	 * @param subDistrictLgdMaster
	 * @return
	 */
	SubDistrictLandUnitTypeMapping findBySubDistrictLgdCode(SubDistrictLgdMaster subDistrictLgdMaster);

	@Query("SELECT e FROM SubDistrictLandUnitTypeMapping e WHERE e.subDistrictLgdCode IN (:subDistrictCodeList)")
	List<SubDistrictLandUnitTypeMapping> findBySubDistrictLgdCodeList(List<SubDistrictLgdMaster> subDistrictCodeList);
}
