package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.CultivatorTypeMaster;

public interface CultivatorTypeRepository extends JpaRepository<CultivatorTypeMaster, Long> {

	List<CultivatorTypeMaster> findByIsActiveTrueAndIsDeletedFalse();

}
