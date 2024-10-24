package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.FarmAreaUnitType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FarmAreaUnitTypeRepository extends JpaRepository<FarmAreaUnitType, Long> {
	
	 List<FarmAreaUnitType> findByIsActiveTrueAndIsDeletedFalse();

    List<FarmAreaUnitType> findByIsActiveTrueAndIsDeletedFalseOrderByUnitTypeAsc();
}
