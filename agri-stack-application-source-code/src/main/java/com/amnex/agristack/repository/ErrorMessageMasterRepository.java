package com.amnex.agristack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.amnex.agristack.entity.ErrorMessageMaster;

public interface ErrorMessageMasterRepository extends JpaRepository<ErrorMessageMaster, Long> {
	Optional<ErrorMessageMaster> findByErrorCode(int errorCode);
	
}
