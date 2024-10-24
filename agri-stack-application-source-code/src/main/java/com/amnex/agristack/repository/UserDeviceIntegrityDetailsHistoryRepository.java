package com.amnex.agristack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.centralcore.entity.UserDeviceIntegrityDetailsHistory;

public interface UserDeviceIntegrityDetailsHistoryRepository extends JpaRepository<UserDeviceIntegrityDetailsHistory, Long>{
	
	Optional<UserDeviceIntegrityDetailsHistory> findByUniqueKeyAndDeviceId(String uniqueKey,String deviceId);

}
