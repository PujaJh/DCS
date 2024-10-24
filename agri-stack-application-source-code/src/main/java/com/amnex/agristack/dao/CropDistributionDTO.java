package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class CropDistributionDTO {
	private Integer userId;
    private Integer seasonId;
    private Integer startYear;
    private Integer endYear;
    private String cropId;
    private String cropVarietyId;
    private String territoryLevel;
}
