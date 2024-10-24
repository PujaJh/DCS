package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.UPRoRDataUploadLog;

public interface UPRoRDataUploadLogRepository  extends JpaRepository<UPRoRDataUploadLog, Long> {

	List<UPRoRDataUploadLog> findByVillageLgdCode(Long villageLgdCode);

	List<UPRoRDataUploadLog> findByIsUploadedIsNull();

	List<UPRoRDataUploadLog> findByIsUploadedIsNullAndIsActiveTrue();

	List<UPRoRDataUploadLog> findByIsUploadedFalseAndIsActiveTrue();

	List<UPRoRDataUploadLog> findByIsUploadedIsNullAndIsActiveTrueAndStateLgdCode(Long stateLgdCode);

	List<UPRoRDataUploadLog> findByIsUploadedIsNullAndIsActiveTrueAndReUploadTrueAndIsReUploadDoneIsNull();
	List<UPRoRDataUploadLog> findByIsMarkNADoneIsNullAndIsActiveTrueAndIsNaMarkIsTrue();
	List<UPRoRDataUploadLog> findByReUploadTrueAndIsReUploadDoneIsNull();

	List<UPRoRDataUploadLog> findByFetchCensusDataTrueAndCensusDataUploadedIsNull();

	@Query(value = "select agri_stack.fn_ror_get_upload_status()", nativeQuery = true)
	String getRoRUploadStatus();

	List<UPRoRDataUploadLog> findByIsUploadedIsNullAndIsActiveTrueOrderByPriorityAsc();
}
