package com.amnex.agristack.notifications;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class MobileNotification {

	private String otpMessage;

	// Generate mobile OTP and send it to user
	public void sendMobileOTP(String mobileNumber, String otp, String otpUrl, String otpAuthkey, String otpSender,
			String otpRoute, String otpUnicode) {
		LocalDateTime myDateObj = LocalDateTime.now();

		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		String formattedDate = myDateObj.format(myFormatObj);

		otpMessage = "{$1} is SECRET OTP for authentication and verification purposes of the {$2} application on {$3}. OTP is valid for {$4} min. Please do not share. FRMAPP";
		if (otpMessage.contains("{$1}")) {
			otpMessage = otpMessage.replace("{$1}", otp);
		}
		if (otpMessage.contains("{$2}")) {
			otpMessage = otpMessage.replace("{$2}", "FarmLive");
		}
		if (otpMessage.contains("{$3}")) {
			otpMessage = otpMessage.replace("{$3}", formattedDate);
		}
		if (otpMessage.contains("{$4}")) {
			otpMessage = otpMessage.replace("{$4}", "5");
		}

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("Content-Type", "application/json");
		headers.setAll(map);

		String sendOTPurl = otpUrl + "authkey=" + otpAuthkey + "&mobiles=" + mobileNumber + "&message=" + otpMessage
				+ "&sender=" + otpSender + "&route=" + otpRoute + "&unicode=" + otpUnicode;

		String response = new RestTemplate().getForObject(sendOTPurl, String.class);
	}
}
