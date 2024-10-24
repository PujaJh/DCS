package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class SurveyDetailCropDTO {
	private Long cropId;
	private Long cropVarietyId;
	private Long cropTypeId;
	private Double area;
	private Long unit;
	private Long cropStatusId;
	private Long numberOfTree;
	private Long cropCategory;
	private Long irrigationTypeId;
	private Integer irrigationSourceId;
	private List<String> media;
	private String remark;
	private Long areaTypeId;
	private Long areaType;
	private Boolean isUnutilizedArea;
	private Boolean isWasteArea;
	private String sowingDate;
	private Long naTypeId;
	private List<String> uploadedMedia;
	private Long cropClassId;
	private Boolean isHarvestedArea;
}
