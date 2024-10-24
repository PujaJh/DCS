package com.amnex.agristack.Enum;

public enum RegistryTypeEnum {

	FARMER("Farmer"),
	GEO_REFERENCE_MAP("Geo Reference Map"),
	CROP_SOWN("Crop sown");


	private String value;

    public String getValue()
    {
        return this.value;
    }

    private RegistryTypeEnum(String value)
    {
        this.value = value;
    }
}
