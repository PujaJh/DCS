package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.SegmentPlotMapping;

public interface SegmentPlotMappingRepository extends JpaRepository<SegmentPlotMapping, Long> {

	@Query(value = "select agri_stack.fn_segment_get_plot_by_user_village(:userId)", nativeQuery = true)
	String getPlotDetailsByUserId(Long userId);
//
//	@Query(value = "select agri_stack.fn_segment_get_segments_by_survey_number(:villageLgdCode,:surveyNumber,:lat,:lon)", nativeQuery = true)
//	String getSegmentsBySurveyNumber(Integer villageLgdCode, String surveyNumber, Double lat, Double lon);

	@Query(value = "select agri_stack.fn_segment_get_segment_plot_mapping_report(:villageCodes)", nativeQuery = true)
	String getSegmentPlotMappingReport(String villageCodes);

	@Query(value = "select agri_stack.fn_segment_get_segments_by_survey_number(:villageLgdCode,:surveyNo,:currentLat,:currentLong)", nativeQuery = true)
	String getNearestSegments(Integer villageLgdCode, String surveyNo, Double currentLat, Double currentLong);

	@Query(value = "select agri_stack.fn_segment_get_segment_by_current_location(:villageLgdCode,:segmentUniqueIds,:flag,:currentLat,:currentLong,:wkt)", nativeQuery = true)
	String getSegmentBasedOnCurrentLocation(Integer villageLgdCode, String segmentUniqueIds, String flag,
			Double currentLat, Double currentLong, String wkt);

}
