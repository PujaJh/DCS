package com.amnex.agristack.entity;

import com.amnex.agristack.Enum.ConfigCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = { "active", "deleted" })

public class ConfigurationMasterHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long configurationHistoryId;

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

	@Transient
	private Integer configCodeValue;

}
