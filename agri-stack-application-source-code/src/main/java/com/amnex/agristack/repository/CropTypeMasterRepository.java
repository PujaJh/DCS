package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.CropTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CropTypeMasterRepository extends JpaRepository<CropTypeMaster, Long> {

	List<CropTypeMaster> findByIsActiveTrueAndIsDeletedFalse();

    List<CropTypeMaster> findByIsActiveTrueAndIsDeletedFalseOrderByCropTypeAsc();
}