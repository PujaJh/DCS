package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.LandParcelSurveyMaster;

import java.sql.Timestamp;

public interface SummaryReportRepository extends JpaRepository<LandParcelSurveyMaster, Long> {
	
	@Query(value = "select agri_stack.fn_mis_misc_for_hourly_status_report_v1(:seasonId,:startYear,:endYear,:page,:limit)", nativeQuery = true)
	String getMiscForHourlyStatusReportV1(Long seasonId, Long startYear, Long endYear,Integer page,Integer limit);


	@Query(value = "select agri_stack.fn_mis_misc_for_hourly_status_reportV1(:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getMiscForHourlyStatusReport(Long seasonId, Long startYear, Long endYear);


	@Query(value = "select agri_stack.fn_get_surveyors_report_v1(:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getSurveyorActivityReport(Long seasonId, Long startYear, Long endYear);

	@Query(value = "select agri_stack.fn_get_total_surveyors_count_v1(:seasonId,:startYear,:endYear)", nativeQuery = true)
	String getSurveyorActivityTotalCount(Long seasonId, Long startYear, Long endYear);

}
