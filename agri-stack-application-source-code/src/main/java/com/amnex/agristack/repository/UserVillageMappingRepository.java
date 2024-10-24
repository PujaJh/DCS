package com.amnex.agristack.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.amnex.agristack.dao.BoundaryDAO;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.entity.UserVillageMapping;
import com.amnex.agristack.entity.VillageLgdMaster;

public interface UserVillageMappingRepository extends JpaRepository<UserVillageMapping, Long> {

	List<UserVillageMapping> findByUserMaster(UserMaster user);

	List<UserVillageMapping> findByUserMaster_UserId(Long userId);
	List<UserVillageMapping> findByUserMaster_UserIdIn(List<Long> userIds);

	@Query(value = "select distinct sld.state_lgd_code from agri_stack.user_village_mapping uv "
			+ "inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code "
			+ "inner join agri_stack.state_lgd_master sld on sld.state_lgd_code = vld.state_lgd_code "
			+ "where user_id=:userId", nativeQuery = true)
	List<Long> getStateCodesById(Long userId);

	@Query(value = "select distinct vld.village_lgd_code from agri_stack.user_village_mapping uv "
			+ "inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code "
			+ "where user_id=:userId and uv.is_active = true and uv.is_deleted = false", nativeQuery = true)
	List<Long> getVillageCodesByUserId(Long userId);
	
	@Query(value = "select distinct vld.village_lgd_code from agri_stack.user_village_mapping uv "
			+ "inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code "
			+ "where user_id=:userId and uv.village_lgd_code=:vcode ", nativeQuery = true)
	List<Long> getVillageCodesByUserIdAndVillageLgdCode(Long userId,Long vcode);

	@Query(value = "select distinct vld.district_lgd_code from agri_stack.user_village_mapping uv " +
			"			inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code " +
			"			inner join agri_stack.District_Lgd_Master sld on sld.district_lgd_code = vld.district_lgd_code "
			+
			"			where user_id=:userId", nativeQuery = true)
	List<Long> getDistrictCodesById(Long userId);

	@Query(value = "				  select distinct vld.sub_district_lgd_code from agri_stack.user_village_mapping uv" +
			"						inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code " +
			"						inner join agri_stack.District_Lgd_Master sld on sld.district_lgd_code = vld.district_lgd_code" +
			"				  		inner join agri_stack.sub_District_Lgd_Master sdlm on sdlm.sub_district_lgd_code = vld.sub_district_lgd_code " +
			"						where user_id=:userId ", nativeQuery = true)
	List<Long> getSubDistrictCodesById(Long userId);
	
	
	
	@Query(value = " select fpr.fpr_village_lgd_code,count(*) from  agri_stack.farmland_plot_registry fpr " + 
			" inner join agri_stack.user_village_mapping uv on uv.village_lgd_code=fpr.fpr_village_lgd_code and uv.user_id= :userId " + 
			"	 group by fpr.fpr_village_lgd_code " + 
			"	 having count(fpr.fpr_village_lgd_code)>0 " + 
			"	 order by fpr.fpr_village_lgd_code asc " + 
			"	  ", nativeQuery = true)
	List<Long> getVillageForPlotsGreaterZero(Long userId);

	@Query(value = "select distinct sld.district_lgd_code from agri_stack.user_village_mapping uv "
			+ "inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code "
			+ "inner join agri_stack.District_Lgd_Master sld on sld.district_lgd_code = vld.district_lgd_code "
			+ "where user_id=:userId and vld.state_lgd_code in (:stateCodes) ", nativeQuery = true)
	List<Long> getDistrictCodesByStateCodesAndId(Long userId, List<Long> stateCodes);

	@Query(value = "select distinct sld.sub_district_lgd_code from agri_stack.user_village_mapping uv "
			+ "inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code "
			+ "inner join agri_stack.sub_District_Lgd_Master sld on sld.sub_district_lgd_code = vld.sub_district_lgd_code "
			+ "where user_id= :userId and vld.district_lgd_code in (:districtCodes) ", nativeQuery = true)
	List<Long> getSubDistrictCodesByDistrictCodesAndId(Long userId, List<Long> districtCodes);

	Set<UserVillageMapping> findByVillageLgdMaster(VillageLgdMaster village);

	List<UserVillageMapping> findByUserMaster_IsActiveAndIsDeletedAndUserMaster_RoleId_RoleNameIgnoreCaseAndVillageLgdMaster_VillageLgdCodeInAndIsActiveAndIsDeleted(
			Boolean isUserActive, Boolean isUserDeleted, String roleName, List<Long> codes, Boolean isActive,
			Boolean isDeleted);

	@Query(value = "select distinct(user_id) from agri_stack.user_village_mapping where village_lgd_code in "
			+ "(select distinct(village_lgd_code) from agri_stack.user_village_mapping where user_id = :userId)", nativeQuery = true)
	List<Long> findUniqueUsersByClientId(Long userId);

	/**
	 *
	 * @param userIds
	 * @param isActive
	 * @param iseDelete
	 */
	@Modifying
	@Transactional
	@Query(value = "Update agri_stack.user_village_mapping set is_active=:isActive,is_deleted=:isDelete where user_id in (:userIds)", nativeQuery = true)
	void UpdateUsersSetIsActiveAndIsDeleted(@Param("userIds") List<Long> userIds, @Param("isActive") Boolean isActive,
			@Param("isDelete") Boolean iseDelete);

	@Modifying
	@Transactional
	@Query(value = "delete from agri_stack.user_village_mapping where user_id =:id ", nativeQuery = true)
	void deleteVillageById(@Param("id") Long id);

	/**
	 * @param userList
	 * @return
	 */
	List<UserVillageMapping> findByUserMasterIn(List<UserMaster> userList);

	List<UserVillageMapping> findByUserMaster_UserIdAndVillageLgdMaster_DistrictLgdCode_DistrictLgdCodeIn(Long userId,
			List<Long> codes);

	List<UserVillageMapping> findByUserMaster_UserIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeIn(
			Long userId, List<Long> codes);

	@Query(value = "select distinct vld.village_lgd_code from agri_stack.user_village_mapping uv "
//			+ "inner join agri_stack.farmland_plot_registry fpr on fpr.fpr_village_lgd_code=uv.village_lgd_code "
			+ "inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code and vld.sub_District_Lgd_Code in(:codes) and uv.user_id= :userId   ", nativeQuery = true)
	List<Long> getVillageCodesByUserIdAndSubDistrictIn(Long userId,List<Long> codes);
	
	List<UserVillageMapping> findByUserMaster_UserIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeInOrderByVillageLgdMaster_VillageNameAsc(
			Long userId, List<Long> codes);

	List<UserVillageMapping> findByUserMaster_UserIdAndVillageLgdMaster_StateLgdCode_StateLgdCodeIn(Long userId,
			List<Long> codes);

	List<UserVillageMapping> findByUserMaster_IsActiveAndUserMaster_IsDeletedAndUserMaster_RoleId_RoleNameIgnoreCaseAndVillageLgdMaster_VillageLgdCodeInAndIsActiveAndIsDeleted(
			Boolean isUserActive, Boolean isUserDeleted, String roleName, List<Long> codes, Boolean isActive,
			Boolean isDeleted);

	public final String getVillageListByUserId = " SELECT V.VILLAGE_LGD_CODE," + "	V.VILLAGE_NAME"
			+ " FROM AGRI_STACK.VILLAGE_LGD_MASTER V"
			+ " INNER JOIN AGRI_STACK.USER_VILLAGE_MAPPING UVM ON(V.VILLAGE_LGD_CODE = UVM.VILLAGE_LGD_CODE)"
			+ " WHERE UVM.USER_ID =:userId";

	@Query(value = getVillageListByUserId, nativeQuery = true)
	public List<Object[]> getVillageList(@Param("userId") Long userId);

	public final String getSubDistrictListByUserId = "SELECT SB.SUB_DISTRICT_LGD_CODE,"
			+ "	SB.SUB_DISTRICT_NAME"
			+ " FROM AGRI_STACK.SUB_DISTRICT_LGD_MASTER SB"
			+ " INNER JOIN AGRI_STACK.VILLAGE_LGD_MASTER V ON(SB.SUB_DISTRICT_LGD_CODE = V.SUB_DISTRICT_LGD_CODE) "
			+ " INNER JOIN AGRI_STACK.USER_VILLAGE_MAPPING UVM ON(V.VILLAGE_LGD_CODE = UVM.VILLAGE_LGD_CODE) "
			+ " WHERE UVM.USER_ID =:userId"
			+ " GROUP BY SB.SUB_DISTRICT_LGD_CODE,"
			+ "	SB.SUB_DISTRICT_NAME";

	@Query(value = getSubDistrictListByUserId, nativeQuery = true)
	public List<Object[]> getSubDistrictList(@Param("userId") Long userId);

	public final String getDistrictListByUserId = "SELECT D.DISTRICT_LGD_CODE,"
			+ "	D.DISTRICT_NAME  from AGRI_STACK.DISTRICT_LGD_MASTER D"
			+ " INNER JOIN AGRI_STACK.VILLAGE_LGD_MASTER V ON(D.DISTRICT_LGD_CODE = V.DISTRICT_LGD_CODE)"
			+ " INNER JOIN AGRI_STACK.USER_VILLAGE_MAPPING UVM ON(V.VILLAGE_LGD_CODE = UVM.VILLAGE_LGD_CODE)"
			+ " where UVM.user_id=:userId group by D.DISTRICT_LGD_CODE,D.DISTRICT_NAME";

	@Query(value = getDistrictListByUserId, nativeQuery = true)
	public List<Object[]> getDistrictList(@Param("userId") Long userId);

	public final String getStateListByUserId = "SELECT S.STATE_LGD_CODE," +
			"	S.STATE_NAME  from AGRI_STACK.STATE_LGD_MASTER S" +
			"	INNER JOIN AGRI_STACK.VILLAGE_LGD_MASTER V ON(S.STATE_LGD_CODE = V.STATE_LGD_CODE)" +
			"	INNER JOIN AGRI_STACK.USER_VILLAGE_MAPPING UVM ON(V.VILLAGE_LGD_CODE = UVM.VILLAGE_LGD_CODE)" +
			"	where UVM.user_id= :userId group by S.STATE_LGD_CODE,S.STATE_NAME";

	@Query(value = getStateListByUserId, nativeQuery = true)
	public List<Object[]> getStateList(@Param("userId") Long userId);

	@Query(value = "select distinct vld.village_lgd_code from agri_stack.user_village_mapping uv "
			+ "inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code "
			+ "where user_id=:userId and vld.state_lgd_code=:code ", nativeQuery = true)
	List<Long> getVillageCodesByUserIdStateCode(Long userId, Long code);

	@Modifying
	@Transactional
	@Query(value = "Update agri_stack.user_village_mapping set is_active=:isActive,is_deleted=:isDelete where user_id =:userId ", nativeQuery = true)
	void UpdateUsersSetIsActiveAndIsDeleted(@Param("userId") Long userId, @Param("isActive") Boolean isActive,
			@Param("isDelete") Boolean iseDelete);

	List<UserVillageMapping> findByUserMaster_UserIdAndIsActiveAndIsDeleted(Long userId, Boolean isActive,
			Boolean isDelete);

	String findTerritoriesByTerritoryLevelAndUser = " Select  " +
			" case when (:territoryLevel =  'VILLAGE') THEN vlm.village_lgd_code  " +
			" 	when (:territoryLevel =  'SUBDISTRICT') THEN sdlm.sub_district_lgd_code " +
			" 	when (:territoryLevel =  'DISTRICT') THEN dlm.district_lgd_code " +
			" 	when (:territoryLevel =  'STATE') THEN slm.state_lgd_code " +
			" 	END AS \"territoryLGDCode\", " +
			" case when (:territoryLevel =  'VILLAGE') THEN vlm.village_name  " +
			" 	when (:territoryLevel =  'SUBDISTRICT') THEN sdlm.sub_district_name " +
			" 	when (:territoryLevel =  'DISTRICT') THEN dlm.district_name " +
			" 	when (:territoryLevel =  'STATE') THEN slm.state_name " +
			" 	END AS \"territoryName\" " +
			" from agri_stack.user_village_mapping uvm " +
			" inner join agri_stack.village_lgd_master vlm " +
			" on uvm.village_lgd_code = vlm.village_lgd_code " +
			" left join agri_stack.sub_district_lgd_master sdlm " +
			" on vlm.sub_district_lgd_code = sdlm.sub_district_lgd_code " +
			" left join agri_stack.district_lgd_master dlm " +
			" on vlm.district_lgd_code = dlm.district_lgd_code " +
			" left join agri_stack.state_lgd_master slm " +
			" on vlm.state_lgd_code = slm.state_lgd_code " +
			" where uvm.user_id = :userId " +
			" group by \"territoryLGDCode\",\"territoryName\" " +
			" order by \"territoryName\" ; ";

	@Query(value = findTerritoriesByTerritoryLevelAndUser, nativeQuery = true)
	List<Object[]> getTerritoriesByTerritoryLevelAndUser(@Param("territoryLevel") String territoryLevel,
			@Param("userId") Long userId);

	List<UserVillageMapping> findByUserMaster_UserIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeInAndVillageLgdMaster_VillageLgdCodeNotInOrderByVillageLgdMaster_VillageNameAsc(
			Long userId, List<Long> codes,List<Long> vodes);

	List<UserVillageMapping> findByUserMaster_RoleId_RoleIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeInAndIsActiveAndIsDeleted(Long roleId,
			List<Long> codes,Boolean isActive,Boolean isDeleted);

	@Modifying
	@Transactional
	@Query(value="update agri_stack.user_village_mapping set is_deleted = :isDeleted,is_active= :isActive, modified_by = :modifiedBy where user_id in (:userIds)",nativeQuery = true)
	void updateUserVillageMappingStatus(boolean isDeleted,boolean isActive,List<Long> userIds,String modifiedBy);

	@Query(value = "select distinct vld.village_lgd_code from agri_stack.user_village_mapping uv "
			+ " inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code "
			+ " where user_id=:userId and vld.sub_district_lgd_code =:subDistrictLgdCode", nativeQuery = true)
	List<Long> getVillageCodeBySubDistrictAndUserId(Long userId,Long subDistrictLgdCode);
	//
	//	@Query(value = "select distinct vld.village_lgd_code from agri_stack.user_village_mapping uv "
	//			+ " inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code "
	//			+ " where user_id=:userId and vld.district_lgd_code =:districtLgdCode", nativeQuery = true)
	//	List<Long> getVillageCodeByDistrictAndUserId(Long userId,Long districtLgdCode);

	@Query(value = "select distinct sld.sub_district_lgd_code from agri_stack.user_village_mapping uv "
			+ "inner join agri_stack.village_lgd_master vld on vld.village_lgd_code = uv.village_lgd_code "
			+ "inner join agri_stack.sub_District_Lgd_Master sld on sld.sub_district_lgd_code = vld.sub_district_lgd_code "
			+ "where user_id= :userId and vld.district_lgd_code =:districtCode ", nativeQuery = true)
	List<Long> getSubDistrictCodesByDistrictCodeAndUserId(Long userId, Long districtCode);

	@Query(value = "select agri_stack.fn_user_wise_boundary(:userId,:level,:codeList)", nativeQuery = true)
	String getUserWiseBoundaryDetails(Long userId, String level, String codeList);
	
	@Query(value = "select state_lgd_code stateLgdCode,district_lgd_code districtLgdCode,sub_district_lgd_code subDistrictLgdCode,uvm.village_lgd_code villageLgdCode " + 
			"from agri_stack.user_village_mapping uvm " + 
			"inner join agri_stack.village_lgd_master vlm on vlm.village_lgd_code=uvm.village_lgd_code " + 
			"where user_id=:userId ", nativeQuery = true)
	List<BoundaryDAO> getUserWiseBoundaryDetailsForRedis(Long userId);
}
