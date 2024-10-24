package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.UploadLandAndOwnershipFileHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadLandAndOwnershipFileHistoryRepo extends JpaRepository<UploadLandAndOwnershipFileHistory, Long> {

	
	public UploadLandAndOwnershipFileHistory findByMediaMasterMediaId(String mediaId);
	
	public List<UploadLandAndOwnershipFileHistory> findAllByOrderByCreatedOnDesc();

	public List<UploadLandAndOwnershipFileHistory> findAllByUploadedByOrderByCreatedOnDesc(Long userId);
	

}
