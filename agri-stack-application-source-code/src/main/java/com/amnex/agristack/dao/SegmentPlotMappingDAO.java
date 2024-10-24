package com.amnex.agristack.dao;

import java.util.List;

import com.amnex.agristack.service.SegmentOwnerMappingDAO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class SegmentPlotMappingDAO {

	private Integer villageLgdCode;
	private String villageName;
	private Long userId;
	private Long farmlandPlotRegistryId;
	private String farmlandId;	
	private String surveyNumber;
	private String subSurveyNumber;
	
	private String geometry;
	private String plotGeom;
	private String segmentNumber;
	private String segmentUniqueId;
	private Double area;
	private Long segmentId;
	
	private Boolean validGeometry;
	
	private List<SegmentOwnerMappingDAO> owners;
	
	private Double currentLat;
	private Double currentLong;
	
	private List<String> segmentUniqueIds;
	
	private String flag;
	
	
}
