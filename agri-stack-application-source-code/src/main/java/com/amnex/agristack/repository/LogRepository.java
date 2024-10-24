package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.LogMaster;

public interface LogRepository extends JpaRepository<LogMaster, Long> {

}
