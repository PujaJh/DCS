package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amnex.agristack.entity.FarmerGrievanceReason;

@Repository
public interface FarmerGrievanceReasonRepository extends JpaRepository<FarmerGrievanceReason,Long>{
    
}
