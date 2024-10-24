/**
 *
 */
package com.amnex.agristack.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amnex.agristack.dao.BoundaryLgdDAO;
import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.SubDistrictLgdMaster;

/**
 * @author kinnari.soni
 *
 */
public interface SubDistrictLgdMasterRepository extends JpaRepository<SubDistrictLgdMaster, Long> {

	/**
	 * @param subDistrictLgdCode
	 * @return
	 */
	SubDistrictLgdMaster findBySubDistrictLgdCode(Long subDistrictLgdCode);

	/**
	 * @param findByStateLgdCode
	 * @param findByDistrictLgdCode
	 * @return
	 */
	List<SubDistrictLgdMaster> findByStateLgdCodeAndDistrictLgdCode(StateLgdMaster findByStateLgdCode,
			DistrictLgdMaster findByDistrictLgdCode);

	/**
	 * @param findByStateLgdCode
	 * @return
	 */
	List<SubDistrictLgdMaster> findByStateLgdCode(StateLgdMaster findByStateLgdCode);

	/**
	 * @param findByDistrictLgdCode
	 * @return
	 */
	List<SubDistrictLgdMaster> findByDistrictLgdCode(DistrictLgdMaster findByDistrictLgdCode);

	//	List<SubDistrictLgdMaster> findBySubDistrictLgdCode(List<Long> subDistrictLgdCode);
	/**
	 * @param districtLgdCodes
	 * @return
	 */
	@Query("select d from SubDistrictLgdMaster d where  d.districtLgdCode.districtLgdCode in (:districtLgdCodes) order by d.subDistrictName ASC" )
	List<SubDistrictLgdMaster> findSubDistrictByDistrictLgdCodes(@Param("districtLgdCodes") List<Long> districtLgdCodes);

	/**
	 * @param subDistrictLgdCodes
	 * @return
	 */
	@Query("select d from SubDistrictLgdMaster d where  d.subDistrictLgdCode in (:subDistrictLgdCodes) order by d.subDistrictName ASC" )
	List<SubDistrictLgdMaster> findByLgdCodes(@Param("subDistrictLgdCodes") List<Long> subDistrictLgdCodes);


	@Query("select d from SubDistrictLgdMaster d where  d.subDistrictLgdCode in (:subDistrictLgdCodes) order by d.subDistrictName ASC" )
	List<SubDistrictLgdMaster> findByLgdCodes(@Param("subDistrictLgdCodes") Set<Long> subDistrictLgdCodes);

	List<SubDistrictLgdMaster> findBySubDistrictLgdCodeInAndDistrictLgdCodeDistrictLgdCode(List<Long> subDistrictCodes,Long districtLgdCode);

	@Query("select sd.subDistrictName  || ' (' ||  sd.subDistrictLgdCode || ')' from SubDistrictLgdMaster sd where  sd.stateLgdCode.stateLgdCode=:stateLgdCode order by sd.subDistrictName ASC" )
	List<String> findAllSubDistrictNameByStateLgdCode(@Param("stateLgdCode") Long stateLgdCode);

	List<SubDistrictLgdMaster> findBySubDistrictLgdCodeInAndDistrictLgdCodeDistrictLgdCodeIn(List<Long> subDistrictCodes,List<Long> districtLgdCodes);
	

	@Query(value="select state_lgd_code stateLgdCode,district_lgd_code districtLgdCode,sub_district_lgd_code subDistrictLgdCode from agri_stack.Sub_District_Lgd_Master " + 
			"where state_lgd_code in (:stateCode) ",nativeQuery = true )
	List<BoundaryLgdDAO> findBySubDistrictLgdCodeByStateCode(List<Long> stateCode);
	
	@Query(value="select state_lgd_code stateLgdCode,district_lgd_code districtLgdCode,sub_district_lgd_code subDistrictLgdCode  from agri_stack.Sub_District_Lgd_Master " + 
			"where sub_district_lgd_code in (:subDistrictCodes) ",nativeQuery = true )
	List<BoundaryLgdDAO> findBySubDistrictLgdCodeBySubDistrictCodes(List<Long> subDistrictCodes);
	
	List<SubDistrictLgdMaster> findByDistrictLgdCode_DistrictLgdCode(Long districtLgdCode);
}
