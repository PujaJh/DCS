package com.amnex.agristack.Enum;

import java.util.HashMap;
import java.util.Map;

public enum ImagePredictionConfigCode {
	PROJECT_ID(0),
	END_POINT(1),
	CONFIDENCE_THRESHOLD(2),
	MAX_PREDICTIONS(3),
	LOCATION(4);
	private int value;

	public int getValue() {
		return this.value;
	}

	private ImagePredictionConfigCode(int value) {
		this.value = value;
	}

	//	 private int legNo;

	private static Map<Integer, ImagePredictionConfigCode> map = new HashMap<Integer, ImagePredictionConfigCode>();

	static {
		for (ImagePredictionConfigCode configCode : ImagePredictionConfigCode.values()) {
			map.put(configCode.value, configCode);
		}
	}

	//	    private LegNo(final int leg) { legNo = leg; }

	public static ImagePredictionConfigCode valueOf(int configCode) {
		return map.get(configCode);
	}

}
