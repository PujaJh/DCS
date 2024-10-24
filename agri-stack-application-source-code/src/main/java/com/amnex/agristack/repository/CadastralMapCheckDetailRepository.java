package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.CadastralMapCheckDetail;

public interface CadastralMapCheckDetailRepository extends JpaRepository<CadastralMapCheckDetail, Long> {

	@Query(value = "select agri_stack.fn_get_cadastrail_map_by_user(:userId)", nativeQuery = true)
	String getCadastrialDataByUser(Integer userId);

	@Query(value = " select u from CadastralMapCheckDetail u "
			+ "	where u.villageLgdCode = :villageLgdCode "
			+ " and u.surveyNo = :surveyNo and u.subSurveyNo= :subSurveyNo "
			+ " and u.nearestPolylineWkt = :nearestPolylineWkt ")
	List<CadastralMapCheckDetail> findByVillageLgdCodeAndSurveyNoAndSubSurveyNoAndNearestPolylineWkt(Integer villageLgdCode,
			String surveyNo, String subSurveyNo, String nearestPolylineWkt);

	@Query(value = "select agri_stack.fn_get_cadastral_survey_report(:userId)", nativeQuery = true)
	String getCadastrialSurveyReport(int userId);
	
}
