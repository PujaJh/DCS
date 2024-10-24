package com.amnex.agristack.repository;

import com.amnex.agristack.entity.MediaMasterUserMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.LandParcelSurveyCropMediaMapping;

public interface UserMediaMappingRepository
        extends JpaRepository<MediaMasterUserMapping, Long> {

}
