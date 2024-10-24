/**
 *
 */
package com.amnex.agristack.dao.common;

import lombok.Data;

/**
 * @author kinnari.soni
 *
 */

@Data
public class BoundaryMasterDAO {

	private Long boundaryLgdCode;

	private String boundaryName;

	private Long boundaryLevelCode;

	private Long parentBoundaryLgdCode;

	private Long parentBoundaryLevelCode;

	public BoundaryMasterDAO(Long boundaryLgdCode, String boundaryName, Long parentBoundaryLgdCode) {
		super();
		this.boundaryLgdCode = boundaryLgdCode;
		this.boundaryName = boundaryName;
		this.parentBoundaryLgdCode = parentBoundaryLgdCode;
	}

	public BoundaryMasterDAO(Long boundaryLgdCode, String boundaryName, Long boundaryLevelCode,
			Long parentBoundaryLgdCode, Long parentBoundaryLevelCode) {
		super();
		this.boundaryLgdCode = boundaryLgdCode;
		this.boundaryName = boundaryName;
		this.boundaryLevelCode = boundaryLevelCode;
		this.parentBoundaryLgdCode = parentBoundaryLgdCode;
		this.parentBoundaryLevelCode = parentBoundaryLevelCode;
	}
}
