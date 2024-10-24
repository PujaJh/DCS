package com.amnex.agristack.service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.PmKisanDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.kyc.dao.EKycDAO;
import com.amnex.agristack.repository.ExternalAPIRepository;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.PmKisanUtil;
import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PmKisanService {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private PmKisanUtil ekycUtil;
	@Autowired
	private ExternalAPIRepository externalAPIRepository;

	public ResponseModel getPmKisanData(EKycDAO inputDao, HttpServletRequest request) {
		try {
			PmKisanDAO finalObject = getPmKisanDetail(inputDao.getAadhaarNumber());

			return new ResponseModel(finalObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public PmKisanDAO getPmKisanDetail(String aadhaarNumber) throws Exception {
		String PmKisanKey= "PMK_123456";
		String hexString = convertStringToSHA256(aadhaarNumber);

		// get Unique Key
		String uniqueKey = ekycUtil.getUniqueKey(15);


		JSONObject text = new JSONObject();
		text.put("Sha_Aadhaar", hexString);
		text.put("Token", PmKisanKey);
		String encryptInput = ekycUtil.encrypt(text.toString(),uniqueKey);

		String requestInput = encryptInput+ "@"+uniqueKey;

		String outputString = getEKycDetail(requestInput);


		String decryptString = ekycUtil.decrypt(outputString,uniqueKey);


		Gson g = new Gson();
		PmKisanDAO finalObject = g.fromJson(decryptString, PmKisanDAO.class);
		return finalObject;
	}


	public static String convertStringToSHA256(String base) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder();

			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);  // Handle the exception appropriately in a real-world scenario
		}
	}

	protected String getEKycDetail(String inputString){
//		System.out.println(inputString);
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://exlink.pmkisan.gov.in/Services/BeneficiaryStatusDetails.asmx/CheckBeneficiaryStatus";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type","application/json");
		headers.add("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

		Map<String, String> map = new HashMap<>();
		map.put("EncryptedRequest", inputString);

		JSONObject inputObj = new JSONObject();
		inputObj.put("EncryptedRequest", inputString);
		HttpEntity<Map<String, String>> entity = new HttpEntity<Map<String, String>>(map,
				headers);

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
		Map<String, JsonElement> resultMap;
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, JsonElement>>() {}.getType();
		JsonElement jsonElement = gson.fromJson(responseEntity.getBody(), JsonElement.class);
		resultMap = gson.fromJson(jsonElement.toString(), type);
		String outputString = resultMap.get("d").getAsJsonObject().get("output").getAsString();

		return outputString;
	}
}
