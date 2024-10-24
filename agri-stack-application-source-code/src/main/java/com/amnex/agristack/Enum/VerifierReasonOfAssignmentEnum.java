package com.amnex.agristack.Enum;

public enum VerifierReasonOfAssignmentEnum {

	RANDOM_PICK("Random Pick for Verification"), SUPERVISOR_REJECTION("PR Survey Rejected by Supervisor"),
	FARMER_OBJECTION("Objection Raised by Farmer");

	private String value;

	public String getValue() {
		return this.value;
	}

	private VerifierReasonOfAssignmentEnum(String value) {
		this.value = value;
	}
}
