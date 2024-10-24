package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.LandParcelSurveyDetail;

public interface MisCenterRepository extends JpaRepository<LandParcelSurveyDetail, Long>{
	
	@Query(value = "select agri_stack.fn_mis_get_center_cultivated_summary_report(:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getCultivatedSummaryDetails(Integer seasonId, Integer startYear, Integer endYear);


//	@Query(value = "select agri_stack.fn_mis_get_center_cultivated_summary_report_v2(:seasonId,:startYear,:endYear)", nativeQuery = true)
//	String getCultivatedSummaryDetails_v2(Integer seasonId, Integer startYear, Integer endYear);

	@Query(value = "select agri_stack.fn_central_mis_aggregated_summary_report(:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getAggregatedDetails(Integer seasonId, Integer startYear, Integer endYear);
	
	@Query(value = "select agri_stack.fn_central_mis_crop_summary_report(:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getCropSummaryDetails(Integer seasonId, Integer startYear, Integer endYear);
	
	@Query(value = "select agri_stack.fn_central_mis_surveyors_department_report(:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getSurveyorsDepartmentDetails(Integer seasonId, Integer startYear, Integer endYear);
	
	@Query(value = "select agri_stack.fn_central_mis_get_irrigation_source_report(:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getIrrigationSourceDetails(Integer seasonId, Integer startYear, Integer endYear);
	
	@Query(value = "select agri_stack.fn_central_mis_get_village_wise_plot_count()", nativeQuery = true)
	String getVillageWisePlotCount();

}
