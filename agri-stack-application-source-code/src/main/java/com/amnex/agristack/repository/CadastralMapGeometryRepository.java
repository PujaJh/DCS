package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.CadastralMapGeometry;

public interface CadastralMapGeometryRepository extends JpaRepository<CadastralMapGeometry, Long>{

	
	@Query(value = " select u from CadastralMapGeometry u "
			+ "	where u.villageLgdCode = :villageLgdCode "
			+ " and u.surveyNumber = :surveyNo and u.subSurveyNumber= :subSurveyNo ")
	List<CadastralMapGeometry> findByVillageLgdCodeAndSurveyNumberAndSubSurveyNumber(Integer villageLgdCode, String surveyNo,
			String subSurveyNo);

}
