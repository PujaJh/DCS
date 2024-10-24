package com.amnex.agristack.repository;

import com.amnex.agristack.entity.SurveyActivityMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SurveyActivityMasterRepository extends JpaRepository<SurveyActivityMaster, Long> {

	List<SurveyActivityMaster> findAll();

    List<SurveyActivityMaster> findByIsActiveTrueAndIsDeletedFalseOrderByActivityNameAsc();
}
