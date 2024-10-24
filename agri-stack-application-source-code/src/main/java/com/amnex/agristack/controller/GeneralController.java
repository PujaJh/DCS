package com.amnex.agristack.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.ExceptionAuditDTO;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.entity.CropSurveyModeMaster;
import com.amnex.agristack.entity.FarmerGrievanceReason;
import com.amnex.agristack.entity.LanguageMaster;
import com.amnex.agristack.repository.CropSurveyModeMasterRepository;
import com.amnex.agristack.repository.FarmerGrievanceReasonRepository;
import com.amnex.agristack.service.ExceptionLogService;
import com.amnex.agristack.service.GeneralService;
import com.amnex.agristack.service.LanguageMasterService;
import com.amnex.agristack.utils.CustomMessages;

/**
 * Surveyor Controller this file will have APIs of Master table
 *
 */
@RestController
@RequestMapping("/admin/general")
public class GeneralController {

	@Autowired
	GeneralService generalService;
	@Autowired
	LanguageMasterService languageMasterService;

	@Autowired
	FarmerGrievanceReasonRepository farmerGrievanceReasonRepository;

	@Autowired
	CropSurveyModeMasterRepository cropSurveyModeMasterRepository;

	@Autowired
	ExceptionLogService exceptionLogService;

	/**
	 * Retrieves the department list.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the department list
	 */
	@GetMapping(value = "/getDepartmentList")
	public ResponseModel getDepartmentList(HttpServletRequest request) {
		try {
			return generalService.getDepartmentList();

		} catch (Exception e) {

			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves the designation list.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the designation list
	 */
	@GetMapping(value = "/getDesignationList")
	public ResponseModel getDesignationList(HttpServletRequest request) {
		try {
			return generalService.getDesignationList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves the bank list.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the bank list
	 */
	@GetMapping(value = "/getBankList")
	public ResponseModel getBankList(HttpServletRequest request) {
		try {
			return generalService.getBankList();
		} catch (Exception e) {

			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves the current season.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the current season
	 */
	@GetMapping(value = "/getCurrentSeason")
	public ResponseModel getCurrentSeason() {
		try {
			return new ResponseModel(generalService.getCurrentSeason(), "current season",
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves the irrigation list.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the irrigation list
	 */
	@GetMapping(value = "/getIrrigationList")
	public ResponseModel getIrrigationList(HttpServletRequest request) {
		try {
			return generalService.getAllIrrigationType();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves the irrigation source.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the irrigation source
	 */
	@GetMapping(value = "/getIrrigationSource")
	public ResponseModel getIrrigationSource(HttpServletRequest request) {
		try {
			return generalService.getAllIrrigationSource();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves the farm area unit type.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the farm area unit type
	 */
	@GetMapping(value = "/getFarmAreaUnitType")
	public ResponseModel getFarmAreaUnitType(HttpServletRequest request) {
		try {
			return generalService.getAllFarmAreaUnitType();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Validates the Aadhaar number.
	 *
	 * @param aadhaarNumber the Aadhaar number to validate
	 * @return the response model containing the validation result
	 */
	@GetMapping(value = "/validateAadhaarNumber")
	public ResponseModel validateAadhaarNumber(@RequestParam("aadhaarNumber") String aadhaarNumber) {
		try {
			return generalService.validateAadhaarNumber(aadhaarNumber);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}

	}

	/**
	 * Retrieves all crop statuses.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing all crop statuses
	 */
	@GetMapping(value = "/getAllCropStatus")
	public ResponseModel getAllCropStatus(HttpServletRequest request) {
		try {
			return generalService.getAllCropStatus();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves all crop types.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing all crop types
	 */
	@GetMapping(value = "/getAllCropType")
	public ResponseModel getAllCropType(HttpServletRequest request) {
		try {
			return generalService.getAllCropType();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves the year list.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the year list
	 */
	@GetMapping(value = "/getYearList")
	public ResponseModel getYearList(HttpServletRequest request) {
		try {
			return generalService.getYearList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves the pincode data.
	 *
	 * @param pincode the pincode to retrieve data for
	 * @return the response model containing the pincode data
	 */
	@GetMapping(value = "/getPincodeData")
	public ResponseModel getPincodeData(@RequestParam("pincode") Long pincode) {
		try {
			return generalService.getPincodeData(pincode);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves all languages.
	 *
	 * @return the response model containing all languages
	 */
	@GetMapping("/getAllLanguages")
	public ResponseModel getAllLanguages() {

		List<LanguageMaster> languageMasters = languageMasterService.getAllLanguageMaster();

		return new ResponseModel(languageMasters, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	 * Retrieves the configuration.
	 *
	 * @return the response model containing the configuration
	 */
	@GetMapping(value = "/getConfiguration")
	public ResponseModel getConfiguration() {
		try {
			return generalService.getConfiguratioin();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves the rejected reasons of a survey.
	 *
	 * @return the response model containing the rejected reasons
	 */
	@GetMapping(value = "/getRejectedReasonOfSurvey")
	public ResponseModel getRejectedReasonOfSurvey() {
		try {
			return generalService.getRejectedReasonOfSurvey();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves a language by its ID.
	 *
	 * @param languageId the ID of the language to retrieve
	 * @param request    the HTTP servlet request
	 * @return the response model containing the language
	 */
	@GetMapping(value = "/getLanguageById")
	public ResponseModel getLanguageById(@RequestParam("languageId") Long languageId, HttpServletRequest request) {
		return languageMasterService.getLanguageById(languageId);
	}

	/**
	 * Retrieves all grievance reasons.
	 *
	 * @return the response model containing all grievance reasons
	 */
	@GetMapping(value = "/getAllGrievanceReason")
	public ResponseModel getAllGrievanceReason() {
		List<FarmerGrievanceReason> reasons = farmerGrievanceReasonRepository.findAll();
		return new ResponseModel(reasons, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	 * Encrypts the village LGD code.
	 *
	 * @param lgdCode the village LGD code to encrypt
	 * @param request the HTTP servlet request
	 * @return the response model containing the encrypted code
	 */
	@GetMapping("/encryptVillageLgdCode/{lgdCode}")
	public ResponseModel encryptVillageLgdCode(@PathVariable("lgdCode") String lgdCode, HttpServletRequest request) {
		try {
//			String key = "UaTAgriStack";

			String key = (request.getHeader("enckey") != null) ? request.getHeader("enckey") : "UtaDataAgriData";
			
			String encrypted = generalService.encryptVillageLgdCode(lgdCode,key);
			
			encrypted = URLEncoder.encode(encrypted, StandardCharsets.UTF_8.toString());


//			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//			byte[] keyBytes = new byte[16];
//			byte[] b = key.getBytes("UTF-8");
//			int len = b.length;
//			if (len > keyBytes.length)
//				len = keyBytes.length;
//			System.arraycopy(b, 0, keyBytes, 0, len);
//			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
//			IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
//			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//
//			byte[] results = cipher.doFinal(lgdCode.getBytes("UTF-8"));
//			String encrypted = Base64.getEncoder().encodeToString(results);

			return new ResponseModel(encrypted, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}

	}

	/**
	 * Retrieves the CAPTCHA.
	 *
	 * @return the CAPTCHA image as a response entity
	 */
	@GetMapping("/getCaptcha")
	public ResponseEntity<?> getCaptcha() {
		return generalService.getCaptcha();
	}
	
	/**
	 * Retrieves the CAPTCHA.
	 *
	 * @return the CAPTCHA image as a response entity
	 */
	@PostMapping("/getCaptchaWithAccountExist")
	public ResponseModel getCaptchaWithAccountExist(@RequestBody UserInputDAO serInputDAO) {
		return generalService.getCaptchaWithAccountExist(serInputDAO);
	}

	/**
	 * Retrieves all survey modes.
	 *
	 * @return the response model containing all survey modes
	 */
	@GetMapping(value = "/getSurveyModes")
	public ResponseModel getSurveyModes() {
		List<CropSurveyModeMaster> reasons = cropSurveyModeMasterRepository.findAll();
		return new ResponseModel(reasons, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	* Retrieves all non agri plot types.
	*
	* @return the response model containing all non agri plot types
	*/
	@GetMapping(value = "/getNonAgriPlotTypes")
	public ResponseModel getNonAgriPlotTypes() {
		return generalService.getNonAgriPlotTypes();
	}

	/**
	 * Retrieves all unable to survey reason.
	 *
	 * @return the response model containing the unable to survey reason
	 */
	@GetMapping(value = "/getUnableToSurveyReason")
	public ResponseModel getUnableToSurveyReason() {
		try {
			return generalService.getUnableToSurveyReason();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}
	
	/**
	* Retrieves the Crop Class Master List.
	*
	* @return a response model containing the Crop Class Master List
	*/
	@GetMapping(value = "/getCropClassMaster")
	public ResponseModel getCropClassMaster() {
		try {
			return generalService.getCropClassMaster();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	* Retrieves the disaster type master list.
	*
	* @return a response model containing the disaster type master list
	*/
	@GetMapping(value = "/getDisasterTypeMaster")
	public ResponseModel getDisasterTypeMaster() {
		try {
			return generalService.getDisasterTypeMaster();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	* Retrieves the disaster damage area type master list.
	*
	* @return a response model containing the disaster damage area type master list.
	*/
	@GetMapping(value = "/getDisasterDamageAreaTypeMaster")
	public ResponseModel getDisasterDamageAreaTypeMaster() {
		try {
			return generalService.getDisasterDamageAreaTypeMaster();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}
	
	/**
	* Retrieves the extent of disaster master details.
	*
	* @return a response model containing the extent of disaster master details.
	*/
	@GetMapping(value = "/getExtentOfDisasterMaster")
	public ResponseModel getExtentOfDisasterMaster() {
		try {
			return generalService.getExtentOfDisasterMaster();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}
	
	/**
	* perform add exception
	* @param request
	* @param inputDao
	* @return  The ResponseModel object representing the response.
	*/
	@PostMapping(value = "/addException")
	public ResponseModel addException(@RequestBody ExceptionAuditDTO inputDao, HttpServletRequest request) {
		try {
			return exceptionLogService.addExceptionFromMobile(inputDao);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}
}
