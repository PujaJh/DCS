package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author majid.belim
 *
 * Entity class representing user availability for specific seasons.
 * Each user's availability is associated with a user ID, season ID, start year, end year, and availability status.
 * It also includes transient fields for total count and not assigned count.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class UserAvailability extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId")
	private UserMaster userId;

	@Column(name ="season_id")
	private Long seasonId;

	@Column(name ="season_start_year", columnDefinition = "VARCHAR(10)")
	private String seasonStartYear;

	@Column(name ="season_end_year", columnDefinition = "VARCHAR(10)")
	private String seasonEndYear;

	@Column(name = "is_available")
	private Boolean isAvailable;
	

	@Transient
	private Long totalCount;
	@Transient
	private Long notAssignCount;
}
