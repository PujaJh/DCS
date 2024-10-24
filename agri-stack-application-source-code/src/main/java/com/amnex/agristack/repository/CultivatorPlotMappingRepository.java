package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.CultivatorPlotMappingDetail;

public interface CultivatorPlotMappingRepository extends JpaRepository<CultivatorPlotMappingDetail, Long> {

}
