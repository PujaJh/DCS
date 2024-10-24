package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.amnex.agristack.Enum.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 *
 *         19 Feb 2024
 *         
 * Entity class representing error message details.
 * Each instance corresponds to a specific error message with its unique identifier,
 * error code, error message, and error description.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class ErrorMessageMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long errorMessageMasterId;
	
	private int errorCode;
	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@Column(columnDefinition = "TEXT")
	private String errorDescription;

}
