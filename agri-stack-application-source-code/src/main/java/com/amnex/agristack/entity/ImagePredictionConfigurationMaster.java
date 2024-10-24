package com.amnex.agristack.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.Enum.ImagePredictionConfigCode;

import lombok.Data;

@Entity
@Table(schema = "public")
@Data
public class ImagePredictionConfigurationMaster {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long imagePredictionConfigurationMasterId;

	private String configKey;

	private String configValue;

	private ImagePredictionConfigCode imagePredictionConfigCode;

	private boolean isActive;

	private boolean isDeleted;

	private String description;

	@CreationTimestamp
	private Timestamp createdOn;

	private String createdBy;

	@UpdateTimestamp
	private Timestamp modifiedOn;

	private String modifiedBy;
	

}
