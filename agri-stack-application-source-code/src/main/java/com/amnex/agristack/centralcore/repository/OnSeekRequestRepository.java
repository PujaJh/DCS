package com.amnex.agristack.centralcore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.centralcore.entity.OnSeekRequestData;

public interface OnSeekRequestRepository  extends JpaRepository<OnSeekRequestData, Long>{

	OnSeekRequestData findByReferenceId(String refId);

}
