package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amnex.agristack.entity.ExceptionAuditLog;

@Repository
public interface ExceptionAuditRepository extends JpaRepository<ExceptionAuditLog, Long> {

}
