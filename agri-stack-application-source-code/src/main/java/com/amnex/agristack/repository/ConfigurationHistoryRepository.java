package com.amnex.agristack.repository;

import com.amnex.agristack.entity.ConfigurationMasterHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConfigurationHistoryRepository extends JpaRepository<ConfigurationMasterHistory, Long> {

}