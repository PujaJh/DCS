package com.amnex.agristack.centralcore.repository;

import com.amnex.agristack.entity.FarmlandPlotRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface AssetIdentificationRepository extends JpaRepository<FarmlandPlotRegistry, Long> {

	@Query(value = "select agristack_api_spec.fn_get_farmer_profile_and_land_detail(:farmerId,:seasonDetails)", nativeQuery = true)
	String getFarmerProfileAndLandDetailsByFarmerId(String farmerId, String seasonDetails);

	@Query(value = "select agristack_api_spec.fn_find_farmer_details_by_filter(:districtCode,:subDistrictCode,:villageCode,:surveyNo,:subSurveyNo,:aadharNo)", nativeQuery = true)
	String getFarmerNumberByFilter(Long districtCode, Long subDistrictCode, Long villageCode, String surveyNo,
			String subSurveyNo, String aadharNo);

}
