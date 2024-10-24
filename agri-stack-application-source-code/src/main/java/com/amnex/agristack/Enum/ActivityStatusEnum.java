/**
 * 
 */
package com.amnex.agristack.Enum;

/**
 * @author majid.belim
 *
 */
public enum ActivityStatusEnum {
	SURVEY_COMPLETED("Survey Completed"),
	survey_rejected("Survey Rejected");
	private String value;

	ActivityStatusEnum(String data) {
		this.value = data;
	}
	public String getValue()
	{
		return this.value;
	}
}
