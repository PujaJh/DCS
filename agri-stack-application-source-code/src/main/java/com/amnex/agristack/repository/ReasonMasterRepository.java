package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amnex.agristack.entity.ConfigurationMaster;
import com.amnex.agristack.entity.ReasonMaster;

@Repository
public interface ReasonMasterRepository extends JpaRepository<ReasonMaster, Long> {

	List<ReasonMaster> findByIsActiveTrueAndIsDeletedFalse();

}
