package com.amnex.agristack.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing district details.
 * Each instance corresponds to a specific district with its unique identifier, name, LGD code.
 */

@Entity
@Data
@NoArgsConstructor
public class DistrictLgdMaster implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "district_id")
	private Long districtId;

	@Column(name = "last_synced_count")
	private Integer lastSyncedCount;

	@Column(name = "last_synced_on")
	private Date lastSyncedOn;

	@Column(name = "district_lgd_code")
	private Long districtLgdCode;

	@Column(name = "district_name")
	private String districtName;

	@ManyToOne
	@JoinColumn(name = "state_lgd_code", referencedColumnName = "state_lgd_code")
	private StateLgdMaster stateLgdCode;

	@Column(name = "district_geometry")
	private String districtGeometry;

	public DistrictLgdMaster(Long districtLGDCode, String districtName, StateLgdMaster stateLgdCode) {
		super();
		this.districtLgdCode = districtLGDCode;
		this.districtName = districtName;
		this.stateLgdCode = stateLgdCode;
	}


}
