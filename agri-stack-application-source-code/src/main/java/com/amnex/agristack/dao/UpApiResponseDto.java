package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpApiResponseDto {
	@JsonProperty("uniquecode")
	private String uniqueCode;
	@JsonProperty("village_LGD_Code")
	private String villageLGDCode;
	@JsonProperty("owner_Number")
	private String ownerNumber;
	@JsonProperty("owner_Name")
	private String ownerName;
	@JsonProperty("indentifier_Name")
	private String indentifierName;
	@JsonProperty("indentifier_type")
	private String indentifierType;
	@JsonProperty("total_Area")
	private String totalArea;
	@JsonProperty("extend")
	private String extend;
	@JsonProperty("area_unit")
	private String areaUnit;
	@JsonProperty("land_Usage_type")
	private String landUsageType;
	@JsonProperty("ownership_type")
	private String ownershipType;
	@JsonProperty("khasrano")
	private String khasraNo;
}
