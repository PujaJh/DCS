package com.amnex.agristack.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import com.amnex.agristack.utils.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.CaptchaRequest;
import com.amnex.agristack.dao.ConfigurationDAO;
import com.amnex.agristack.dao.UserInputDAO;

import cn.apiclub.captcha.Captcha;
import reactor.core.publisher.Mono;

@Service
public class GeneralService {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private DesignationRepository designationRepository;

	@Autowired
	private BankRepository bankRepository;

	@Autowired
	SeasonMasterRepository seasonRepository;

	@Autowired
	private IrrigationMasterRepository irrigationMasterRepository;

	@Autowired
	private FarmAreaUnitTypeRepository farmAreaUnitTypeRepository;

	@Autowired
	private IrrigationSourceRepository irrigationSourceRepository;

	@Autowired
	private CropStatusRepository cropStatusRepository;

	@Autowired
	private CropTypeMasterRepository cropTypeMasterRepository;

	@Autowired
	private YearRepository yearRepository;

	@Autowired
	private UserMasterRepository userRepository;

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	private ReasonMasterRepository reasonMasterRepository;

	@Autowired
	private FlexibleSurveyReasonRepository flexibleSurveyReasonRepository;

	@Autowired
	private NonAgriPlotTypeMasterRepository nonAgriPlotTypeMasterRepository;

	@Autowired
	private UnableToSurveyReasonMasterRespository unableToSurveyReasonMasterRespository;
	
	@Autowired
	private CropClassNameRepository cropClassNameRepository;
	
	@Autowired
	private DisasterTypeMasterRepository disasterTypeMasterRepository;
	
	@Autowired
	private DisasterDamageAreaTypeMasterRepository disasterDamageAreaTypeRepository;
	
	@Autowired
	private ExtentOfDisasterMasterRepository extentOfDisasterMasterRepository;

	@Autowired
	private ConfigurationHistoryRepository configurationHistoryRepository;

	@Autowired
	private SeasonMasterRepository seasonMasterRepository;
	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private UserCaptchaDetailsRepository userCaptchaDetailsRepository;

	@Autowired
	private SeasonMasterHistoryRepository seasonMasterHistoryRepository;

	/**
	 * Retrieves the department list.
	 *
	 * @return the response model containing the department list {@link DepartmentMaster}
	 */
	public ResponseModel getDepartmentList() {
		List<DepartmentMaster> departmentList = new ArrayList<>();
		List<DepartmentMaster> data = departmentRepository.findByIsDeletedFalseOrderByDepartmentNameAsc();
		data.forEach(ele -> {
			departmentList
					.add(new DepartmentMaster(ele.getDepartmentId(), ele.getDepartmentName(), ele.getDepartmentType()));
		});
		return new ResponseModel(departmentList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	 * Retrieves the designation list.
	 *
	 * @return the response model containing the designation list {@code DesignationMaster}
	 */
	public ResponseModel getDesignationList() {
		List<DesignationMaster> designationList = new ArrayList<>();
		List<DesignationMaster> data = designationRepository.findByIsDeletedFalseOrderByDesignationNameAsc();
		data.forEach(ele -> {
			designationList.add(new DesignationMaster(ele.getDesignationId(), ele.getDesignationName()));
		});
		return new ResponseModel(designationList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	 * Retrieves the bank list.
	 *
	 * @return the response model containing the bank list {@code BankMaster}
	 */
	public ResponseModel getBankList() {
		List<BankMaster> bankList = new ArrayList<>();
		List<BankMaster> data = bankRepository.findByIsDeletedFalseOrderByBankNameAsc();
		data.forEach(ele -> {
			bankList.add(new BankMaster(ele.getBankId(), ele.getBankName()));
		});
		return new ResponseModel(bankList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	 * Retrieves the year list.
	 *
	 * @return the response model containing the year list {@code YearMaster}
	 */
	public ResponseModel getYearList() {
		List<YearMaster> yearList = new ArrayList<>();
		List<YearMaster> list = yearRepository.findAll();

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//		cal.set(2023, Calendar.JULY, 1);
		Integer CurrentMonth = cal.get(Calendar.MONTH) + 1;
		Integer date = cal.get(Calendar.DAY_OF_MONTH);
		Integer CurrentYear = cal.get(Calendar.YEAR);

		list.forEach(ele -> {

			Integer fromYear = 0;
			Integer toYear = 0;

			if (CurrentMonth < (Calendar.JULY + 1)) {
				fromYear = CurrentYear - 1;
				toYear = fromYear + 1;
			} else if (CurrentMonth >= (Calendar.JULY + 1)) {
				fromYear = CurrentYear;
				toYear = fromYear + 1;
			}

			Boolean isCurrentYear = false;
			if (ele.getStartYear().equals(String.valueOf(fromYear))
					&& ele.getEndYear().equals(String.valueOf(toYear))) {
				isCurrentYear = true;
			}
			yearList.add(new YearMaster(ele.getYearValue(), ele.getStartYear(), ele.getEndYear(), isCurrentYear));
		});
		return new ResponseModel(yearList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	
	/**
	 * Retrieves the current season.
	 *
	 * @return the response model containing the current season {@code SowingSeason}
	 */
	public SowingSeason getCurrentSeason() {
		try {
			SowingSeason cropSeason = new SowingSeason();
			List<SowingSeason> seasons = seasonRepository.findAllByIsDeletedFalse();
			Integer toYear = 0;
			Integer fromYear = 0;
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			Integer CurrentMonth = cal.get(Calendar.MONTH) + 1;
			Integer date = cal.get(Calendar.DAY_OF_MONTH);
			Integer CurrentYear = cal.get(Calendar.YEAR);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
			for (SowingSeason season : seasons) {
				String startingDate = season.getStartingMonth();
				String endingDate = season.getEndingMonth();

				Calendar calendarStart = Calendar.getInstance();
				Calendar calendarEnd = Calendar.getInstance();

				calendarStart.setTime(sdf.parse(startingDate));
				calendarEnd.setTime(sdf.parse(endingDate));

				Integer SFMonth = calendarStart.get(Calendar.MONTH) + 1;
				Integer STMonth = calendarEnd.get(Calendar.MONTH) + 1;

				Integer SFDate = calendarStart.get(Calendar.DAY_OF_MONTH);
				Integer STDate = calendarEnd.get(Calendar.DAY_OF_MONTH);

				if (SFMonth > STMonth) {
					if (SFMonth == CurrentMonth) {
						fromYear = CurrentYear;
						toYear = CurrentYear + 1;
					} else if (STMonth == CurrentMonth) {
						fromYear = CurrentYear - 1;
						toYear = CurrentYear;
					} else if (SFMonth < CurrentMonth) {
						fromYear = CurrentYear;
						toYear = CurrentYear + 1;
					} else if (STMonth > CurrentMonth) {
						fromYear = CurrentYear - 1;
						toYear = CurrentYear;
					} else if (SFMonth > CurrentMonth && STMonth < CurrentMonth) {
						fromYear = CurrentYear;
						toYear = CurrentYear + 1;
					} else if (SFMonth > CurrentMonth && STMonth > CurrentMonth) {
						fromYear = CurrentYear - 1;
						toYear = CurrentYear;
					}
				} else if (SFMonth <= STMonth) {
					fromYear = CurrentYear;
					toYear = CurrentYear;
				}

				LocalDate currentDate = LocalDate.of(CurrentYear, CurrentMonth, date);

				LocalDate fromDate = LocalDate.of(fromYear, SFMonth, SFDate);

				LocalDate toDate = LocalDate.of(toYear, STMonth, STDate);

				// currentDate fromdate & ToDate betn
				if ((currentDate.isAfter(fromDate) || currentDate.isEqual(fromDate))
						&& (currentDate.isBefore(toDate) || currentDate.isEqual(toDate))) {
					cropSeason = season;

					if (CurrentMonth < (Calendar.JULY + 1)) {
						cropSeason.setStartingYear(CurrentYear -1 );
						cropSeason.setEndingYear(CurrentYear);
					} else if (CurrentMonth >= (Calendar.JULY + 1)) {
						cropSeason.setStartingYear(CurrentYear);
						cropSeason.setEndingYear(CurrentYear +1);
					}
					cropSeason.setCreatedIp(null);
					cropSeason.setModifiedIp(null);
					break;
				}
			}
			return cropSeason;


		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the irrigation list.
	 *
	 * @return the response {@link IrrigationMaster} model containing the irrigation list
	 */
	public ResponseModel getAllIrrigationType() {
		List<IrrigationMaster> irrigationList = new ArrayList<>();
		List<IrrigationMaster> data = irrigationMasterRepository
				.findByIsActiveTrueAndIsDeletedFalseOrderByIrrigationTypeAsc();
		data.forEach(ele -> {
			irrigationList.add(new IrrigationMaster(ele.getIrrigationId(), ele.getIrrigationType()));
		});
		return new ResponseModel(irrigationList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	 * Retrieves the farm area unit type.
	 *
	 * @return the response {@code FarmAreaUnitType} model containing the farm area unit type
	 */
	public ResponseModel getAllFarmAreaUnitType() {
		List<FarmAreaUnitType> unitTypeList = new ArrayList<>();
		List<FarmAreaUnitType> list = farmAreaUnitTypeRepository
				.findByIsActiveTrueAndIsDeletedFalseOrderByUnitTypeAsc();
		list.forEach(ele -> {
			unitTypeList.add(new FarmAreaUnitType(ele.getUnitTypeId(), ele.getUnitType()));
		});
		return new ResponseModel(unitTypeList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	public ResponseModel getAllIrrigationSource() {
		List<IrrigationSource> irrigationSourceList = new ArrayList<>();
		List<IrrigationSource> list = irrigationSourceRepository
				.findByIsActiveTrueAndIsDeletedFalseOrderByIrrigationTypeAsc();
		list.forEach(ele -> {
			irrigationSourceList.add(new IrrigationSource(ele.getIrrigationId(), ele.getIrrigationType()));
		});
		return new ResponseModel(irrigationSourceList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	 * Validates the Aadhaar number.
	 *
	 * @return the response model containing the validation result
	 */
	public ResponseModel validateAadhaarNumber(String aadhaarNumber) {

		String encodedAadhaarNumber = new String(Base64.getDecoder().decode(aadhaarNumber));
		boolean isValid = Verhoeff.validateAadhaarNumber(encodedAadhaarNumber);
		if (isValid) {
			return new ResponseModel(isValid, CustomMessages.SUCCESS, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} else {
			return new ResponseModel(isValid, CustomMessages.INVALID_AADHAAR, CustomMessages.GET_DATA_ERROR,
					CustomMessages.INVALID_INPUT, CustomMessages.METHOD_GET);
		}

	}

	/**
	 * Retrieves all crop statuses.
	 *
	 * @return the response {@code CropStatusMaster} model containing all crop statuses
	 */
	public ResponseModel getAllCropStatus() {
		List<CropStatusMaster> cropStatusMasterList = new ArrayList<>();
		List<CropStatusMaster> list = cropStatusRepository
				.findByIsActiveTrueAndIsDeletedFalseOrderByCropStatusTypeAsc();
		list.forEach(ele -> {
			cropStatusMasterList.add(new CropStatusMaster(ele.getCropStatusId(), ele.getCropStatusType()));
		});
		return new ResponseModel(cropStatusMasterList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	 * Retrieves all crop types.
	 *
	 * @return the response {@code CropTypeMaster} model containing all crop types
	 */
	public ResponseModel getAllCropType() {
		List<CropTypeMaster> cropTypeList = new ArrayList<>();
		List<CropTypeMaster> list = cropTypeMasterRepository.findByIsActiveTrueAndIsDeletedFalseOrderByCropTypeAsc();
		list.forEach(ele -> {
			cropTypeList.add(new CropTypeMaster(ele.getCropTypeId(), ele.getCropType()));
		});
		return new ResponseModel(cropTypeList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	 * @param pincode
	 * @return
	 */
	public ResponseModel getPincodeData(Long pincode) {

		String url = "https://api.postalpincode.in/pincode/" + pincode;
		WebClient client = WebClient.create();
		WebClient.ResponseSpec responseSpec = client.get().uri(url).retrieve();
		String stringResp = responseSpec.bodyToMono(String.class).block();
		return new ResponseModel(stringResp, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	/**
	 * @param prefix
	 * @param user
	 * @return
	 */
	public String generatePrNumber(String prefix, UserMaster user) {
		Integer serialNumber = userRepository.getUserCountForDepartmentAndVillage(
				user.getDepartmentId().getDepartmentId(), user.getUserVillageLGDCode());
		String departmentCode = (user.getDepartmentId().getDepartmentCode());
		return prefix + "_" + departmentCode + "_" + (serialNumber + 1) + "_" + user.getUserVillageLGDCode().toString();
	}

	/**
	 *
	 * @param prefix
	 * @param departmentCode
	 * @param serialNumber
	 * @param lgdCode
	 * @return
	 */

	public String generateUserNameUsingPattern(String prefix, String departmentCode, Long serialNumber,
			String territoryLevel, Long lgdCode) {
		return prefix + "_" + departmentCode + "_" + serialNumber + "_" + territoryLevel + "_" + lgdCode;
	}

	public String generateUserNameUsingPatternForStateUser(String prefix, Long serialNumber, String territoryLevel,
			Long lgdCode) {
		return prefix + "_" + serialNumber + "_" + territoryLevel + "_" + lgdCode;
	}

	public ResponseModel getConfiguratioin() {
		List<ConfigurationMaster> configurationList = configurationRepository.findByIsActiveTrueAndIsDeletedFalse();
		configurationList.stream().forEach(ele -> {
			ele.setConfigCodeValue(ele.getConfigCode().getValue());
		});
		return new ResponseModel(configurationList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	public Date getDateFromString(String date, String pattern) throws ParseException {
		DateFormat df = new SimpleDateFormat(pattern);
		java.util.Date dateObj = df.parse(date); // Returns a Date format object with the pattern
		java.sql.Date sqlDate = new java.sql.Date(dateObj.getTime());
		return sqlDate;
	}

	public String createTempPassword() {
		String chars = "abcdefghijklmnopqrstuvwxyz";
		String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String NUMS = "1234567890";
		String SPEC = "@$";
//		String pass = twoRandChars(chars) + twoRandChars(CHARS) + twoRandChars(SPEC) + twoRandChars(NUMS);
		return "12345678";
	}

	public String twoRandChars(String src) {
		Random rnd = new Random();
		int index1 = (int) (rnd.nextFloat() * src.length());
		int index2 = (int) (rnd.nextFloat() * src.length());
		return "" + src.charAt(index1) + src.charAt(index2);
	}

	/**
	 * Retrieves the rejected reasons of a survey.
	 *
	 * @return the response model containing the rejected reasons {@link ReasonMaster}
	 */
	public ResponseModel getRejectedReasonOfSurvey() {
		List<ReasonMaster> list = reasonMasterRepository.findByIsActiveTrueAndIsDeletedFalse();
		return new ResponseModel(list, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}

	public SowingSeason getSeasonStartEndYear(Long seasonId) {
		try {
			SowingSeason cropSeason = seasonRepository.findBySeasonId(seasonId);
			Integer toYear = 0;
			Integer fromYear = 0;
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			Integer CurrentMonth = cal.get(Calendar.MONTH) + 1;
			Integer date = cal.get(Calendar.DAY_OF_MONTH);
			Integer CurrentYear = cal.get(Calendar.YEAR);
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

			String startingDate = cropSeason.getStartingMonth();
			String endingDate = cropSeason.getEndingMonth();

			Calendar calendarStart = Calendar.getInstance();
			Calendar calendarEnd = Calendar.getInstance();

			calendarStart.setTime(sdf.parse(startingDate));
			calendarEnd.setTime(sdf.parse(endingDate));

			Integer SFMonth = calendarStart.get(Calendar.MONTH) + 1;
			Integer STMonth = calendarEnd.get(Calendar.MONTH) + 1;

			Integer SFDate = calendarStart.get(Calendar.DAY_OF_MONTH);
			Integer STDate = calendarEnd.get(Calendar.DAY_OF_MONTH);

			if (SFMonth > STMonth) {
				if (SFMonth == CurrentMonth) {
					fromYear = CurrentYear;
					toYear = CurrentYear + 1;
				} else if (STMonth == CurrentMonth) {
					fromYear = CurrentYear - 1;
					toYear = CurrentYear;
				} else if (SFMonth < CurrentMonth) {
					fromYear = CurrentYear;
					toYear = CurrentYear + 1;
				} else if (STMonth > CurrentMonth) {
					fromYear = CurrentYear - 1;
					toYear = CurrentYear;
				} else if (SFMonth > CurrentMonth && STMonth < CurrentMonth) {
					fromYear = CurrentYear;
					toYear = CurrentYear + 1;
				} else if (SFMonth > CurrentMonth && STMonth > CurrentMonth) {
					fromYear = CurrentYear - 1;
					toYear = CurrentYear;
				}
			} else if (SFMonth <= STMonth) {
				fromYear = CurrentYear;
				toYear = CurrentYear;
			}

			LocalDate currentDate = LocalDate.of(CurrentYear, CurrentMonth, date);

			LocalDate fromDate = LocalDate.of(fromYear, SFMonth, SFDate);

			LocalDate toDate = LocalDate.of(toYear, STMonth, STDate);

			// currentDate fromdate & ToDate betn
			if ((currentDate.isAfter(fromDate) || currentDate.isEqual(fromDate))
					&& (currentDate.isBefore(toDate) || currentDate.isEqual(toDate))) {

				if (CurrentMonth < (Calendar.JULY + 1)) {
					cropSeason.setStartingYear(fromYear - 1);
					cropSeason.setEndingYear(toYear);
				} else if (CurrentMonth >= (Calendar.JULY + 1)) {
					cropSeason.setStartingYear(fromYear);
					cropSeason.setEndingYear(toYear + 1);
				}

			}

			return cropSeason;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the active configurations for mobile devices.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the active configurations {@link ConfigurationMaster}
	 */
	public ResponseModel getConfigurationForMobile(HttpServletRequest request) {
		try {
			// Change To Query For Set To ConfigCode Asc
			List<ConfigurationMaster> configurationList = configurationRepository
					.findByIsActiveTrueAndIsDeletedFalseOrderByConfigCodeAsc();
			configurationList.stream().forEach(ele -> {
				// if(ele.getConfigCode() == ConfigCode.AREA){
				// 	ele.setConfigValue(ele.getDescription());
				// }
				ele.setConfigCodeValue(ele.getConfigCode().getValue());
			});
			return new ResponseModel(configurationList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Retrieves the CAPTCHA.
	 *
	 * @return the CAPTCHA image as a response entity
	 */
	public ResponseEntity<?> getCaptcha() {
		try {
			CaptchaRequest captchaRequest = new CaptchaRequest();
			Captcha captcha = CaptchaService.createCaptcha(240, 70);

			Base64.Encoder encoder = Base64.getEncoder();
			String str = encoder.encodeToString(captcha.getAnswer().getBytes());
			captchaRequest.setHiddenCaptcha(str);
			captchaRequest.setCaptcha(""); // value entered by the User
			captchaRequest.setRealCaptcha(CaptchaService.encodeCaptcha(captcha));

			return new ResponseEntity<String>(ResponseMessages.Toast("success", "Captcha generated.", captchaRequest),
					HttpStatus.OK);
		} catch (Exception e) {

			return new ResponseEntity<String>(ResponseMessages.Toast("internalServerError", e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@Transactional
	public ResponseModel getCaptchaWithAccountExist( UserInputDAO userInputDAO) {
		try {
			Optional<UserMaster> userOptional = userMasterRepository.getUserWithoutStatus(userInputDAO.getUserName());
			if(userOptional.isPresent()) {
				List<UserCaptchaDetails> userCaptchaDetailsList=userCaptchaDetailsRepository.findByUserIdAndIsActiveAndIsDeleted(userOptional.get().getUserId(), true, false);
				if(userCaptchaDetailsList!=null && userCaptchaDetailsList.size()>0) {
					userCaptchaDetailsRepository.updateUserCaptchaDetailsByUserId(userOptional.get().getUserId());		
				}
			
			CaptchaRequest captchaRequest = new CaptchaRequest();
			Captcha captcha = CaptchaService.createCaptcha(240, 70);
//			System.out.println(captcha.getAnswer());
			List<UserCaptchaDetails> captchaList=userCaptchaDetailsRepository.findByCaptcha(captcha.getAnswer());
			
			if(captchaList!=null && captchaList.size()>0) {
				captchaRequest = new CaptchaRequest();
				captcha = CaptchaService.createCaptcha(240, 70);
//				System.out.println(captcha.getAnswer());
			}
			
			UserCaptchaDetails userCaptchaDetails=new UserCaptchaDetails();
			userCaptchaDetails.setUserId(userOptional.get().getUserId());
			userCaptchaDetails.setCaptcha(captcha.getAnswer());
			userCaptchaDetails.setIsActive(true);
			userCaptchaDetails.setIsDeleted(false);
			userCaptchaDetailsRepository.save(userCaptchaDetails);
//			captcha.getAnswer();
			Base64.Encoder encoder = Base64.getEncoder();
			String str = encoder.encodeToString(captcha.getAnswer().getBytes());
			captchaRequest.setHiddenCaptcha(str);
			captchaRequest.setCaptcha(""); // value entered by the User
			captchaRequest.setRealCaptcha(CaptchaService.encodeCaptcha(captcha));

//			return new ResponseEntity<String>(ResponseMessages.Toast("success", "Captcha generated.", captchaRequest),
//					HttpStatus.OK);
			return CustomMessages.makeResponseModel(captchaRequest, CustomMessages.LOGIN_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.ACCOUNT_DOES_NOT_EXISTS,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}
			
		} catch (Exception e) {


			return  CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
	

	/**
	 * Retrieves the flexible survey reasons for mobile devices.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the flexible survey reasons {@code FlexibleSurveyReasonMaster}
	 */

	public ResponseModel getFlexibleSurveyReasons(HttpServletRequest request) {
		List<FlexibleSurveyReasonMaster> reasons = new ArrayList<>();

		try {
			reasons = flexibleSurveyReasonRepository.findByIsActiveTrueAndIsDeletedFalse();

			return new ResponseModel(reasons, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {

			return new ResponseModel(reasons, CustomMessages.GET_RECORD, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILURE, CustomMessages.METHOD_GET);
		}
	}

	/**
	 * add configuration 
	 * @param ConfigurationDAO
	 * @return  The ResponseModel object representing the response.{@link ConfigurationDAO}
	 */
	public ResponseModel addConfiguration(ConfigurationDAO configurationDAO, HttpServletRequest request) {
		try {
			String userId = CustomMessages.getUserId(request, jwtTokenUtil);
			userId = (userId != null && !userId.isEmpty()) ? userId : "0";
			ConfigurationMaster configuration = new ConfigurationMaster();
			if (configurationDAO.getConfigurationId() != null) {

				BeanUtils.copyProperties(configurationDAO, configuration);
				configuration.setActive(true);
				configuration.setDeleted(false);
				configuration.setConfigCode(ConfigCode.valueOf(configurationDAO.getConfigCodeValue()));
				configuration.setConfigValue(configurationDAO.getConfigValue());
				// configuration = configurationRepository.save(configuration);
				configuration.setDescription(configurationDAO.getDescription());
			} else {

				Optional<ConfigurationMaster> configKey = configurationRepository
						.findByConfigKey(configurationDAO.getConfigKey());

				if (configKey.isPresent()) {

					return new ResponseModel(configuration, CustomMessages.CONFIG_KEY_EXIST,
							CustomMessages.NO_DATA_FOUND, CustomMessages.FAILURE, CustomMessages.METHOD_POST);
				}

				Optional<ConfigurationMaster> configCode = configurationRepository
						.findByConfigCode(configurationDAO.getConfigCodeValue());

				if (configCode.isPresent()) {
					return new ResponseModel(configuration, CustomMessages.CONFIG_CODE_EXIST,
							CustomMessages.NO_DATA_FOUND, CustomMessages.FAILURE, CustomMessages.METHOD_POST);
				}

				BeanUtils.copyProperties(configurationDAO, configuration);
				configuration.setActive(true);
				configuration.setDeleted(false);
				configuration.setConfigCode(ConfigCode.valueOf(configurationDAO.getConfigCodeValue()));
				configuration.setConfigKey(configurationDAO.getConfigKey());
				configuration.setConfigValue(configurationDAO.getConfigValue());
				configuration.setDescription(configurationDAO.getDescription());
				configuration.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				configuration.setCreatedBy(userId);
			}
			configuration.setModifiedBy(userId);
			configuration.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			configuration = configurationRepository.save(configuration);

			ConfigurationMasterHistory configurationMasterHistory = new ConfigurationMasterHistory();
			BeanUtils.copyProperties(configuration, configurationMasterHistory);
			configurationMasterHistory.setCreatedBy(configuration.getModifiedBy());
			configurationMasterHistory.setCreatedOn(configuration.getModifiedOn());
			configurationHistoryRepository.save(configurationMasterHistory);

			return new ResponseModel(configuration, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}

	}

	/**
	 * @param request
	 * @param key
	 * @return {@code ConfigurationMaster}
	 */
	public ResponseModel getConfigById(HttpServletRequest request, String key) {
		try {

			Optional<ConfigurationMaster> coOp = configurationRepository.findByConfigKey(key);
			if (coOp.isPresent()) {
				return new ResponseModel(coOp.get(), CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			} else {
				return new ResponseModel(null, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Retrieves all non agri plot types.
	 *
	 * @return the response model containing all non agri plot types {@code NonAgriPlotTypeMaster}
	 */
	public ResponseModel getNonAgriPlotTypes() {
		try {
			List<NonAgriPlotTypeMaster> typeMasterList = nonAgriPlotTypeMasterRepository.findActiveNonAgriPlotTypeIds();
			return new ResponseModel(typeMasterList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}

	}

	public YearMaster getCurrentYear() {
		List<YearMaster> yearList = new ArrayList<>();
		List<YearMaster> list = yearRepository.findAll();

		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//		cal.set(2023, Calendar.JULY, 1);
		Integer CurrentMonth = cal.get(Calendar.MONTH) + 1;
		Integer CurrentYear = cal.get(Calendar.YEAR);

		list.forEach(ele -> {

			Integer fromYear = 0;
			Integer toYear = 0;

			if (CurrentMonth < (Calendar.JULY + 1)) {
				fromYear = CurrentYear - 1;
				toYear = fromYear + 1;
			} else if (CurrentMonth >= (Calendar.JULY + 1)) {
				fromYear = CurrentYear;
				toYear = fromYear + 1;
			}

			Boolean isCurrentYear = false;
			if (ele.getStartYear().equals(String.valueOf(fromYear))
					&& ele.getEndYear().equals(String.valueOf(toYear))) {
				isCurrentYear = true;
			}
			yearList.add(new YearMaster(ele.getId(), ele.getYearValue(), ele.getStartYear(), ele.getEndYear(), isCurrentYear));
		});
		YearMaster currentYear = yearList.stream().filter(ele -> ele.getIsCurrentYear().equals(true))
				.collect(Collectors.toList()).get(0);
		return currentYear;
	}

	/**
	 * Retrieves all unable to survey reason.
	 *
	 * @return the response model containing the unable to survey reason {@link UnableToSurveyReasonMaster}
	 */
	public ResponseModel getUnableToSurveyReason() {

		try {
			List<UnableToSurveyReasonMaster> unableToSurveyReasonMasterList = unableToSurveyReasonMasterRespository
					.findByIsActiveTrue();
			return new ResponseModel(unableToSurveyReasonMasterList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}

	}

	public String encryptVillageLgdCode(String lgdCode, String key) {

		try {
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

			byte[] results = cipher.doFinal(lgdCode.getBytes("UTF-8"));
			String encrypted = Base64.getEncoder().encodeToString(results);
			return encrypted;

		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Retrieves the Crop Class Master List.
	 *
	 * @return a response model containing the Crop Class Master List
	 */
	public ResponseModel getCropClassMaster() {
		try {
			List<CropClassMaster> cropClassMaster = cropClassNameRepository.findByIsActiveTrueAndIsDeletedFalse();
			
			return new ResponseModel(cropClassMaster, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			
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
	public ResponseModel getDisasterTypeMaster() {
		try {
			List<DisasterTypeMaster> disasterTypeMaster = disasterTypeMasterRepository.findByIsActiveTrueAndIsDeletedFalseOrderByDisasterTypeNameAsc();
			
			return new ResponseModel(disasterTypeMaster, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			
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
	public ResponseModel getDisasterDamageAreaTypeMaster() {
		try {
			List<DisasterDamageAreaTypeMaster> disasterDamageAreaTypeMaster = disasterDamageAreaTypeRepository.findByIsActiveTrueAndIsDeletedFalseOrderByDisasterDamageAreaTypeNameAsc();
			
			return new ResponseModel(disasterDamageAreaTypeMaster, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			
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
	public ResponseModel getExtentOfDisasterMaster() {
		try {
			List<ExtentOfDisasterMaster> extentOfDisasterMaster = extentOfDisasterMasterRepository.findByIsActiveTrueAndIsDeletedFalseOrderByExtentOfDisasterIdAsc();
			
			return new ResponseModel(extentOfDisasterMaster, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	public void updateCurrentSeason(SowingSeason season) {
		SowingSeason currentSeason = getCurrentSeason();
		if(currentSeason.getSeasonId().equals(season.getSeasonId()) && (Objects.isNull(currentSeason.getIsCurrentSeason())|| !currentSeason.getIsCurrentSeason())) {
			// make previous current season to false
			SowingSeason dbCurrentSeason = seasonMasterRepository.findByIsCurrentSeasonTrue();
			dbCurrentSeason.setIsCurrentSeason(null);
			addSowingSeasonHistory(dbCurrentSeason);
			seasonMasterRepository.save(dbCurrentSeason);

			season.setIsCurrentSeason(true);
			addSowingSeasonHistory(season);
			seasonMasterRepository.save(season);
		}
	}

	private void addSowingSeasonHistory(SowingSeason seasonMaster) {
		SowingSeasonHistory sowingSeasonHistory = new SowingSeasonHistory();
		BeanUtils.copyProperties(seasonMaster,sowingSeasonHistory);
		sowingSeasonHistory.setCreatedBy(null);
		sowingSeasonHistory.setModifiedBy(null);
		sowingSeasonHistory.setCreatedOn(new Timestamp(new java.util.Date().getTime()));
		sowingSeasonHistory.setModifiedOn(new Timestamp(new java.util.Date().getTime()));
		sowingSeasonHistory.setCreatedIp(null);
		sowingSeasonHistory.setModifiedIp(null);
		seasonMasterHistoryRepository.save(sowingSeasonHistory);
	}
	
	public String decodeBase64(String data) {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] decodedBytes = decoder.decode(data);
		return  new String(decodedBytes);
	}

}
