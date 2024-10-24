package com.amnex.agristack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.amnex.agristack.Enum.ImagePredictionConfigCode;

import com.amnex.agristack.entity.ImagePredictionConfigurationMaster;

public interface ImagePredictionConfigurationMasterRepository extends JpaRepository<ImagePredictionConfigurationMaster, Long>{
	Optional<ImagePredictionConfigurationMaster> findByImagePredictionConfigCode(ImagePredictionConfigCode configCode);
}
