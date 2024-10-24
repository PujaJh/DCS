package com.amnex.agristack.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a village in the LGD master data.
 * It stores information such as the village ID, village LGD code,
 * village name, state LGD code, district LGD code, sub-district LGD code.
 */
@Entity
@Table(indexes = @Index(name = "village_lgd_code_index", columnList = "village_lgd_code"))
@Data
@NoArgsConstructor
public class VillageLgdMaster implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "village_id")
	private Long villageId;

	@Column(name = "last_synced_count")
	private Integer lastSyncedCount;

	@Column(name = "last_synced_on")
	private Date lastSyncedOn;

	@Column(name = "village_lgd_code")
	private Long villageLgdCode;

	@Column(name = "village_name")
	private String villageName;

	@ManyToOne
	@JoinColumn(name = "state_lgd_code", referencedColumnName = "state_lgd_code")
	private StateLgdMaster stateLgdCode;

	@ManyToOne
	@JoinColumn(name = "district_lgd_code", referencedColumnName = "district_lgd_code")
	private DistrictLgdMaster districtLgdCode;

	@ManyToOne
	@JoinColumn(name = "sub_district_lgd_code", referencedColumnName = "sub_district_lgd_code")
	private SubDistrictLgdMaster subDistrictLgdCode;

	@Column(name = "village_geometry")
	private String villageGeometry;

	public VillageLgdMaster(Long villageLGDCode, String villageName, StateLgdMaster stateLgdCode,
			DistrictLgdMaster districtLgdCode, SubDistrictLgdMaster subDistrictLgdCode) {
		super();
		this.villageLgdCode = villageLGDCode;
		this.villageName = villageName;
		this.stateLgdCode = stateLgdCode;
		this.districtLgdCode = districtLgdCode;
		this.subDistrictLgdCode = subDistrictLgdCode;
	}
}
