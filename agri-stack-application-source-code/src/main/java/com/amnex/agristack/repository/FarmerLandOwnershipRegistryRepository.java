/**
 *
 */
package com.amnex.agristack.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.amnex.agristack.entity.FarmerLandOwnershipRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.amnex.agristack.entity.FarmerRegistry;

/**
 * @author kinnari.soni
 *
 */
public interface FarmerLandOwnershipRegistryRepository extends JpaRepository<FarmerLandOwnershipRegistry, Long> {

	public List<FarmerLandOwnershipRegistry> findByFarmerRegistryIdFarmerRegistryIdAndIsDeletedIsFalse(
			Long farmerRegistryId);

	public final String getLandOwnerShipDetailsWithFarmerQuery = "select  flo.farmerRegistryId from  FarmerLandOwnershipRegistry flo where flo.farmlandPlotRegistryId.villageLgdMaster.villageLgdCode=:villageLgdCode";

	@Query(value = getLandOwnerShipDetailsWithFarmerQuery)
	public List<FarmerRegistry> getLandOwnerShipDetailsWithFarmer(@Param("villageLgdCode") Long villageLgdCode);

	public final String getLandOwnerByDateAndFarmerisDrafted = " SELECT FLOR.* "
			+ "FROM AGRI_STACK.FARMER_LAND_OWNERSHIP_REGISTRY FLOR "
			+ " JOIN AGRI_STACK.FARMER_REGISTRY FR on(FLOR.flor_fr_farmer_registry_id=fR.fr_farmer_registry_id) "
			+ " INNER JOIN AGRI_STACK.FARMLAND_PLOT_REGISTRY FLPR ON(FLOR.FLOR_FPR_FARMLAND_PLOT_REGISTRY_ID = FLPR.FPR_FARMLAND_PLOT_REGISTRY_ID) "
			+ " INNER JOIN AGRI_STACK.VILLAGE_LGD_MASTER VLM ON(VLM.VILLAGE_LGD_CODE = FLPR.FPR_VILLAGE_LGD_CODE) "
			+ " INNER JOIN AGRI_STACK.STATE_LGD_MASTER SLM ON (VLM.STATE_LGD_CODE = SLM.STATE_LGD_CODE) "
			+ " WHERE FLOR.MODIFIED_ON \\:\\:date <=:draftedDate " + "    AND FR.FR_IS_DRAFTED IS TRUE "
			+ "    and FLOR.flor_fr_farmer_registry_id is not null "
			+ "    AND SLM.STATE_LGD_CODE =:stateLgdCode and FLPR.FPR_VILLAGE_LGD_CODE is not null";

	@Query(value = getLandOwnerByDateAndFarmerisDrafted, nativeQuery = true)
	public List<FarmerLandOwnershipRegistry> getLandOwnerByDateAndFarmerisDrafted(
			@Param("draftedDate") Date draftedDate, @Param("stateLgdCode") Long stateLgdcode);

	public List<FarmerLandOwnershipRegistry> findByMainOwnerNoAsPerRorAndFarmlandPlotRegistryIdSurveyNumberAndFarmlandPlotRegistryIdSubSurveyNumberAndFarmlandPlotRegistryIdVillageLgdMasterVillageLgdCode(
			Integer id, String surveyNumber, String subSurveyNumber, Long villageLgdCode);

	final String getAllOwnerNumberWithSameSurveyNumberAndVillageCodeQuery = " select flor.flor_owner_no_as_per_ror from agri_stack.farmer_land_ownership_registry flor inner join agri_stack.farmland_plot_registry flpr \r\n"

			+ " on(flor.flor_fpr_farmland_plot_registry_id=flpr.fpr_farmland_plot_registry_id ) where flpr.fpr_survey_number=:surveyNumber and flpr.fpr_village_lgd_code=:villageCode order by flor.flor_owner_no_as_per_ror ";

	@Query(value = getAllOwnerNumberWithSameSurveyNumberAndVillageCodeQuery, nativeQuery = true)
	public List<String> getAllOwnerNumberWithSameSurveyNumberAndVillageCode(String surveyNumber, Long villageCode);

	
	@Modifying
	@Transactional
	@Query(value = "DELETE from agri_stack.farmer_land_ownership_registry "
			+ "	where flor_fpr_farmland_plot_registry_id in (select fpr_farmland_plot_registry_id "
			+ "					from agri_stack.farmland_plot_registry "
			+ "					where fpr_village_lgd_code IN (:villageLgdCode)) ", nativeQuery = true)
	public void deleteAllByVillageLgdCode(Long villageLgdCode);


	@Modifying
	@Transactional
	@Query(value = "DELETE from agri_stack.farmer_land_ownership_registry "
			+ "	where flor_fpr_farmland_plot_registry_id in (:fprIds) ", nativeQuery = true)
	public int deleteAllByFarmlandPlotRegistryId(List<Long> fprIds);
	
	

}
