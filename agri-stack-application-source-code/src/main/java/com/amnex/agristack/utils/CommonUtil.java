package com.amnex.agristack.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.entity.StatePrefixMaster;
import com.amnex.agristack.repository.FarmlandPlotRegistryRepository;
import com.amnex.agristack.repository.StatePrefixMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.Enum.MenuTypeEnum;
import com.amnex.agristack.dao.MenuOutputDAO;
import com.amnex.agristack.entity.ConfigurationMaster;
import com.amnex.agristack.entity.MenuMaster;
import com.amnex.agristack.entity.SowingSeason;
import com.amnex.agristack.repository.ConfigurationRepository;

@Component
public class CommonUtil {

	@Autowired
	private StatePrefixMasterRepository statePrefixMasterRepository;

	@Autowired
	private FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;
	@Autowired
	private ConfigurationRepository configurationRepository;

	public String genrateLandParcelId(Long stateLgdCode) {
		Optional<StatePrefixMaster> state = statePrefixMasterRepository.findById(stateLgdCode);
		return state.get().getStateShortName() + Verhoeff.getFarmLandUniqueIdWithChecksum();
	}

	public String genrateLandParcelIdV2(Long stateLgdCode, String stateShortName) {
		return stateShortName + Verhoeff.getFarmLandUniqueIdWithChecksum();
	}

	public String genrateLandParcelIdV3(Long stateLgdCode, String stateShortName) {
		return stateShortName + Verhoeff.getFarmLandUniqueIdWithChecksum_v3();
	}

	public String genrateLandParcelIdV3(String stateShortName) {
		return stateShortName + Verhoeff.getFarmLandUniqueIdWithChecksum();
	}

	public static SowingSeason getCurrentSeason() {
		return new SowingSeason();
	}

	public static String GeneratePassword(int length) {
//		String[] characters = { "0123456789", "~!@#$%^&*()-_=+[{]}|;:\'\",<.>/?", "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
//				"abcdefghijklmnopqrstuvwxyz" };

//		String[] characters = { "0123456789", "~!@#$%^&*", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyz" };
//		Random rand = new Random();
//		String password = "";
//		for (int i = 0; i < 8; i++) {
//			int random = rand.nextInt(4);// choose a number from 0 to 3(inclusive)
//			int string_size = characters[random].length();// get length of the selected string
//			int random1 = rand.nextInt(string_size);// choose a number from0 to length-1 of selected string
//			char item = characters[random].charAt(random1);// selects the character
//			password = password + item;// Concatenates with the password
//		}

//		return password;

		Random rand = new Random();
		StringBuilder password = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int digit = rand.nextInt(10); // Generates a random digit between 0 and 9 (inclusive)
			password.append(digit);
		}
		return "12345678";// password.toString();
//		 }
	}

	public static MenuMaster returnMenuMaster(Long menuId, String menuName, Long menuParentId, MenuTypeEnum menuType,
			String projectType, Long menuMainId) {
		MenuMaster menu = new MenuMaster();
		menu.setMenuId(menuId);
		menu.setMenuName(menuName);
		menu.setMenuParentId(menuParentId);
		menu.setMenuType(menuType);
		menu.setProjectType(projectType);
		menu.setMenuMainId(menuMainId);
		return menu;

	}

	public static MenuOutputDAO returnMenuOutputDAO(MenuMaster menuMaster, Long menuId, String menuName) {

		MenuOutputDAO menuOutputDAO = new MenuOutputDAO();
		menuOutputDAO.setDisplaySrNo(menuMaster.getDisplaySrNo());
		menuOutputDAO.setMenuDescription(menuMaster.getMenuDescription());
		menuOutputDAO.setMenuId(menuId);
		menuOutputDAO.setMenuName(menuName);
		menuOutputDAO.setMenuParentId(menuMaster.getMenuParentId());
		menuOutputDAO.setMenuUrl(menuMaster.getMenuUrl());
		menuOutputDAO.setMenuIcon(menuMaster.getMenuIcon());
		menuOutputDAO.setMenuLevel(menuMaster.getMenuLevel());
		menuOutputDAO.setMenuType(menuMaster.getMenuType().name());
		return menuOutputDAO;
	}

	// public static String

	public static MenuOutputDAO returnMenuOutputDAO(MenuMaster menuMaster) {

		MenuOutputDAO menuOutputDAO = new MenuOutputDAO();
		menuOutputDAO.setDisplaySrNo(menuMaster.getDisplaySrNo());
		menuOutputDAO.setMenuDescription(menuMaster.getMenuDescription());
		menuOutputDAO.setMenuId(menuMaster.getMenuId());
		menuOutputDAO.setMenuName(menuMaster.getMenuName());
		menuOutputDAO.setMenuParentId(menuMaster.getMenuParentId());
		menuOutputDAO.setMenuUrl(menuMaster.getMenuUrl());
		menuOutputDAO.setMenuIcon(menuMaster.getMenuIcon());
		menuOutputDAO.setMenuLevel(menuMaster.getMenuLevel());
		menuOutputDAO.setMenuType(menuMaster.getMenuType().name());
		menuOutputDAO.setMenuCode(menuMaster.getMenuCode());
		return menuOutputDAO;
	}

	public static String getEncryptedVillageCode(Long villageCode)
			throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeyException,
			InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {

		String key = "UaTAgriStack";

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");
		int len = b.length;
		if (len > keyBytes.length)
			len = keyBytes.length;
		System.arraycopy(b, 0, keyBytes, 0, len);
		SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

		byte[] results = cipher.doFinal(villageCode.toString().getBytes("UTF-8"));
		String encrypted = Base64.getEncoder().encodeToString(results);
		return encrypted;
	}

	public static String getRequestIp(HttpServletRequest request) {

		try {
			if (request.getHeader("X-Forwarded-For") != null) {
				return request.getHeader("X-Forwarded-For");
			} else {
				return request.getRemoteAddr();
			}
		} catch (Exception e) {
			return request.getRemoteAddr();
		}
	}

	public static String ConvertStringToSHA256(String aadhaarNumber) {
		return org.apache.commons.codec.digest.DigestUtils.sha256Hex(aadhaarNumber);
	}

	public static String getSHA256OfEncodeAadhar(String encodeAadharNumber) {
		String decodeaadhar = new String(
				Base64.getDecoder().decode(encodeAadharNumber.getBytes(StandardCharsets.UTF_8)),
				StandardCharsets.UTF_8);
		return org.apache.commons.codec.digest.DigestUtils.sha256Hex(decodeaadhar);
	}

	public static String getAadhaarOfBase64EncodeString(String encodeAadharNumber) {
		return new String(Base64.getDecoder().decode(encodeAadharNumber.getBytes(StandardCharsets.UTF_8)),
				StandardCharsets.UTF_8);
	}

	public String getValuefromConfigCode(ConfigCode configCode) {

		try {
			Optional<ConfigurationMaster> configKeyValue = configurationRepository.findByConfigCode(configCode);

			if (configKeyValue.isPresent()) {
				ConfigurationMaster configvalue = configKeyValue.get();
				return configvalue.getConfigValue();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return "";
	}
	
	public String hashData(String input) {
        byte[] inputData = input.getBytes(); // Convert the input string to bytes
        byte[] hashBytes = Blake2bHashUtility.calculateBlake2bHash(inputData);

        if (hashBytes != null) {
            // Convert the hash bytes to a hexadecimal representation
            StringBuilder hexStringBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hexStringBuilder.append(String.format("%02x", b));
            }
            return hexStringBuilder.toString();
        } else {
            return null; // Handle the case where hashing failed
        }
    }
}
