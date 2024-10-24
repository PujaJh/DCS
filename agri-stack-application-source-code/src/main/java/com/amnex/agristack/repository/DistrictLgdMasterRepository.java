/**
 *
 */
package com.amnex.agristack.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.VillageLgdMaster;


/**
 * @author kinnari.soni
 *
 */
public interface DistrictLgdMasterRepository extends JpaRepository<DistrictLgdMaster, Long> {

	/**
	 * @param districtLgdCode
	 * @return
	 */
	DistrictLgdMaster findByDistrictLgdCode(Long districtLgdCode);

	/**
	 * @param findByStateLgdCode
	 * @return
	 */
	List<DistrictLgdMaster> findByStateLgdCode(StateLgdMaster findByStateLgdCode);

	/**
	 * @param stateLgdCodes
	 * @return
	 */
	@Query("select d from DistrictLgdMaster d where  d.stateLgdCode.stateLgdCode in (:stateLgdCodes) order by d.districtName ASC" )
	List<DistrictLgdMaster> findAllByStateLgdCode(@Param("stateLgdCodes") List<Long> stateLgdCodes);

	/**
	 * @param districtLgdCodes
	 * @return
	 */
	@Query("select d from DistrictLgdMaster d where  d.districtLgdCode in (:districtLgdCodes) order by d.districtName ASC" )
	List<DistrictLgdMaster> findAllByDistrictLgdCode(@Param("districtLgdCodes") List<Long> districtLgdCodes);


	@Query("select d from DistrictLgdMaster d where  d.districtLgdCode in (:districtLgdCodes) order by d.districtName ASC" )
	List<DistrictLgdMaster> findAllByDistrictLgdCode(@Param("districtLgdCodes") Set<Long> districtLgdCodes);


	@Query("select d.districtName  || ' (' ||  d.districtLgdCode || ')' from DistrictLgdMaster d where  d.stateLgdCode.stateLgdCode=:stateLgdCode order by d.districtName ASC" )
	List<String> findAllDistrictNameByStateLgdCode(@Param("stateLgdCode") Long stateLgdCode);

	List<DistrictLgdMaster> findByStateLgdCode_StateLgdCode(Long stateLgdCode);
	
	List<DistrictLgdMaster> findByStateLgdCode_StateLgdCodeIn(List<Long> stateLgdCodeList);
}
