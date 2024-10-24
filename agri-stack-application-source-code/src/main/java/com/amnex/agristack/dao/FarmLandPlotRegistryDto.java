package com.amnex.agristack.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.amnex.agristack.entity.LocationTypeMaster;
import com.amnex.agristack.entity.PlotStatusMaster;
import com.amnex.agristack.entity.VillageLgdMaster;

import lombok.Data;

import javax.persistence.Column;

@Data
public class FarmLandPlotRegistryDto {

	private Long farmLandPlotRegisterId;

	private String landParcelId;

	private String parcelGeometry;

	private String landParcelUlpin;

	private String plotGeometry;

	private Double plotArea;

	//	private UnitTypeMaster utUnitTypeMasterId;

	private PlotStatusMaster psPlotStatusId;

	private Integer villageLgdCode;

	private LocalDateTime synchronisationDate;

	private LocalDateTime updateDate;

	private LocationTypeMaster locationTypeMasterId;

	private Integer ulbCode;

	private Integer wardCode;

	private String farmlandId;

	private Integer circleCode;

	private Integer maujaCode;

	private String landType;

	private VillageLgdMaster village;

	private List<FarmerRegistryCustomFieldDto> farmerRegistryCustomFieldDtos;

	private String surveyNumber;

	private String subSurveyNumber;

	private String ownerShipType;

	private String landUsageType;

	private String plotId;

	private Integer isOwnerShipData;

	private String seasonName;
	private Integer year;
	private Long seasonId;

}
