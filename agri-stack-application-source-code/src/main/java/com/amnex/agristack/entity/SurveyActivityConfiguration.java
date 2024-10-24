package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Date;
import java.util.Calendar;

/**
 * Entity class representing the configuration for survey activities within a specific season.
 * Each configuration includes an identifier, season ID, start date, end date, year, days,
 * and a reference to the survey activity master.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
public class SurveyActivityConfiguration extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "season_id")
	private Long seasonId;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "start_date")
	private Date startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Column(name = "end_date")
	private Date endDate;
	@Column(name = "year")
	private Long year;
	@Column(name = "days")
	private Integer days;


	@OneToOne
	@JoinColumn(name = "survey_activity_id", referencedColumnName = "id")
	private SurveyActivityMaster surveyActivity;
}
