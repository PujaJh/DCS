package com.amnex.agristack.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.UserDeviceIntegrityDetails;

public interface UserDeviceIntegrityDetailsRepository extends JpaRepository<UserDeviceIntegrityDetails, Long>{

	Optional<UserDeviceIntegrityDetails> findByDeviceId(String deviceId);
	Optional<UserDeviceIntegrityDetails> findByUniqueKey(String uniqueKey);
	
	@Query(nativeQuery = true,value="select udid.device_id " + 
			"	 from agri_stack.user_master um	" + 
			"inner join agri_stack.user_device_integrity_details udid on  um.imei_number=udid.device_id " + 
			"where udid.token=:token and user_id=:userId ")
	List<String> checkIntegrityToken(Long userId,String token);
	
}
