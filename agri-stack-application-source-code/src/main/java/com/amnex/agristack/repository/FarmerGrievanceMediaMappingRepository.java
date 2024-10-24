package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amnex.agristack.entity.FarmerGrievanceMediaMapping;

@Repository
public interface FarmerGrievanceMediaMappingRepository extends JpaRepository<FarmerGrievanceMediaMapping, Long> {

}
