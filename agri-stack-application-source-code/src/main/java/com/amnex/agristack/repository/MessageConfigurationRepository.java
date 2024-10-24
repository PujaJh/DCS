/**
 *
 */
package com.amnex.agristack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.Enum.MessageType;
import com.amnex.agristack.entity.MessageConfigurationMaster;

/**
 * @author kinnari.soni
 *
 */

public interface MessageConfigurationRepository extends JpaRepository<MessageConfigurationMaster, Long> {

	public Optional<MessageConfigurationMaster> findByTemplateType(String templateType);

	public Optional<MessageConfigurationMaster> findByTemplateId(String templateType);
	
	public Optional<MessageConfigurationMaster> findByMessageType(MessageType messageType);

}
