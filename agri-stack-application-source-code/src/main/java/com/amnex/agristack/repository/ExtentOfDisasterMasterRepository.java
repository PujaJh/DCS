package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.ExtentOfDisasterMaster;

public interface ExtentOfDisasterMasterRepository extends JpaRepository<ExtentOfDisasterMaster, Long>{
	
	List<ExtentOfDisasterMaster> findByIsActiveTrueAndIsDeletedFalseOrderByExtentOfDisasterIdAsc();
}
