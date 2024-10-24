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
 * Entity class representing sub-district LGD master data.
 * Each sub-district has a unique identifier
 * LGD code, name, state LGD code, district LGD code.
 */


@Entity
@Data
@NoArgsConstructor
public class SubDistrictLgdMaster implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sub_district_id")
	private Long subDistrictId;

	@Column(name = "last_synced_count")
	private Integer lastSyncedCount;

	@Column(name = "last_synced_on")
	private Date lastSyncedOn;

	@Column(name = "sub_district_lgd_code")
	private Long subDistrictLgdCode;

	@Column(name = "sub_district_name")
	private String subDistrictName;

	@ManyToOne
	@JoinColumn(name = "state_lgd_code", referencedColumnName = "state_lgd_code")
	private StateLgdMaster stateLgdCode;

	@ManyToOne
	@JoinColumn(name = "district_lgd_code", referencedColumnName = "district_lgd_code")
	private DistrictLgdMaster districtLgdCode;

	@Column(name = "sub_district_geometry")
	private String subDistrictGeometry;

	public SubDistrictLgdMaster(Long subDistrictLGDCode, String subDistrictName, StateLgdMaster stateLgdCode,
								DistrictLgdMaster districtLgdCode) {
		super();
		this.subDistrictLgdCode = subDistrictLGDCode;
		this.subDistrictName = subDistrictName;
		this.stateLgdCode = stateLgdCode;
		this.districtLgdCode = districtLgdCode;
	}
}
