package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
public class EarlyLateVillage extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "season_id")
	private Long seasonId;
	@Column(name = "state_lgd_code")
	private Integer stateLGDCode;

	@Column(name = "district_lgd_code")
	private Integer districtLGDCode;

	@Column(name = "sub_district_lgd_code")
	private Integer subDistrictLGDCode;

	@Column(name = "village_lgd_codes")
	@Type(type = "text")
	private String villageLGDCodes;
	@Column(name = "start_year")
	private Integer startYear;
	@Column(name = "end_year")
	private Integer endYear;
}
