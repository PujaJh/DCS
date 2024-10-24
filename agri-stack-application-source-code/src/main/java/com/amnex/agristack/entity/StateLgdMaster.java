package com.amnex.agristack.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing state LGD master data.
 * Each state has a unique identifier, name, LGD code.
 */

@Data
@Entity
@NoArgsConstructor
public class StateLgdMaster implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "state_id")
	private Long stateId;

	@Column(name = "last_synced_count")
	private Integer lastSyncedCount;

	@Column(name = "last_synced_on")
	private Date lastSyncedOn;

	@Column(name = "state_name")
	private String stateName;

	@Column(name = "state_lgd_code")
	private Long stateLgdCode;

	@Column(name = "state_geometry")
	private String stateGeometry;
	
	@Transient
	private Long districtCount;
	
	@Transient
	private Long subDistrictCount;
	
	@Transient
	private Long villageCount;

	public StateLgdMaster(Long stateLGDCode,String stateName) {
		super();
		this.stateName = stateName;
		this.stateLgdCode = stateLGDCode;
	}


}
