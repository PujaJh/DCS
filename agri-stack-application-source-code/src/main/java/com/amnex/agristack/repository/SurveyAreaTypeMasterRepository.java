package com.amnex.agristack.repository;

import com.amnex.agristack.entity.SurveyAreaTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SurveyAreaTypeMasterRepository extends JpaRepository<SurveyAreaTypeMaster, Long> {

}
