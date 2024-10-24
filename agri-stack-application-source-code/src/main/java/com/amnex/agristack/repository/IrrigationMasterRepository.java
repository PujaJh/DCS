package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.IrrigationMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IrrigationMasterRepository extends JpaRepository<IrrigationMaster, Long> {
	
	public List<IrrigationMaster> findByIsActiveTrueAndIsDeletedFalse();

	List<IrrigationMaster> findByIsActiveTrueAndIsDeletedFalseOrderByIrrigationTypeAsc();
}
