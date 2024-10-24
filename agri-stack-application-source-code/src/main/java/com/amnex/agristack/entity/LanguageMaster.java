package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity class representing language details.
 * Each instance corresponds to a specific language with its unique identifier, name, and code.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class LanguageMaster extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "language_id")
	private Long languageId;
	
	@Column(name="language")
	private String language;
	
	@Column(name="code")
	private String languageCode;
}
