package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amnex.agristack.entity.SurveyActivityLog;

@Repository
public interface SurveyActivityLogRepository extends JpaRepository<SurveyActivityLog,Long>{
    
}
