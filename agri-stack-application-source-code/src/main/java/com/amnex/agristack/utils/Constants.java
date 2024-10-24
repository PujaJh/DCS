/**
 *
 */
package com.amnex.agristack.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author kinnari.soni
 *
 *         21 Feb 2023 6:01:58 pm
 */

public class Constants {

	public final static String EMAIL_TEMPLATE = "EMAIL";
	public final static String MOBILE_TEMPLATE = "MOBILE";
	//public final static String OTP_VERIFICATION_TEMPLATE_ID = "1507163414970503446";
	public final static String OTP_VERIFICATION_TEMPLATE_ID = "1407167791505332217";

	public final static String ON_REGISTER_PR_FROM_WEB = "1407167791731709894";

	public final static String USERID_SMS_TEMPLATE_ID = "1407167903324653238";
	
	public final static String NEW_USERID_SMS_TEMPLATE_ID = "1407169080604803488";

	public final static String VERIFY_EMAIL_OTP_TEMPLATE = "Email_VerifyOtp";
	public final static String VERIFY_EMAIL_OTP_CS_TEMPLATE = "Email_VerifyOtp_CS";
	public final static String DOWNLOAD_MEDIA_TEMPLATE = "DOWNLOAD_MEDIA_TEMPLATE";
	public final static String EMAIL_PASSWORD_TEMPLATE = "EMAIL_PASSWORD_TEMPLATE";
	public final static String NEW_EMAIL_PASSWORD_TEMPLATE = "NEW_EMAIL_PASSWORD_TEMPLATE";
	public final static String EMAIL_PASSWORD_TEMPLATE_2 = "EMAIL_PASSWORD_TEMPLATE_2";
	public final static Integer LOGIN_PAGE_FOR_FarmerRegistry = 1;

	public final static String VERIFY_USER_EMAIL_OTP_TEMPLATE = "UserEmail_VerifyOtp";

	public final static String AVAILABILITY_FOR_UPCOMING_SEASON = "AVAILABILITY_FOR_UPCOMING_SEASON";
	public final static String AVAILABILITY_FOR_UPCOMING_SEASON_MOBILE = "1407167965356729493";
	public final static String PR_REJECTION = "1407167965325595957";
	public final static String PR_APPROVAL = "1407167965315528881";

	public final static String SURVEYOR_APPROVAL = "SURVEYOR_APPROVAL";
	public final static String SURVEYOR_REJECTED = "SURVEYOR_REJECTED";
	public final static String CROP_UPDATE = "CROP_UPDATE";

	public final static List<String> USER_HEADERS = Arrays.asList("User ID","User Name", "Mobile Number", "Email Address");
	
	public final static String UNDER_SCORE = "_";
	public final static String REDIS_USER_KEY = "user_";
	public final static String REDIS_ASSIGN_STATE = "assign_state_";
	public final static String REDIS_ASSIGN_DISTRICT = "assign_district_";
	public final static String REDIS_ASSIGN_SUBDISTRICT = "assign_subdistrict_";
	public final static String REDIS_ASSIGN_VILLAGE = "assign_village_";
	public final static String STATE_LGD_MASTER = "state_lgd_master";
	public final static String DISTRICT_STATE_LGD_CODE = "district_state_lgd_code";
	public final static String SUBDISTRICT_DISTRICT_LGD_CODE = "subdistrict_district_lgd_code";
	public final static String VILLAGE_SUBDISTRICT_LGD_CODE = "village_subDistrict_lgd_code";
	public final static String STATE = "state";
	public final static String DISTRICT = "district";
	public final static String SUBDISTRICT = "subdistrict";
	public final static String VILLAGE = "village";
	public final static String CSBETA = "cs_beta";
	public final static String DISTRICT_LGD_MASTER = "district_lgd_master";
	public final static String SUBDISTRICT_LGD_MASTER = "sub_district_lgd_master";
	public final static String VILLAGE_LGD_MASTER = "village_lgd_master";
	public final static String YEAR_MASTER = "year_master";
	public final static String SOWING_SEASON = "sowing_season";

	public final static String FARMER_CONSENT_ACCESS_REQUEST_SMS_TEMPLATE_ID = "1507170357096991025";

	public final static String FORGOT_FARMER_OTP_SMS_TEMPLATE_ID = "1507170357086489913";
	
}
