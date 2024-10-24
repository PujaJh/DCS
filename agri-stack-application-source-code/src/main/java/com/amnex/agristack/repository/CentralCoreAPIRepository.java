package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.LandParcelSurveyMaster;
import org.springframework.data.repository.query.Param;

import java.sql.Date;

public interface CentralCoreAPIRepository extends JpaRepository<LandParcelSurveyMaster, Long>{

//	@Query(value = "select agri_stack.fn_api_central_crop_sown_details()", nativeQuery = true)
//	String getCropSownDetails();
//
//	@Query(value = "select agri_stack.fn_get_crop_survey_detail_central(:page, :limit)", nativeQuery = true)
//	String getCropSownDetailsCentral( Integer page, Integer limit);


	@Query(value = "select agri_stack.fn_api_central_crop_sown_goemetry_details()", nativeQuery = true)
	String getCropSownGeometryDetails();

	@Query(value = "select agri_stack.fn_get_crop_survey_detail_count_central()", nativeQuery = true)
	String getCropSownDetailsCentralCount();

	@Query(value = "select agri_stack.fn_get_state_crop_survey_data(:page, :limit)", nativeQuery = true)
	String getCropSownDetailsCentralData( Integer page, Integer limit);

	@Query(value = "select agri_stack.fn_get_state_crop_survey_dataV2(:page, :limit, :referenceId)", nativeQuery = true)
	String getCropSownDetailsCentralDataV2(Integer page, Integer limit, String referenceId);

	@Query(value = "select agri_stack.fn_acknowledge_state_crop_survey_data(:page, :limit)", nativeQuery = true)
	String acknowledgeSharedData( Integer page, Integer limit);
	
	@Query(value = "select agri_stack.fn_get_geomaps_details(:page, :limit)", nativeQuery = true)
	String getGeomapsDetails( Long page, Long limit);
}
