package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.DisasterDamageAreaTypeMaster;

public interface DisasterDamageAreaTypeMasterRepository extends JpaRepository<DisasterDamageAreaTypeMaster, Long>{
	
	List<DisasterDamageAreaTypeMaster> findByIsActiveTrueAndIsDeletedFalseOrderByDisasterDamageAreaTypeNameAsc();
}