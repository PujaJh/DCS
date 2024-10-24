package com.amnex.agristack.entity;

import com.amnex.agristack.Enum.SurveyActivityEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Entity class representing master data for survey activities.
 * Each activity includes an identifier, activity name, and activity code.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class SurveyActivityMaster extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name= "activity_name")
	private String activityName;

	@Column(name = "activity_code")
	private SurveyActivityEnum activityCode;

}
