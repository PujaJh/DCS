package com.amnex.agristack.Enum;

public enum RoleEnum {

	SUPER_ADMIN_TECHNICAL(1),
	SURVEYOR(2),
	VERIFIER(3),
    INSPECTION_OFFICER(203);
	
	
	private Integer value;

    public Integer getValue()
    {
        return this.value;
    }

    private RoleEnum(Integer value)
    {
        this.value = value;
    }
}
