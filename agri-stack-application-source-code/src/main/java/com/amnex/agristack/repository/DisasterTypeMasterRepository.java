package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.DisasterTypeMaster;

public interface DisasterTypeMasterRepository extends JpaRepository<DisasterTypeMaster, Long>{
	
	List<DisasterTypeMaster> findByIsActiveTrueAndIsDeletedFalseOrderByDisasterTypeNameAsc();
}