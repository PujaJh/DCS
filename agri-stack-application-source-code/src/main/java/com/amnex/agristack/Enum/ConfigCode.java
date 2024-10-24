package com.amnex.agristack.Enum;

import java.util.HashMap;
import java.util.Map;

public enum ConfigCode {
	ZOOM_LEVEL(0),
//    SURVEY_PLOT_ASSIGNMENT_NUMBER(1),
	MAXIMUM_SURVEY_PLOT_ASSIGNMENT_NUMBER(1),
//	ACCURACY(2),
	GPS_ACCURACY_LIMIT(2),
	AREA(3),
	
//    START_TIME(4),
//    END_TIME(5),
//    VERSION_CODE(6),
//    VERSION_NAME(7),
//    INSIDE_BOUNDARY_SURVEY(8) 
    SURVEY_START_TIME(4),
    SURVEY_END_TIME(5),
    MOBILE_APP_VERSION_CODE(6),
    MOBILE_APP_VERSION_NAME(7),
    INSIDE_BOUNDARY_SURVEY(8),
    ONE_USER_ONE_DEVICE(9),
    RATE_PER_SURVEY(10),
    RATE_PER_ADDITIONAL(11),
    MAX_AMOUNT(12),
    CROP_SEQUENCE_LIMIT(13),
	SEND_SEASON_CHANGE_NOTIFICATION(14),
	MIN_AMOUNT(15),
	NA_AMOUNT(16),
	FALLOW_AMOUNT(17),
	PER_CROP_AMOUNT(18),
	UP_BULK_DATA_PROCESS(19),
	STATE_LGD_CODE(20),

	UP_BULK_DATA_PROCESS_1(21),
	CENTRAL_CORE_DATA_SHARING_LIMIT(22),
	VERSION_CHECK_REQUIRED(23),
	APP_SIGNED(24),
	ALLOWED_PREVIOUS_APP_VERSION(25),
	FORCE_UPDATE(26),
	MAX_UPLOAD_PENDING_COUNT(27);

	private int value;

	public int getValue() {
		return this.value;
	}

	private ConfigCode(int value) {
		this.value = value;
	}

	//	 private int legNo;

	private static Map<Integer, ConfigCode> map = new HashMap<Integer, ConfigCode>();

	static {
		for (ConfigCode configCode : ConfigCode.values()) {
			map.put(configCode.value, configCode);
		}
	}

	//	    private LegNo(final int leg) { legNo = leg; }

	public static ConfigCode valueOf(int configCode) {
		return map.get(configCode);
	}
}
