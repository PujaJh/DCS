package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class SurveyCropDetailDAO {
	public Double cropArea;
	public Boolean isTree;
	public Boolean isUnutilizedArea;
	public Boolean isWasteArea;
	public Integer noOfTrees;
	public String cropRemarks;
	public Long cropStageId;
	public String cropStage;
	public Integer unitId;
	public Long cropId;
	public String cropCode;
	public String cropName;
	public long cropCategoryId;
	public String cropCategory;
	public long cropVarietyId;
	public String cropVariety;
	public long cropTypeId;
	public String cropType;
	public long irrigationSourceId;
	public String irrigationSource;
	public Long irrigationTypeId;
	public String irrigationType;
	public Long landTypeId;
	public String landType;
	public List<MediaDAO> media;
	public String unitName;
	public Long areaTypeId;
	public String areaType;
	public String sowingDate;
	
	public Long nonAgriTypeId;
	public String nonAgriTypeName;
	public Long cropClassId;
	public String cropClassName;

}
