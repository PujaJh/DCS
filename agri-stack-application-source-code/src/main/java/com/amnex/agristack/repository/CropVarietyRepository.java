package com.amnex.agristack.repository;

import com.amnex.agristack.entity.CropVarietyMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CropVarietyRepository extends JpaRepository<CropVarietyMaster, Long> {
	@Override
	Optional<CropVarietyMaster> findById(Long aLong);

	Optional<CropVarietyMaster> findByCropVarietyId(Long cropCategoryId);

	List<CropVarietyMaster> findByIsDeletedFalseOrderByModifiedOnDesc();

	List<CropVarietyMaster> findByCropMaster_CropIdAndIsActiveTrueAndIsDeletedFalse(Long cropId);
	
	List<CropVarietyMaster> findByIsActiveTrueAndIsDeletedFalseAndCropMaster_CropIdIn(List<Long> cropIdList);

	List<CropVarietyMaster> findByIsActiveTrueAndIsDeletedFalseOrderByCropVarietyNameAsc();

	List<CropVarietyMaster> findByIsDeletedFalseOrderByCropVarietyIdAsc();
}
