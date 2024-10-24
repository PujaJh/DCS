package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.amnex.agristack.entity.FarmerGrievance;

@Repository
public interface FarmerGrievanceRepository extends JpaRepository<FarmerGrievance, Long> {

    public List<FarmerGrievance> findAllByUserId(Long userId);

}
