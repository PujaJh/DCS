package com.amnex.agristack.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class UploadLandAndOwnershipFileHistory extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Lob
	@Type(type = "text")
	private String fileName;
	
	private Boolean isread;
	private Boolean containIssue;
	
	private Long uploadedBy;
	
	private Boolean isDeletedExistingData;
	
	@OneToOne
	private MediaMaster mediaMaster;
	

}
