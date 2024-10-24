package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.CropClassMaster;

public interface CropClassNameRepository extends JpaRepository<CropClassMaster, Long>{

	List<CropClassMaster> findByIsActiveTrueAndIsDeletedFalse();
}
