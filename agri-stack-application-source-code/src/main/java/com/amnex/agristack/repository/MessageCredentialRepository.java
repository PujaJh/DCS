package com.amnex.agristack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.MessageCredentialMaster;

/**
 * @author kinnari.soni
 *
 */

public interface MessageCredentialRepository extends JpaRepository<MessageCredentialMaster, Long> {

	public Optional<MessageCredentialMaster> findByMessageCredentialType(String credentailType);
}
