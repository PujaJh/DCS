package com.amnex.agristack.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vividsolutions.jts.geom.Geometry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kinnari.soni
 *
 */
/**
 * @author kinnari.soni
 *
 */
@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class BoundaryMaster implements Serializable{

	@Id
	//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long boundaryId;

	@Column(name = "boundary_lgd_code")
	private Long boundaryLgdCode;

	private String boundaryName;

	@ManyToOne
	@JoinColumn(name = "boundary_level_code", referencedColumnName = "boundary_level_code")
	private BoundaryLevelMaster boundaryLevelCode;

	@Column(name = "parent_boundary_lgd_code")
	private Long parentBoundaryLgdCode;

	@ManyToOne
	@JoinColumn(name = "parent_boundary_level_code", referencedColumnName = "boundary_level_code")
	private BoundaryLevelMaster parentBoundaryLevelCode;

	private String geometry_wkt;

	private Geometry geom;

	public BoundaryMaster(Long boundaryLgdCode, String boundaryName, BoundaryLevelMaster boundaryLevelCode,
			Long parentBoundaryLgdCode, BoundaryLevelMaster parentBoundaryLevelCode) {
		super();
		this.boundaryLgdCode = boundaryLgdCode;
		this.boundaryName = boundaryName;
		this.boundaryLevelCode = boundaryLevelCode;
		this.parentBoundaryLgdCode = parentBoundaryLgdCode;
		this.parentBoundaryLevelCode = parentBoundaryLevelCode;
	}

	public BoundaryMaster() {
		super();
	}
}
