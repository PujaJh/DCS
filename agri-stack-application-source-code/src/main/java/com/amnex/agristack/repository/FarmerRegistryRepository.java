package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.entity.FarmerRegistry;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.SubDistrictLgdMaster;
import com.amnex.agristack.entity.VillageLgdMaster;

public interface FarmerRegistryRepository extends JpaRepository<FarmerRegistry, Long> {

	/**
	 * @param aadhaarNumber
	 * @return
	 */
	FarmerRegistry getByFarmerAadhaarHash(String aadhaarNumber);

	/*final String unionQuery="Select * from "
			+ "("
			+ "	Select fr_farmer_registry_id as \"farmerId\", fr_name_en as \"name\",fr_dob as \"dob\" , 'residental detail' as \"category\" from agri_stack.farmer_registry "
			+ "	union all "
			+ "	Select fr_farmer_registry_id as \"farmerId\",fr_name_en as \"name\", 'basic details' as \"category\" from agri_stack.farmer_registry"
			+ "	union all"
			+ "	Select fr_farmer_registry_id as \"farmerId\",fr_name_en as \"name\", 'abc' as \"category\"  from agri_stack.farmer_registry"
			+ ") as abc order by \"farmerId\";*/

	final String farmerRegistryBasicDetailsQuery="select fr.farmerRegistryId,fr.farmerNameEn,fr.farmerNameLocal,fr.farmerGenederType from FarmerRegistry fr ";

	@Query(value=farmerRegistryBasicDetailsQuery)
	public List<FarmerRegistry> getfarmerRegistryBasicDetails();

	public String getAllFarmerRegistryWithLandAndWithoutLandQuery=
			"SELECT "+
					" FR.fr_farmer_registry_id,FR.fr_name_en,FR.fr_name_local,FR.fr_farmer_number,FR.created_on,FR.fr_as_approval_status_id, FLOR.FLOR_FR_FARMER_REGISTRY_ID"+
					" FROM AGRI_STACK.FARMER_REGISTRY FR"
					+ " LEFT JOIN AGRI_STACK.FARMER_LAND_OWNERSHIP_REGISTRY FLOR"
					+ "    ON(FLOR.FLOR_FR_FARMER_REGISTRY_ID = FR.FR_FARMER_REGISTRY_ID)"
					+ " LEFT JOIN AGRI_STACK.FARMLAND_PLOT_REGISTRY FLPR "
					+ "    ON (FLOR.FLOR_FPR_FARMLAND_PLOT_REGISTRY_ID = FLPR.FPR_FARMLAND_PLOT_REGISTRY_ID)"
					+ " WHERE FLPR.FPR_VILLAGE_LGD_CODE =:villageCode"
					+ "    OR FR.FR_VILL_VILLAGE_LGD_CODE =:villageCode"
					+ " group by FR.fr_farmer_registry_id, FLOR.FLOR_FR_FARMER_REGISTRY_ID"
					+ " ORDER BY FR.modified_on DESC";


	@Query(value=getAllFarmerRegistryWithLandAndWithoutLandQuery,nativeQuery=true)
	public List<Object[]>getAllFarmerRegistryWithLandAndWithoutLand( @Param("villageCode")Long  villageCode);


	/**
	 * @param encode
	 * @return
	 */
	Boolean existsByFarmerAadhaarHash(String encode);

	public String getAllFarmerRegistryWithVillageCodeQuery="SELECT FR.FR_FARMER_REGISTRY_ID,"
			+ "	FR.FR_NAME_EN,"
			+ "	FR.FR_NAME_LOCAL,"
			+ "	FR.FR_FARMER_NUMBER,"
			+ "	FR.CREATED_ON,"
			+ "	FR.FR_AS_APPROVAL_STATUS_ID,"
			+ "	FRWFRT.REQUEST_TYPE,"
			+ "		FRWFM.request_raised_on, "
			+" 		FRWF.frwf_farmer_registration_work_flow_id "
			+ " "
			+ " FROM AGRI_STACK.FARMER_REGISTRY FR "
			+ " INNER JOIN AGRI_STACK.FARMER_REGISTRY_WORK_FLOW_MAPPING FRWFM ON (FR.FR_FARMER_REGISTRY_ID = FRWFM.FARMER_REGISTRY_ID)"
			+ " INNER JOIN AGRI_STACK.FARMER_REGISTRY_WORK_FLOW FRWF on(FRWFM.farmer_registry_work_flow_frwf_farmer_registration_work_flow_id=FRWF.frwf_farmer_registration_work_flow_id)"
			+ " INNER JOIN AGRI_STACK.FARMER_REGISTRY_WORK_FLOW_REQUEST_TYPE FRWFRT ON (FRWF.FRWF_FARMER_REGISTRATION_WORK_FLOW_REQUEST_ID = FRWFRT.FRWR_FARMER_REGISTRY_WORK_FLOW_REQUEST_ID)"
			+ " WHERE FR.FR_VILL_VILLAGE_LGD_CODE in(:villageLgdCode) "
			+ " and  FR.IS_DELETED IS false "
			+ " ORDER BY FR.MODIFIED_ON DESC";
	@Query(value=getAllFarmerRegistryWithVillageCodeQuery,nativeQuery=true)
	public List<Object[]>findFarmerRegistryWithVillageCode(Long villageLgdCode);


	public String getAllFarmerRegistryWithVillageCodeInQuery="SELECT FR.FR_FARMER_REGISTRY_ID,"
			+ "	FR.FR_NAME_EN,"
			+ "	FR.FR_NAME_LOCAL,"
			+ "	FR.FR_FARMER_NUMBER,"
			+ "	FR.CREATED_ON,"
			+ "	FR.FR_AS_APPROVAL_STATUS_ID,"
			+ "	FRWFRT.REQUEST_TYPE,"
			+ "	FRWFM.request_raised_on, "
			+ " FRWF.frwf_farmer_registration_work_flow_id, "
			+ "	FR.IS_ACTIVE,"
			+" FRWFM.ID ,"
			+" FRWFM.is_for_occupation, "
			+" FR.FR_FARMER_ENROLLEMENT_NUMBER"
			+ " FROM AGRI_STACK.FARMER_REGISTRY FR"
			+ " INNER JOIN AGRI_STACK.FARMER_REGISTRY_WORK_FLOW_MAPPING FRWFM ON (FR.FR_FARMER_REGISTRY_ID = FRWFM.FARMER_REGISTRY_ID)"
			+ " INNER JOIN AGRI_STACK.FARMER_REGISTRY_WORK_FLOW FRWF on(FRWFM.farmer_registry_work_flow_frwf_farmer_registration_work_flow_id=FRWF.frwf_farmer_registration_work_flow_id)"
			+ " INNER JOIN AGRI_STACK.FARMER_REGISTRY_WORK_FLOW_REQUEST_TYPE FRWFRT ON (FRWF.FRWF_FARMER_REGISTRATION_WORK_FLOW_REQUEST_ID = FRWFRT.FRWR_FARMER_REGISTRY_WORK_FLOW_REQUEST_ID)"
			+ " WHERE FR.FR_VILL_VILLAGE_LGD_CODE in (:villageLgdCode) "
			+ " AND  FR.IS_DELETED IS false "
			+ " ORDER BY FR.MODIFIED_ON DESC";
	@Query(value=getAllFarmerRegistryWithVillageCodeInQuery,nativeQuery=true)
	public List<Object[]>findFarmerRegistryWithVillageInCode(@Param("villageLgdCode") List<Long> villageLgdCode);

	public String getFarmerApprovalLatestStatusQuery=" WITH max_sequence AS (\r\n" +
			"  SELECT\r\n" +
			"    MAX(frwkfe.sequence) AS max_sequence\r\n" +
			"  FROM\r\n" +
			"    agri_stack.farmer_registry_approval_workflow_transaction frawt\r\n" +
			"    INNER JOIN agri_stack.farmer_registry_work_flow_escalations frwkfe\r\n" +
			"      ON frawt.frawt_work_flow_id = frwkfe.farmer_registry_work_flow_frwf_farmer_registration_work_flow_id\r\n" +
			"    INNER JOIN agri_stack.user_master um\r\n" +
			"      ON um.role_id = frwkfe.role_master_role_id AND frawt.verified_by_user_id = um.user_id\r\n" +
			"  WHERE\r\n" +
			"    frawt.frawt_farmer_registry_id = :farmerRegistryId\r\n" +
			"    AND frawt.frawt_work_flow_id = :workFlowId\r\n" +
			"    AND frwkfe.department_master_department_id = :departmentId\r\n" +
			"    AND frwkfe.role_master_role_id IN (\r\n" +
			"      SELECT DISTINCT(role_id)\r\n" +
			"      FROM agri_stack.user_master\r\n" +
			"      WHERE department_id = :departmentId\r\n" +
			"    )\r\n" +
			" )\r\n" +
			" SELECT\r\n" +
			"  frawt.frawt_verification_status\r\n" +
			" FROM\r\n" +
			"  agri_stack.farmer_registry_approval_workflow_transaction frawt\r\n" +
			"  INNER JOIN agri_stack.farmer_registry_work_flow_escalations frwkfe\r\n" +
			"    ON frawt.frawt_work_flow_id = frwkfe.farmer_registry_work_flow_frwf_farmer_registration_work_flow_id\r\n" +
			"  INNER JOIN agri_stack.user_master um\r\n" +
			"    ON um.role_id = frwkfe.role_master_role_id AND frawt.verified_by_user_id = um.user_id\r\n" +
			
			" INNER JOIN AGRI_STACK.FARMER_REGISTRY_WORK_FLOW_MAPPING FRWFM on (FRWFM.id=frawt.frawt_farmer_work_flow_mapping_id) "+
			" WHERE\r\n" +
			"  frawt.frawt_farmer_registry_id = :farmerRegistryId\r\n" +
			"  AND frawt.frawt_work_flow_id = :workFlowId\r\n" +
			"  AND frwkfe.department_master_department_id = :departmentId\r\n" +
			"  AND frwkfe.sequence = (SELECT max_sequence FROM max_sequence)\r\n" +
			" AND FRWFM.id=:farmerWorkFlowId"+
			" ORDER BY\r\n" +
			"  frawt.modified_on DESC\r\n" +
			" LIMIT 1;";
	@Query(value=getFarmerApprovalLatestStatusQuery,nativeQuery=true)
	public Long getFarmerApprovalLatestStatus(Long workFlowId,Long departmentId,Long farmerRegistryId,@Param("farmerWorkFlowId") Long farmerWorkFlowId);

	public List<FarmerRegistry> findByStateLgdMasterAndDistrictLgdMasterAndSubDistrictLgdMasterAndVillageLgdMasterOrderByFarmerRegistryIdDesc
	(StateLgdMaster state,DistrictLgdMaster district,SubDistrictLgdMaster subDistrict,VillageLgdMaster village);
	 Optional<FarmerRegistry> findByFarmerMobileNumber(String mobileNo);
	 
	@Query(value = "select fr.fr_name_local,fr.fr_name_en || ' ' || fr.fr_identifier_name_en,fr.fr_farmer_registry_id from agri_stack.farmer_registry fr " 
	            + "inner join agri_stack.farmer_land_ownership_registry flor on flor.flor_fr_farmer_registry_id = fr.fr_farmer_registry_id "
				+ "inner join agri_stack.farmland_plot_registry fpr on fpr.fpr_farmland_plot_registry_id = flor.flor_fpr_farmland_plot_registry_id "
	          + "where fpr.fpr_village_lgd_code = :villageLgdCode", nativeQuery = true)
	public List<Object[]> getFarmerDetailByVillageLgdCodes(Integer villageLgdCode);
	    
//	@Query(value = "select fpr.fpr_survey_number,fpr.fpr_sub_survey_number,fpr.fpr_farmland_plot_registry_id from agri_stack.farmer_registry fr inner join agri_stack.farmer_land_ownership_registry flor on flor.flor_fr_farmer_registry_id = fr.fr_farmer_registry_id inner join agri_stack.farmland_plot_registry fpr on fpr.fpr_farmland_plot_registry_id = flor.flor_fpr_farmland_plot_registry_id where fr.fr_farmer_registry_id = :farmerId", nativeQuery = true)
//	public List<Object[]> getSurveyNoDetailByFarmerId(Long farmerId);
	
	@Query(value = "select agri_stack.fn_get_farmer_golden_record_details_by_plot(:farmerId,:parcelId,:startYear,:endYear,:seasonId)", nativeQuery = true)
	String getFarmerGoldenRecords(Long farmerId,Long parcelId, Integer startYear, Integer endYear, Integer seasonId);
	    
	@Query(value = "select agri_stack.fn_get_farmer_profile_and_land_detail(:farmerId)", nativeQuery = true)    
	String getFarmerProfileAndLandDetails(Long farmerId);

	@Query(value ="Select f.farmerMobileNumber from FarmerRegistry f where f.farmerRegistryNumber = :farmerRegistryNumber")
	String findByFarmerRegistryNumber(String farmerRegistryNumber);
}
