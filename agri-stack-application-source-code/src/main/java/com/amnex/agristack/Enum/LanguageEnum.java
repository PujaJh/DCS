package com.amnex.agristack.Enum;

public enum LanguageEnum {
	  EN("English"),GU("Gujarati"),HI("Hindi"),BN("Bengali"),MR("Marathi"),TE("Telugu"),TA("Tamil"),UR("Urdu"),KN("Kannada"),OD("Odia"),ML("Malayalam"),PA("Punjabi"),AS("Assamese"),MA("Maithili"),SA("Sanskrit");

	private String value;

	public String getValue()
	{
		return this.value;
	}

	private LanguageEnum(String value)
	{
		this.value = value;
	}
}
