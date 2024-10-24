/**
 *
 */
package com.amnex.agristack.Enum;

/**
 * @author majid.belim
 *
 */
public enum FarmerRegistrationTypeEnum {
	SELF("SELF"),
	BULK_UPLOAD("BULK UPLOAD"),
	OPERATOR("OPERATOR");

	private String value;

	FarmerRegistrationTypeEnum(String data) {
		this.value = data;
	}

	public String getValue()
	{
		return this.value;
	}
}
