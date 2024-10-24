package com.amnex.agristack.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.models.auth.In;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.Enum.ActivityCodeEnum;
import com.amnex.agristack.Enum.ActivityStatusEnum;
import com.amnex.agristack.Enum.ActivityTypeEnum;
import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.Enum.CropSurveyModeEnum;
import com.amnex.agristack.Enum.ErrorCode;
import com.amnex.agristack.Enum.LandTypeEnum;
import com.amnex.agristack.Enum.MasterTableName;
import com.amnex.agristack.Enum.MessageType;
import com.amnex.agristack.Enum.NotificationType;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.Enum.SurveyAreaTypeEnum;
import com.amnex.agristack.Enum.VerifierReasonOfAssignmentEnum;
import com.amnex.agristack.config.GooglePlayIntegrityService;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.DBUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.api.client.util.Strings;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Service class responsible for handling survey review-related operations. This
 * class provides methods to retrieve and manipulate survey review data. Author:
 * Darshan,Harshal
 */
@Service
public class SurveyReviewService {

	@Autowired
	DBUtils dbUtils;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${app.kafka.enabled}")
	private String kafkaEnabled;

	@Value("${file.upload-dir}")
	private String path;

	@Value("${media.virtual.url}")
	private String virtualUrl;

	@Value("${media.folder.image}")
	private String folderImage;
	@Value("${media.folder.audio}")
	private String folderAudio;
	@Value("${media.folder.video}")
	private String folderVideo;

	@Value("${media.folder.document}")
	private String folderDocument;

	@Value("${media.url.path}")
	private String urlPath;

	@Autowired
	LandParcelSurveyMasterRespository landParcelSurveyMasterRespository;

	@Autowired
	LandParcelSurveyDetailRepository landParcelSurveyDetailRepository;

	@Autowired
	LandParcelSurveyCropDetailRepository landParcelSurveyCropDetailRepository;

	@Autowired
	FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	FarmerRegistryRepository farmerRegistryRepository;

	@Autowired
	IrrigationMasterRepository irrigationMasterRepository;

	@Autowired
	IrrigationSourceRepository irrigationSourceRepository;

	@Autowired
	ReasonMasterRepository reasonMasterRepository;

	@Autowired
	MediaMasterService mediaMasterService;

	@Autowired
	LandParcelSurveyCropMediaMappingRepository landParcelSurveyCropMediaMappingRepository;

	@Autowired
	CropMasterRepository cropRegistryRepository;

	@Autowired
	CropVarietyRepository cropVarietyRepository;

	@Autowired
	CropTypeMasterRepository cropTypeMasterRepository;

	@Autowired
	CropStatusRepository cropStatusRepository;

	@Autowired
	CropCategoryRepository cropCategoryRepository;

	@Autowired
	SurveyAreaTypeMasterRepository surveyAreaTypeMasterRepository;

	@Autowired
	VerifierLandAssignmentRepository verifierLandAssignmentRepository;

	@Autowired
	VillageLgdMasterRepository villageMasterRepository;

	@Autowired
	SurveyVerificationConfigurationMasterRepository surveyVerificationConfigurationMasterRepository;

	@Autowired
	SeasonMasterRepository seasonMasterRepository;

	@Autowired
	VerifierLandAssignmentService verifierLandAssignmentService;

	@Autowired
	SurveyActivityLogRepository surveyActivityLogRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private MessageConfigurationRepository messageConfigurationRepository;

	@Autowired
	private ActivityTypeRepository activityTypeRepository;

	@Autowired
	private ActivityStatusRepository activityStatusRepository;

	@Autowired
	private UserLandAssignmentRepository userLandAssignmentRepository;

	@Autowired
	SurveyActivityConfigService surveyActivityConfigService;

	@Autowired
	private NonAgriPlotTypeMasterRepository nonAgriPlotTypeMasterRepository;

	@Autowired
	KCropSurveyService kCropSurveyService;

	@Autowired
	private UnableToSurveyRepository unableToSurveyRepository;

	@Autowired
	private CropClassNameRepository cropClassNameRepository;

	@Autowired
	private UnableToSurveyReasonMasterRespository unableToSurveyReasonMasterRespository;

	@Autowired
	private MISDashboardRepository misDashboardRepository;

	@Autowired
	private CultivatorPlotMappingRepository cultivatorPlotMappingRepository;

	@Autowired
	ExceptionLogService exceptionLogService;

	@Autowired
	CommonUtil commonUtil;
	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	private GooglePlayIntegrityService googlePlayIntegrityService;

	@Value("${app.allowed.appIntegrity}")
	private Boolean isAppIntegrity;

	@Autowired
	private UserDeviceIntegrityDetailsRepository userDeviceIntegrityDetailsRepository;

	@Autowired
	private ErrorMessageMasterRepository errorMessageMasterRepository;

	@Autowired
	private LpsmTokenRepository lpsmTokenRepository;

	@Autowired
	private GeneralService generalService;


	/**
	 * Retrieves assigned plot details for a mobile application based on the
	 * provided parameters.
	 *
	 * @param surveyReviewFetchRequestMobileDTO The {@code SurveyReviewFetchRequestMobileDTO} containing the request
	 *                                          parameters.
	 * @return The result of executing the stored procedure.
	 */
	public Object mobileGetAssignedPlot(SurveyReviewFetchRequestMobileDTO surveyReviewFetchRequestMobileDTO) {
		// (96,4,2022,2023)
		// Define the name of the stored procedure
		String fnName = "agri_stack.fn_mobile_get_assigned_plot_details";

		// Prepare the parameters for the stored procedure
		SqlData[] params = {new SqlData(1, surveyReviewFetchRequestMobileDTO.getUserId(), "int"),
				new SqlData(2, surveyReviewFetchRequestMobileDTO.getSeasonId(), "int"),
				new SqlData(3, surveyReviewFetchRequestMobileDTO.getStartYear(), "int"),
				new SqlData(4, surveyReviewFetchRequestMobileDTO.getEndYear(), "int"),
				new SqlData(5, surveyReviewFetchRequestMobileDTO.getPlotIds(), "string"),
				new SqlData(6, surveyReviewFetchRequestMobileDTO.getSurveyMasterId(), "string"),
				new SqlData(7, surveyReviewFetchRequestMobileDTO.getLat().toString(), "double"),
				new SqlData(8, surveyReviewFetchRequestMobileDTO.getLon().toString(), "double")};
		// Execute the stored procedure using the provided parameters and return the
		// result
		return dbUtils.executeStoredProcedure(fnName, "{}", params);

	}

	/**
	 * Retrieves assigned plot details along with their status for a mobile
	 * application based on the provided parameters.
	 *
	 * @param surveyReviewFetchRequestMobileDTO The {@code SurveyReviewFetchRequestMobileDTO} containing the request
	 *                                          parameters.
	 * @return The result of executing the stored procedure.
	 */
	public Object mobileGetAssignedPlotAndStatus(SurveyReviewFetchRequestMobileDTO surveyReviewFetchRequestMobileDTO) {
		// (96,4,2022,2023)
		String fnName = "agri_stack.fn_mobile_get_assigned_plot_and_status";
		SqlData[] params = {new SqlData(1, surveyReviewFetchRequestMobileDTO.getUserId(), "int"),
				new SqlData(2, surveyReviewFetchRequestMobileDTO.getSeasonId(), "int"),
				new SqlData(3, surveyReviewFetchRequestMobileDTO.getStartYear(), "int"),
				new SqlData(4, surveyReviewFetchRequestMobileDTO.getEndYear(), "int"),
				new SqlData(5, surveyReviewFetchRequestMobileDTO.getLat().toString(), "double"),
				new SqlData(6, surveyReviewFetchRequestMobileDTO.getLon().toString(), "double")};
		return dbUtils.executeStoredProcedure(fnName, "{}", params);
	}

	/**
	 * Retrieves the summary details of land surveys based on the provided input
	 * parameters.
	 *
	 * @param inputDao The {@code ReviewSurveyInputDAO} object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The response model containing the survey summary details.
	 */
	public Object getSurveySummary(ReviewSurveyInputDAO inputDao, HttpServletRequest request) {
		try {
			// Extract the user ID from the request using jwtTokenUtil and
			// CustomMessages.getUserId method

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			// Get the joined values from the inputDao lists
			String stateLgdCode = getJoinedValuefromList(inputDao.getStateLgdCodes());
			String districtLgdCode = getJoinedValuefromList(inputDao.getDistrictLgdCodes());
			String subDistrictLgdCode = getJoinedValuefromList(inputDao.getSubDistrictLgdCodes());
			String villageLgdCode = getJoinedValuefromList(inputDao.getVillageLgdCodes());
			String statusCode = getJoinedValuefromList(inputDao.getStatusCodes());

			String fnName = "agri_stack.fn_web_get_land_survey_summary_details";
			SqlData[] params = {new SqlData(1, userId, "string"), new SqlData(2, stateLgdCode, "string"),
					new SqlData(3, districtLgdCode, "string"), new SqlData(4, subDistrictLgdCode, "string"),
					new SqlData(5, villageLgdCode, "string"), new SqlData(6, statusCode, "string"),
					new SqlData(7, inputDao.getSeasonId(), "int"), new SqlData(8, inputDao.getStartYear(), "int"),
					new SqlData(9, inputDao.getEndYear(), "int")};
			String response = dbUtils.executeStoredProcedure(fnName, "[]", params).toString();

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<ReviewSurveyOutputDAO> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, ReviewSurveyOutputDAO.class));

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {

			// e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Converts a list of Long values into a comma-separated string.
	 *
	 * @param list The List of Long values.
	 * @return A comma-separated string representation of the Long values in the
	 * list. An empty string is returned if any exception occurs during the
	 * conversion.
	 */
	private String getJoinedValuefromList(List<Long> list) {
		try {
			return list.stream().map(i -> i.toString()).collect(Collectors.joining(", "));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Adds a survey based on the provided survey detail request and HTTP servlet
	 * request.
	 *
	 * @param surveyDetailMasterRequestDTO The {@code SurveyDetailMasterRequestDTO} containing the survey detail
	 *                                     master request.
	 * @param request                      The HTTP servlet request.
	 * @return An object representing the survey review fetch request for the mobile
	 * application.
	 * @throws Exception if an error occurs during the survey addition process.
	 */
	@Transactional
	public Object addSurvey(SurveyDetailMasterRequestDTO surveyDetailMasterRequestDTO, HttpServletRequest request)
			throws Exception {

		/**
		 * VALIDATION AND CHECK START HERE
		 */

		// String userId = CustomMessages.getUserId(request, jwtTokenUtil);
		// userId = (userId != null && !userId.isEmpty()) ? userId : "0";
		ObjectMapper mapper = new ObjectMapper();
		SurveyDetailRequestDTO surveyDetailRequestDTO = mapper.readValue(surveyDetailMasterRequestDTO.getData(),
				SurveyDetailRequestDTO.class);
		//
		// Uncomment below for survey activities config
		//

//		SurveyActivityInputDAO surveyActivityInput = new SurveyActivityInputDAO();
//		surveyActivityInput.setActivityIds(Arrays.asList(1L));
//		surveyActivityInput.setSeasonId(surveyDetailRequestDTO.getSeasonId());
//		surveyActivityInput.setYear(null);
//		ResponseModel surveyActivityTrack = surveyActivityConfigService.getSurveyActivityConfig(request,
//				surveyActivityInput);
//		if (surveyActivityTrack.getData() != null) {
//			List<SurveyActivityOutputDAO> outputList = (List<SurveyActivityOutputDAO>) surveyActivityTrack.getData();
//			if (outputList.size() > 0) {
//				SurveyActivityOutputDAO output = outputList.get(0);
//				if (surveyDetailRequestDTO.getSurveyDate() != null) {
//					try {
//						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
//						Date parsedDate = formatter.parse(surveyDetailRequestDTO.getSurveyDate());
//						if (output.getStarL̥tDate().compareTo(parsedDate)
//								* parsedDate.compareTo(output.getEndDate()) < 0) {
//							throw new Exception("Survey date not in range");
//						}
//					} catch (java.text.ParseException e) {
//						// e.printStackTrace();
//					}
//				}
//			}
//		}

		SowingSeason currentSowingSeason = generalService.getCurrentSeason();

		if (!surveyDetailRequestDTO.getSeasonId().equals(currentSowingSeason.getSeasonId())) {
			Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.CHECK_ACTIVE_SEASON.getNumericalCode());
			String message = "Survey dates are closed for this season. Please conduct survey in active season. (Error: 118)";
			if (cmop.isPresent()) {
				message = cmop.get().getErrorMessage();
			}
			throw new NoSuchElementException(message);
		}


		Boolean isCheck = configurationRepository.checkAccuracy(surveyDetailRequestDTO.getSurveyorLat(),
				surveyDetailRequestDTO.getSurveyorLong(), surveyDetailRequestDTO.getFarmlandPlotRegisterId());

		if (surveyDetailRequestDTO.getSurveyorLat() == 0 || surveyDetailRequestDTO.getSurveyorLat() == null
				|| surveyDetailRequestDTO.getSurveyorLong() == 0 || surveyDetailRequestDTO.getSurveyorLong() == null) {

			Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.GEO_LOCATION_CHECK_INVALID.getNumericalCode());
			String message = "GPS co-ordinates are not correct. Please refresh the co-ordinates and redo the survey. (Error: 116)";
			if (cmop.isPresent()) {
				message = cmop.get().getErrorMessage();
			}
			throw new NoSuchElementException(message);
		}

		if (isCheck.equals(Boolean.TRUE)) {
			Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.MAXIMUM_AREA_DISTANCE_CHECK.getNumericalCode());
			String message = "Distance from plot is greater than maximum allowed distance. (Error: 333)";
			if (cmop.isPresent()) {
				message = cmop.get().getErrorMessage();
			}
			throw new NoSuchElementException(message);
//			throw new NoSuchElementException("Distance from plot is greater than maximum allowed distance. (Error: 333)");
		}

		/**
		 * VALIDATE APP VERSION START
		 **/

		String versionCheckReqValue = commonUtil.getValuefromConfigCode(ConfigCode.VERSION_CHECK_REQUIRED);

		if (!Strings.isNullOrEmpty(versionCheckReqValue) && versionCheckReqValue.equalsIgnoreCase("TRUE")) {
			String versionCodeValue = commonUtil.getValuefromConfigCode(ConfigCode.MOBILE_APP_VERSION_CODE);

			String data = commonUtil.getValuefromConfigCode(ConfigCode.ALLOWED_PREVIOUS_APP_VERSION);

			List<String> prevVersionList = new ArrayList<String>();

			if (data != null) {
				prevVersionList = new ArrayList<String>(Arrays.asList(data.split(",")));
				if (prevVersionList.contains(surveyDetailRequestDTO.getAppVersion())) {

				}
			}

			if (Strings.isNullOrEmpty(versionCodeValue)
					|| (!versionCodeValue.equals(surveyDetailRequestDTO.getAppVersion()) ||
					Strings.isNullOrEmpty(data) ||
					prevVersionList == null || prevVersionList.size() == 0 ||
					!prevVersionList.contains(surveyDetailRequestDTO.getAppVersion())
			)
					|| Strings.isNullOrEmpty(surveyDetailRequestDTO.getAppVersion())) {
//				throw new NoSuchElementException("You are using incorrect version of the app. (Error: 1111)");
				Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.VERSION_MISMATCH.getNumericalCode());
				String message = "You are using incorrect version of the app. (Error: 1111)";
				if (cmop.isPresent()) {
					message = cmop.get().getErrorMessage();
				}
				throw new NoSuchElementException(message);
			}
			/**
			 * VALIDATE APP SIGNED START
			 **/

			String appSignedValue = commonUtil.getValuefromConfigCode(ConfigCode.APP_SIGNED);
			if (Strings.isNullOrEmpty(appSignedValue)
					|| (!appSignedValue.equals(surveyDetailRequestDTO.getIsSigned()))) {


				Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.APP_SIGNED.getNumericalCode());
				String message = "App signature is not valid. Please contact admin. (Error: 2222)";
				if (cmop.isPresent()) {
					message = cmop.get().getErrorMessage();
				}
				throw new NoSuchElementException(message);
//				throw new NoSuchElementException("App signature is not valid. Please contact admin. (Error: 2222)");
			}
		}

		/**
		 * VALIDATE APP VERSION END
		 **/

		if (isAppIntegrity.equals(Boolean.TRUE)) {
			String token = surveyDetailRequestDTO.getToken();
			List<String> deviceIds = userDeviceIntegrityDetailsRepository.checkIntegrityToken(surveyDetailRequestDTO.getSurveyBy(), token);
			if (deviceIds == null || deviceIds.size() == 0) {
				googlePlayIntegrityService.appIntegrity(token, surveyDetailRequestDTO.getSurveyBy());
			}


		}

//		if (isAppIntegrity.equals(Boolean.TRUE)) {
//			String token = surveyDetailRequestDTO.getToken();
//			googlePlayIntegrityService.appIntegrity(token,surveyDetailRequestDTO.getSurveyBy());
//		}
		/**
		 * CHECK MEDIA REQUEST start
		 **/

		Integer mediaCount = 0; // Multipart files count
		List<String> requestMediaNameList = new ArrayList<>(); // All media Names array from request
		if (surveyDetailRequestDTO.getCrops() != null && !surveyDetailRequestDTO.getCrops().isEmpty()) {

			for (SurveyDetailCropDTO crop : surveyDetailRequestDTO.getCrops()) {
				List<String> mediaList = crop.getMedia();
				if (mediaList != null && !mediaList.isEmpty()) {
					mediaCount += mediaList.size();
					requestMediaNameList.addAll(mediaList);
				}

				if (crop.getAreaTypeId().equals(3L) // for crop
						&& (mediaList == null || mediaList.isEmpty())) {

					Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.CROP_MEDIA_CHECK_NULL.getNumericalCode());
					String message = "Please ensure that photos are present for all crops. (Error: 101)";
					if (cmop.isPresent()) {
						message = cmop.get().getErrorMessage();
					}
					throw new NoSuchElementException(message);
//					throw new NoSuchElementException("Please ensure that photos are present for all crops. (Error: 101)");
				} else if (crop.getAreaTypeId().equals(1L) // UNUTILIZED
						&& (crop.getIsUnutilizedArea() == true) && (mediaList == null || mediaList.isEmpty())) {

					Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.UNUTILIZED_MEDIA_CHECK_NULL.getNumericalCode());
					String message = "Please ensure that photos are present for NA area. (Error: 102)";
					if (cmop.isPresent()) {
						message = cmop.get().getErrorMessage();
					}
					throw new NoSuchElementException(message);
//					throw new NoSuchElementException("Please ensure that photos are present for NA area. (Error: 102)");
				} else if (crop.getAreaTypeId().equals(2L) // WASTE/VACANT
						&& (crop.getIsWasteArea() == true) && (mediaList == null || mediaList.isEmpty())) {
					Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.WASTE_OR_VACANT_MEDIA_CHECK_NULL.getNumericalCode());
					String message = "Please ensure that photos are present for fallow area. (Error: 103)";
					if (cmop.isPresent()) {
						message = cmop.get().getErrorMessage();
					}
					throw new NoSuchElementException(message);
//					throw new NoSuchElementException("Please ensure that photos are present for fallow area. (Error: 103)");
				} else if (crop.getAreaTypeId().equals(4L) // HARVESTED
						&& (crop.getIsHarvestedArea() == true) && (mediaList == null || mediaList.isEmpty())) {
					Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.HARVESTED_MEDIA_CHECK_NULL.getNumericalCode());
					String message = "Please ensure that photos are present for harvested area. (Error: 104)";
					if (cmop.isPresent()) {
						message = cmop.get().getErrorMessage();
					}
					throw new NoSuchElementException(message);
//					throw new NoSuchElementException("Please ensure that photos are present for harvested area. (Error: 104)");
				}

			}
		}

		Integer mediaRequestCount = 0;
		if (surveyDetailMasterRequestDTO.getMedia() != null) {
			mediaRequestCount = surveyDetailMasterRequestDTO.getMedia().size();

			for (String mediaName : requestMediaNameList) {
				Optional<MultipartFile> media = surveyDetailMasterRequestDTO.getMedia().stream()
						.filter((MultipartFile data) -> mediaName.equals(data.getOriginalFilename())).findFirst();
				if (!media.isPresent()) {
					Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.MEDIA_CHECK_NULL.getNumericalCode());
					String message = "Please ensure that photos are present for all land usage type. (Error: 105)";
					if (cmop.isPresent()) {
						message = cmop.get().getErrorMessage();
					}
					throw new NoSuchElementException(message);
//					throw new NoSuchElementException("Please ensure that photos are present for all land usage type. (Error: 105)");
				}
			}
		}

		if (mediaCount != mediaRequestCount) {
			Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.MEDIA_CHECK_NULL.getNumericalCode());
			String message = "Please ensure that photos are present for all land usage type. (Error: 105)";
			if (cmop.isPresent()) {
				message = cmop.get().getErrorMessage();
			}
			throw new NoSuchElementException(message);
//			throw new NoSuchElementException("Please ensure that photos are present for all land usage type. (Error: 105)");
		}

		/**
		 * CHECK MEDIA REQUEST end
		 **/

		// Get FarmlandPlotRegistry based on the provided farmlandPlotRegisterId
		Optional<FarmlandPlotRegistry> flpr = Optional.empty();
		if (surveyDetailRequestDTO.getFarmlandPlotRegisterId() != null) {
			flpr = farmlandPlotRegistryRepository.findById(surveyDetailRequestDTO.getFarmlandPlotRegisterId());
		}

		if (flpr.isPresent() && surveyDetailRequestDTO.getReviewNo() != 3 && surveyDetailRequestDTO.getReviewNo() != 5) {
			Optional<UserLandAssignment> ula = userLandAssignmentRepository
					.findUserLandAssignmentWithUserIdAndFarmlandId(surveyDetailRequestDTO.getSurveyBy(),
							flpr.get().getFarmlandId(), surveyDetailRequestDTO.getSeasonId(),
							surveyDetailRequestDTO.getStartYear(), surveyDetailRequestDTO.getEndYear());
			if (!ula.isPresent()) {

				Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.LAND_ASSIGNMENT_CHECK_NOT_FOUND.getNumericalCode());
				String message = "It seems plot assignment is removed for you. Please contact supervisor.(Error: 106)";
				if (cmop.isPresent()) {
					message = cmop.get().getErrorMessage();
				}
				throw new NoSuchElementException(message);
//				throw new NoSuchElementException("It seems plot assignment is removed for you. Please contact supervisor.(Error: 106)");

			}

			unableToSurveyRepository.updateStatusByParcelIdSeasonAndYear(flpr.get().getFarmlandPlotRegistryId(),
					surveyDetailRequestDTO.getSeasonId(), surveyDetailRequestDTO.getStartYear(),
					surveyDetailRequestDTO.getEndYear());
		}

		/**
		 * VALIDATION AND CHECK ENDS
		 */

		/** KAFKA CHECK START **/
//		if (String.valueOf(kafkaEnabled).equalsIgnoreCase("TRUE")) {
//
//			for (SurveyDetailCropDTO surveyCrop : surveyDetailRequestDTO.getCrops()) {
//				List<String> uploadedMediaIds = addCropSurveyMediaMappingWithNames(
//						surveyDetailMasterRequestDTO.getMedia(), surveyCrop.getMedia());
//				surveyCrop.setUploadedMedia(uploadedMediaIds);
//			}
//			String jsonData = new Gson().toJson(surveyDetailRequestDTO);
//			surveyDetailMasterRequestDTO.setData(jsonData);
//			kCropSurveyService.validateCropSurveyRecordAndAddInKafka(surveyDetailMasterRequestDTO, request);
//		}
		/** KAFKA CHECK END **/

		// Create a new instance of LandParcelSurveyMaster and check if there is an
		// existing record
		LandParcelSurveyMaster landParcelSurveyMaster = new LandParcelSurveyMaster();
		Optional<LandParcelSurveyMaster> landParcelSurveyOptionalMaster = Optional.empty();
		if (surveyDetailRequestDTO.getLandParcelSurveyMasterId() != null
				&& surveyDetailRequestDTO.getLandParcelSurveyMasterId() != 0) {
			landParcelSurveyOptionalMaster = landParcelSurveyMasterRespository
					.findById(surveyDetailRequestDTO.getLandParcelSurveyMasterId());
		} else {
			landParcelSurveyOptionalMaster = landParcelSurveyMasterRespository.findByParcelSeasonAndYear(
					surveyDetailRequestDTO.getFarmlandPlotRegisterId(), surveyDetailRequestDTO.getStartYear(),
					surveyDetailRequestDTO.getEndYear(), surveyDetailRequestDTO.getSeasonId());
		}

		String token = null;
		if (isAppIntegrity.equals(Boolean.TRUE)) {
//			landParcelSurveyMaster.setToken(surveyDetailRequestDTO.getToken());
			token = surveyDetailRequestDTO.getToken();
		}
		// Check if the optional contains a value and update the landParcelSurveyMaster
		// accordingly
		if (landParcelSurveyOptionalMaster.isPresent()) {
			landParcelSurveyMaster = landParcelSurveyOptionalMaster.get();
		}

		// Update the landParcelSurveyMaster with farmlandPlotRegisterId parcelId
		if (flpr.isPresent()) {
			landParcelSurveyMaster.setParcelId(flpr.get());

			VillageLgdMaster village = flpr.get().getVillageLgdMaster();
			landParcelSurveyMaster.setVillageLgdCode(village.getVillageLgdCode());
			landParcelSurveyMaster.setSubDistrictLgdCode(village.getSubDistrictLgdCode().getSubDistrictLgdCode());
			landParcelSurveyMaster.setDistrictLgdCode(village.getDistrictLgdCode().getDistrictLgdCode());
			landParcelSurveyMaster.setStateLgdCode(village.getStateLgdCode().getStateLgdCode());

		}
		landParcelSurveyMaster.setSeasonId(surveyDetailRequestDTO.getSeasonId());
		landParcelSurveyMaster.setSeasonStartYear(surveyDetailRequestDTO.getStartYear());
		landParcelSurveyMaster.setSeasonEndYear(surveyDetailRequestDTO.getEndYear());
		landParcelSurveyMaster.setAppVersion(surveyDetailRequestDTO.getAppVersion());

		// Update the survey status based on the review number
		if (surveyDetailRequestDTO.getReviewNo() == 1) {
			landParcelSurveyMaster.setSurveyOneStatus(StatusEnum.PENDING.getValue());
			landParcelSurveyMaster.setSurveyStatus(StatusEnum.PENDING.getValue());
		}

		if (surveyDetailRequestDTO.getReviewNo() == 2) {
			landParcelSurveyMaster.setSurveyTwoStatus(StatusEnum.PENDING.getValue());
			landParcelSurveyMaster.setSurveyStatus(StatusEnum.PENDING.getValue());
		}

		if (surveyDetailRequestDTO.getReviewNo() == 3) {
			landParcelSurveyMaster.setVerifierStatus(StatusEnum.APPROVED.getValue());
			if (landParcelSurveyMaster.getSurveyOneStatus() == null
					&& landParcelSurveyMaster.getSurveyTwoStatus() == null) {
				landParcelSurveyMaster.setSurveyStatus(StatusEnum.SURVEY_PENDING.getValue());
			} else {
				landParcelSurveyMaster.setSurveyStatus(landParcelSurveyMaster.getSurveyStatus());

			}

		}
		if (surveyDetailRequestDTO.getReviewNo() == 5) {
			landParcelSurveyMaster.setInspectionOfficerStatus(StatusEnum.APPROVED.getValue());
			if (landParcelSurveyMaster.getSurveyOneStatus() == null
					&& landParcelSurveyMaster.getSurveyTwoStatus() == null) {
				landParcelSurveyMaster.setSurveyStatus(StatusEnum.SURVEY_PENDING.getValue());
			} else {
				landParcelSurveyMaster.setSurveyStatus(landParcelSurveyMaster.getSurveyStatus());

			}

		}


		/**
		 * VALIDATION CHECK FOR SURVEYOR AND REVIEW NO
		 **/

		if (surveyDetailRequestDTO.getReviewNo() == null || surveyDetailRequestDTO.getReviewNo() == 0) {
			Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.REVIEW_NO_CHECK_ZERO.getNumericalCode());
			String message = "Survey review information is not available. Please contact admin. (Error: 107)";
			if (cmop.isPresent()) {
				message = cmop.get().getErrorMessage();
			}
			throw new NoSuchElementException(message);
//			throw new NoSuchElementException("Survey review information is not available. Please contact admin. (Error: 107)");
		}

		Optional<UserMaster> surveyorOp = userMasterRepository
				.findByUserIdAndIsDeleted(surveyDetailRequestDTO.getSurveyBy(), false);
		if (surveyorOp.isPresent()) {
			UserMaster surveyor = surveyorOp.get();

			if (surveyor.getRoleId().getRoleId().equals(Long.valueOf(202)) && surveyDetailRequestDTO.getReviewNo() != 1
					&& surveyDetailRequestDTO.getReviewNo() != 2) {

				Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.REVIEW_NO_CHECK_IN_VALID.getNumericalCode());
				String message = "Survey information is invalid. Please contact admin. (Error: 108)";
				if (cmop.isPresent()) {
					message = cmop.get().getErrorMessage();
				}
				throw new NoSuchElementException(message + surveyDetailRequestDTO.getReviewNo());
//				throw new NoSuchElementException("Survey information is invalid. Please contact admin. (Error: 108) " + surveyDetailRequestDTO.getReviewNo());

			} else if (surveyor.getRoleId().getRoleId().equals(Long.valueOf(3))
					&& surveyDetailRequestDTO.getReviewNo() != 3) {
				Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.REVIEW_NO_CHECK_IN_VALID.getNumericalCode());
				String message = "Survey information is invalid. Please contact admin. (Error: 108)";
				if (cmop.isPresent()) {
					message = cmop.get().getErrorMessage();
				}
				throw new NoSuchElementException(message + surveyDetailRequestDTO.getReviewNo());
//				throw new NoSuchElementException("Survey information is invalid. Please contact admin. (Error: 108) " + surveyDetailRequestDTO.getReviewNo());
			} else if (surveyor.getRoleId().getRoleId().equals(Long.valueOf(203))
					&& surveyDetailRequestDTO.getReviewNo() != 5) {
				Optional<ErrorMessageMaster> cmop = errorMessageMasterRepository.findByErrorCode(ErrorCode.REVIEW_NO_CHECK_IN_VALID.getNumericalCode());
				String message = "Survey information is invalid. Please contact admin. (Error: 108)";
				if (cmop.isPresent()) {
					message = cmop.get().getErrorMessage();
				}
				throw new NoSuchElementException(message + surveyDetailRequestDTO.getReviewNo());
//				throw new NoSuchElementException("Survey information is invalid. Please contact admin. (Error: 108) " + surveyDetailRequestDTO.getReviewNo());
			}
//			else {
//				throw new NoSuchElementException("Survey review information is not available. Please contact admin. (Error: 107)");
//			}
		}

		/**
		 * VALIDATION CHECK FOR SURVEYOR AND REVIEW NO END
		 **/

		// Update the survey mode and flexible survey reason
		if ((surveyDetailRequestDTO.getSurveyMode() != null) && (Integer.valueOf(CropSurveyModeEnum.FLEXIBLE.getValue())
				.equals(surveyDetailRequestDTO.getSurveyMode())
				// surveyDetailRequestDTO.getSurveyMode() ==
				// CropSurveyModeEnum.FLEXIBLE.getValue()
		)) {
			landParcelSurveyMaster.setSurveyMode(CropSurveyModeEnum.FLEXIBLE.getValue());
			landParcelSurveyMaster.setFlexibleSurveyReasonId(surveyDetailRequestDTO.getFlexibleSurveyReasonId());
		} else {
			landParcelSurveyMaster.setSurveyMode(CropSurveyModeEnum.MANDATORY.getValue());
		}

		// Save the updated landParcelSurveyMaster
		landParcelSurveyMaster = landParcelSurveyMasterRespository.save(landParcelSurveyMaster);
		surveyDetailRequestDTO.setLandParcelSurveyMasterId(landParcelSurveyMaster.getLpsmId());


		if (token != null) {
			LpsmToken lpsmToken = new LpsmToken();
			lpsmToken.setLpsmId(landParcelSurveyMaster.getLpsmId());
			lpsmToken.setToken(token);
			lpsmToken.setIsActive(Boolean.TRUE);
			lpsmToken.setIsDeleted(Boolean.FALSE);
			lpsmToken.setCreatedBy(landParcelSurveyMaster.getCreatedBy());
			lpsmToken.setModifiedBy(landParcelSurveyMaster.getCreatedBy());
			lpsmToken.setCreatedOn(new Timestamp(new Date().getTime()));
			lpsmToken.setModifiedOn(new Timestamp(new Date().getTime()));
			lpsmTokenRepository.save(lpsmToken);
		}

		// Add LandParcelSurveyDetail
		LandParcelSurveyDetail landParcelSurveyDetail = addLandParcelSurveyDetails(landParcelSurveyMaster,
				surveyDetailMasterRequestDTO, surveyDetailRequestDTO);

		/// add crop media in GCS
		for (SurveyDetailCropDTO surveyCrop : surveyDetailRequestDTO.getCrops()) {
			List<String> uploadedMediaIds = addCropSurveyMediaMappingWithNames(surveyDetailMasterRequestDTO.getMedia(),
					surveyCrop.getMedia());
			surveyCrop.setUploadedMedia(uploadedMediaIds);
		}

		// add Crops
		if (surveyDetailRequestDTO.getCrops() != null) {
			addCropDetails(landParcelSurveyDetail, surveyDetailMasterRequestDTO, surveyDetailRequestDTO, flpr.get());
		}

		// add Cultivators

		if (surveyDetailRequestDTO.getCultivators() != null && !surveyDetailRequestDTO.getCultivators().isEmpty()) {
			addCultivatorPlotMappingDetails(surveyDetailRequestDTO.getCultivators(), surveyDetailRequestDTO);
		}

		// Start Send notification to admin
//		try {
//			List<String> receiverIds = userLandAssignmentRepository.getUserListByFilter(
//					surveyDetailRequestDTO.getStartYear(), surveyDetailRequestDTO.getEndYear(),
//					surveyDetailRequestDTO.getSeasonId(),
//					landParcelSurveyMaster.getParcelId().getFarmlandPlotRegistryId());
//			if (receiverIds != null && receiverIds.size() > 0) {
//				sendNotification(request, MessageType.SURVEY_COMPLETED, NotificationType.WEB,
//						ActivityTypeEnum.CROP_SURVEY, ActivityStatusEnum.SURVEY_COMPLETED,
//						surveyDetailRequestDTO.getSurveyBy(), receiverIds, landParcelSurveyMaster.getLpsmId(),
//						MasterTableName.LAND_PARCEL_SURVEY_MASTER);
//			}
//		} catch (Exception e) {
//			System.out.print(e.getMessage());
//		}

		// End Send notification to admin

		// Return response
		// SurveyReviewFetchRequestMobileDTO surveyReviewFetchRequestMobileDTO = new
		// SurveyReviewFetchRequestMobileDTO();
		// surveyReviewFetchRequestMobileDTO.setUserId(surveyDetailRequestDTO.getSurveyBy().toString());
		// surveyReviewFetchRequestMobileDTO.setSeasonId(surveyDetailRequestDTO.getSeasonId().toString());
		// surveyReviewFetchRequestMobileDTO.setStartYear(surveyDetailRequestDTO.getStartYear().toString());
		// surveyReviewFetchRequestMobileDTO.setEndYear(surveyDetailRequestDTO.getEndYear().toString());
		// surveyReviewFetchRequestMobileDTO.setPlotIds("0");
		// surveyReviewFetchRequestMobileDTO.setSurveyMasterId(landParcelSurveyMaster.getLpsmId().toString());

		List<AssignedPlotResponseDTO> responseList = new ArrayList<AssignedPlotResponseDTO>();
		AssignedPlotResponseDTO responseDTO = new AssignedPlotResponseDTO();
		responseDTO.setLandParcelSurveyId(landParcelSurveyMaster.getLpsmId());
		responseDTO.setFarmlandPlotRegistryId(surveyDetailRequestDTO.getFarmlandPlotRegisterId());
		responseList.add(responseDTO);
		return responseList;

	}

	/**
	 * Sends a notification based on the provided parameters.
	 *
	 * @param request            The HTTP servlet request.
	 * @param messageType        The type of the message.
	 * @param notificationType   The type of the notification.
	 * @param activityTypeEnum   The type of the activity.
	 * @param activityStatusEnum The status of the activity.
	 * @param senderId           The ID of the sender.
	 * @param receiverIds        The IDs of the receivers.
	 * @param masterTableId      The ID of the master table.
	 * @param masterTableName    The name of the master table.
	 */
	void sendNotification(HttpServletRequest request, MessageType messageType, NotificationType notificationType,
						  ActivityTypeEnum activityTypeEnum, ActivityStatusEnum activityStatusEnum, Long senderId,
						  List<String> receiverIds, Long masterTableId, MasterTableName masterTableName) {
		Optional<MessageConfigurationMaster> messageop = messageConfigurationRepository.findByMessageType(messageType);
		NotificationMasterDao notificationMasterDao = new NotificationMasterDao();
		if (messageop.isPresent()) {
			MessageConfigurationMaster messageConfigurationMaster = messageop.get();
			notificationMasterDao.setNotificationTitle(messageConfigurationMaster.getEmailSubject());
			notificationMasterDao.setMessage(messageConfigurationMaster.getTemplate());
		}
		notificationMasterDao.setNotificationType(notificationType);

		Optional<ActivityType> activityTypeOP = activityTypeRepository
				.findByActivityTypeName(activityTypeEnum.CROP_SURVEY.getValue());
		if (activityTypeOP.isPresent()) {
			notificationMasterDao.setActivityTypeId(activityTypeOP.get().getActivityTypeId());
		}
		Optional<ActivityStatus> activityStatusOP = activityStatusRepository
				.findByActivityStatusName(activityStatusEnum.SURVEY_COMPLETED.getValue());
		if (activityStatusOP.isPresent()) {
			notificationMasterDao.setActivityStatusId(activityStatusOP.get().getActivityStatusId());
		}
		List<ReceiverDAO> receiverList = new ArrayList<ReceiverDAO>();
		if (receiverIds != null) {
			List<Long> ids = receiverIds.stream().map(m -> Long.parseLong(m)).collect(Collectors.toList());
			List<UserMaster> userList = userMasterRepository.findByUserIdInAndIsDeletedAndIsActive(ids, false, true);
			userList.forEach(action -> {
				ReceiverDAO receiverDAO = new ReceiverDAO();
				receiverDAO.setDeviceToken(action.getDeviceToken());
				receiverDAO.setReceiverId(action.getUserId());
				receiverList.add(receiverDAO);
				notificationMasterDao.setReceiverList(receiverList);
			});

		}
		notificationMasterDao.setSenderId(senderId);
		notificationMasterDao.setMasterTableId(masterTableId);
		notificationMasterDao.setMasterTableName(masterTableName);

		notificationService.sendNotification(request, notificationMasterDao);
	}

	/**
	 * Adds a land parcel survey detail based on the provided parameters.
	 *
	 * @param landParcelSurveyMaster       The land parcel survey master.
	 * @param surveyDetailMasterRequestDTO The survey detail master request DTO.
	 * @param surveyDetailRequestDTO       The survey detail request DTO.
	 * @return The added land parcel survey detail.
	 */
	public LandParcelSurveyDetail addLandParcelSurveyDetails(LandParcelSurveyMaster landParcelSurveyMaster,
															 SurveyDetailMasterRequestDTO surveyDetailMasterRequestDTO, SurveyDetailRequestDTO surveyDetailRequestDTO) {

		Optional<LandParcelSurveyDetail> landParcelSurveyDetailOptioanl = landParcelSurveyDetailRepository
				.findByLpsmIdAndReviewNo(landParcelSurveyMaster.getLpsmId(), surveyDetailRequestDTO.getReviewNo());
		LandParcelSurveyDetail landParcelSurveyDetail = new LandParcelSurveyDetail();
		if (landParcelSurveyDetailOptioanl.isPresent()) {
			landParcelSurveyDetail = landParcelSurveyDetailOptioanl.get();
		}
		// Optional<FarmerRegistry> farmer = Optional.empty();
		if (surveyDetailRequestDTO.getOwnerId() != null && surveyDetailRequestDTO.getOwnerId() != 0) {
			// farmer =
			// farmerRegistryRepository.findById(surveyDetailRequestDTO.getOwnerId());
			landParcelSurveyDetail.setOwnerId(surveyDetailRequestDTO.getOwnerId());
		}
		landParcelSurveyDetail.setLpsmId(landParcelSurveyMaster.getLpsmId());
		// if (farmer.isPresent()) {
		// }
		if (surveyDetailRequestDTO.getUniqueSurveyId() != null) {
			landParcelSurveyDetail.setUniqueSurveyId(surveyDetailRequestDTO.getUniqueSurveyId());
		}
		landParcelSurveyDetail.setMobileNo(surveyDetailRequestDTO.getOwnerMobile());
		landParcelSurveyDetail.setArea(surveyDetailRequestDTO.getArea());
		landParcelSurveyDetail.setUnit(surveyDetailRequestDTO.getUnit());

//		landParcelSurveyDetail.setBalancedArea(surveyDetailRequestDTO.getBalancedArea());
//		landParcelSurveyDetail.setBalancedAreaUnit(surveyDetailRequestDTO.getBalancedAreaUnit());
//		landParcelSurveyDetail.setBalancedArea(surveyDetailRequestDTO.getBalancedArea());
//		landParcelSurveyDetail.setBalancedAreaUnit(surveyDetailRequestDTO.getBalancedAreaUnit());
		landParcelSurveyDetail.setBalancedArea(0.0);
		landParcelSurveyDetail.setBalancedAreaUnit(surveyDetailRequestDTO.getUnit());

		landParcelSurveyDetail.setSurveyorLat(surveyDetailRequestDTO.getSurveyorLat());
		landParcelSurveyDetail.setSurveyorLong(surveyDetailRequestDTO.getSurveyorLong());
		// wkt to geom
		if (surveyDetailRequestDTO.getGeom() != null) {
			WKTReader wktReader = new WKTReader();
			try {
				landParcelSurveyDetail.setGeom(wktReader.read(surveyDetailRequestDTO.getGeom()));
			} catch (ParseException e) {
				System.out.println("Error while parsing wkt to geom");
			}
		}
		if (surveyDetailRequestDTO.getIsOnline() != null) {
			landParcelSurveyDetail.setIsOnline(surveyDetailRequestDTO.getIsOnline());
		}
		landParcelSurveyDetail.setTotalArea(surveyDetailRequestDTO.getArea());
		landParcelSurveyDetail.setTotalAreaUnit(surveyDetailRequestDTO.getUnit());
		landParcelSurveyDetail.setReviewNo(surveyDetailRequestDTO.getReviewNo());
		landParcelSurveyDetail.setSurveyBy(surveyDetailRequestDTO.getSurveyBy());
		landParcelSurveyDetail.setStatus(StatusEnum.PENDING.getValue());
		if (surveyDetailRequestDTO.getSurveyDate() != null) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
				Date parsedDate = formatter.parse(surveyDetailRequestDTO.getSurveyDate());
				Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
				landParcelSurveyDetail.setSurveyDate(timestamp);
			} catch (java.text.ParseException e) {
				// e.printStackTrace();
			}
		}
		landParcelSurveyDetail = landParcelSurveyDetailRepository.save(landParcelSurveyDetail);
		addSurveyActivityLog(landParcelSurveyMaster.getLpsmId(), landParcelSurveyDetail.getLpsdId(),
				landParcelSurveyDetail.getStatus().longValue(), landParcelSurveyDetail.getSurveyBy());
		return landParcelSurveyDetail;
	}

	/**
	 * Adds a survey activity log with the specified parameters.
	 *
	 * @param lpsmId   The ID of the land parcel survey master.
	 * @param lpsdId   The ID of the land parcel survey detail.
	 * @param statusId The ID of the status.
	 * @param userId   The ID of the user.
	 */
	public void addSurveyActivityLog(Long lpsmId, Long lpsdId, Long statusId, Long userId) {
		try {
			SurveyActivityLog surveyActivityLog = new SurveyActivityLog();
			surveyActivityLog.setLpsmId(lpsmId);
			surveyActivityLog.setLpsdId(lpsdId);
			surveyActivityLog.setStatusId(statusId);
			surveyActivityLog.setUserId(userId);
			surveyActivityLogRepository.save(surveyActivityLog);
		} catch (Exception e) {
			System.out.println("Error while adding activity load");
		}
	}

	/**
	 * Adds crop details for a land parcel survey.
	 *
	 * @param landParcelSurveyDetail       The land parcel survey detail.
	 * @param surveyDetailMasterRequestDTO The survey detail master request DTO.
	 * @param surveyDetailRequestDTO       The survey detail request DTO.
	 * @param farmlandPlotRegistry
	 * @throws Exception
	 */
//	@Transactional
	public void addCropDetails(LandParcelSurveyDetail landParcelSurveyDetail,
							   SurveyDetailMasterRequestDTO surveyDetailMasterRequestDTO, SurveyDetailRequestDTO surveyDetailRequestDTO,
							   FarmlandPlotRegistry farmlandPlotRegistry) throws Exception {
		try {
			landParcelSurveyCropDetailRepository.deleteCropMediaByLpsdId(landParcelSurveyDetail.getLpsdId());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			landParcelSurveyCropDetailRepository.deleteCropMediaMappingByLpsdId(landParcelSurveyDetail.getLpsdId());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			landParcelSurveyCropDetailRepository.deleteByLpsdId(landParcelSurveyDetail.getLpsdId());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		for (SurveyDetailCropDTO surveyCrop : surveyDetailRequestDTO.getCrops()) {
			LandParcelSurveyCropDetail landParcelSurveyCropDetail = new LandParcelSurveyCropDetail();
			landParcelSurveyCropDetail.setLpsdId(landParcelSurveyDetail.getLpsdId());
			// area;
			landParcelSurveyCropDetail.setArea(surveyCrop.getArea());
			// unit;
			landParcelSurveyCropDetail.setUnit(surveyCrop.getUnit());
			// remark
			landParcelSurveyCropDetail.setRemarks(surveyCrop.getRemark());

			Double totalPlotArea = farmlandPlotRegistry.getPlotArea();

			if (surveyCrop.getSowingDate() != null) {
				try {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
					Date parsedDate = formatter.parse(surveyCrop.getSowingDate());
					Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
					landParcelSurveyCropDetail.setSowingDate(timestamp);
				} catch (java.text.ParseException e) {
					// e.printStackTrace();
				}
			}

			landParcelSurveyCropDetail.setAreaTypeId(surveyCrop.getAreaTypeId());

			Integer areaTypeId = surveyCrop.getAreaTypeId().intValue();

			Boolean areaCheckFlag = false;

			if (surveyCrop.getArea() != null
//					&& diffArea >= -3
//					&& Math.abs(diffArea) <= 3
//					&& surveyCrop.getArea() <= totalPlotArea
			) {
				Double diffArea = totalPlotArea - surveyCrop.getArea();
				if (diffArea >= 0) {
					areaCheckFlag = true;
				} else {
					throw new NoSuchElementException("Area entered is not valid as per validation rule. (Error: 109)");
				}

			}

			// FALLOW AREA
			if (areaTypeId.equals(SurveyAreaTypeEnum.WASTEVACANT.getValue())) {

				landParcelSurveyCropDetail.setIsWasteArea(surveyCrop.getIsWasteArea());
				landParcelSurveyCropDetail.setLandtypeId(LandTypeEnum.AGRICULTURAL.getValue());

				if (Boolean.TRUE.equals(surveyCrop.getIsWasteArea())
//						&& surveyCrop.getArea() != null && surveyCrop.getArea() <= totalPlotArea
						&& areaCheckFlag) {
					landParcelSurveyCropDetail.setArea(surveyCrop.getArea());
				} else {
					landParcelSurveyCropDetail.setArea(Double.valueOf(0));
				}

				// NON AGRICULTURE AREA

			} else if (areaTypeId.equals(SurveyAreaTypeEnum.UNUTILIZED.getValue())) {

				landParcelSurveyCropDetail.setIsUnutilizedArea(surveyCrop.getIsUnutilizedArea());
				landParcelSurveyCropDetail.setLandtypeId(LandTypeEnum.NONAGRICULTURAL.getValue());

				if (Boolean.TRUE.equals(surveyCrop.getIsUnutilizedArea())
//						&& surveyCrop.getArea() != null && surveyCrop.getArea() <= totalPlotArea
						&& areaCheckFlag) {
					landParcelSurveyCropDetail.setArea(surveyCrop.getArea());
				} else {
					landParcelSurveyCropDetail.setArea(Double.valueOf(0));
				}

				if (surveyCrop.getNaTypeId() != null && surveyCrop.getNaTypeId() > 0) {

					landParcelSurveyCropDetail.setNaTypeId(surveyCrop.getNaTypeId());

				}

				// HARVESTED AREA
			} else if (areaTypeId.equals(SurveyAreaTypeEnum.HARVESTED.getValue())) {
				landParcelSurveyCropDetail.setIsHarvestedArea(surveyCrop.getIsHarvestedArea());
				landParcelSurveyCropDetail.setLandtypeId(LandTypeEnum.AGRICULTURAL.getValue());

				if (Boolean.TRUE.equals(surveyCrop.getIsHarvestedArea())
//						&& surveyCrop.getArea() != null && surveyCrop.getArea() <= totalPlotArea
						&& areaCheckFlag) {
					landParcelSurveyCropDetail.setArea(surveyCrop.getArea());
				} else {
					landParcelSurveyCropDetail.setArea(Double.valueOf(0));
				}

//				CROP AREA 
			} else {

				// CROP AREA
				// && surveyCrop.getArea() != null && surveyCrop.getArea() <= totalPlotArea
				if (areaCheckFlag) {
					landParcelSurveyCropDetail.setArea(surveyCrop.getArea());
				} else {
					landParcelSurveyCropDetail.setArea(Double.valueOf(0));
				}

				// cropId;

				if (surveyCrop.getCropId() != null && surveyCrop.getCropId() > 0) {
					landParcelSurveyCropDetail.setCropId(surveyCrop.getCropId());
				}
				// cropVarietyId;

				if (surveyCrop.getCropVarietyId() != null && surveyCrop.getCropVarietyId() > 0) {
					landParcelSurveyCropDetail.setCropVarietyId(surveyCrop.getCropVarietyId());
				}
				// cropTypeId;

				if (surveyCrop.getCropTypeId() != null && surveyCrop.getCropTypeId() > 0) {
					landParcelSurveyCropDetail.setCropTypeId(surveyCrop.getCropTypeId());
				}
				// cropStatusId
				if (surveyCrop.getCropStatusId() != null && surveyCrop.getCropStatusId() > 0) {
					landParcelSurveyCropDetail.setCropStage(surveyCrop.getCropStatusId());
				}
				// irrigationTypeId

				if (surveyCrop.getIrrigationTypeId() != null && surveyCrop.getIrrigationTypeId() > 0) {
					landParcelSurveyCropDetail.setIrrigationTypeId(surveyCrop.getIrrigationTypeId());
				}

				// irrigationSourceId

				if (surveyCrop.getIrrigationSourceId() != null && surveyCrop.getIrrigationSourceId() > 0) {
					landParcelSurveyCropDetail.setIrrigationSourceId(surveyCrop.getIrrigationSourceId());
				}

				// cropCategoryId

				if (surveyCrop.getCropCategory() != null && surveyCrop.getCropCategory() > 0) {
					landParcelSurveyCropDetail.setCropCategoryId(surveyCrop.getCropCategory());
				}

				// cropClassId
				if (surveyCrop.getCropClassId() != null && surveyCrop.getCropClassId() > 0) {

					landParcelSurveyCropDetail.setCropClassId(surveyCrop.getCropClassId());

				}
				if (surveyCrop.getNumberOfTree() != null && surveyCrop.getNumberOfTree() > 0) {
					landParcelSurveyCropDetail.setNoOfTree(surveyCrop.getNumberOfTree());
				}

				landParcelSurveyCropDetail.setLandtypeId(LandTypeEnum.AGRICULTURAL.getValue());

			}

			if ((surveyCrop.getUploadedMedia() != null && surveyCrop.getMedia() != null
					&& surveyCrop.getUploadedMedia().size() == surveyCrop.getMedia().size())
					|| (surveyCrop.getUploadedMedia() == null && surveyCrop.getMedia() == null)) {

				landParcelSurveyCropDetail = landParcelSurveyCropDetailRepository.save(landParcelSurveyCropDetail);

				// remove try catch to handle media exceptions
//				addCropSurveyMediaMapping(surveyDetailMasterRequestDTO.getMedia(), surveyCrop.getMedia(),
//						landParcelSurveyCropDetail.getCropSdId());

				saveCropSurveyMediaMapping(surveyCrop.getUploadedMedia(), landParcelSurveyCropDetail.getCropSdId());

			} else {
				throw new NoSuchElementException("Photos are not uploaded properly. Please try again. (Error: 110)");
			}

		}
	}

	private void addCultivatorPlotMappingDetails(List<CultivatorsRequestDAO> cultivators,
												 SurveyDetailRequestDTO surveyDetailRequestDTO) throws Exception {

		List<CultivatorPlotMappingDetail> mappingDetails = new ArrayList<>();
		for (CultivatorsRequestDAO cultivatorsRequestDAO : cultivators) {
			CultivatorPlotMappingDetail cultivatorPlotMappingDetail = new CultivatorPlotMappingDetail();

			cultivatorPlotMappingDetail.setSeasonId(surveyDetailRequestDTO.getSeasonId());
			cultivatorPlotMappingDetail.setSeasonStartYear(surveyDetailRequestDTO.getStartYear());
			cultivatorPlotMappingDetail.setSeasonEndYear(surveyDetailRequestDTO.getEndYear());
			cultivatorPlotMappingDetail.setParcelId(surveyDetailRequestDTO.getFarmlandPlotRegisterId());
			cultivatorPlotMappingDetail.setSurveyBy(surveyDetailRequestDTO.getSurveyBy());

			cultivatorPlotMappingDetail.setCreatedBy(String.valueOf(surveyDetailRequestDTO.getSurveyBy()));
			cultivatorPlotMappingDetail.setCreatedOn(new Timestamp(new Date().getTime()));

			cultivatorPlotMappingDetail.setCultivatorId(cultivatorsRequestDAO.getCultivatorId());
			cultivatorPlotMappingDetail.setCultivatorTypeId(cultivatorsRequestDAO.getCultivatorTypeId());

			mappingDetails.add(cultivatorPlotMappingDetail);
		}

		cultivatorPlotMappingRepository.saveAll(mappingDetails);
	}

	/**
	 * Adds media mappings for crop survey details.
	 *
	 * @param mediaList                    The list of multipart files representing
	 *                                     the media.
	 * @param mediaNames                   The list of media names.
	 * @param landParcelSurveyCropDetailId The ID of the land parcel survey crop
	 *                                     detail.
	 * @throws Exception
	 */
//	@Transactional
	public void addCropSurveyMediaMapping(List<MultipartFile> mediaList, List<String> mediaNames,
										  Long landParcelSurveyCropDetailId) throws Exception {
		for (String mediaName : mediaNames) {
			Optional<MultipartFile> media = mediaList.stream()
					.filter((MultipartFile data) -> mediaName.equals(data.getOriginalFilename())).findFirst();
			if (media.isPresent()) {
				try {

					MediaMaster cropMedia = mediaMasterService.storeFile(media.get(), "agristack", folderImage,
							ActivityCodeEnum.CROP_SURVEY.getValue());
					LandParcelSurveyCropMediaMapping landParcelSurveyCropMediaMapping = new LandParcelSurveyCropMediaMapping();
					landParcelSurveyCropMediaMapping.setMediaId(cropMedia.getMediaId());
					landParcelSurveyCropMediaMapping.setSurveyCropId(landParcelSurveyCropDetailId);
					landParcelSurveyCropMediaMappingRepository.save(landParcelSurveyCropMediaMapping);

				} catch (Exception e) {
					throw new Exception(
							"Could not store file " + media.get().getOriginalFilename() + ". Please try again!", e);
				}
			}
		}
	}

	/**
	 * Adds media mappings for crop survey details.
	 *
	 * @param mediaIds
	 * @param landParcelSurveyCropDetailId The ID of the land parcel survey crop
	 *                                     detail.
	 */
	public void addCropSurveyMediaMappingFromKafka(List<String> mediaIds, Long landParcelSurveyCropDetailId) {
		for (String mediaId : mediaIds) {
			LandParcelSurveyCropMediaMapping landParcelSurveyCropMediaMapping = new LandParcelSurveyCropMediaMapping();
			landParcelSurveyCropMediaMapping.setMediaId(mediaId);
			landParcelSurveyCropMediaMapping.setSurveyCropId(landParcelSurveyCropDetailId);
			landParcelSurveyCropMediaMappingRepository.save(landParcelSurveyCropMediaMapping);
		}
	}

	/**
	 * Adds media mappings for crop survey details.
	 *
	 * @param mediaIds
	 * @param landParcelSurveyCropDetailId The ID of the land parcel survey crop
	 *                                     detail.
	 */
	public void saveCropSurveyMediaMapping(List<String> mediaIds, Long landParcelSurveyCropDetailId) {
		for (String mediaId : mediaIds) {
			LandParcelSurveyCropMediaMapping landParcelSurveyCropMediaMapping = new LandParcelSurveyCropMediaMapping();
			landParcelSurveyCropMediaMapping.setMediaId(mediaId);
			landParcelSurveyCropMediaMapping.setSurveyCropId(landParcelSurveyCropDetailId);
			landParcelSurveyCropMediaMappingRepository.save(landParcelSurveyCropMediaMapping);
		}
	}

	/**
	 * Retrieves summary details by master ID.
	 *
	 * @param masterId The ID of the master.
	 * @param request  The HttpServletRequest object.
	 * @return An Object containing the summary details.
	 */
	public Object getSummaryDetailsByMasterId(Long masterId, HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

			String fnName = "agri_stack.fn_web_get_survey_details_by_master_id";
			SqlData[] params = {new SqlData(1, String.valueOf(userId), "int"),
					new SqlData(2, String.valueOf(masterId), "int")};
			String response = dbUtils.executeStoredProcedure(fnName, "[]", params).toString();

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<SurveyMasterOutputDAO> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, SurveyMasterOutputDAO.class));

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {

			// e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public void updateLandParcelSurveyStatus(Integer reviewNo, LandParcelSurveyMaster landParcelSurveyMaster,
											 Integer reviewStatus) {
		switch (reviewNo) {
			case 1:
				landParcelSurveyMaster.setSurveyOneStatus(reviewStatus);
				if (reviewStatus == StatusEnum.APPROVED.getValue()) {
					landParcelSurveyMaster.setSurveyStatus(reviewStatus);
				} else {
					landParcelSurveyMaster.setSurveyStatus(StatusEnum.REASSIGNED.getValue());
				}
				break;
			case 2:
				landParcelSurveyMaster.setSurveyTwoStatus(reviewStatus);
				landParcelSurveyMaster.setSurveyStatus(StatusEnum.REJECTED.getValue());
				if (reviewStatus.equals(StatusEnum.REJECTED.getValue())) {
					landParcelSurveyMaster.setSurveyStatus(StatusEnum.REJECTED.getValue());
					landParcelSurveyMaster.setVerifierStatus(StatusEnum.SURVEY_PENDING.getValue());
				} else {
					landParcelSurveyMaster.setSurveyStatus(StatusEnum.APPROVED.getValue());
				}
				break;
			case 3:
				landParcelSurveyMaster.setVerifierStatus(reviewStatus);
				// landParcelSurveyMaster.setSurveyStatus(reviewStatus);
				break;
			case 5:
				landParcelSurveyMaster.setInspectionOfficerStatus(reviewStatus);
				break;
			default:
				break;
		}
		landParcelSurveyMasterRespository.save(landParcelSurveyMaster);
	}

	/**
	 * Updates the survey status of a LandParcelSurveyMaster object based on the
	 * review number and review status.
	 *
	 * @param landParcelSurveyDetailDTO The {@code LandParcelSurveyReviewDTO} containing the request
	 *                                  parameters.
	 * @param request                   The HttpServletRequest object containing the request information.
	 * @return
	 */
	public ResponseModel reviewSurveyDetails(LandParcelSurveyReviewDTO landParcelSurveyDetailDTO,
											 HttpServletRequest request) {
		try {
			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			Optional<LandParcelSurveyDetail> landParcelSurveyDetailOptional = landParcelSurveyDetailRepository
					.findById(landParcelSurveyDetailDTO.getLpsdId());
			if (landParcelSurveyDetailOptional.isPresent()) {
				LandParcelSurveyDetail landParcelSurveyDetail = landParcelSurveyDetailOptional.get();

				Optional<LandParcelSurveyMaster> landParcelSurveyMasterOptional = landParcelSurveyMasterRespository
						.findById(landParcelSurveyDetail.getLpsmId());
				LandParcelSurveyMaster landParcelSurveyMaster = landParcelSurveyMasterOptional.get();
				updateReviewStatus(landParcelSurveyDetailDTO, landParcelSurveyDetail, landParcelSurveyMaster, userId);
				// if
				// (landParcelSurveyMaster.getSurveyMode().equals(CropSurveyModeEnum.FLEXIBLE.getValue()))
				// {
				if (Integer.valueOf(CropSurveyModeEnum.FLEXIBLE.getValue())
						.equals(landParcelSurveyMaster.getSurveyMode())) {
					updateFlexibleModeLPSMs(landParcelSurveyDetailDTO, landParcelSurveyMaster,
							landParcelSurveyDetail.getReviewNo(), userId);
				}

//				ResponseModel res = (ResponseModel) getSummaryDetailsByMasterId(landParcelSurveyMaster.getLpsmId(),
//						request);
				return new ResponseModel(null, CustomMessages.UPDATE_SURVEY_VERIFICATION_SUCCESSFULLY,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.RECORD_UPDATE, CustomMessages.METHOD_POST);
			} else {
				return new ResponseModel("LPSD Id does not exists!", CustomMessages.NOT_FOUND,
						CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED, CustomMessages.METHOD_POST);
			}

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

	}

	public void updateFlexibleModeLPSMs(LandParcelSurveyReviewDTO landParcelSurveyDetailDTO,
										LandParcelSurveyMaster landParcelSurveyMaster, Integer reviewNo, String userId) {
		List<Object[]> flexibleModeLPSMs = landParcelSurveyMasterRespository.getLpsmForDuplicateSurveyNumbers(
				landParcelSurveyMaster.getParcelId().getSurveyNumber(),
				landParcelSurveyMaster.getParcelId().getSubSurveyNumber(), landParcelSurveyMaster.getLpsmId(),
				landParcelSurveyMaster.getSeasonStartYear(), landParcelSurveyMaster.getSeasonEndYear(),
				landParcelSurveyMaster.getSeasonId());
		for (Object[] obj : flexibleModeLPSMs) {
			Optional<LandParcelSurveyMaster> lpsm = landParcelSurveyMasterRespository
					.findById(Long.valueOf(obj[0].toString()));
			if (lpsm.isPresent()) {
				Optional<LandParcelSurveyDetail> landParcelSurveyDetailOptional = landParcelSurveyDetailRepository
						.findByLpsmIdAndReviewNo(lpsm.get().getLpsmId(), reviewNo);
				LandParcelSurveyDetail landParcelSurveyDetail = landParcelSurveyDetailOptional.get();
				updateReviewStatus(landParcelSurveyDetailDTO, landParcelSurveyDetail, lpsm.get(), userId);
			}
		}
	}

	public void updateReviewStatus(LandParcelSurveyReviewDTO landParcelSurveyDetailDTO,
								   LandParcelSurveyDetail landParcelSurveyDetail, LandParcelSurveyMaster landParcelSurveyMaster,
								   String userId) {
		if (landParcelSurveyDetailDTO.getIsAccepted()) {
			landParcelSurveyDetail.setStatus(StatusEnum.APPROVED.getValue());
			landParcelSurveyDetail.setRejectedReason(null);
			landParcelSurveyDetail.setRejectedRemarks(null);
			updateLandParcelSurveyStatus(landParcelSurveyDetail.getReviewNo(), landParcelSurveyMaster,
					StatusEnum.APPROVED.getValue());
		} else {
			landParcelSurveyDetail.setStatus(StatusEnum.REJECTED.getValue());
			// Optional<ReasonMaster> reasonMaster = reasonMasterRepository
			// .findById(landParcelSurveyDetailDTO.getReasonId());
			landParcelSurveyDetail.setRejectedReason(landParcelSurveyDetailDTO.getReasonId());
			landParcelSurveyDetail.setRejectedRemarks(landParcelSurveyDetailDTO.getRejectedRemarks());
			updateLandParcelSurveyStatus(landParcelSurveyDetail.getReviewNo(), landParcelSurveyMaster,
					StatusEnum.REJECTED.getValue());
		}
		if (userId != null) {
			landParcelSurveyDetail.setReviewBy(Long.valueOf(userId));
		}

		landParcelSurveyDetail.setReviewDate(new Timestamp(new Date().getTime()));

		landParcelSurveyDetail = landParcelSurveyDetailRepository.save(landParcelSurveyDetail);
		addSurveyActivityLog(landParcelSurveyMaster.getLpsmId(), landParcelSurveyDetail.getLpsdId(),
				landParcelSurveyMaster.getSurveyStatus().longValue(), landParcelSurveyDetail.getSurveyBy());
		if (landParcelSurveyDetail.getReviewNo() == 2 && !landParcelSurveyDetailDTO.getIsAccepted()) {
			Long stateCode = 0L;
			try {
				autoAssignVerifierForTheParcel(landParcelSurveyMaster,
						VerifierReasonOfAssignmentEnum.SUPERVISOR_REJECTION);
//				Start Old code for verifier assignment
//				stateCode = landParcelSurveyMaster.getParcelId().getVillageLgdMaster().getStateLgdCode()
//						.getStateLgdCode();
//				if (stateCode != null && stateCode != 0) {
//					SurveyVerificationConfigurationMasterDAO surveyVerificationConfigurationMasterDAO = surveyVerificationConfigurationMasterRepository
//							.findByStateLgdCode(stateCode);
//					if (surveyVerificationConfigurationMasterDAO.getSurveyRejectedBySupervisorForSecondTime() != null
//							&& surveyVerificationConfigurationMasterDAO.getSurveyRejectedBySupervisorForSecondTime()) {
//						autoAssignVerifierForTheParcel(landParcelSurveyMaster,
//								VerifierReasonOfAssignmentEnum.SUPERVISOR_REJECTION);
//					}
//				}
//				End Old Code 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void autoAssignVerifierForTheParcel(LandParcelSurveyMaster landParcelSurveyMaster,
											   VerifierReasonOfAssignmentEnum verifierReason) {
		VerifierLandAssignment verifierLandAssignment = new VerifierLandAssignment();

		List<Object[]> availableVerifierList = verifierLandAssignmentRepository.findVerifierForAutoAssignment(
				landParcelSurveyMaster.getParcelId().getVillageLgdMaster().getVillageLgdCode(), landParcelSurveyMaster
						.getParcelId().getVillageLgdMaster().getSubDistrictLgdCode().getSubDistrictLgdCode());
		if (availableVerifierList != null && availableVerifierList.size() > 0) {
			Object[] verifier;
			verifier = availableVerifierList.get(0);

			SowingSeason season = seasonMasterRepository.findBySeasonId(landParcelSurveyMaster.getSeasonId());
			Optional<UserMaster> user = userMasterRepository
					.findByUserIdAndIsDeletedAndIsActive(Long.valueOf(verifier[0].toString()), false, true);
			if (user.isPresent()) {

				/***
				 * ADDED TO CHECK IF VERIFIER PLOT ASSIGNMENT IS ALREADY EXIST OR NOT BY SEASON
				 * YEAR VERIFIER AND PLOT
				 **/

				Boolean checkIfAssignmentExists = verifierLandAssignmentRepository
						.findByFarmlandIdAndSeasonAndStartYearAndEndYear(
								landParcelSurveyMaster.getParcelId().getFarmlandId(), season.getSeasonId(),
								landParcelSurveyMaster.getSeasonStartYear(), landParcelSurveyMaster.getSeasonEndYear());

				// verifierLandAssignment = verifierLandAssignmentRepository
				// .findByVerifierAndFarmlandId(user.get(), landParcelSurveyDetail
				// .getLpsmId().getParcelId().getFarmlandId());

				if (!checkIfAssignmentExists) {

					verifierLandAssignment.setVerifier(user.get());
					verifierLandAssignment.setSeason(season);
					verifierLandAssignment.setStartingYear(landParcelSurveyMaster.getSeasonStartYear());
					verifierLandAssignment.setEndingYear(landParcelSurveyMaster.getSeasonEndYear());
					verifierLandAssignment.setFarmlandId(landParcelSurveyMaster.getParcelId().getFarmlandId());
					verifierLandAssignment.setLandParcelId(landParcelSurveyMaster.getParcelId().getLandParcelId());
					verifierLandAssignment
							.setVillageLgdCode(landParcelSurveyMaster.getParcelId().getVillageLgdMaster());
					verifierLandAssignment.setReasonOfAssignment(verifierReason.getValue());
					verifierLandAssignment.setReasonOfAssignmentType(verifierReason);
					verifierLandAssignment.setIsSecondTimeRejected(Boolean.TRUE);
					verifierLandAssignmentRepository.save(verifierLandAssignment);
				}
			}
		}
	}

	/**
	 * Adds media mappings for crop survey details.
	 *
	 * @param mediaList  The list of multipart files representing the media.
	 * @param mediaNames The list of media names.
	 * @return
	 * @throws Exception
	 */
	public List<String> addCropSurveyMediaMappingWithNames(List<MultipartFile> mediaList, List<String> mediaNames)
			throws Exception {

		List<String> mediaIds = new ArrayList<>();

		for (String mediaName : mediaNames) {
			Optional<MultipartFile> media = mediaList.stream()
					.filter((MultipartFile data) -> mediaName.equals(data.getOriginalFilename())).findFirst();
			if (media.isPresent()) {
				MediaMaster cropMedia = mediaMasterService.storeFile(media.get(), "agristack", folderImage,
						ActivityCodeEnum.CROP_SURVEY.getValue());

				mediaIds.add(cropMedia.getMediaId());
			}
		}

		return mediaIds;
	}

	/**
	 * fetch mark unable to survey details
	 *
	 * @param requestDTO The {@code UnableToSurveyRequestDAO} containing the request
	 *                   parameters.
	 * @param request    The HttpServletRequest object containing the request information.
	 * @return The ResponseModel object representing the response.
	 */
	public Object markUnableToSurvey(UnableToSurveyRequestDAO requestDAO, HttpServletRequest request) {
		try {
			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			if (requestDAO.getParcelId() != null) {
				Optional<FarmlandPlotRegistry> farmLandPlotRegistryId = farmlandPlotRegistryRepository
						.findByFarmlandPlotRegistryId(Long.valueOf(requestDAO.getParcelId()));

				if (farmLandPlotRegistryId.isPresent()) {

					// List<Long> userLandAssignmentIds = userLandAssignmentRepository
					// .findUserLandAssignmentWithUserIdAndFarmlandIds(requestDAO.getStartYear(),
					// requestDAO.getEndYear(), requestDAO.getSeasonId(), requestDAO.getSurveyBy(),
					// Long.valueOf(requestDAO.getParcelId()));
					List<Long> userLandAssignmentIds = userLandAssignmentRepository
							.findUserLandAssignmentWithUserIdAndFarmlandIdV2(requestDAO.getStartYear(),
									requestDAO.getEndYear(), requestDAO.getSeasonId(), requestDAO.getSurveyBy(),
									Long.valueOf(requestDAO.getParcelId()));
					if (userLandAssignmentIds != null && userLandAssignmentIds.size() > 0) {

						// ADDED TO DEACTIVE ALL OLD RECORDS BY PARCEL ID, SEASON AND YEAR
						unableToSurveyRepository.updateStatusByParcelIdSeasonAndYear(
								Long.valueOf(requestDAO.getParcelId()), requestDAO.getSeasonId(),
								requestDAO.getStartYear(), requestDAO.getEndYear());

						UnableToSurveyDetails unableToSurveyDetails = new UnableToSurveyDetails();

						unableToSurveyDetails.setSeasonEndYear(requestDAO.getEndYear());
						unableToSurveyDetails.setSeasonStartYear(requestDAO.getStartYear());
						unableToSurveyDetails.setSeasonId(requestDAO.getSeasonId());

						unableToSurveyDetails.setSurveyorLat(requestDAO.getSurveyorLat());
						unableToSurveyDetails.setSurveyorLong(requestDAO.getSurveyorLong());

						unableToSurveyDetails.setIsActive(Boolean.TRUE);
						unableToSurveyDetails.setIsDeleted(Boolean.FALSE);

						unableToSurveyDetails.setCreatedBy(userId);
						unableToSurveyDetails.setCreatedOn(new Timestamp(new Date().getTime()));
						unableToSurveyDetails.setCreatedIp(CommonUtil.getRequestIp(request));
						unableToSurveyDetails.setParcelId(farmLandPlotRegistryId.get());
						unableToSurveyDetails.setSurveyBy(requestDAO.getSurveyBy());
						unableToSurveyDetails.setStatus(StatusEnum.PENDING.getValue());
						if (requestDAO.getReasonId() != null) {
							Optional<UnableToSurveyReasonMaster> reasonId = unableToSurveyReasonMasterRespository
									.findById(Long.valueOf(requestDAO.getReasonId()));

							if (reasonId.isPresent()) {
								unableToSurveyDetails.setReasonId(reasonId.get());
							}
						}
						unableToSurveyRepository.save(unableToSurveyDetails);
						Optional<UserLandAssignment> userLandAssignmentOp = userLandAssignmentRepository
								.findById(userLandAssignmentIds.get(0));
						if (userLandAssignmentOp.isPresent()) {
							UserLandAssignment userLandAssignment = userLandAssignmentOp.get();
							userLandAssignment.setIsActive(false);
							userLandAssignment.setIsDeleted(true);
							userLandAssignmentRepository.save(userLandAssignment);
						}

					} else {
						return CustomMessages.makeResponseModel(null, CustomMessages.USER_LAND_ASSIGN_NOT_FOUND,
								CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED);
					}

				} else {
					return CustomMessages.makeResponseModel(CustomMessages.INVALID_INPUT, CustomMessages.PLOT_NOT_FOUND,
							CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED);
				}

			} else {
				return CustomMessages.makeResponseModel(CustomMessages.INVALID_INPUT, CustomMessages.PARCELID_REQUIRED,
						CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED);
			}

			return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * fetch survey summary count by user details
	 *
	 * @param request The HttpServletRequest object containing the request information.
	 * @return The ResponseModel object representing the response.
	 */
	public ResponseModel getSurveySummaryCountByUser(HttpServletRequest request) {
		try {
			Long userId = Long.parseLong(CustomMessages.getUserId(request, jwtTokenUtil));

			String surveyCounts = misDashboardRepository.getSurveySummaryCountByUser(userId);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			Object resultObject = mapper.readValue(surveyCounts, Object.class);

			return CustomMessages.makeResponseModel(resultObject, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * fetch survey summary count by user details
	 *
	 * @param request    The HttpServletRequest object containing the request information.
	 * @param requestDTO The {@code SurveyDetailRequestDTO} containing the request
	 *                   parameters.
	 * @return The ResponseModel object representing the response.
	 */
	public ResponseModel getSurveySummaryCountByUserV2(SurveyDetailRequestDTO requestDTO, HttpServletRequest request) {
		try {
			Long userId = Long.parseLong(CustomMessages.getUserId(request, jwtTokenUtil));

			if (requestDTO.getSeasonId() != null && requestDTO.getStartYear() != null
					&& requestDTO.getEndYear() != null) {

				ReviewSurveyCountDAO surveyCounts = misDashboardRepository.getSurveySummaryCountByUserV2(userId,
						requestDTO.getSeasonId(), requestDTO.getStartYear(), requestDTO.getEndYear());
				return CustomMessages.makeResponseModel(surveyCounts, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}

			return CustomMessages.makeResponseModel(null, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Retrieves the summary details of land surveys based on the provided input
	 * parameters.
	 *
	 * @param inputDao The {@code ReviewSurveyInputDAO} object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The response {@link ReviewSurveyOutputDAO} model containing the survey summary details.
	 */
	public Object getSurveySummaryPagination(ReviewSurveyInputDAO inputDao, HttpServletRequest request) {
		try {
			// Extract the user ID from the request using jwtTokenUtil and
			// CustomMessages.getUserId method

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			// Get the joined values from the inputDao lists
			String stateLgdCode = getJoinedValuefromList(inputDao.getStateLgdCodes());
			String districtLgdCode = getJoinedValuefromList(inputDao.getDistrictLgdCodes());
			String subDistrictLgdCode = getJoinedValuefromList(inputDao.getSubDistrictLgdCodes());
			String villageLgdCode = getJoinedValuefromList(inputDao.getVillageLgdCodes());
			String statusCode = getJoinedValuefromList(inputDao.getStatusCodes());

//			String fnName = "agri_stack.fn_web_get_land_survey_summary_details_pagination";
//			SqlData[] params = { new SqlData(1, userId, "string"), new SqlData(2, stateLgdCode, "string"),
//					new SqlData(3, districtLgdCode, "string"), new SqlData(4, subDistrictLgdCode, "string"),
//					new SqlData(5, villageLgdCode, "string"), new SqlData(6, statusCode, "string"),
//					new SqlData(7, inputDao.getSeasonId(), "int"), new SqlData(8, inputDao.getStartYear(), "int"),
//					new SqlData(9, inputDao.getEndYear(), "int"), new SqlData(10, inputDao.getPage(), "int"),
//					new SqlData(11, inputDao.getLimit(), "int"), new SqlData(12, inputDao.getSortField(), "string"),
//					new SqlData(13, inputDao.getSortOrder(), "string"), new SqlData(14, inputDao.getSearch(), "string"),
//					new SqlData(15, inputDao.getStartDate(), "string"),
//					new SqlData(16, inputDao.getEndDate(), "string") };
//			String response = dbUtils.executeStoredProcedure(fnName, "[]", params).toString();

			String search = inputDao.getSearch();
			if (search == null) {
				search = "";
			}
			String response = misDashboardRepository.getSurveySummaryPagination(userId, stateLgdCode, districtLgdCode,
					subDistrictLgdCode, villageLgdCode, statusCode, Integer.valueOf(inputDao.getSeasonId()),
					Integer.valueOf(inputDao.getStartYear()), Integer.valueOf(inputDao.getEndYear()),
					Integer.valueOf(inputDao.getPage()), Integer.valueOf(inputDao.getLimit()), inputDao.getSortField(),
					inputDao.getSortOrder(), search, inputDao.getStartDate(), inputDao.getEndDate());

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			JSONObject geomResponse = new JSONObject(response);

			String countResponse = misDashboardRepository.getSurveySummaryPaginationCount(userId, stateLgdCode,
					districtLgdCode, subDistrictLgdCode, villageLgdCode, statusCode,
					Integer.valueOf(inputDao.getSeasonId()), Integer.valueOf(inputDao.getStartYear()),
					Integer.valueOf(inputDao.getEndYear()), Integer.valueOf(inputDao.getPage()),
					Integer.valueOf(inputDao.getLimit()), inputDao.getSortField(), inputDao.getSortOrder(), search,
					inputDao.getStartDate(), inputDao.getEndDate());

			List<ReviewSurveyOutputDAO> resultObject = mapper.readValue(geomResponse.get("data").toString(),
					typeFactory.constructCollectionType(List.class, ReviewSurveyOutputDAO.class));
			geomResponse = new JSONObject(countResponse);

			ObjectMapper jacksonObjMapper = new ObjectMapper();

			Map<String, Object> responseData = new HashMap();
			responseData.put("data", resultObject);
//			responseData.put("total", jacksonObjMapper.readTree(countResponse.get("count").toString()));
			responseData.put("total", mapper.readValue(geomResponse.get("count").toString(), Long.class));

			responseData.put("page", inputDao.getPage());
			responseData.put("limit", inputDao.getLimit());
			responseData.put("sortField", inputDao.getSortField());
			responseData.put("sortOrder", inputDao.getSortOrder());
			return new ResponseModel(responseData, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {

			// e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * fetch owner and cultivators details by parcel
	 *
	 * @param request    The HttpServletRequest object containing the request information.
	 * @param requestDTO The {@code SurveyDetailRequestDTO} containing the request
	 *                   parameters.
	 * @return The ResponseModel object representing the response.
	 */
	public Object getOwnerAndCultivatorDetailsByParcel(SurveyDetailRequestDTO requestDTO, HttpServletRequest request) {
		try {
			Long userId = Long.parseLong(CustomMessages.getUserId(request, jwtTokenUtil));

			Long lpsmId = (requestDTO.getLandParcelSurveyMasterId() != null) ? requestDTO.getLandParcelSurveyMasterId()
					: 0L;
			Long parcelId = (requestDTO.getFarmlandPlotRegisterId() != null) ? requestDTO.getFarmlandPlotRegisterId()
					: 0L;
			Long seasonId = (requestDTO.getSeasonId() != null) ? requestDTO.getSeasonId() : 0L;
			Integer startYear = (requestDTO.getStartYear() != null) ? requestDTO.getStartYear() : 0;
			Integer endYear = (requestDTO.getEndYear() != null) ? requestDTO.getEndYear() : 0;

			String resultList = landParcelSurveyMasterRespository.getOwnerAndCultivatorDetailsByParcel(lpsmId, parcelId,
					seasonId, startYear, endYear);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			Object resultObject = mapper.readValue(resultList, Object.class);

			return CustomMessages.makeResponseModel(resultObject, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public Object getCropSurveyDetailForLandId(SurveyReviewFetchRequestMobileDTO surveyReviewFetchRequestMobileDTO) {
		// (96,4,2022,2023)
		// Define the name of the stored procedure
		String fnName = "agri_stack.fn_mobile_get_survey_detail_sfdb";

		// Prepare the parameters for the stored procedure
		SqlData[] params = {new SqlData(1, surveyReviewFetchRequestMobileDTO.getSeasonId(), "int"),
				new SqlData(2, surveyReviewFetchRequestMobileDTO.getYear(), "string"),
				new SqlData(3, surveyReviewFetchRequestMobileDTO.getPlotIds(), "string")};
		// Execute the stored procedure using the provided parameters and return the
		// result
		return dbUtils.executeStoredProcedure(fnName, "{}", params);
	}

	/**
	 * fetch all surveyor details
	 *
	 * @param request    The HttpServletRequest object containing the request information.
	 * @param requestDTO The {@code SurveyDetailRequestDTO} containing the request
	 *                   parameters.
	 * @return The Response object representing the response.
	 */
	public String getAllSurveyorV2(SurveyDetailRequestDTO requestDTO, HttpServletRequest request) {
		JSONObject responseObj = new JSONObject();
		try {
			String userId = CustomMessages.getUserId(request, jwtTokenUtil);
			String stateCodes = getJoinedValuefromList(requestDTO.getStateLgdCodeList());
			String districtCodes = getJoinedValuefromList(requestDTO.getDistrictLgdCodeList());
			String talukCodes = getJoinedValuefromList(requestDTO.getSubDistrictLgdCodeList());
			String villageCodes = getJoinedValuefromList(requestDTO.getVillageLgdCodeList());
			String departmentIds = getJoinedValuefromList(requestDTO.getDepartmentIds());
			String result = userMasterRepository.getAllSurveyorWithFilter(Long.parseLong(userId),
					requestDTO.getPageNo().intValue(), requestDTO.getPageSize().intValue(), requestDTO.getSortField(),
					requestDTO.getSortOrder(), requestDTO.getSearch() != null ? requestDTO.getSearch() : "",
					requestDTO.getStateLgdCodeList() != null ? stateCodes : "",
					requestDTO.getDistrictLgdCodeList() != null ? districtCodes : "",
					requestDTO.getSubDistrictLgdCodeList() != null ? talukCodes : "",
					requestDTO.getVillageLgdCodeList() != null ? villageCodes : "",
					String.valueOf(requestDTO.getStartYear()), String.valueOf(requestDTO.getEndYear()),
					requestDTO.getSeasonId().intValue(), requestDTO.getDepartmentIds() != null ? departmentIds : "",
					requestDTO.getStatus().intValue());

			ObjectMapper mapper = new ObjectMapper();
			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(result,
					typeFactory.constructCollectionType(List.class, Object.class));
			responseObj.put("data", resultObject);
			responseObj.put("responseCode", HttpStatus.OK);
			responseObj.put("responseMessage", CustomMessages.SUCCESS);
			return responseObj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.put("responseCode", HttpStatus.INTERNAL_SERVER_ERROR);
			responseObj.put("responseMessage", CustomMessages.FAILURE);
			return responseObj.toString();
		}
	}

	/**
	 * fetch approved crop survey details
	 *
	 * @param request    The HttpServletRequest object containing the request information.
	 * @param requestDTO The {@code CommonRequestDAO} containing the request
	 *                   parameters.
	 * @return The ResponseModel object representing the response.
	 */
	public String getApprovedCropSurveyForStateByDateRange(CommonRequestDAO requestDTO, HttpServletRequest request) {
		JSONObject responseObj = new JSONObject();
		try {
			String result = landParcelSurveyMasterRespository.getApprovedCropSurveyForStateByDateRange(
					String.valueOf(requestDTO.getStartDate()), String.valueOf(requestDTO.getEndDate()));

			ObjectMapper mapper = new ObjectMapper();
			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(result,
					typeFactory.constructCollectionType(List.class, Object.class));
			responseObj.put("data", resultObject);
			responseObj.put("responseCode", HttpStatus.OK);
			responseObj.put("responseMessage", CustomMessages.SUCCESS);
			return responseObj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.put("responseCode", HttpStatus.INTERNAL_SERVER_ERROR);
			responseObj.put("responseMessage", CustomMessages.FAILURE);
			return responseObj.toString();
		}
	}

	public ResponseModel GetSurveyWiseImagesForAUser(@RequestBody CommonRequestDAO requestDTO) throws IOException {
		Map<Integer, String> areaTypeMap = new HashMap<>();
		areaTypeMap.put(1, "Non-Agriculture Land");
		areaTypeMap.put(2, "Fallow Waste and Vacant Land");
		areaTypeMap.put(3, "Harvested Land");
		areaTypeMap.put(4, "Perennial Crop Land");
		areaTypeMap.put(5, "Biennial Crop Land");
		areaTypeMap.put(6, "Seasonal Crop Land");

		Path folderPath = Paths.get("/home/vassarlabs/images_20241005_2300/");

		// List to hold the combined results
		List<Object[]> combinedResults = new ArrayList<>();
		List<Object[]> distinctUserIDs = landParcelSurveyCropDetailRepository.getDistinctUserId();
		System.out.println("distinctUserIDs " + distinctUserIDs);
		for (Object[] row : distinctUserIDs) {
			Long userID = ((Number) row[0]).longValue();  // Convert Object to Long
			List<Object[]> result = landParcelSurveyDetailRepository.getAllSurveyDetails(userID);
			combinedResults.addAll(result);

		}


		// Check if the folder exists and is a directory
//		if (Files.exists(folderPath) && Files.isDirectory(folderPath)) {
//
//			// Stream through the subdirectories in the folder
//			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
//				for (Path path : stream) {
//					if (Files.isDirectory(path)) {
//						// Extract the folder name (assumed to be the user ID)
//						String folderName = path.getFileName().toString();
//
//						// Parse the folder name as a user ID (assuming it's a Long)
//						try {
//							Long userId = Long.valueOf(folderName);
//
//							// Fetch the survey details for this user ID
//							List<Object[]> result = landParcelSurveyDetailRepository.getAllSurveyDetails(userId);
//
//							// Add the result to the combined list
//							combinedResults.addAll(result);
//						} catch (NumberFormatException e) {
//							System.out.println("Folder name is not a valid user ID: " + folderName);
//						}
//					}
//				}
//			}
//		} else {
//			System.out.println("The provided path is not a valid directory: " + folderPath);
//		}


//		List<Object[]> result = landParcelSurveyDetailRepository.getAllSurveyDetails(Long.valueOf(requestDTO.getUserId()));
		Map<Long, TreeMap<Long, Long>> surveyDetailsMap = new TreeMap<>();

		for (Object[] row : combinedResults) {
			Long lpsdId = ((Number) row[0]).longValue();  // Convert Object to Long
			Timestamp surveyDate = (Timestamp) row[1];
			Long userID = ((Number) row[2]).longValue();
			Long surveyDateInMillis = surveyDate.getTime(); // Convert Object to Timestamp

			// Check if the userID already exists in the map
			TreeMap<Long, Long> userSurveyMap = surveyDetailsMap.getOrDefault(userID, new TreeMap<>());

			// Add the surveyDate and lpsdId to the user's TreeMap
			userSurveyMap.put(surveyDateInMillis, lpsdId);

			// Put the updated userSurveyMap back into the surveyDetailsMap
			surveyDetailsMap.put(userID, userSurveyMap);
		}

		System.out.println("surveyDetailsMap " + surveyDetailsMap);
		;
		Map<Long, String> imageOfUser = new HashMap<>();
		List<SurveyImage> sureveyVsSurveDatevsImage = new ArrayList<>();
		for (Long userID : surveyDetailsMap.keySet()) {
			Long preVTs = 0l;
			for (Long surveyTs : surveyDetailsMap.get(userID).keySet()) {
				Long surveyID = surveyDetailsMap.get(userID).get(surveyTs);
//			SurveyImage surveyImage = new SurveyImage();
				imageOfUser = getImagesForUserV2(preVTs, surveyTs, String.valueOf(userID));
//			sureveyVsSurveDatevsImage.computeIfAbsent(surveyID, k-> new TreeMap<>());
				for (Long imageCickts : imageOfUser.keySet()) {

					Date date = new Date(imageCickts);
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					format.setTimeZone(TimeZone.getTimeZone("IST"));
					String formatted = format.format(date);
					Timestamp timeStamp = Timestamp.valueOf(formatted);
//				surveyImage.setImageDate(timeStamp);
//				surveyImage.setImagePath(imageOfUser.get(imageCickts));
//				surveyImage.setStageID("0");


					sureveyVsSurveDatevsImage.add(new SurveyImage(surveyID, surveyTs, imageOfUser.get(imageCickts), "0", timeStamp, userID));


				}
				if (!imageOfUser.isEmpty()) {
					System.out.println("imageOfUser :: " + imageOfUser);
					System.out.println("sureveyVsSurveDatevsImage :: " + sureveyVsSurveDatevsImage);
				}
				preVTs = surveyTs;

			}
		}
		System.out.println("sureveyVsSurveDatevsImage " + sureveyVsSurveDatevsImage);


//		for(Long surveyID : sureveyVsSurveDatevsImage.keySet()){
//			for(Long surveyTs : sureveyVsSurveDatevsImage.get(surveyID).keySet()){
		for (SurveyImage surveyImage : sureveyVsSurveDatevsImage) {
//			SurveyImage surveyImage = sureveyVsSurveDatevsImage.get(surveyID).get(surveyTs);
			Date date = new Date(surveyImage.getSurveyDate());
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			format.setTimeZone(TimeZone.getTimeZone("IST"));
			String formatted = format.format(date);
			Timestamp surveyTimestamp = Timestamp.valueOf(formatted);
			landParcelSurveyDetailRepository.insertTOSurveyImageMapping(Math.toIntExact(surveyImage.getUserID()), Math.toIntExact(surveyImage.getSurveyId()), surveyTimestamp, surveyImage.getImageDate(), Integer.valueOf(surveyImage.getStageID()), surveyImage.getImagePath());

		}
		//originalSurveyIdVsStageIdVsMediaList
		// Define Maps to hold surveyID vs StageID vs MediaList and MediaCount
//		Map<Long, Map<Integer, List<MediaMaster>>> surveyMediaListMap = new HashMap<>();
//		Map<Long, Map<Integer, Integer>> surveyMediaCountMap = new HashMap<>();
//
//// Iterate over survey details and process each surveyID
//		for(Long userID : surveyDetailsMap.keySet()){
//		for (Long surveyTs : surveyDetailsMap.get(userID).keySet()) {
//			System.out.println("Processing surveyID: " + surveyDetailsMap.get(userID).get(surveyTs));
//			Long surveyID = surveyDetailsMap.get(userID).get(surveyTs);
//
//			// Fetch crop details based on surveyID
//			List<LandParcelSurveyCropDetail> cropDetails = landParcelSurveyCropDetailRepository.findByLpsdId(surveyID);
//
//			for (LandParcelSurveyCropDetail cropDetail : cropDetails) {
//				// Initialize the maps if they don't exist for the current surveyID
//				surveyMediaListMap.computeIfAbsent(surveyID, k -> new HashMap<>());
//				surveyMediaCountMap.computeIfAbsent(surveyID, k -> new HashMap<>());
//
//				// Ensure all stages are initialized with 0 counts
//				for (int stage = 1; stage <= 6; stage++) {
//					surveyMediaCountMap.get(surveyID).putIfAbsent(stage, 0);
//					surveyMediaListMap.get(surveyID).putIfAbsent(stage, new ArrayList<>());
//				}
//
//				System.out.println("CropSdId: " + cropDetail.getCropSdId());
//
//				// Fetch media mappings for the current crop detail
//				List<Object[]> mediaMappings = landParcelSurveyCropDetailRepository.getCropMediaMappingBySurveyCropId(cropDetail.getCropSdId());
//				List<MediaMaster> mediaMasters = new ArrayList<>();
//
//				// Populate the mediaMasters list if media mappings exist
//				if (mediaMappings != null && !mediaMappings.isEmpty()) {
//					for (Object[] row : mediaMappings) {
//						MediaMaster mediaMaster = new MediaMaster();
//						mediaMaster.setMediaId((String) row[1]);
//						mediaMasters.add(mediaMaster);
//					}
//				} else {
//					System.out.println("No media mappings found for CropSdId: " + cropDetail.getCropSdId());
//				}
//
//				System.out.println("mediaMasters size: " + mediaMasters.size());
//				System.out.println("mediaMasters size: " + mediaMasters.size());
//
//				// Skip processing if no media is found
//				if (mediaMasters.isEmpty()) continue;
//
//				// Determine the stage
//				int stage = determineStage(cropDetail);
//
//				if (stage > 0) {
//					// Add media to the corresponding stage list
//					surveyMediaListMap.get(surveyID)
//							.get(stage) // Ensure we add to the existing list
//							.addAll(mediaMasters);
//
//					// Update the media count for the stage
//					surveyMediaCountMap.get(surveyID)
//							.merge(stage, mediaMasters.size(), Integer::sum);
//				}
//			}
//
//			System.out.println("Processed crop details for surveyID: " + surveyID);
//		}}
//
//
//// Handle downloaded media list processing
//		Map<Long, List<MediaMasterV2>> downloadedMediaListMap = new HashMap<>();
//
//		for(Long userID : surveyDetailsMap.keySet()){
//		for (Long surveyTs : surveyDetailsMap.get(userID).keySet()) {
//			Long surveyID = surveyDetailsMap.get(userID).get(surveyTs);
//
//			List<Object[]> surveyImageMappings = landParcelSurveyDetailRepository.getSurveyImageMapping(surveyID);
//			System.out.println("surveyImageMapping for surveyID " + surveyID + ": " + surveyImageMappings);
//
//			List<MediaMasterV2> mediaMastersV2 = new ArrayList<>();
//
//			// Populate the mediaMastersV2 list with survey image mappings
//			if (surveyImageMappings != null && !surveyImageMappings.isEmpty()) {
//				for (Object[] row : surveyImageMappings) {
//					MediaMasterV2 mediaMasterV2 = new MediaMasterV2();
//					mediaMasterV2.setMediaUrl((String) row[6]);
//					mediaMasterV2.setInserted_at((Timestamp) row[4]);
//					mediaMasterV2.setUserId(((Number) row[1]).longValue());
//					mediaMastersV2.add(mediaMasterV2);
//				}
//				// Sort the media by insertion time
//				mediaMastersV2.sort(Comparator.comparing(MediaMasterV2::getInserted_at));
//			} else {
//				System.out.println("No media mappings found for surveyID: " + surveyID);
//			}
//
//			// Add the media list to the map for the current surveyID
//			downloadedMediaListMap.put(surveyID, mediaMastersV2);
//		}}
//
//
//
//// Check the contents of the map
//		System.out.println("downloadedMediaListMap: " + downloadedMediaListMap);
//
//
//
//
//
//		System.out.println("surveyMediaListMap "+surveyMediaListMap);
//		System.out.println("surveyMediaCountMap "+ surveyMediaCountMap);
//
//		for(Long userID : surveyDetailsMap.keySet()){
//		// After processing downloaded media list
//		for (Long surveyTs : surveyDetailsMap.get(userID).keySet()) {
//			Long surveyID = surveyDetailsMap.get(userID).get(surveyTs);
//
//			System.out.println("surveyID: " + surveyID);
//
//			// Calculate the total original media count for the current surveyID
//			Integer originalMediaCount = surveyMediaCountMap.get(surveyID).values().stream().mapToInt(Integer::intValue).sum();
//			System.out.println("originalMediaCount: " + originalMediaCount);
//
//			List<MediaMasterV2> downloadedMediaMasters = downloadedMediaListMap.get(surveyID);
//			int downloadedMediaCount = downloadedMediaMasters != null ? downloadedMediaMasters.size() : 0;
//			//System.out.println("downloadedMediaCount: " + downloadedMediaCount);
//
//			// Check if image counts match
//			if (originalMediaCount.equals(downloadedMediaCount)) {
//				// Initialize a pointer to the first available image
//				int imagePointer = 0;
//
//				// Loop through each stage to update the user_survey_image_mapping table
//				for (Map.Entry<Integer, Integer> entry : surveyMediaCountMap.get(surveyID).entrySet()) {
//					int stage = entry.getKey();  // Stage ID
//					int mediaCount = entry.getValue();  // Media count required for this stage
//
//					// Create a list to hold the images for this stage
//					List<MediaMasterV2> stageImages = new ArrayList<>();
//
//					// Assign images for the current stage
//					for (int i = 0; i < mediaCount && imagePointer < downloadedMediaCount; i++) {
//						MediaMasterV2 image = downloadedMediaMasters.get(imagePointer);
//						stageImages.add(image);  // Add the image to the list
//						Timestamp imageDate = image.getInserted_at();  // Get the image date
//						System.out.println("imageDate "+imageDate);
//						System.out.println("surveyID "+surveyID);
//						System.out.println("stage "+stage);
//
//						// Update stage_id in the database based on imageDate
//						updateUserSurveyImageMapping(surveyID, imageDate, stage);
//
//						imagePointer++; // Move to the next available image
//					}
//				}
//
//				// Handle remaining images for the remaining stages
//				if (imagePointer < downloadedMediaCount) {
//					for (Map.Entry<Integer, Integer> entry : surveyMediaCountMap.get(surveyID).entrySet()) {
//						int stage = entry.getKey();  // Stage ID
//						int remainingMediaCount = entry.getValue();  // Remaining media count required for this stage
//
//						// Only assign remaining images if there are images left
//						if (remainingMediaCount > 0 && imagePointer < downloadedMediaCount) {
//							List<MediaMasterV2> remainingStageImages = new ArrayList<>();
//
//							// Use the remaining images
//							for (int i = 0; i < remainingMediaCount && imagePointer < downloadedMediaCount; i++) {
//								MediaMasterV2 image = downloadedMediaMasters.get(imagePointer);
//								remainingStageImages.add(image);
//								Timestamp imageDate = image.getInserted_at();  // Get the image date for the remaining image
//
//								// Update stage_id in the database based on imageDate
//								updateUserSurveyImageMapping(surveyID, imageDate, stage);
//
//								imagePointer++; // Move to the next available image
//							}
//						}
//					}
//				}
//			} else {
//				// Populate stage as 0 for this survey if counts do not match
//				//updateUserSurveyImageMapping(surveyID, null, 0);  // Update with stage 0, using null for imageDate
//				// Initialize a pointer to track the processed images
//				int processedImagePointer = 0;
//
//				// Loop through each stage and assign images until the counts mismatch
//				for (Map.Entry<Integer, Integer> entry : surveyMediaCountMap.get(surveyID).entrySet()) {
//					int stage = entry.getKey();  // Stage ID
//					int mediaCount = entry.getValue();  // Media count required for this stage
//
//					// Assign images for the current stage until the downloadedMediaCount is reached
//					for (int i = 0; i < mediaCount && processedImagePointer < downloadedMediaCount; i++) {
//						MediaMasterV2 image = downloadedMediaMasters.get(processedImagePointer);
//						Timestamp imageDate = image.getInserted_at();  // Get the image date
//						updateUserSurveyImageMapping(surveyID, imageDate, stage);  // Update stage ID in the database
//						processedImagePointer++; // Move to the next image
//					}
//
//					// If the downloaded images are exhausted, stop the updates
//					if (processedImagePointer >= downloadedMediaCount) {
//						break;  // Exit the loop when we run out of images
//					}
//				}
//
//				// Optionally, log or handle remaining stages that couldn't be processed
//				if (processedImagePointer < downloadedMediaCount) {
//					System.out.println("Not all stages were updated due to a mismatch in image counts.");
//				}
//			}
//		}}


//				SurveyImage surveyImage = sureveyVsSurveDatevsImage.get(surveyID).get(surveyTs);
//				Date date = new Date(surveyTs);
//				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				format.setTimeZone(TimeZone.getTimeZone("IST"));
//				String formatted = format.format(date);
//				Timestamp surveyTimestamp = Timestamp.valueOf(formatted);
//				landParcelSurveyDetailRepository.insertTOSurveyImageMapping(Integer.valueOf(requestDTO.getUserId()),Math.toIntExact(surveyID),surveyTimestamp,surveyImage.getImageDate(),Integer.valueOf(surveyImage.getStageID()),surveyImage.getImagePath());

//			}
//		}


//		userId, serveyId, surveyDate, pojo(imagePath, captureTime, stageId)

		//System.out.println("result "+result);
		ObjectMapper mapper = new ObjectMapper();
		TypeFactory typeFactory = mapper.getTypeFactory();
//		List<Object> resultObject = mapper.readValue(result,
//				typeFactory.constructCollectionType(List.class, Object.class));
		//System.out.println("resultObject "+resultObject);

		return new ResponseModel(true,
				null, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
				CustomMessages.METHOD_GET);


	}
	/**
	 * Retrieve the appropriate stage for a survey based on your logic.
	 *
	 * @param surveyID The ID of the survey.
	 * @return The corresponding stage number.
	 */

	/**
	 * Determines the stage based on the properties of the given crop detail.
	 *
	 * @param cropDetail the crop detail to evaluate
	 * @return the stage number (1-6), or 0 if no applicable stage is found
	 */

	private int determineStage(LandParcelSurveyCropDetail cropDetail) {
		if (cropDetail.getIsUnutilizedArea()) {
			return 1; // Stage 1: Unutilized Area
		} else if (cropDetail.getIsWasteArea()) {
			return 2; // Stage 2: Waste Area
		} else if (cropDetail.getIsHarvestedArea()) {
			return 3; // Stage 3: Harvested Area
		} else if (cropDetail.getCropSdId() == 1) {
			return 4; // Stage 4: Specific Crop ID (1)
		} else if (cropDetail.getCropClassId() == 2) {
			return 5; // Stage 5: Specific Crop Class ID (2)
		} else if (cropDetail.getCropClassId() == 3) {
			return 6; // Stage 6: Specific Crop Class ID (3)
		} else if (cropDetail.getCropClassId() == null && !cropDetail.getIsWasteArea() && !cropDetail.getIsWasteArea() && !cropDetail.getIsHarvestedArea()) {
			return 7;
		}

		return 0; // No applicable stage found
	}

	private int getStageForSurvey(Long surveyID, Map<Long, Map<Integer, Integer>> mediaCountMap) {
		// Implement logic to get the relevant stage based on your existing mediaCountMap
		return mediaCountMap.get(surveyID).entrySet().stream()
				.max(Map.Entry.comparingByValue()) // Find the stage with the maximum count
				.map(Map.Entry::getKey)
				.orElse(0); // Default to 0 if no stages found
	}

	/**
	 * Update the user_survey_image_mapping table with the survey ID and stage.
	 *
	 * @param surveyID The ID of the survey.
	 * @param stage    The stage to populate.
	 */
	private void updateUserSurveyImageMapping(Long surveyID, Timestamp imageDate, int stage) {
		landParcelSurveyDetailRepository.updateSurveyImageMapping(surveyID, imageDate, stage);

	}


	private Map<String, TreeMap<Long, String>> userVsCaptureTimeVsImagePath = new HashMap<>();
	Long milliesInHr = 3600000l;


	public Map<Long, String> getImagesForUser(Long previousSurveyTs, Long currSurveyTs, String userId) {
		Map<Long, String> imageClickedTimeVsImagePath = new HashMap<>();

		// get all the images of user userVsDateVsImagePath
		//if(!userVsCaptureTimeVsImagePath.containsKey(userId)) {
		Path folderPath = Paths.get("/home/vassarlabs/images_20241005_2300/");

		try {
			// Regex pattern to extract the timestamp from the filename
			String pattern = "([A-Za-z]{3} [A-Za-z]{3} \\d{2} \\d{2}_\\d{2}_\\d{2} GMT\\+\\d{2}_\\d{2} \\d{4})";
			Pattern regex = Pattern.compile(pattern);

			// Listing user-specific directories in the main folder (non-recursive)
			Files.list(folderPath)
					.filter(Files::isDirectory)  // Only directories (ignore files at the root level)
					.forEach(userFolderPath -> {
						String userID = userFolderPath.getFileName().toString();
						//System.out.println("User ID: " + userID);

						try {
							// Now list the files inside each user-specific directory
							Files.list(userFolderPath)
									.filter(Files::isRegularFile)  // Only regular files (ignore subdirectories)
									.forEach(filePath -> {
										String imagePath = filePath.getFileName().toString();
										//System.out.println("File name: " + imagePath);

										// Match the regex pattern to extract the timestamp
										Matcher matcher = regex.matcher(imagePath);
										if (matcher.find()) {
											// Extract the timestamp string from the filename
											String timestampStr = matcher.group(1);
											//System.out.println("timestampStr "+timestampStr);

											// Replace underscores with colons for parsing
											String timestampWithColon = timestampStr.replace("_", ":");
											//System.out.println("timestampWithColon "+timestampWithColon);

											// DateTimeFormatter matching your timestamp format
//											DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'Z yyyy");
											DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy");


											// Parse the timestamp string into ZonedDateTime
											ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestampWithColon, formatter);
											//System.out.println("Parsed Date: " + zonedDateTime + " millies ::  " + zonedDateTime.toInstant().toEpochMilli());

											// Store the capture time and image path for the user
											userVsCaptureTimeVsImagePath
													.computeIfAbsent(userID, k -> new TreeMap<>())
													.put(zonedDateTime.toInstant().toEpochMilli(), imagePath);
										}
									});
						} catch (IOException e) {
							System.out.println("Error reading files for user " + userID);
							e.printStackTrace();
						}
					});
		} catch (IOException e) {
			System.out.println("Error reading the main folder.");
			e.printStackTrace();
		}


		//userVsCaptureTimeVsImagePath = new HashMap<>();
		//	Path folderPath = Paths.get("/home/vassarlabs/images_20241005_2300/" + userId);
//
//			try {
//				// regex to extract time
//				String pattern = "([A-Za-z]{3} [A-Za-z]{3} \\d{2} \\d{2}_\\d{2}_\\d{2} GMT\\+\\d{2}_\\d{2} \\d{4})";
//				Pattern regex = Pattern.compile(pattern);
//
//				// Using Files.list to list files in the directory (non-recursive)
//				Files.list(folderPath)
//						.filter(Files::isRegularFile)  // Only regular files (ignore directories)
//						.forEach(filePath -> {
//
//							String imagePath = filePath.getFileName().toString();
//							System.out.println("File name: " + imagePath);
//
//							// Find the date part using regex
//							Matcher matcher = regex.matcher(imagePath);
//							if (matcher.find()) {
//								// Extract the timestamp string
//								String timestampStr = matcher.group(1);
//
//								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy");
//
//								// Replace the underscores with colons for parsing
//								String timestampWithColon = timestampStr.replace("_", ":");
//
//								ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestampWithColon, formatter);
//								System.out.println("Parsed Date: " + zonedDateTime + " millies ::  " + zonedDateTime.toInstant().toEpochMilli() );
//								// Parse the input string into a Date object
//								userVsCaptureTimeVsImagePath.computeIfAbsent(userId, k -> new TreeMap<>()).put(zonedDateTime.toInstant().toEpochMilli(), imagePath);
//							}
//						});
//			} catch (IOException e) {
//				System.out.println("Error reading the folder.");
//				e.printStackTrace();
//			}
//		}


		// get all the images which are clicked b/w startTs and endTs

		for (Long captureTs : userVsCaptureTimeVsImagePath.get(userId).keySet()) {

			if (captureTs >= previousSurveyTs && captureTs <= currSurveyTs) {

				// get all the images which are clicked before 1 hr from currSurveyTs
				if (captureTs >= (currSurveyTs - milliesInHr)) {
					imageClickedTimeVsImagePath.put(captureTs, userVsCaptureTimeVsImagePath.get(userId).get(captureTs));
				}
			}
		}

		System.out.println("total  " + imageClickedTimeVsImagePath.size() + " image found for this servey");

		return imageClickedTimeVsImagePath;
	}

	public Map<Long, String> getImagesForUserV2(Long previousSurveyTs, Long currSurveyTs, String userId) {
		Map<Long, String> imageClickedTimeVsImagePath = new HashMap<>();

		// get all the images of user userVsDateVsImagePath
		//if(!userVsCaptureTimeVsImagePath.containsKey(userId)) {
		Path folderPath = Paths.get("/home/vassarlabs/images_20241005_2300/");

		try {

			// Listing user-specific directories in the main folder (non-recursive)

			try {
				List<Object[]> userWiseImageList = landParcelSurveyCropDetailRepository.getAllUserImage(Long.valueOf(userId));
				for (Object[] row : userWiseImageList) {
					// Now list the files inside each user-specific directory
					//System.out.println("File name: " + imagePath);

					// Match the regex pattern to extract the timestamp
					//System.out.println("timestampStr "+timestampStr);

					// Replace underscores with colons for parsing
					//String timestampWithColon = timestampStr.replace("_", ":");
					//System.out.println("timestampWithColon "+timestampWithColon);

					// DateTimeFormatter matching your timestamp format
//											DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'Z yyyy");
					//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy");

					Timestamp imageDate = (Timestamp) row[6];
					String imagePath = (String) row[2];

					// Parse the timestamp string into ZonedDateTime
					//ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestampWithColon, formatter);
					System.out.println("Parsed Date: " + imageDate + " millies ::  " + imageDate.toInstant().toEpochMilli());

					// Store the capture time and image path for the user
					userVsCaptureTimeVsImagePath
							.computeIfAbsent(userId, k -> new TreeMap<>())
							.put(imageDate.toInstant().toEpochMilli(), imagePath);

				}
			} catch (Exception e) {
				System.out.println("Error reading files for user " + userId);
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.out.println("Error reading the main folder.");
			e.printStackTrace();
		}


		// get all the images which are clicked b/w startTs and endTs

		for (Long captureTs : userVsCaptureTimeVsImagePath.get(userId).keySet()) {

			if (captureTs >= previousSurveyTs && captureTs <= currSurveyTs) {

				// get all the images which are clicked before 1 hr from currSurveyTs
				if (captureTs >= (currSurveyTs - milliesInHr)) {
					imageClickedTimeVsImagePath.put(captureTs, userVsCaptureTimeVsImagePath.get(userId).get(captureTs));
				}
			}
		}

		System.out.println("total  " + imageClickedTimeVsImagePath.size() + " image found for this servey");

		return imageClickedTimeVsImagePath;
	}

	public class SurveyImage {

		Long surveyId;

		Long surveyDate;
		String imagePath;
		String stageID;
		Timestamp imageDate;

		Long userID;

		public Long getSurveyId() {
			return surveyId;
		}

		public void setSurveyId(Long surveyId) {
			this.surveyId = surveyId;
		}

		public Long getSurveyDate() {
			return surveyDate;
		}

		public void setSurveyDate(Long surveyDate) {
			this.surveyDate = surveyDate;
		}

		public String getImagePath() {
			return imagePath;
		}

		public void setImagePath(String imagePath) {
			this.imagePath = imagePath;
		}

		public String getStageID() {
			return stageID;
		}

		public void setStageID(String stageID) {
			this.stageID = stageID;
		}

		public Timestamp getImageDate() {
			return imageDate;
		}

		public void setImageDate(Timestamp imageDate) {
			this.imageDate = imageDate;
		}

		public Long getUserID() {
			return userID;
		}

		public void setUserID(Long userID) {
			this.userID = userID;
		}

		public SurveyImage(String imagePath, String stageID, Timestamp imageDate) {
			this.imagePath = imagePath;
			this.stageID = stageID;
			this.imageDate = imageDate;
		}

		public SurveyImage(Long surveyId, Long surveyDate, String imagePath, String stageID, Timestamp imageDate, Long userID) {
			this.surveyId = surveyId;
			this.surveyDate = surveyDate;
			this.imagePath = imagePath;
			this.stageID = stageID;
			this.imageDate = imageDate;
			this.userID = userID;
		}

		public SurveyImage(Long surveyId, Long surveyDate, String imagePath, String stageID, Timestamp imageDate) {
			this.surveyId = surveyId;
			this.surveyDate = surveyDate;
			this.imagePath = imagePath;
			this.stageID = stageID;
			this.imageDate = imageDate;
		}

		public SurveyImage() {
		}

	}

	public List<String> GetDownloadedImages(@RequestBody CommonRequestDAO requestDTO) throws IOException {

		List<Object[]> userImageMappings = landParcelSurveyDetailRepository.getAllimageForUser(Long.valueOf(requestDTO.getUserId()));
//			System.out.println("userImageMappings " + surveyID + ": " + userImageMappings);

		List<String> imagePathList = new ArrayList<>();

		// Populate the mediaMastersV2 list with survey image mappings
		if (userImageMappings != null && !userImageMappings.isEmpty()) {
			for (Object[] row : userImageMappings) {

				imagePathList.add((String) row[6]);
			}
		}
		return imagePathList;

	}

}
