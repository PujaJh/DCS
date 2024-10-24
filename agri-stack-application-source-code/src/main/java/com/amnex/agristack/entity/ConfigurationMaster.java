package com.amnex.agristack.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;

import com.amnex.agristack.Enum.ConfigCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


/**
 * Entity class representing configuration settings.
 * It stores information such as the configuration ID, key, value, code, 
 * activation status, deletion status, description, creation and modification timestamps, and creator/modifier details.
 */
@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = { "active", "deleted" })

public class ConfigurationMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long configurationId;

	private String configKey;

	private String configValue;

	private ConfigCode configCode;

	private boolean isActive;

	private boolean isDeleted;
	
	private String description;

	@CreationTimestamp
	private Timestamp createdOn;

	private String createdBy;

	@UpdateTimestamp
	private Timestamp modifiedOn;

	private String modifiedBy;

	@Transient
	private Integer configCodeValue;

}
