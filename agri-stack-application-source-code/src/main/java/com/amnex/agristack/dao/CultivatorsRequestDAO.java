package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class CultivatorsRequestDAO {

	private Long cpmdId;

	private Long seasonId;

	private Integer seasonStartYear;

	private Integer seasonEndYear;

	private Long parcelId;

	private Long cultivatorId;

	private Long surveyBy;

	private Integer cultivatorTypeId;

	private Double cultivatorArea;
}
