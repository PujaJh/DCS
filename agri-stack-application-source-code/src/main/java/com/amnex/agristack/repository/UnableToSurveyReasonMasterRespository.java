package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.UnableToSurveyReasonMaster;

public interface UnableToSurveyReasonMasterRespository extends JpaRepository<UnableToSurveyReasonMaster, Long>{
	
	List<UnableToSurveyReasonMaster> findByIsActiveTrue();
	
	@Override
	Optional<UnableToSurveyReasonMaster> findById(Long id);

}
