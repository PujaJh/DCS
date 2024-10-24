/**
 *
 */
package com.amnex.agristack.repository;

import java.util.List;
import java.util.Set;

import com.amnex.agristack.dao.BoudaryCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.VillageLgdMaster;

/**
 * @author kinnari.soni
 *
 */
public interface VillageLgdMasterRepository extends JpaRepository<VillageLgdMaster, Long> {

	List<VillageLgdMaster> findByStateLgdCode_StateLgdCodeIn(List<Long> stateLgdCodeList);

	List<VillageLgdMaster> findByDistrictLgdCode_DistrictLgdCodeIn(List<Long> districtLgdCodeList);

	VillageLgdMaster findByVillageLgdCode(Long lgdCode);

	List<VillageLgdMaster> findBySubDistrictLgdCode_SubDistrictLgdCodeIn(List<Long> subDistrictLgdCodeList);
	List<VillageLgdMaster> findBySubDistrictLgdCode_SubDistrictLgdCodeInOrderByVillageLgdCodeAsc(List<Long> subDistrictLgdCodeList);
	List<VillageLgdMaster> findBySubDistrictLgdCode_SubDistrictLgdCodeInAndVillageNameContaining(List<Long> subDistrictLgdCodeList,String villageName);

	List<VillageLgdMaster> findByVillageLgdCodeInOrderByVillageName(List<Long> villageCodeList);

	List<VillageLgdMaster> findByVillageLgdCodeIn(List<Long> villageCodeList);

	List<VillageLgdMaster> findByVillageLgdCodeIn(Set<Long> villageCodeList);

	/**
	 * @param findByStateLgdCode
	 * @return
	 */
	List<VillageLgdMaster> findByStateLgdCode(StateLgdMaster findByStateLgdCode);

	List<VillageLgdMaster> findByVillageLgdCodeInAndSubDistrictLgdCodeSubDistrictLgdCode(List<Long> villageLgdCodes,Long subDistrictCode);

	List<VillageLgdMaster> findByDistrictLgdCode_DistrictLgdCode(Long districtLgdCode);

	List<VillageLgdMaster> findBySubDistrictLgdCode_SubDistrictLgdCode(Long subDistrictLgdCode);

	@Query(value = "select state_Lgd_Code as code ,count(*) as totalCount from agri_stack.District_Lgd_Master " +
			"where state_Lgd_Code in (:codes) " +
			"group by state_Lgd_Code ", nativeQuery = true)
	List<BoudaryCount> getDistrictCountDetailsByStateCode(List<Long> codes);

	@Query(value = "select state_Lgd_Code as code ,count(*) as totalCount from agri_stack.sub_District_Lgd_Master " +
			"where state_Lgd_Code in (:codes) " +
			"group by state_Lgd_Code ", nativeQuery = true)
	List<BoudaryCount> getSubDistrictCountDetailsByStateCode(List<Long> codes);

	@Query(value = "select state_Lgd_Code as code ,count(*) as totalCount from agri_stack.Village_Lgd_Master " +
			"where state_Lgd_Code in (:codes) " +
			"group by state_Lgd_Code ", nativeQuery = true)
	List<BoudaryCount> getVillageCountDetailsByStateCode(List<Long> codes);

	@Query("select v.villageName  || ' (' ||  v.villageLgdCode || ')' from VillageLgdMaster v where  v.stateLgdCode.stateLgdCode=:stateLgdCode order by v.villageName ASC" )
	List<String> findAllVillageNameByStateLgdCode(@Param("stateLgdCode") Long stateLgdCode);

	List<VillageLgdMaster> findByVillageLgdCodeInAndSubDistrictLgdCodeSubDistrictLgdCodeIn
	(List<Long> villageLgdCodes,List<Long> subDistrictCode);
}
