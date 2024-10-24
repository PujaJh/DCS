/**
 *
 */
package com.amnex.agristack.Enum;

/**
 * @author kinnari.soni
 *
 */
public enum BoundaryLevelEnum {
	NOPARENT(new Long(0)),
	STATE(new Long(1)),
	DISTRICT(new Long(2)),
	SUBDISTRICT(new Long(3)),
	VILLAGE(new Long(4));

	public final Long levelCode;

	private BoundaryLevelEnum(Long levelCode) {
		this.levelCode = levelCode;
	}

	public long getLevelCode() {
		return this.levelCode;
	}

}
