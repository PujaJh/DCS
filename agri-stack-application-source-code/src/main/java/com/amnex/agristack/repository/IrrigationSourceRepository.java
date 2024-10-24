package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.IrrigationSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IrrigationSourceRepository extends JpaRepository<IrrigationSource, Integer> {

	List<IrrigationSource> findByIsActiveTrueAndIsDeletedFalse();

	List<IrrigationSource> findByIsActiveTrueAndIsDeletedFalseOrderByIrrigationTypeAsc();
}
