package com.amnex.agristack.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * Entity class representing details of land parcel survey crop.
 * Each instance corresponds to a specific survey crop detail with various attributes including
 * land parcel survey ID, crop ID, crop variety ID, crop type ID, crop category ID, area, unit,
 * land type ID, unutilized area flag, waste area flag, tree flag, harvested area flag,
 * number of trees, crop stage, irrigation type ID, irrigation source ID, remarks,
 * area type ID, sowing date, NA type ID, and crop class ID.
 */

@Entity
@Data
public class LandParcelSurveyCropDetail {
	
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cropSdId;

//  commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL
	// @OneToOne
	// @JoinColumn(name = "lpsd_id", referencedColumnName = "lpsdId")
	private Long lpsdId;

//  commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL
	// @OneToOne
	// @JoinColumn(name = "crop_id", referencedColumnName = "crop_Id")
	private Long cropId;

//  commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL
	// @OneToOne
	// @JoinColumn(name = "crop_variety_id", referencedColumnName =
	// "crop_variety_id")
	private Long cropVarietyId;

//  commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL
	// @OneToOne
	// @JoinColumn(name = "crop_type_id", referencedColumnName = "cropTypeId")
	private Long cropTypeId;

//  commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL
	// @OneToOne
	// @JoinColumn(name = "crop_category_id", referencedColumnName =
	// "crop_category_id")
	private Long cropCategoryId;
	private Double area;
	private Long unit;

	private Integer landtypeId;
	private Boolean isUnutilizedArea = false;
	private Boolean isWasteArea = false;
	private Boolean isTree = false;
	private Boolean isHarvestedArea = false;
	private Long noOfTree;
	private Long cropStage;

//  commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL
	// @OneToOne
	// @JoinColumn(name = "irrigation_type_id", referencedColumnName =
	// "irrigationId")
	private Long irrigationTypeId;

//  commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL
	// @OneToOne
	// @JoinColumn(name = "irrigation_source_id", referencedColumnName =
	// "irrigationId")
	private Integer irrigationSourceId;
	private String remarks;

//  commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL
	// @OneToOne
	// @JoinColumn(name = "area_type_id", referencedColumnName = "id")
	private Long areaTypeId;

	private Timestamp sowingDate;

//  commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL
	// @OneToOne
	// @JoinColumn(name = "na_type_id", referencedColumnName = "id")
	private Long naTypeId;

//  commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL
	// @OneToOne
	// @JoinColumn(name = "crop_class_id", referencedColumnName = "crop_class_id")
	private Long cropClassId;
}
