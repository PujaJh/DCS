/**
 * 
 */
package com.amnex.agristack.Enum;

/**
 * @author majid.belim
 *
 */
public enum ActivityTypeEnum {
	CROP_SURVEY("Crop Survey");
	private String value;

	ActivityTypeEnum(String data) {
		this.value = data;
	}
	public String getValue()
	{
		return this.value;
	}
}
