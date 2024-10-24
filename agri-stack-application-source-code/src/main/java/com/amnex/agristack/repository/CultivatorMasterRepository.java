package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.CultivatorMaster;

public interface CultivatorMasterRepository extends JpaRepository<CultivatorMaster, Long> {

	List<CultivatorMaster> findByIsActiveTrueAndIsDeletedFalseAndVillageLgdCodeIn(
			List<Long> villageLgdCodeList);

}
