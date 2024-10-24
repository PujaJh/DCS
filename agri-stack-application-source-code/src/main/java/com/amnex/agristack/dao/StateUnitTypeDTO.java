package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class StateUnitTypeDTO {

	private Long unitTypeMasterId;
	private String unitTypeDescEng;
	private String unitTypeDescLl;
	private Long stateLgdCode;
	private double unitValue;
	private boolean isDefault;
}
