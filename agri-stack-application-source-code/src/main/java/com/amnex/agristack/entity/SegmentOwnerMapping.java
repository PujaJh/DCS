package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.Type;

import lombok.Data;

/**
 * Entity class representing the mapping of segment owners to segments.
 * Each mapping has a unique identifier, owner name, owner number,
 * and segment plot mapping ID.
 */

@Entity
@Data
public class SegmentOwnerMapping extends BaseEntity {


	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "segment_owner_mapping_id")
	private Long segmentOwnerMappingId;
	
	@Lob
	@Type(type = "text")
	@Column(name = "owner_name")
	private String ownerName;

	@Column(name = "owner_number")
	private Integer ownerNumber;
	
	@Column(name = "segment_plot_mapping_id")
	private Long segmentPlotMappingId;

}
