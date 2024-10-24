package com.amnex.agristack.Enum;

public enum ProjectType {

	CROP_SURVEY("cs"),
	FARMER_REGISTRY("fr");


	private String value;

    public String getValue()
    {
        return this.value;
    }

    private ProjectType(String value)
    {
        this.value = value;
    }

    public static ProjectType valueOfLabel(String label) {
        for (ProjectType e : values()) {
            if (e.getValue().equals(label)) {
                return e;
            }
        }
        return null;
    }
}
