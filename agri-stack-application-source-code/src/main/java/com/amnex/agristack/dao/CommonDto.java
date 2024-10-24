package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class CommonDto {

	private List<Long> boundaryLgdCodes;
	private Long parentBoundaryLgdCode;
	private List<Long> parentBoundaryLgdCodes;
}
