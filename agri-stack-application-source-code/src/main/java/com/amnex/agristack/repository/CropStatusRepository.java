package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.CropStatusMaster;
import com.amnex.agristack.entity.CropStatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CropStatusRepository extends JpaRepository<CropStatusMaster, Long> {

	List<CropStatusMaster> findByIsActiveTrueAndIsDeletedFalse();
    List<CropStatusMaster> findByIsActiveTrueAndIsDeletedFalseOrderByCropStatusTypeAsc();
}
