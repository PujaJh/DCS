package com.amnex.agristack.repository;


import com.amnex.agristack.entity.ValidationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ValidationLogRepository extends JpaRepository<ValidationLog, Long> {
    // You can define custom query methods here, for example:

    // Find logs by recordId
    List<ValidationLog> findByRecordId(UUID recordId);

    // Find logs by status
    List<ValidationLog> findByStatus(String status);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO api_tokens (token, token_type, service_name, issued_at, expires_at, is_active) " +
            "VALUES (:token, :tokenType, :serviceName, :issuedAt, :expiresAt, :isActive)", nativeQuery = true)
    void insertToken(@Param("token") String token,
                     @Param("tokenType") String tokenType,
                     @Param("serviceName") String serviceName,
                     @Param("issuedAt") Timestamp issuedAt,
                     @Param("expiresAt") Timestamp expiresAt,
                     @Param("isActive") Boolean isActive);


    @Modifying
    @Transactional
    @Query(value = "SELECT token FROM api_tokens WHERE service_name = :serviceName AND is_active = true", nativeQuery = true)
    String getActiveTokenByServiceName(@Param("serviceName") String serviceName);

}
