package com.amnex.agristack.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * Entity class representing the mapping between land parcel survey crops and media.
 * Each instance corresponds to a specific mapping with its unique identifier, 
 * survey crop ID, and media ID.
 */

@Data
@Entity
public class LandParcelSurveyCropMediaMapping {
	
	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long surveyCropId;
    private String mediaId;
}
