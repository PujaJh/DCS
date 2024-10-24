package com.amnex.agristack.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.amnex.agristack.entity.BaseEntity;
import com.amnex.agristack.entity.LanguageMaster;
import com.amnex.agristack.entity.StateLgdMaster;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Hardik.Siroya
 *
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class StateDefaultLanguageMapping extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "state_lgd_code", referencedColumnName = "state_lgd_code")
	private StateLgdMaster stateLgdMaster;

	@ManyToOne
	@JoinColumn(name = "language_id", referencedColumnName = "language_id")
	private LanguageMaster languageId;

}
