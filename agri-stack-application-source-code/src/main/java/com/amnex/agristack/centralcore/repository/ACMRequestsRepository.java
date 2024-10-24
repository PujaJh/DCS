package com.amnex.agristack.centralcore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.centralcore.entity.ACMRequests;

public interface ACMRequestsRepository extends JpaRepository<ACMRequests, Long> {
    public Optional<ACMRequests> findByRequestId(String requestId);
}
