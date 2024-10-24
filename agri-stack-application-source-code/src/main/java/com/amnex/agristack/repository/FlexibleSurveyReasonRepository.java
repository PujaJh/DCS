package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.ConfigurationMaster;
import com.amnex.agristack.entity.FlexibleSurveyReasonMaster;

public interface FlexibleSurveyReasonRepository extends JpaRepository<FlexibleSurveyReasonMaster, Long> {

	List<FlexibleSurveyReasonMaster> findByIsActiveTrueAndIsDeletedFalse();

}
