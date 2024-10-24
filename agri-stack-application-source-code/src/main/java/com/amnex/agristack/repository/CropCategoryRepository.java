package com.amnex.agristack.repository;

import com.amnex.agristack.entity.CropCategoryMaster;
import com.amnex.agristack.entity.SowingSeason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CropCategoryRepository extends JpaRepository<CropCategoryMaster, Long> {
	Optional<CropCategoryMaster> findByCropCategoryId(Long cropCategoryId);

	List<CropCategoryMaster> findByIsDeletedFalseOrderByModifiedOnDesc();

	List<CropCategoryMaster> findByIsActiveTrueAndIsDeletedFalseOrderByCropCategoryNameAsc();

	List<CropCategoryMaster> findByIsDeletedFalseOrderByCropCategoryIdDesc();

	List<CropCategoryMaster> findByIsDeletedFalseOrderByCropCategoryIdAsc();
}
