package com.amnex.agristack.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.Mail;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.Enum.ActivityCodeEnum;
import com.amnex.agristack.Enum.DepartmentEnum;
import com.amnex.agristack.Enum.MenuTypeEnum;
import com.amnex.agristack.Enum.RoleEnum;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.Enum.UserType;
import com.amnex.agristack.centralcore.entity.UserDeviceIntegrityDetailsHistory;
import com.amnex.agristack.centralcore.util.AESUtil;
import com.amnex.agristack.config.GooglePlayIntegrityService;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.config.JwtUserDetailsService;
import com.amnex.agristack.notifications.NotificationThread;
import com.amnex.agristack.utils.Blake2bHashUtility;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.Constants;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.DBUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import antlr.debug.NewLineEvent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

/**
 * @author majid.belim
 *
 */
@Service
public class UserService {
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private RoleMasterRepository roleMasterRepository;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	private StateLgdMasterRepository stateLgdMasterRepository;

	@Autowired
	private DistrictLgdMasterRepository districtLgdMasterRepository;

	@Autowired
	private SubDistrictLgdMasterRepository subDistrictLgdMasterRepository;

	@Autowired
	private StatusMasterRepository statusRepository;

	@Autowired
	private MessageConfigurationService messageConfigService;

	@Autowired
	private GeneralService generalService;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	StateLgdMasterService stateService;

	@Autowired
	DistrictLgdMasterService districtService;
	@Autowired
	SubDistrictLgdMasterService subDistrictService;

	@Autowired
	VillageLgdMasterService villageService;

	@Autowired
	UserBankDetailRepository userBankDetailRepository;

	@Value(value = "${otp.testing}")
	private Boolean isTesting;

	@Autowired
	private OTPRepository otpRepository;

	@Autowired
	private MessageConfigurationService messageConfigurationService;

	@Autowired
	LoginLogutActivityLogService logService;

	@Autowired
	LoginLogoutActivityLogReposiptory logReposiptory;

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Autowired
	private UserVillageMappingService userVillageMappingService;

	//	@Autowired
	//	private UserDefaultLanguageMappingService userDefLanMapService;

	@Autowired
	private StateDefaultLanguageMappingRepository stateDefaultLanguageMappingRepository;

	@Autowired
	DBUtils dbUtils;

	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	private MenuMasterRepository menuMasterRepository;

	@Autowired
	private MessageCredentialRepository messageCredentialRepository;

	@Autowired
	private MessageConfigurationRepository messageConfigurationRepository;

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private BankRepository bankRepository;

	@Autowired
	private MediaMasterService mediaService;

	@Value("${media.folder.image}")
	private String folderImage;

	@Autowired
	private MediaMasterRepository mediaMasterRepository;

	@Value("${app.datastore.networktype}")
	private int storageType;

	@Value("${file.upload-dir}")
	private String localStoragePath;

	@Autowired
	private RolePatternMappingRepository rolePatternMappingRepository;
	@Autowired
	private RoleMenuMasterMappingRepository roleMenuMasterMappingRepository;
	@Value("${app.redis.name}")
	private String redisAppName;
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private GooglePlayIntegrityService googlePlayIntegrityService;
	
	@Autowired
	private UserDeviceIntegrityDetailsRepository userDeviceIntegrityDetailsRepository;
	
	@Autowired
	private UserDeviceIntegrityDetailsHistoryRepository userDeviceIntegrityDetailsHistoryRepository;
	@Autowired
	private UserCaptchaDetailsRepository userCaptchaDetailsRepository;	
	
	
	
	/**
	 * Performs user login.
	 *
	 * @param httpServletRequest
	 * @param userInputDAO The user input data.
	 * @return The ResponseModel containing the response data.
	 */
	@Transactional
	public ResponseModel login(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		try {
			Optional<UserMaster> userOptional = null;
			if (userInputDAO.getIsFarmerGrievance() != null && userInputDAO.getIsFarmerGrievance().equals(true)) {

				userOptional = userMasterRepository.findByIsDeletedAndIsActiveAndUserMobileNumberAndRoleId_Code(false,
						true, userInputDAO.getUserName(), UserType.FARMER.toString());

			} else {
				userOptional = userMasterRepository.getUserWithoutStatus(userInputDAO.getUserName());
			}
			System.out.println(" userOptional "+userOptional);

			if (userOptional.isPresent()) {

				if(!userOptional.get().getIsActive()) {
					return CustomMessages.makeResponseModel(null, CustomMessages.INACTIVE_USER,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}

				UserMaster usermaster = userOptional.get();
				List<UserCaptchaDetails> captchaList=userCaptchaDetailsRepository.findByCaptchaAndUserIdAndIsActiveAndIsDeleted(userInputDAO.getCaptcha(),userOptional.get().getUserId(),true,false);
				if(captchaList==null || captchaList.size()==0) {
					return	responseModel = CustomMessages.makeResponseModel(null, CustomMessages.INVALID_CAPTCHA,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}else {
					List<UserCaptchaDetails> userCaptchaDetailsList=userCaptchaDetailsRepository.findByUserIdAndIsActiveAndIsDeleted(userOptional.get().getUserId(), true, false);
					if(userCaptchaDetailsList!=null && userCaptchaDetailsList.size()>0) {
						userCaptchaDetailsRepository.updateUserCaptchaDetailsByUserId(userOptional.get().getUserId());		
					}
				}
//				String decodedCaptcha = generalService.decodeBase64(userInputDAO.getUniqueKey());
//				if(!decodedCaptcha.equals(userInputDAO.getCaptcha())) {
//					return	responseModel = CustomMessages.makeResponseModel(null, CustomMessages.INVALID_CAPTCHA,
//							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
//				}
				String dPassword=AESUtil.passwordDecrypt(userInputDAO.getUserPassword(),userInputDAO.getUniqueKey());
				System.out.println("dPassword "+dPassword);
				// System.out.println("usermaster.getUserPassword() "+AESUtil.decrypt(usermaster.getUserPassword()));
//				String decodedpassword = generalService.decodeBase64(userInputDAO.getUserPassword());
//				String encodedpassword =CommonUtil.ConvertStringToSHA256(usermaster.getUserPassword());
				if (encoder.matches(dPassword, usermaster.getUserPassword())) {

					UserDetails userDetails = jwtUserDetailsService.loadUserByUsername((usermaster.getUserId() + ""));
					String token = jwtTokenUtil.generateToken(userDetails, false);
					usermaster.setUserToken(token);

					System.out.println("userDetails 123 "+userDetails);

					UserOutputDAO userOutputDAO = CustomMessages.returnUserOutputDAO(usermaster);
					try {
						ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
						if(!usermaster.getRoleId().getRoleName().equals("SuperAdmin")) {
						List<BoundaryDAO> userBoundaryDetails=userVillageMappingRepository.getUserWiseBoundaryDetailsForRedis(userOutputDAO.getUserId());
							if(userBoundaryDetails!=null && userBoundaryDetails.size()>0 ) {
								usermaster.setStateLGDCodeList(userBoundaryDetails.stream().map(x->x.getStateLgdCode()).collect(Collectors.toSet()));
								usermaster.setDistrictLgdCodeList(userBoundaryDetails.stream().map(x->x.getDistrictLgdCode()).collect(Collectors.toSet()));
								usermaster.setSubDistrictLgdCodeList(userBoundaryDetails.stream().map(x->x.getSubDistrictLgdCode()).collect(Collectors.toSet()));
	//							usermaster.setSubDistrictLgdCodeList(userBoundaryDetails.stream().map(x->x.getSubDistrictLgdCode()).distinct().collect(Collectors.toSet()).stream().map(x->x).collect(Collectors.toList()));
								usermaster.setVillageLgdCodeList(userBoundaryDetails.stream().map(x->x.getVillageLgdCode()).collect(Collectors.toList()));
								
							}
						}
						String userDataJson = ow.writeValueAsString(usermaster);
//						System.out.println(userDataJson);
//					redisService.setValue("usermaster_"+usermaster.getUserId(), userDataJson);


//					List<Long> stateCodes=userVillageMappingRepository.getStateCodesById(usermaster.getUserId());
					

						String userKey=redisAppName+"_"+Constants.REDIS_USER_KEY+userOutputDAO.getUserId();
						
						//Set user data into redis
						redisService.setValue(userKey, userDataJson);
						
					

					
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					StatusMaster status = statusRepository
							.findByIsDeletedFalseAndStatusCode(usermaster.getUserStatus());
					userOutputDAO.setUserStatus(Objects.isNull(status) ? null : status.getStatusName());
					userOutputDAO.getRole().setCreatedIp(null);
					userOutputDAO.getRole().setModifiedIp(null);


					LanguageMaster defaultLanguageMaster = usermaster.getDefaultLanguageMaster();
					if(Objects.isNull(defaultLanguageMaster) && !Objects.isNull(usermaster.getUserStateLGDCode())) {
						StateDefaultLanguageMapping defaultLanguageMapping = stateDefaultLanguageMappingRepository.findByIsActiveTrueAndIsDeletedFalseAndStateLgdMasterStateLgdCode(usermaster.getUserStateLGDCode().longValue());
						if(!Objects.isNull(defaultLanguageMapping)) {
							defaultLanguageMaster = defaultLanguageMapping.getLanguageId();
						}
					}

					if (!Objects.isNull(defaultLanguageMaster)) {
						userOutputDAO.setDefaultLanguageMaster(defaultLanguageMaster);
						userOutputDAO.setDefaultLanguageId(defaultLanguageMaster.getLanguageId());
					}

					//					userOutputDAO.setDefaultLanguageId(
					//							userDefLanMapService.getDefaultLanguageByUserId(usermaster.getUserId()));

					LoginLogoutActivityLog activityLog = new LoginLogoutActivityLog();
					activityLog.setAuthToken(token);
					activityLog.setIsActive(true);
					activityLog.setIsDeleted(false);
					activityLog.setLogin(true);
					activityLog.setUserId(usermaster.getUserId());
					activityLog.setUserName(usermaster.getUserName());
					activityLog.setLogInDate(new Date());
//					activityLog.setCreatedIp(InetAddress.getLocalHost().getHostAddress());
					activityLog.setCreatedIp(httpServletRequest.getRemoteAddr());
					logReposiptory.save(activityLog);

					List<RoleMenuMasterMapping> roleMenuMaster = roleMenuMasterMappingRepository
							.findByRoleAndIsActiveAndIsDeleted(usermaster.getRoleId(), true, false);

					List<MenuOutputDAO> menuList = new ArrayList<>();
					if (roleMenuMaster != null && roleMenuMaster.size() > 0) {
						roleMenuMaster.forEach(action -> {
							MenuOutputDAO menuOutputDAO = CommonUtil.returnMenuOutputDAO(action.getMenu());
							menuOutputDAO.setIsAdd(action.getIsAdd());
							menuOutputDAO.setIsEdit(action.getIsEdit());
							menuOutputDAO.setIsDelete(action.getIsDelete());
							menuOutputDAO.setIsView(action.getIsView());
							menuList.add(menuOutputDAO);
						});
					}
					List<MenuMaster> parentList = new ArrayList<>();
					roleMenuMaster.stream().distinct().forEach(ele -> {
						if(Objects.nonNull(ele.getMenu())) {
							parentList.add(getParentMenu(ele.getMenu()));
						}
					});

					parentList.stream().distinct().collect(Collectors.toList());
					parentList.forEach(action -> {
						MenuOutputDAO menuOutputDAO = CommonUtil.returnMenuOutputDAO(action);
						menuList.add(menuOutputDAO);
					});
					List<MenuOutputDAO> assignedMenuList = menuList.stream().collect(Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(MenuOutputDAO::getMenuId)))).stream().collect(Collectors.toList());
					userOutputDAO.setMenuList(assignedMenuList);
					
					userOutputDAO.setIsDefault(userOptional.get().getRoleId().getIsDefault());
					Optional<RolePatternMapping> rolePatternMapping=rolePatternMappingRepository.findById(userOptional.get().getRoleId().getRoleId());
					if(rolePatternMapping.isPresent()) {
						userOutputDAO.setTerritoryLevel(rolePatternMapping.get().getTerritoryLevel());
					}
					
					responseModel = CustomMessages.makeResponseModel(userOutputDAO, CustomMessages.LOGIN_SUCCESS,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				}
				else {
					responseModel = CustomMessages.makeResponseModel(null, CustomMessages.INVALID_PASSWORD,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}

			} else {
				responseModel = CustomMessages.makeResponseModel(null, CustomMessages.INVALID_PASSWORD,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * @param menu
	 * @return
	 */
	protected MenuMaster getParentMenu(MenuMaster menu) {
		if(Objects.nonNull(menu.getMenuParentId())) {
			MenuMaster menuObject = menuMasterRepository.findByMenuId(menu.getMenuParentId());
			return getParentMenu(menuObject);
		} else {
			return menu;
		}
	}

	/**
	 * @param geographicalArea
	 * @return
	 */
	public List<UserStateDAO> getuserStateList(List<GeographicalAreaDAO> geographicalArea) {
		List<UserStateDAO> userStateDaoList = new ArrayList<UserStateDAO>();

		List<UserDistrictDAO> userDistrictDAOList = new ArrayList<UserDistrictDAO>();
		Set<UserSubDistrictDAO> userSubDistrictDAOList = new HashSet<UserSubDistrictDAO>();
		List<UserVillageDAO> userVillageDAOList = new ArrayList<UserVillageDAO>();
		geographicalArea.forEach(action -> {

			switch (action.getBoundary()) {

			case "state":
				UserStateDAO userStateDao = new UserStateDAO();
				userStateDao.setStateLgdCode(action.getStateLgdCode());
				userStateDao.setIsAllSelected(Boolean.TRUE);
				userStateDaoList.add(userStateDao);
				break;
			case "district":
				UserDistrictDAO userDistrictDAO = new UserDistrictDAO();
				userDistrictDAO.setStateLgdCode(action.getStateLgdCode());
				userDistrictDAO.setDistrictLgdCode(action.getDistrictLgdCode());
				userDistrictDAO.setIsAllSelected(Boolean.TRUE);
				userDistrictDAOList.add(userDistrictDAO);
				break;
			case "subDistrict":
				UserSubDistrictDAO userSubDistrictDAO = new UserSubDistrictDAO();
				userSubDistrictDAO.setStateLgdCode(action.getStateLgdCode());
				userSubDistrictDAO.setDistrictLgdCode(action.getDistrictLgdCode());
				userSubDistrictDAO.setSubDistrictLgdCode(action.getSubDistrictLgdCode());
				userSubDistrictDAO.setIsAllSelected(Boolean.TRUE);
				userSubDistrictDAOList.add(userSubDistrictDAO);
				break;
			case "village":
				UserVillageDAO userVillageDAO = new UserVillageDAO();
				userVillageDAO.setStateLgdCode(action.getStateLgdCode());
				userVillageDAO.setDistrictLgdCode(action.getDistrictLgdCode());
				userVillageDAO.setSubDistrictLgdCode(action.getSubDistrictLgdCode());
				userVillageDAO.setVillageLgdCode(action.getVillageLgdCode());
				userVillageDAO.setIsAllSelected(Boolean.TRUE);
				userVillageDAOList.add(userVillageDAO);
				break;
			}
		});

		List<UserStateDAO> finalUserStateDaoList = new ArrayList<UserStateDAO>();
		if (userStateDaoList != null && userStateDaoList.size() > 0) {
			List<Long> statelgdCodes = userStateDaoList.stream().map(x -> x.getStateLgdCode()).distinct()
					.collect(Collectors.toList());

			List<VillageLgdMaster> villageList = villageLgdMasterRepository
					.findByStateLgdCode_StateLgdCodeIn(statelgdCodes);

			if (villageList != null && statelgdCodes != null && statelgdCodes.size() > 0) {
				List<DistrictLgdMaster> districtlgdCodes = villageList.stream().map(x -> x.getDistrictLgdCode())
						.distinct().collect(Collectors.toList());

				List<SubDistrictLgdMaster> subDistrictlgdCodes = villageList.stream()
						.map(x -> x.getSubDistrictLgdCode()).distinct().collect(Collectors.toList());

				userStateDaoList.forEach(action -> {

					// statelgdCodes.forEach(state->{
					// if(state.equals(action.getStateLgdCode())) {

					List<UserDistrictDAO> tempdistrictList = new ArrayList<UserDistrictDAO>();
					districtlgdCodes.forEach(action2 -> {

						if (action.getStateLgdCode().equals(action2.getStateLgdCode().getStateLgdCode())) {
							UserDistrictDAO userDistrictDAO = new UserDistrictDAO();
							List<UserSubDistrictDAO> tempSubDistrictList = new ArrayList<UserSubDistrictDAO>();

							subDistrictlgdCodes.forEach(action3 -> {

								if (action3.getDistrictLgdCode().getDistrictLgdCode()
										.equals(action2.getDistrictLgdCode())) {
									UserSubDistrictDAO userSubDistrictDAO = new UserSubDistrictDAO();
									List<UserVillageDAO> tempVillageList = new ArrayList<UserVillageDAO>();
									villageList.forEach(action4 -> {

										if (action4.getSubDistrictLgdCode().getSubDistrictLgdCode()
												.equals(action3.getSubDistrictLgdCode())) {

											UserVillageDAO userVillageDAO = new UserVillageDAO();
											userVillageDAO.setIsAllSelected(Boolean.TRUE);
											userVillageDAO.setStateLgdCode(action4.getStateLgdCode().getStateLgdCode());
											userVillageDAO.setDistrictLgdCode(
													action4.getDistrictLgdCode().getDistrictLgdCode());
											userVillageDAO.setSubDistrictLgdCode(
													action4.getSubDistrictLgdCode().getSubDistrictLgdCode());
											userVillageDAO.setVillageLgdCode(action4.getVillageLgdCode());
											tempVillageList.add(userVillageDAO);
										}
									});

									userSubDistrictDAO.setVillage(tempVillageList);
									userSubDistrictDAO.setIsAllSelected(Boolean.TRUE);
									userSubDistrictDAO.setStateLgdCode(action3.getStateLgdCode().getStateLgdCode());
									userSubDistrictDAO
									.setDistrictLgdCode(action3.getDistrictLgdCode().getDistrictLgdCode());
									userSubDistrictDAO.setSubDistrictLgdCode(action3.getSubDistrictLgdCode());
									tempSubDistrictList.add(userSubDistrictDAO);
								}

							});
							userDistrictDAO.setIsAllSelected(Boolean.TRUE);
							userDistrictDAO.setSubDistrict(tempSubDistrictList);
							userDistrictDAO.setStateLgdCode(action2.getStateLgdCode().getStateLgdCode());
							userDistrictDAO.setDistrictLgdCode(action2.getDistrictLgdCode());
							tempdistrictList.add(userDistrictDAO);
						}
					});

					action.setDistrict(tempdistrictList);
					// }
					// });
				});
				// finalUserStateDaoList.addAll(userStateDaoList);
			}
		}

		if (userDistrictDAOList != null && userDistrictDAOList.size() > 0) {

			Set<Long> districtStatelgdCodes = userDistrictDAOList.stream().map(x -> x.getStateLgdCode()).distinct()
					.collect(Collectors.toSet());
			List<Long> statelgdCodes = userStateDaoList.stream().map(x -> x.getStateLgdCode()).distinct()
					.collect(Collectors.toList());
			districtStatelgdCodes.removeAll(statelgdCodes);

			if (districtStatelgdCodes != null && districtStatelgdCodes.size() > 0) {
				districtStatelgdCodes.forEach(element -> {
					UserStateDAO userStateDao = new UserStateDAO();
					userStateDao.setStateLgdCode(element);
					userStateDao.setIsAllSelected(Boolean.FALSE);
					userStateDaoList.add(userStateDao);
				});
			}

			List<Long> distictlgdCodes = userDistrictDAOList.stream().map(x -> x.getDistrictLgdCode()).distinct()
					.collect(Collectors.toList());

			List<VillageLgdMaster> villageList = villageLgdMasterRepository
					.findByDistrictLgdCode_DistrictLgdCodeIn(distictlgdCodes);

			List<DistrictLgdMaster> districtlgdCodes = villageList.stream().map(x -> x.getDistrictLgdCode()).distinct()
					.collect(Collectors.toList());

			List<SubDistrictLgdMaster> subDistrictlgdCodes = villageList.stream().map(x -> x.getSubDistrictLgdCode())
					.distinct().collect(Collectors.toList());

			userStateDaoList.forEach(action -> {

				districtStatelgdCodes.forEach(matchState -> {

					if (action.getStateLgdCode().equals(matchState)) {

						List<UserDistrictDAO> tempdistrictList = new ArrayList<UserDistrictDAO>();
						districtlgdCodes.forEach(action2 -> {

							if (action.getStateLgdCode().equals(action2.getStateLgdCode().getStateLgdCode())) {
								UserDistrictDAO userDistrictDAO = new UserDistrictDAO();
								List<UserSubDistrictDAO> tempSubDistrictList = new ArrayList<UserSubDistrictDAO>();

								subDistrictlgdCodes.forEach(action3 -> {

									if (action3.getDistrictLgdCode().getDistrictLgdCode()
											.equals(action2.getDistrictLgdCode())) {
										UserSubDistrictDAO userSubDistrictDAO = new UserSubDistrictDAO();
										List<UserVillageDAO> tempVillageList = new ArrayList<UserVillageDAO>();
										villageList.forEach(action4 -> {

											if (action4.getSubDistrictLgdCode().getSubDistrictLgdCode()
													.equals(action3.getSubDistrictLgdCode())) {

												UserVillageDAO userVillageDAO = new UserVillageDAO();
												userVillageDAO.setIsAllSelected(Boolean.FALSE);
												userVillageDAO
												.setStateLgdCode(action4.getStateLgdCode().getStateLgdCode());
												userVillageDAO.setDistrictLgdCode(
														action4.getDistrictLgdCode().getDistrictLgdCode());
												userVillageDAO.setSubDistrictLgdCode(
														action4.getSubDistrictLgdCode().getSubDistrictLgdCode());
												userVillageDAO.setVillageLgdCode(action4.getVillageLgdCode());
												tempVillageList.add(userVillageDAO);
											}
										});

										userSubDistrictDAO.setVillage(tempVillageList);
										userSubDistrictDAO.setIsAllSelected(Boolean.FALSE);
										userSubDistrictDAO.setStateLgdCode(action3.getStateLgdCode().getStateLgdCode());
										userSubDistrictDAO
										.setDistrictLgdCode(action3.getDistrictLgdCode().getDistrictLgdCode());
										userSubDistrictDAO.setSubDistrictLgdCode(action3.getSubDistrictLgdCode());
										tempSubDistrictList.add(userSubDistrictDAO);
									}

								});
								userDistrictDAO.setIsAllSelected(Boolean.TRUE);
								userDistrictDAO.setSubDistrict(tempSubDistrictList);
								userDistrictDAO.setStateLgdCode(action2.getStateLgdCode().getStateLgdCode());
								userDistrictDAO.setDistrictLgdCode(action2.getDistrictLgdCode());
								tempdistrictList.add(userDistrictDAO);
							}
						});

						action.setDistrict(tempdistrictList);

					}
				});

			});

		}

		if (userSubDistrictDAOList != null && userSubDistrictDAOList.size() > 0) {

			List<Long> distictlgdCodes = userSubDistrictDAOList.stream().map(x -> x.getDistrictLgdCode()).distinct()
					.collect(Collectors.toList());

			List<Long> matchDistrictlgdCodes = userDistrictDAOList.stream().map(x -> x.getDistrictLgdCode()).distinct()
					.collect(Collectors.toList());
			distictlgdCodes.removeAll(matchDistrictlgdCodes);

			List<Long> subDistictlgdCodes = userSubDistrictDAOList.stream().map(x -> x.getSubDistrictLgdCode())
					.distinct().collect(Collectors.toList());


			List<VillageLgdMaster> villageList = villageLgdMasterRepository
					.findBySubDistrictLgdCode_SubDistrictLgdCodeIn(subDistictlgdCodes);
			if (villageList != null) {
				Set<Long> statelgdCodes = userSubDistrictDAOList.stream().map(x -> x.getStateLgdCode()).distinct()
						.collect(Collectors.toSet());
				Set<Long> distictStatelgdCodes = userDistrictDAOList.stream().map(x -> x.getStateLgdCode()).distinct()
						.collect(Collectors.toSet());
				statelgdCodes.removeAll(distictStatelgdCodes);
				statelgdCodes.forEach(action -> {
					UserStateDAO userStateDao = new UserStateDAO();
					userStateDao.setStateLgdCode(action);
					userStateDao.setIsAllSelected(Boolean.FALSE);
					userStateDaoList.add(userStateDao);
				});

				List<DistrictLgdMaster> districtlgdCodes = villageList.stream().map(x -> x.getDistrictLgdCode())
						.distinct().collect(Collectors.toList());

				List<SubDistrictLgdMaster> subDistrictlgdCodes = villageList.stream()
						.map(x -> x.getSubDistrictLgdCode()).distinct().collect(Collectors.toList());

				userStateDaoList.forEach(action -> {

					statelgdCodes.forEach(substateLGDCode -> {

						if (action.getStateLgdCode().equals(substateLGDCode)) {

							List<UserDistrictDAO> tempdistrictList = new ArrayList<UserDistrictDAO>();
							districtlgdCodes.forEach(action2 -> {

								if (action.getStateLgdCode().equals(action2.getStateLgdCode().getStateLgdCode())) {
									UserDistrictDAO userDistrictDAO = new UserDistrictDAO();
									List<UserSubDistrictDAO> tempSubDistrictList = new ArrayList<UserSubDistrictDAO>();

									subDistrictlgdCodes.forEach(action3 -> {

										if (action3.getDistrictLgdCode().getDistrictLgdCode()
												.equals(action2.getDistrictLgdCode())) {
											UserSubDistrictDAO userSubDistrictDAO = new UserSubDistrictDAO();
											List<UserVillageDAO> tempVillageList = new ArrayList<UserVillageDAO>();
											villageList.forEach(action4 -> {

												if (action4.getSubDistrictLgdCode().getSubDistrictLgdCode()
														.equals(action3.getSubDistrictLgdCode())) {

													UserVillageDAO userVillageDAO = new UserVillageDAO();
													userVillageDAO.setIsAllSelected(Boolean.FALSE);
													userVillageDAO.setStateLgdCode(
															action4.getStateLgdCode().getStateLgdCode());
													userVillageDAO.setDistrictLgdCode(
															action4.getDistrictLgdCode().getDistrictLgdCode());
													userVillageDAO.setSubDistrictLgdCode(
															action4.getSubDistrictLgdCode().getSubDistrictLgdCode());
													userVillageDAO.setVillageLgdCode(action4.getVillageLgdCode());
													tempVillageList.add(userVillageDAO);
												}
											});

											userSubDistrictDAO.setVillage(tempVillageList);
											userSubDistrictDAO.setIsAllSelected(Boolean.TRUE);
											userSubDistrictDAO
											.setStateLgdCode(action3.getStateLgdCode().getStateLgdCode());
											userSubDistrictDAO.setDistrictLgdCode(
													action3.getDistrictLgdCode().getDistrictLgdCode());
											userSubDistrictDAO.setSubDistrictLgdCode(action3.getSubDistrictLgdCode());
											tempSubDistrictList.add(userSubDistrictDAO);
										}

									});
									userDistrictDAO.setIsAllSelected(Boolean.FALSE);
									userDistrictDAO.setSubDistrict(tempSubDistrictList);
									userDistrictDAO.setStateLgdCode(action2.getStateLgdCode().getStateLgdCode());
									userDistrictDAO.setDistrictLgdCode(action2.getDistrictLgdCode());
									tempdistrictList.add(userDistrictDAO);
								}
							});

							action.setDistrict(tempdistrictList);
						}
					});

				});

				System.out.println(userStateDaoList);

			}

		}

		if (userVillageDAOList != null && userVillageDAOList.size() > 0) {

			List<Long> villagelgdCodes = userVillageDAOList.stream().map(x -> x.getVillageLgdCode()).distinct()
					.collect(Collectors.toList());

			if (villagelgdCodes != null && villagelgdCodes.size() > 0) {

				List<VillageLgdMaster> villageList = villageLgdMasterRepository.findByVillageLgdCodeIn(villagelgdCodes);

				if (villageList != null) {

					Set<Long> statelgdCodes = userVillageDAOList.stream().map(x -> x.getStateLgdCode()).distinct()
							.collect(Collectors.toSet());
					Set<Long> subdistictStatelgdCodes = userSubDistrictDAOList.stream().map(x -> x.getStateLgdCode())
							.distinct().collect(Collectors.toSet());
					Set<Long> subdistictlgdCodes = userVillageDAOList.stream().map(x -> x.getSubDistrictLgdCode())
							.distinct().collect(Collectors.toSet());

					ArrayList<Long> statelgdCodesDuplicates = new ArrayList<Long>(statelgdCodes);
					statelgdCodesDuplicates.retainAll(subdistictStatelgdCodes);

					System.out.println(statelgdCodesDuplicates);
					ArrayList<Long> statelgdCodesUniques = new ArrayList<Long>(statelgdCodes);
					statelgdCodesUniques.removeAll(subdistictStatelgdCodes);

					System.out.println(statelgdCodesUniques);
					statelgdCodes.removeAll(subdistictStatelgdCodes);

					List<DistrictLgdMaster> distList = villageList.stream().map(map -> map.getDistrictLgdCode())
							.collect(Collectors.toList());
					List<SubDistrictLgdMaster> subDistList = villageList.stream()
							.map(map -> map.getSubDistrictLgdCode()).collect(Collectors.toList());
					statelgdCodesUniques.forEach(action -> {
						UserStateDAO userStateDao = new UserStateDAO();
						userStateDao.setStateLgdCode(action);
						userStateDao.setIsAllSelected(Boolean.FALSE);
						userStateDaoList.add(userStateDao);
					});
					if (statelgdCodesDuplicates != null && statelgdCodesDuplicates.size() > 0) {
						List<DistrictLgdMaster> districtlgdCodes = villageList.stream().map(x -> x.getDistrictLgdCode())
								.distinct().collect(Collectors.toList());
						List<Long> villageDlgdCodes = districtlgdCodes.stream().map(x -> x.getDistrictLgdCode())
								.collect(Collectors.toList());
						List<Long> subFordistrictLGD = userSubDistrictDAOList.stream().map(x -> x.getDistrictLgdCode())
								.collect(Collectors.toList());
						villageDlgdCodes.removeAll(subFordistrictLGD);
						if (villageDlgdCodes != null && villageDlgdCodes.size() > 0) {
							userStateDaoList.forEach(action -> {
								List<UserDistrictDAO> tempdistrictList = new ArrayList<UserDistrictDAO>();
								districtlgdCodes.forEach(match -> {

									if (action.getStateLgdCode().equals(match.getStateLgdCode().getStateLgdCode())) {
										villageDlgdCodes.forEach(action3 -> {

											if (action3.equals(match.getDistrictLgdCode())) {
												UserDistrictDAO userDistrictDAO = new UserDistrictDAO();
												userDistrictDAO.setIsAllSelected(Boolean.FALSE);
												userDistrictDAO.setStateLgdCode(action.getStateLgdCode());
												userDistrictDAO.setDistrictLgdCode(action3);
												tempdistrictList.add(userDistrictDAO);

											}
										});

									}
								});
								if (tempdistrictList != null && tempdistrictList.size() > 0) {

									List<UserDistrictDAO> newList = new ArrayList<UserDistrictDAO>(
											action.getDistrict());
									newList.addAll(tempdistrictList);
									action.setDistrict(newList);
								}

							});
						}
						List<SubDistrictLgdMaster> subDistrictlgdCodes = villageList.stream()
								.map(x -> x.getSubDistrictLgdCode()).distinct().collect(Collectors.toList());
						userStateDaoList.forEach(action -> {
							statelgdCodesDuplicates.forEach(match -> {

								if (action.getStateLgdCode().equals(match)) {
									action.getDistrict().forEach(action2 -> {
										List<UserSubDistrictDAO> tempSubDistrictList = new ArrayList<UserSubDistrictDAO>();
										subDistrictlgdCodes.forEach(action3 -> {

											if (action3.getDistrictLgdCode().getDistrictLgdCode()
													.equals(action2.getDistrictLgdCode())) {
												UserSubDistrictDAO userSubDistrictDAO = new UserSubDistrictDAO();
												List<UserVillageDAO> tempVillageList = new ArrayList<UserVillageDAO>();
												villageList.forEach(action4 -> {

													if (action4.getSubDistrictLgdCode().getSubDistrictLgdCode()
															.equals(action3.getSubDistrictLgdCode())) {

														UserVillageDAO userVillageDAO = new UserVillageDAO();
														userVillageDAO.setIsAllSelected(Boolean.FALSE);
														userVillageDAO.setStateLgdCode(
																action4.getStateLgdCode().getStateLgdCode());
														userVillageDAO.setDistrictLgdCode(
																action4.getDistrictLgdCode().getDistrictLgdCode());
														userVillageDAO.setSubDistrictLgdCode(action4
																.getSubDistrictLgdCode().getSubDistrictLgdCode());
														userVillageDAO.setVillageLgdCode(action4.getVillageLgdCode());
														tempVillageList.add(userVillageDAO);
													}
												});

												userSubDistrictDAO.setVillage(tempVillageList);
												userSubDistrictDAO.setIsAllSelected(Boolean.FALSE);
												userSubDistrictDAO
												.setStateLgdCode(action3.getStateLgdCode().getStateLgdCode());
												userSubDistrictDAO.setDistrictLgdCode(
														action3.getDistrictLgdCode().getDistrictLgdCode());
												userSubDistrictDAO
												.setSubDistrictLgdCode(action3.getSubDistrictLgdCode());
												tempSubDistrictList.add(userSubDistrictDAO);
											}

										});
										if (tempSubDistrictList != null && tempSubDistrictList.size() > 0) {

											List<UserSubDistrictDAO> newList = new ArrayList<UserSubDistrictDAO>();
											if (action2.getSubDistrict() != null) {
												newList = new ArrayList<UserSubDistrictDAO>(action2.getSubDistrict());
											}
											newList.addAll(tempSubDistrictList);
											action2.setSubDistrict(newList);
										}

									});

								}

							});

						});

					}
					if (statelgdCodesUniques != null && statelgdCodesUniques.size() > 0) {
						List<DistrictLgdMaster> districtlgdCodes = villageList.stream().map(x -> x.getDistrictLgdCode())
								.distinct().collect(Collectors.toList());

						List<SubDistrictLgdMaster> subDistrictlgdCodes = villageList.stream()
								.map(x -> x.getSubDistrictLgdCode()).distinct().collect(Collectors.toList());

						userStateDaoList.forEach(action -> {

							statelgdCodesUniques.forEach(match -> {
								if (action.getStateLgdCode().equals(match)) {

									List<UserDistrictDAO> tempdistrictList = new ArrayList<UserDistrictDAO>();
									districtlgdCodes.forEach(action2 -> {

										if (action.getStateLgdCode()
												.equals(action2.getStateLgdCode().getStateLgdCode())) {
											UserDistrictDAO userDistrictDAO = new UserDistrictDAO();
											List<UserSubDistrictDAO> tempSubDistrictList = new ArrayList<UserSubDistrictDAO>();

											subDistrictlgdCodes.forEach(action3 -> {

												if (action3.getDistrictLgdCode().getDistrictLgdCode()
														.equals(action2.getDistrictLgdCode())) {
													UserSubDistrictDAO userSubDistrictDAO = new UserSubDistrictDAO();
													List<UserVillageDAO> tempVillageList = new ArrayList<UserVillageDAO>();
													villageList.forEach(action4 -> {

														if (action4.getSubDistrictLgdCode().getSubDistrictLgdCode()
																.equals(action3.getSubDistrictLgdCode())) {

															UserVillageDAO userVillageDAO = new UserVillageDAO();
															userVillageDAO.setIsAllSelected(Boolean.FALSE);
															userVillageDAO.setStateLgdCode(
																	action4.getStateLgdCode().getStateLgdCode());
															userVillageDAO.setDistrictLgdCode(
																	action4.getDistrictLgdCode().getDistrictLgdCode());
															userVillageDAO.setSubDistrictLgdCode(action4
																	.getSubDistrictLgdCode().getSubDistrictLgdCode());
															userVillageDAO
															.setVillageLgdCode(action4.getVillageLgdCode());
															tempVillageList.add(userVillageDAO);
														}
													});

													userSubDistrictDAO.setVillage(tempVillageList);
													userSubDistrictDAO.setIsAllSelected(Boolean.FALSE);
													userSubDistrictDAO.setStateLgdCode(
															action3.getStateLgdCode().getStateLgdCode());
													userSubDistrictDAO.setDistrictLgdCode(
															action3.getDistrictLgdCode().getDistrictLgdCode());
													userSubDistrictDAO
													.setSubDistrictLgdCode(action3.getSubDistrictLgdCode());
													tempSubDistrictList.add(userSubDistrictDAO);
												}

											});
											userDistrictDAO.setIsAllSelected(Boolean.FALSE);
											userDistrictDAO.setSubDistrict(tempSubDistrictList);
											userDistrictDAO
											.setStateLgdCode(action2.getStateLgdCode().getStateLgdCode());
											userDistrictDAO.setDistrictLgdCode(action2.getDistrictLgdCode());
											tempdistrictList.add(userDistrictDAO);
										}
									});

									action.setDistrict(tempdistrictList);
								}
							});

						});
					}

				}

			}

		}

		return userStateDaoList;
	}

	public ResponseModel addUser(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		try {

			Optional<RoleMaster> roleOP = roleMasterRepository.findByIsDeletedAndIsActiveAndRoleId(Boolean.FALSE,
					Boolean.TRUE, userInputDAO.getRoleId());

			if (roleOP.isPresent()) {
				Optional<UserMaster> userNameOP = userMasterRepository.findByUserName(userInputDAO.getUserName());
				if (userNameOP.isPresent()) {
					return CustomMessages.makeResponseModel(null, CustomMessages.USER_NAME_EXIST,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);

				}
				Optional<UserMaster> userMobileNoOP = userMasterRepository
						.findByUserMobileNumber(userInputDAO.getUserMobileNumber());
				if (userMobileNoOP.isPresent()) {
					return CustomMessages.makeResponseModel(null, CustomMessages.USER_MOBILE_NAME_EXIST,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);

				}
				Optional<UserMaster> userEmailOP = userMasterRepository
						.findByUserEmailAddress(userInputDAO.getUserEmailAddress());
				if (userEmailOP.isPresent()) {
					return CustomMessages.makeResponseModel(null, CustomMessages.USER_EMAIL_ADDRESS_EXIST,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}

				UserMaster userMaster = CustomMessages.returnUserMasterFromUserInputDAO(userInputDAO);
				userMaster.setUserPassword(encoder.encode(userInputDAO.getUserPassword()));
				try {
					Gson gson = new Gson();
					JsonArray passwordHistory = new JsonArray();
					if (userMaster.getPasswordHistory() != null) {
						passwordHistory = gson.fromJson(userMaster.getPasswordHistory().toString(), JsonArray.class);
					}
					passwordHistory.add(userMaster.getUserPassword());
					if (passwordHistory.size() > 3) {
						passwordHistory.remove(0);
					}
					userMaster.setPasswordHistory(passwordHistory.toString());
				} catch (Exception e) {
					return CustomMessages.makeResponseModel(true, "Issue while saving passwordhistory",
							CustomMessages.GET_DATA_ERROR, CustomMessages.INVALID_INPUT);
				}
				userMaster.setRoleId(roleOP.get());
				userMaster.setIsActive(true);
				userMaster.setIsDeleted(false);
				userMaster.setIsPasswordChanged(false);
				userMaster.setUserStatus(StatusEnum.APPROVED.getValue());

				userMaster.setCreatedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));
				userMaster.setModifiedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));
				if (userInputDAO.getGeographicalArea() != null) {
					List<UserStateDAO> userStateDaoList = getuserStateList(userInputDAO.getGeographicalArea());
					GeographicalAreaOutputDAO geographicalAreaOutputDAO = new GeographicalAreaOutputDAO();
					geographicalAreaOutputDAO.setState(userStateDaoList);
					ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
					String json = ow.writeValueAsString(geographicalAreaOutputDAO);
					userMaster.setGeographicalArea(json);

					// For the supervisor
					if (roleOP.get().getCode().equalsIgnoreCase("CS_SUPERVISOR")) {
						Optional<DepartmentMaster> department = departmentRepository.findById(new Long(2));
						userMaster.setUserVillageLGDCode(
								userInputDAO.getGeographicalArea().get(0).getVillageLgdCode().intValue());
						userMaster.setDepartmentId(department.get());
						String userPrId = generalService.generatePrNumber("SUP", userMaster);
						userMaster.setUserPrId(userPrId);
					}
				}
				UserMaster savedUser = userMasterRepository.save(userMaster);
				Runnable r = () -> {
					userVillageMappingService.addUserVillage(savedUser, userInputDAO);
				};
				Thread t = new Thread(r);
				t.start();

				messageConfigService.sendEmailToUser(userInputDAO);
				return CustomMessages.makeResponseModel(userInputDAO, CustomMessages.USER_ADDED_SUCCESS,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.ROLE_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

		} catch (

				Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * Adds a new user.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing user input data.
	 * @return The ResponseModel object representing the response.
	 */
	public ResponseModel addStateUser(HttpServletRequest request, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		try {

			Optional<RoleMaster> roleOP = roleMasterRepository.findByIsDeletedAndIsActiveAndRoleId(Boolean.FALSE,
					Boolean.TRUE, userInputDAO.getRoleId());

			if (roleOP.isPresent()) {
				Optional<UserMaster> userNameOP = userMasterRepository.findByUserName(userInputDAO.getUserName());
				if (userNameOP.isPresent()) {
					return CustomMessages.makeResponseModel(null, CustomMessages.USER_NAME_EXIST,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);

				}
				Optional<UserMaster> userMobileNoOP = userMasterRepository
						.findByUserMobileNumber(userInputDAO.getUserMobileNumber());
				if (userMobileNoOP.isPresent()) {
					return CustomMessages.makeResponseModel(null, CustomMessages.USER_MOBILE_NAME_EXIST,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);

				}
				Optional<UserMaster> userEmailOP = userMasterRepository
						.findByUserEmailAddress(userInputDAO.getUserEmailAddress());
				if (userEmailOP.isPresent()) {
					return CustomMessages.makeResponseModel(null, CustomMessages.USER_EMAIL_ADDRESS_EXIST,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}

				UserMaster userMaster = new UserMaster();
				if (userInputDAO.getGeographicalArea().size() == 1 ) {
					String generatedUserName = generateNewUserId(userInputDAO,false,null);
					//					System.out.println(generatedUserName);
					List<UserMaster> userPresent = userMasterRepository.findByIsDeletedAndIsActiveAndUserStateLGDCodeAndRoleId_RoleIdOrderByUserIdDesc(Boolean.FALSE, Boolean.TRUE, userInputDAO.getGeographicalArea().get(0).getStateLgdCode().intValue(), userInputDAO.getRoleId());
					//					System.out.println(!userPresent.isEmpty());
					if(!userPresent.isEmpty()) {
						String generatedNewUserName = generateNewUserId(userInputDAO,true,userPresent.get(0).getUserName());
						System.out.println(generatedNewUserName);
						userMaster = CustomMessages.returnUserMasterFromUserInputDAOForStateUser(userInputDAO,generatedNewUserName);
					}else {
						userMaster = CustomMessages.returnUserMasterFromUserInputDAOForStateUser(userInputDAO,generatedUserName);
					}

				}else {
					userMaster = CustomMessages.returnUserMasterFromUserInputDAOForMultipleStateUser(userInputDAO);
				}

				userMaster.setUserPassword(encoder.encode(userInputDAO.getUserPassword()));
				try {
					Gson gson = new Gson();
					JsonArray passwordHistory = new JsonArray();
					if (userMaster.getPasswordHistory() != null) {
						passwordHistory = gson.fromJson(userMaster.getPasswordHistory().toString(), JsonArray.class);
					}
					passwordHistory.add(userMaster.getUserPassword());
					if (passwordHistory.size() > 3) {
						passwordHistory.remove(0);
					}
					userMaster.setPasswordHistory(passwordHistory.toString());
				} catch (Exception e) {
					return CustomMessages.makeResponseModel(true, "Issue while saving passwordhistory",
							CustomMessages.GET_DATA_ERROR, CustomMessages.INVALID_INPUT);
				}
				userMaster.setRoleId(roleOP.get());
				userMaster.setIsActive(true);
				userMaster.setIsDeleted(false);
				userMaster.setIsPasswordChanged(false);
				userMaster.setUserStatus(StatusEnum.APPROVED.getValue());

				userMaster.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				userMaster.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				if (userInputDAO.getGeographicalArea() != null) {

					List<UserStateDAO> userStateDaoList = new ArrayList<UserStateDAO>();

					userInputDAO.getGeographicalArea().forEach(action -> {

						switch (action.getBoundary()) {

						case "state":
							UserStateDAO userStateDao = new UserStateDAO();
							userStateDao.setStateLgdCode(action.getStateLgdCode());
							userStateDao.setIsAllSelected(Boolean.TRUE);
							userStateDaoList.add(userStateDao);
							break;
						}
					});


					UserMaster savedStateUser = userMasterRepository.save(userMaster);
					if (userStateDaoList != null && userStateDaoList.size() > 0) {
						List<Long> statelgdCodes = userStateDaoList.stream().map(x -> x.getStateLgdCode()).distinct()
								.collect(Collectors.toList());
						System.out.println(statelgdCodes);

						new Thread() {
							@Override
							public void run() {
								try {
									List<UserVillageMapping> userVillageList = new ArrayList<>();

									List<DistrictLgdMaster> districtCode = districtLgdMasterRepository
											.findByStateLgdCode_StateLgdCodeIn(statelgdCodes);

									districtCode.forEach(district -> {
										List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
												.findByDistrictLgdCode_DistrictLgdCode(district.getDistrictLgdCode());

										villageLgdMaster.forEach(village -> {
											UserVillageMapping userVillageMapping = new UserVillageMapping();
											userVillageMapping.setUserMaster(savedStateUser);
											userVillageMapping.setVillageLgdMaster(village);
											userVillageMapping
											.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
											userVillageMapping
											.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
											userVillageMapping.setIsActive(true);
											userVillageMapping.setIsDeleted(false);
											userVillageMappingRepository.save(userVillageMapping);
										});
									});
									//									});
									System.out.println("Inside thread  count:" + userVillageList.size());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}.start();

					}

				}
				return CustomMessages.makeResponseModel(userInputDAO, CustomMessages.USER_ADDED_SUCCESS,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.ROLE_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

		} catch (

				Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * @param userDao
	 * @param isPresent
	 * @param existingName
	 * @return
	 */
	public String generateNewUserId(UserInputDAO userDao, Boolean isPresent, String existingName) {
		if (userDao.getGeographicalArea() != null) {
			Long serialNumber = new Long(1);
			if(isPresent) {
				existingName.split("_");
				String[] a = existingName.split("_");
				serialNumber = Long.parseLong(a[1]);
				serialNumber += 1;
			}else {
				serialNumber = (long) 1;
			}


			RoleMaster roleCode = roleMasterRepository
					.findByRoleIdAndIsDeletedFalse(userDao.getRoleId());
			String userId = generalService.generateUserNameUsingPatternForStateUser(roleCode.getPrefix(), serialNumber,
					"S", userDao.getGeographicalArea().get(0).getStateLgdCode());


			return userId;
		}
		return null;
	}


	/**
	 * Updates a user.
	 *
	 * @param httpServletRequest
	 * @param userInputDAO The {@code UserInputDAO} object containing user input data.
	 * @return The ResponseModel object representing the response.
	 */
	public ResponseModel updateUser(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		try {
			Optional<RoleMaster> roleOP = roleMasterRepository.findByIsDeletedAndIsActiveAndRoleId(Boolean.FALSE,
					Boolean.TRUE, userInputDAO.getRoleId());

			if (roleOP.isPresent()) {
				Optional<UserMaster> userOP = userMasterRepository.findByIsDeletedAndUserId(Boolean.FALSE,
						userInputDAO.getUserId());

				if (userOP.isPresent()) {
					UserMaster usermaster = userOP.get();
					if (usermaster.getUserName().equals(userInputDAO.getUserName())
							&& !StringUtils.isEmpty(usermaster.getUserMobileNumber())
							&& usermaster.getUserMobileNumber().equals(userInputDAO.getUserMobileNumber())
							&& !StringUtils.isEmpty(usermaster.getUserEmailAddress())
							&& usermaster.getUserEmailAddress().equals(userInputDAO.getUserEmailAddress())) {

					} else if (!usermaster.getUserName().equals(userInputDAO.getUserName())) {
						Optional<UserMaster> userNameOP = userMasterRepository
								.findByUserName(userInputDAO.getUserName());
						if (userNameOP.isPresent()) {
							return CustomMessages.makeResponseModel(null, CustomMessages.USER_NAME_EXIST,
									CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);

						}
					} else if (!StringUtils.isEmpty(usermaster.getUserFullName())
							&& !usermaster.getUserFullName().equals(userInputDAO.getUserFullName())) {
						Optional<UserMaster> userNameOP = userMasterRepository
								.findByUserFullName(userInputDAO.getUserFullName());
						if (userNameOP.isPresent()) {
							return CustomMessages.makeResponseModel(null, CustomMessages.USER_NAME_EXIST,
									CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);

						}
					} else if (!StringUtils.isEmpty(usermaster.getUserMobileNumber())
							&& !usermaster.getUserMobileNumber().equals(userInputDAO.getUserMobileNumber())) {
						Optional<UserMaster> userMobileNoOP = userMasterRepository
								.findByUserMobileNumber(userInputDAO.getUserMobileNumber());
						if (userMobileNoOP.isPresent()) {
							return CustomMessages.makeResponseModel(null, CustomMessages.USER_MOBILE_NAME_EXIST,
									CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);

						}
					} else if (!StringUtils.isEmpty(usermaster.getUserEmailAddress())
							&& !usermaster.getUserEmailAddress().equals(userInputDAO.getUserEmailAddress())) {
						Optional<UserMaster> userEmailOP = userMasterRepository
								.findByUserEmailAddress(userInputDAO.getUserEmailAddress());
						if (userEmailOP.isPresent()) {
							return CustomMessages.makeResponseModel(null, CustomMessages.USER_EMAIL_ADDRESS_EXIST,
									CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
						}
					}

					usermaster.setUserFullName(userInputDAO.getUserFullName());
					usermaster.setUserName(userInputDAO.getUserName());
					usermaster.setUserEmailAddress(userInputDAO.getUserEmailAddress());
					usermaster.setUserMobileNumber(userInputDAO.getUserMobileNumber());
					usermaster.setUserId(userInputDAO.getUserId());
					// usermaster
					if (userInputDAO.getUserPassword() != null) {
						usermaster.setUserPassword(encoder.encode(userInputDAO.getUserPassword()));
					}

					usermaster.setRoleId(roleOP.get());
					usermaster.setModifiedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));
					if (userInputDAO.getGeographicalArea() != null) {
						List<UserStateDAO> userStateDaoList = getuserStateList(userInputDAO.getGeographicalArea());
						GeographicalAreaOutputDAO geographicalAreaOutputDAO = new GeographicalAreaOutputDAO();
						geographicalAreaOutputDAO.setState(userStateDaoList);
						ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
						String json = ow.writeValueAsString(geographicalAreaOutputDAO);
						usermaster.setGeographicalArea(json);

						// For the supervisor
						if (roleOP.get().getCode().equalsIgnoreCase("CS_SUPERVISOR")) {
							Optional<DepartmentMaster> department = departmentRepository.findById(new Long(2));
							usermaster.setUserVillageLGDCode(
									userInputDAO.getGeographicalArea().get(0).getVillageLgdCode().intValue());
							usermaster.setDepartmentId(department.get());
							String userPrId = generalService.generatePrNumber("SUP", usermaster);
							usermaster.setUserPrId(userPrId);
						}
					}
					// password change start
//					String randomPassword = CommonUtil.GeneratePassword(8);
					usermaster.setUserPasswordText("Admin@123");
					usermaster.setUserPassword(encoder.encode("Admin@123"));
					usermaster.setIsPasswordChanged(false);
					usermaster.setLastPasswordChangedDate(new Date());

					Gson gson = new Gson();
					JsonArray passwordHistory = new JsonArray();
					if (usermaster.getPasswordHistory() != null) {
						passwordHistory = gson.fromJson(usermaster.getPasswordHistory().toString(), JsonArray.class);
					}
					passwordHistory.add(usermaster.getUserPassword());
					if (passwordHistory.size() > 3) {
						passwordHistory.remove(0);
					}
					usermaster.setPasswordHistory(passwordHistory.toString());
					// password change end
					UserMaster savedUser = userMasterRepository.save(usermaster);
					if (savedUser.getUserMobileNumber() != null && savedUser.getUserEmailAddress() != null) {

						sendMobileMessage(savedUser);
						sendEmailMessage(savedUser);
					}
					Runnable r = () -> {
						removeOldVillageAddNew(savedUser, userInputDAO, httpServletRequest);
					};
					Thread t = new Thread(r);
					t.start();
					return CustomMessages.makeResponseModel(null, CustomMessages.USER_UPDATED_SUCCESS,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

				} else {
					return CustomMessages.makeResponseModel(null, CustomMessages.USER_ID_NOT_FOUND,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}
			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.ROLE_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

		} catch (

				Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * Sends a mobile message to the user.
	 *
	 * @param userMaster The user information.
	 */
	private void sendMobileMessage(UserMaster userMaster) {

		MessageCredentialMaster messageCredentialMaster = messageCredentialRepository
				.findByMessageCredentialType("MOBILE").get();
		// findByTemplateId
//		MessageConfigurationMaster messageConfiguartionMaster = messageConfigurationRepository
//				.findByTemplateId(Constants.USERID_SMS_TEMPLATE_ID).get();

		MessageConfigurationMaster messageConfiguartionMaster = messageConfigurationRepository
				.findByTemplateId(Constants.NEW_USERID_SMS_TEMPLATE_ID).get();

		
		// DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy
		// HH:mm:ss");

		String template = messageConfiguartionMaster.getTemplate();
		String test = template.replace("{$1}", userMaster.getUserName());
		String test1 = test.replace("{$2}", userMaster.getUserPasswordText());
//		String sendOTPurl = messageCredentialMaster.getHost() + "authkey=" + messageCredentialMaster.getPassword()
//		+ "&mobiles=" + userMaster.getUserMobileNumber() + "&message=" + test1 + "&sender="
//		+ messageCredentialMaster.getUserName() + "&route=" + messageCredentialMaster.getRoute() + "&unicode="
//		+ messageCredentialMaster.getUniCode();
//
//		String response = new RestTemplate().getForObject(sendOTPurl, String.class);
		messageConfigurationService.sendOTP(messageCredentialMaster.getHost(), messageCredentialMaster.getUserName(), messageCredentialMaster.getPassword(), userMaster.getUserMobileNumber(), test1);
//		System.out.print(response);
	}

	/**
	 * Sends an email message to the user.
	 *
	 * @param userMaster The user information.
	 */
	public void sendEmailMessage(UserMaster userMaster) {
		try {
			// Get credential for username
			Optional<MessageCredentialMaster> credentialDetails = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);

			// Get configuration details
//			Optional<MessageConfigurationMaster> configuraitonDetails = messageConfigurationRepository
//					.findByTemplateType(Constants.EMAIL_PASSWORD_TEMPLATE);
			
			Optional<MessageConfigurationMaster> configuraitonDetails = messageConfigurationRepository
					.findByTemplateType(Constants.NEW_EMAIL_PASSWORD_TEMPLATE);

			// Initialize velocity engine
			VelocityEngine velocityEngine = initializeVelocity();
			
			String template = configuraitonDetails.get().getTemplate();
			String test = template.replace("{$1}", userMaster.getUserName());
			String test1 = test.replace("{$2}", userMaster.getUserPasswordText());

			// Add template to repository
			StringResourceRepository repository = StringResourceLoader.getRepository();
			repository.putStringResource("NEW_EMAIL_PASSWORD_TEMPLATE", test1);

			// Set parameters
			VelocityContext context = new VelocityContext();
			context.put("userName", userMaster.getUserFullName());
			context.put("PASSWORD", userMaster.getUserPasswordText());

			// Process the template
			StringWriter writer = new StringWriter();
			velocityEngine.getTemplate("NEW_EMAIL_PASSWORD_TEMPLATE").merge(context, writer);

			String[] emailTo = new String[] { userMaster.getUserEmailAddress() };
			String[] emailcc = new String[] {};
			if (configuraitonDetails.get().getCcList() != null) {
				emailcc = configuraitonDetails.get().getCcList().trim().split(",");
			}

			Mail mail = new Mail();
			mail.setMailFrom(credentialDetails.get().getUserName());
			mail.setMailTo(emailTo);
			mail.setMailCc(emailcc);
			mail.setMailSubject(configuraitonDetails.get().getEmailSubject());
			mail.setMailContent(writer.toString());
			try {
				new NotificationThread(mail, mailSender).start();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Initializes the Velocity engine for processing email templates.
	 *
	 * @return The initialized VelocityEngine.
	 */
	private VelocityEngine initializeVelocity() {
		// Initialize the engine
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "string");
		velocityEngine.setProperty("resource.loader.string.class", StringResourceLoader.class.getName());
		velocityEngine.init();
		return velocityEngine;
	}

	/**
	 * @param savedUser
	 * @param userInputDAO
	 * @param httpServletRequest
	 */
	public void removeOldVillageAddNew(UserMaster savedUser, UserInputDAO userInputDAO,
			HttpServletRequest httpServletRequest) {
		if (userInputDAO.getVillageLgdCodeList() != null && userInputDAO.getVillageLgdCodeList().size() > 0) {
			userVillageMappingRepository.deleteVillageById(savedUser.getUserId());
			List<VillageLgdMaster> vList = villageLgdMasterRepository
					.findByVillageLgdCodeIn(userInputDAO.getVillageLgdCodeList());

			if (vList != null && vList.size() > 0) {
				List<UserVillageMapping> finalDataList = new ArrayList<>();

				vList.forEach(action -> {

					UserVillageMapping userVillageMapping = new UserVillageMapping();
					userVillageMapping.setUserMaster(savedUser);
					userVillageMapping.setVillageLgdMaster(action);
					userVillageMapping.setIsActive(true);
					userVillageMapping.setIsDeleted(false);
					userVillageMapping.setCreatedBy(getUserId(httpServletRequest));
					userVillageMapping.setModifiedBy(getUserId(httpServletRequest));
					finalDataList.add(userVillageMapping);

				});
				userVillageMappingRepository.saveAll(finalDataList);
			}

		}

	}

	/**
	 * Retrieves a list of all active users.
	 *
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response containing the list of active users.
	 */
	public ResponseModel getAllActiveUserList(HttpServletRequest httpServletRequest) {
		ResponseModel responseModel = null;
		try {

			List<UserMaster> userList = userMasterRepository
					.findByIsDeletedAndRoleId_IsDefaultOrderByCreatedOnDesc(Boolean.FALSE, Boolean.FALSE);

			userList.forEach(action -> {
				GeographicalAreaOutputDAO geographicalAreaOutputDAO = new Gson().fromJson(action.getGeographicalArea(),
						GeographicalAreaOutputDAO.class);

				action.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);

			});

			return CustomMessages.makeResponseModel(userList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (

				Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * Retrieves a user by their ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param id The ID of the user to retrieve.
	 * @return The ResponseModel object representing the response containing the user.
	 */
	public ResponseModel getUserById(HttpServletRequest httpServletRequest, Long id) {
		ResponseModel responseModel = null;
		try {

			Optional<UserMaster> usop = userMasterRepository.findByIsDeletedAndUserId(Boolean.FALSE, id);
			UserMaster userMaster = null;
			if (usop.isPresent()) {
				userMaster = usop.get();

				GeographicalAreaOutputDAO geographicalAreaOutputDAO = new Gson()
						.fromJson(userMaster.getGeographicalArea(), GeographicalAreaOutputDAO.class);

				userMaster.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);

			}

			return CustomMessages.makeResponseModel(userMaster, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (

				Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * @param paginationDao
	 * @return
	 */
	Page<UserMaster> getUserPageList(PaginationDao paginationDao) {
		Pageable pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
				Sort.by(paginationDao.getSortField()).descending());

		if (paginationDao.getSortOrder().equals("asc")) {
			pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
					Sort.by(paginationDao.getSortField()).ascending());
		}

//		Page<UserMaster> list = userMasterRepository
//				.findByIsDeletedAndRoleId_IsDefaultOrderByCreatedOnDesc(Boolean.FALSE, Boolean.FALSE, pageable);
		Page<UserMaster> list = userMasterRepository
				.findByIsDeletedAndRoleId_IsDefaultAndCreatedByOrderByCreatedOnDesc(Boolean.FALSE, Boolean.FALSE,paginationDao.getUserId().toString(), pageable);
		if (paginationDao.getUserId() != null) {
			if (paginationDao.getSearch() != null && !paginationDao.getSearch().equals("")) {
		
				if(paginationDao.getRoleName() != "") {
					list = userMasterRepository.
							findByRoleId_RoleNameAndIsDeletedAndRoleId_IsDefaultAndCreatedByAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrderByCreatedOnDesc(
										paginationDao.getRoleName(),Boolean.FALSE, Boolean.FALSE, paginationDao.getUserId() + "", paginationDao.getSearch(),paginationDao.getSearch(),
											paginationDao.getSearch(),paginationDao.getSearch(),pageable);
					
					if(paginationDao.getBoundaryType() != null){
						list = userMasterRepository.findByRoleId_RoleNameAndRolePatternMappingId_TerritoryLevelAndIsDeletedAndRoleId_IsDefaultAndCreatedByAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrderByCreatedOnDesc(paginationDao.getRoleName(),paginationDao.getBoundaryType(),Boolean.FALSE, Boolean.FALSE, paginationDao.getUserId() + "", paginationDao.getSearch(),paginationDao.getSearch(),
						paginationDao.getSearch(),paginationDao.getSearch(),pageable);
					}
				}else {
					list = userMasterRepository.
							findByIsDeletedAndRoleId_IsDefaultAndCreatedByAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrRoleId_RoleNameIgnoreCaseContainsOrderByCreatedOnDescs(
							 				Boolean.FALSE, Boolean.FALSE, paginationDao.getUserId() + "", paginationDao.getSearch(),paginationDao.getSearch(),
							 				paginationDao.getSearch(),paginationDao.getSearch(),paginationDao.getSearch(),pageable);
				}
				
			} else {
				list = userMasterRepository.findByIsDeletedAndRoleId_IsDefaultAndCreatedBy(
						Boolean.FALSE, Boolean.FALSE, paginationDao.getUserId() + "", pageable);
			}
		}
		List<UserMasterReturnDAO> finalResultList = new ArrayList<>();
		if (list != null && list.getSize() > 0) {
			list.getContent().forEach(action -> {
				UserMasterReturnDAO userMasterReturnDAO = new UserMasterReturnDAO();
				userMasterReturnDAO.setUserId(action.getUserId());
				userMasterReturnDAO.setUserName(action.getUserName());
				userMasterReturnDAO.setUserToken(action.getUserToken());

				userMasterReturnDAO.setUserFirstName(action.getUserFirstName());
				userMasterReturnDAO.setUserLastName(action.getUserLastName());
				userMasterReturnDAO.setUserFullName(action.getUserFullName());

				userMasterReturnDAO.setUserCountryLGDCode(action.getUserCountryLGDCode());
				userMasterReturnDAO.setUserStateLGDCode(action.getUserStateLGDCode());
				userMasterReturnDAO.setUserDistrictLGDCode(action.getUserDistrictLGDCode());
				userMasterReturnDAO.setUserTalukaLGDCode(action.getUserTalukaLGDCode());
				userMasterReturnDAO.setUserVillageLGDCode(action.getUserVillageLGDCode());

				userMasterReturnDAO.setUserAadhaarHash(action.getUserAadhaarHash());
				userMasterReturnDAO.setUserAadhaarVaultRefCentral(action.getUserAadhaarVaultRefCentral());
				userMasterReturnDAO.setUserStatus(action.getUserStatus());

				userMasterReturnDAO.setUserRejectionReason(action.getUserRejectionReason());

				userMasterReturnDAO.setUserMobileNumber(action.getUserMobileNumber());
				userMasterReturnDAO.setUserAlternateMobileNumber(action.getUserAlternateMobileNumber());
				userMasterReturnDAO.setUserEmailAddress(action.getUserEmailAddress());

				userMasterReturnDAO.setRoleId(action.getRoleId().getRoleId());
				userMasterReturnDAO.setRoleName(action.getRoleId().getRoleName());

				userMasterReturnDAO.setAddressLine1(action.getAddressLine1());
				userMasterReturnDAO.setAddressLine2(action.getAddressLine2());
				userMasterReturnDAO.setUserDeviceToken(action.getUserDeviceToken());
				userMasterReturnDAO.setAppVersion(action.getAppVersion());

				userMasterReturnDAO.setUserDeviceName(action.getUserDeviceName());

				userMasterReturnDAO.setUserOs(action.getUserOs());

				userMasterReturnDAO.setUserLocalLangauge(action.getUserLocalLangauge());

				userMasterReturnDAO.setIsEmailVerified(action.getIsEmailVerified());
				userMasterReturnDAO.setIsMobileVerified(action.getIsMobileVerified());
				userMasterReturnDAO.setUserPrId(action.getUserPrId());

				userMasterReturnDAO.setPinCode(action.getPinCode());

				userMasterReturnDAO.setIsAadhaarVerified(action.getIsAadhaarVerified());
				userMasterReturnDAO.setFarmAssignCount(action.getFarmAssignCount());
				userMasterReturnDAO.setFarmPendingCount(action.getFarmPendingCount());

				userMasterReturnDAO.setIsAvailable(action.getIsAvailable());
				userMasterReturnDAO.setIsPasswordChanged(action.getIsPasswordChanged());
				userMasterReturnDAO.setLastPasswordChangedDate(action.getLastPasswordChangedDate());
				userMasterReturnDAO.setDefaultPage(action.getDefaultPage());

				userMasterReturnDAO.setPasswordHistory(action.getPasswordHistory());

				GeographicalAreaOutputDAO geographicalAreaOutputDAO = new Gson().fromJson(action.getGeographicalArea(),
						GeographicalAreaOutputDAO.class);
				userMasterReturnDAO.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);
				// finalResultList.add(userMasterReturnDAO);
				action.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);
			});
		}

		return list;

	}

	/**
	 * Retrieves all active users with pagination.
	 *
	 * @param httpServletRequest
	 * @param paginationDao The PaginationDao object containing pagination parameters.
	 * @return The ResponseModel object representing the response containing the active users.
	 */
	public ResponseModel getAllActiveUserByPagination(HttpServletRequest httpServletRequest,
			PaginationDao paginationDao) {
		ResponseModel responseModel = null;
		try {
			List<UserMaster> finalList = new ArrayList<>();
			long totalElement = 0;
			Long userId = Long.parseLong(getUserId(httpServletRequest));

			if (userId != null) {
				Optional<UserMaster> opUser = userMasterRepository.findByUserIdAndIsDeletedAndIsActive(userId, false,
						true);
				if (opUser.isPresent()) {
					UserMaster userMaster = opUser.get();
					if (userMaster.getRoleId().getRoleName().equals("Admin")
							|| userMaster.getRoleId().getRoleName().equals("Super Admin")) {
						Page<UserMaster> list = getUserPageList(paginationDao);
						finalList.addAll(list.getContent());
						totalElement = list.getTotalElements();
					} else {
						paginationDao.setUserId(userId);
						Page<UserMaster> list = getUserPageList(paginationDao);
						finalList.addAll(list.getContent());
						totalElement = list.getTotalElements();
					}
				}
			} else {

				Page<UserMaster> list = getUserPageList(paginationDao);
				finalList.addAll(list.getContent());
				totalElement = list.getTotalElements();

			}
			Map<String, Object> responseData = new HashMap();
			responseData.put("data", finalList);
			responseData.put("total", totalElement);
			responseData.put("page", paginationDao.getPage());
			responseData.put("limit", paginationDao.getLimit());
			responseData.put("sortField", paginationDao.getSortField());
			responseData.put("sortOrder", paginationDao.getSortOrder());

			return CustomMessages.makeResponseModel(responseData, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (

				Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}


	/**
	 * Updates the status of a user.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object representing the response after updating the status.
	 */
	public ResponseModel updateStatus(HttpServletRequest request, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		try {

			Optional<UserMaster> opUser = userMasterRepository.findByIsDeletedAndUserId(Boolean.FALSE,
					userInputDAO.getUserId());
			if (opUser.isPresent()) {
				UserMaster userMaster = opUser.get();

				userMaster.setIsActive(userInputDAO.getIsActive());
				userMaster.setIsDeleted(userInputDAO.getIsDeleted());
				userMaster.setDeviceToken(null);
				userMaster.setUserToken(null);
				userMasterRepository.save(userMaster);
			}
			responseModel = CustomMessages.makeResponseModel(null, CustomMessages.STATUS_UPDATED_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * Checks if an email already exists in the system.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object representing the response of the email existence check.
	 */
	public ResponseModel checkEmailExist(HttpServletRequest request, UserInputDAO userInputDAO) {
		try {
			Optional<UserMaster> userNameOP = userMasterRepository
					.findByUserEmailAddress(userInputDAO.getUserEmailAddress());
			if (userNameOP.isPresent()) {
				return CustomMessages.makeResponseModel(true, CustomMessages.USER_EMAIL_ADDRESS_EXIST,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);

			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(true, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Checks if a mobile number already exists in the system.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object representing the response of the mobile number existence check.
	 */
	public ResponseModel checkMobileExist(HttpServletRequest request, UserInputDAO userInputDAO) {
		try {
			Optional<UserMaster> userNameOP = userMasterRepository
					.findByUserMobileNumber(userInputDAO.getUserMobileNumber());
			if (userNameOP.isPresent()) {
				return CustomMessages.makeResponseModel(true, CustomMessages.USER_MOBILE_NAME_EXIST,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(true, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Checks if a government ID already exists in the system.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object representing the response of the government ID existence check.
	 */
	public ResponseModel checkGovIdExists(HttpServletRequest request, UserInputDAO userInputDAO) {
		try {
			Optional<UserMaster> userNameOP = userMasterRepository.findByGovernmentId(userInputDAO.getGovernmentId());
			if (userNameOP.isPresent()) {
				return CustomMessages.makeResponseModel(true, CustomMessages.GOV_ID_EXIST,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(true, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Checks if an Aadhaar number already exists in the system.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object representing the response of the Aadhaar existence check.
	 */
	public ResponseModel checkAadhaarExist(HttpServletRequest request, UserInputDAO userInputDAO) {
		try {
			System.out.println("userInputDAO.getUserAadhaarHash() "+userInputDAO.getUserAadhaarHash());
			List<UserMaster> userNameOP = userMasterRepository
					.findByUserAadhaarHash(userInputDAO.getUserAadhaarHash());
			System.out.println("userNameOP"+userNameOP);
			if (!userNameOP.isEmpty()) {
				return CustomMessages.makeResponseModel(true, CustomMessages.USER_AADHAAR_EXIST,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(true, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public ResponseModel checkBankAccountExist(HttpServletRequest request, UserInputDAO userInputDAO) {
		try {
			//System.out.println("userInputDAO.checkBankAccountExist() "+userInputDAO.ge());
			UserBankDetail bank = userBankDetailRepository.findByUserBankAccounNumber(userInputDAO.getBankAccountNo());
			System.out.println("bank "+bank);
//			if (bank.getUserBankId()!=null) {
//
//			}
			if (bank != null && bank.getUserBankDetailId() != null) {
				return CustomMessages.makeResponseModel(true, CustomMessages.ACCOUNT_DOES_NOT_EXISTS_3,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(true, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Retrieves the accessible states based on user input.
	 *
	 * @param httpServletRequest
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object representing the response of the accessible states retrieval.
	 */
	public ResponseModel getAccessibleStates(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		List<StateLgdMaster> stateList = new ArrayList<>();
		String geographicalArea = "";
		try {
			UserMaster user = userMasterRepository.findByUserId(userInputDAO.getUserId()).orElse(null);
			if (Objects.nonNull(user)) {
				GeographicalAreaOutputDAO geographicalAreaOutputDAO = new Gson().fromJson(user.getGeographicalArea(),
						GeographicalAreaOutputDAO.class);
				user.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);
				List<UserStateDAO> userStates = geographicalAreaOutputDAO.getState();
				List<Long> stateLGDCodes = userStates.stream().distinct().map(UserStateDAO::getStateLgdCode)
						.collect(Collectors.toList());
				stateList = stateLgdMasterRepository.findAllByStateLgdCodeIn(stateLGDCodes);
			}
			return CustomMessages.makeResponseModel(stateList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (

				Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}


	/**
	 * Retrieves the accessible districts based on user input.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object representing the response of the accessible districts retrieval.
	 */
	public ResponseModel getAccessibleDistrict(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;

		List<DistrictLgdMaster> accessibleDistricts = new ArrayList<>();
		try {
			UserMaster user = userMasterRepository.findByUserId(userInputDAO.getUserId()).orElse(null);
			if (Objects.nonNull(user)) {
				GeographicalAreaOutputDAO geographicalAreaOutputDAO = new Gson().fromJson(user.getGeographicalArea(),
						GeographicalAreaOutputDAO.class);
				user.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);
				List<UserStateDAO> userStates = geographicalAreaOutputDAO.getState();
				List<Long> matchedStateLgdCodes = new ArrayList<>();
				List<Long> stateLGDCodes = userStates.stream().distinct().map(UserStateDAO::getStateLgdCode)
						.collect(Collectors.toList());

				userInputDAO.getStateLGDCodeList().forEach(ele -> {
					stateLGDCodes.forEach(code -> {
						if (code.equals(ele)) {
							matchedStateLgdCodes.add(ele);
						}
					});
				});
				if (matchedStateLgdCodes.size() > 0) {
					List<Long> stateAccessibleDistrictCodes = new ArrayList<>();
					userStates.forEach(ele -> {
						if (matchedStateLgdCodes.contains(ele.getStateLgdCode())) {
							ele.getDistrict().forEach(district -> {
								stateAccessibleDistrictCodes.add(district.getDistrictLgdCode());
							});
						}
					});
					if (stateAccessibleDistrictCodes.size() > 0) {
						accessibleDistricts = districtLgdMasterRepository
								.findAllByDistrictLgdCode(stateAccessibleDistrictCodes);
					}
				}
			}
			return CustomMessages.makeResponseModel(accessibleDistricts, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Retrieves the accessible taluks based on user input.
	 *
	 * @param httpServletRequest
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object representing the response of the accessible taluks retrieval.
	 */
	public ResponseModel getAccessibleTaluk(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		List<SubDistrictLgdMaster> accessibleSubDistricts = new ArrayList<>();
		List<DistrictLgdMaster> accessibleDistricts = new ArrayList<>();
		try {
			UserMaster user = userMasterRepository.findByUserId(userInputDAO.getUserId()).orElse(null);
			if (Objects.nonNull(user)) {
				List<DistrictLgdMaster> stateDistricts = new ArrayList<>();
				List<Long> userAccessibleTalukCodes = new ArrayList<>();
				List<Long> matchedStateLgdCodes = new ArrayList<>();
				List<Long> matchedDistrictLgdCodes = new ArrayList<>();
				GeographicalAreaOutputDAO geographicalAreaOutputDAO = new Gson().fromJson(user.getGeographicalArea(),
						GeographicalAreaOutputDAO.class);
				user.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);

				List<UserStateDAO> userStates = geographicalAreaOutputDAO.getState();

				List<Long> stateLGDCodes = userStates.stream().distinct().map(UserStateDAO::getStateLgdCode)
						.collect(Collectors.toList());
				userInputDAO.getStateLGDCodeList().forEach(ele -> {
					stateLGDCodes.forEach(code -> {
						if (code.equals(ele) && userInputDAO.getStateLGDCodeList().contains(code)) {
							matchedStateLgdCodes.add(ele);
						}
					});
				});
				if (matchedStateLgdCodes.size() > 0) {
					stateDistricts.addAll(districtLgdMasterRepository.findAllByStateLgdCode(matchedStateLgdCodes));
					List<Long> stateAccessibleDistrictCodes = new ArrayList<>();
					userStates.forEach(ele -> {
						if (matchedStateLgdCodes.contains(ele.getStateLgdCode())) {
							ele.getDistrict().forEach(state -> {
								stateAccessibleDistrictCodes.add(state.getDistrictLgdCode());
							});
						}
					});
					if (stateAccessibleDistrictCodes.size() > 0) {
						List<Long> stateDistrictsCodes = stateDistricts.stream().distinct()
								.map(DistrictLgdMaster::getDistrictLgdCode).collect(Collectors.toList());
						stateDistrictsCodes.forEach(ele -> {
							stateAccessibleDistrictCodes.forEach(code -> {
								if (code.equals(ele) && userInputDAO.getDistrictLgdCodeList().contains(code)) {
									matchedDistrictLgdCodes.add(ele);
								}
							});
						});
						if (matchedDistrictLgdCodes.size() > 0) {
							accessibleDistricts = districtLgdMasterRepository
									.findAllByDistrictLgdCode(matchedDistrictLgdCodes);
							if (accessibleDistricts.size() > 0) {
								userStates.forEach(ele -> {
									if (matchedStateLgdCodes.contains(ele.getStateLgdCode())) {
										ele.getDistrict().forEach(district -> {
											if (matchedDistrictLgdCodes.contains(district.getDistrictLgdCode())) {
												district.getSubDistrict().forEach(subDistrict -> {
													userAccessibleTalukCodes.add(subDistrict.getSubDistrictLgdCode());
												});
											}
										});
									}
								});
								if (userAccessibleTalukCodes.size() > 0) {
									accessibleSubDistricts = subDistrictLgdMasterRepository
											.findByLgdCodes(userAccessibleTalukCodes);
								}
							}
						}
					}
				}
			}
			return CustomMessages.makeResponseModel(accessibleSubDistricts, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Retrieves the accessible villages based on user input.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object representing the response of the accessible villages retrieval.
	 */
	public ResponseModel getAccessibleVillage(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		List<DistrictLgdMaster> accessibleDistricts = new ArrayList<>();
		List<VillageLgdMaster> accessibleVillages = new ArrayList<>();
		try {
			UserMaster user = userMasterRepository.findByUserId(userInputDAO.getUserId()).orElse(null);
			if (Objects.nonNull(user)) {
				List<DistrictLgdMaster> stateDistricts = new ArrayList<>();
				List<Long> userAccessibleVillageCodes = new ArrayList<>();
				List<Long> matchedStateLgdCodes = new ArrayList<>();
				List<Long> matchedDistrictLgdCodes = new ArrayList<>();
				GeographicalAreaOutputDAO geographicalAreaOutputDAO = new Gson().fromJson(user.getGeographicalArea(),
						GeographicalAreaOutputDAO.class);
				user.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);

				List<UserStateDAO> userStates = geographicalAreaOutputDAO.getState();

				List<Long> stateLGDCodes = userStates.stream().distinct().map(UserStateDAO::getStateLgdCode)
						.collect(Collectors.toList());
				userInputDAO.getStateLGDCodeList().forEach(ele -> {
					stateLGDCodes.forEach(code -> {
						if (code.equals(ele) && userInputDAO.getStateLGDCodeList().contains(code)) {
							matchedStateLgdCodes.add(ele);
						}
					});
				});
				if (matchedStateLgdCodes.size() > 0) {
					stateDistricts.addAll(districtLgdMasterRepository.findAllByStateLgdCode(matchedStateLgdCodes));
					List<Long> stateAccessibleDistrictCodes = new ArrayList<>();
					userStates.forEach(ele -> {
						if (matchedStateLgdCodes.contains(ele.getStateLgdCode())) {
							ele.getDistrict().forEach(state -> {
								stateAccessibleDistrictCodes.add(state.getDistrictLgdCode());
							});
						}
					});
					if (stateAccessibleDistrictCodes.size() > 0) {
						List<Long> stateDistrictsCodes = stateDistricts.stream().distinct()
								.map(DistrictLgdMaster::getDistrictLgdCode).collect(Collectors.toList());
						stateDistrictsCodes.forEach(ele -> {
							stateAccessibleDistrictCodes.forEach(code -> {
								if (code.equals(ele) && userInputDAO.getDistrictLgdCodeList().contains(code)) {
									matchedDistrictLgdCodes.add(ele);
								}
							});
						});
						if (matchedDistrictLgdCodes.size() > 0) {
							accessibleDistricts = districtLgdMasterRepository
									.findAllByDistrictLgdCode(matchedDistrictLgdCodes);
							if (accessibleDistricts.size() > 0) {
								userStates.forEach(ele -> {
									if (matchedStateLgdCodes.contains(ele.getStateLgdCode())) {
										ele.getDistrict().forEach(district -> {
											if (matchedDistrictLgdCodes.contains(district.getDistrictLgdCode())) {
												district.getSubDistrict().forEach(subDistrict -> {
													if (userInputDAO.getSubDistrictLgdCodeList()
															.contains(subDistrict.getSubDistrictLgdCode())) {
														userAccessibleVillageCodes.addAll(subDistrict.getVillage()
																.stream().map(UserVillageDAO::getVillageLgdCode)
																.collect(Collectors.toList()));
													}
												});
											}
										});
									}
								});
								if (userAccessibleVillageCodes.size() > 0) {
									accessibleVillages = villageLgdMasterRepository
											.findByVillageLgdCodeIn(userAccessibleVillageCodes);
								}
							}
						}
					}
				}
			}
			return CustomMessages.makeResponseModel(accessibleVillages, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * @param password
	 * @param userName
	 * @return
	 */
	public static String isValidPassword(String password, String userName) {
		String validationMsg = "";
		if (password.length() > 20 || password.length() < 8) {
			validationMsg = "Password must be less than 20 and more than 8 characters in length.";
		}
		String upperCaseChars = "(.*[A-Z].*)";
		if (!password.matches(upperCaseChars)) {
			validationMsg = "Password must have atleast one uppercase character";
		}
		String lowerCaseChars = "(.*[a-z].*)";
		if (!password.matches(lowerCaseChars)) {
			validationMsg = "Password must have atleast one lowercase character";
		}
		String numbers = "(.*[0-9].*)";
		if (!password.matches(numbers)) {
			validationMsg = "Password must have atleast one number";
		}
		String specialChars = "(.*[@,#,$,%].*$)";
		if (!password.matches(specialChars)) {
			validationMsg = "Password must have atleast one special character among @#$%";
		}
		if (password.contains(userName)) {
			validationMsg = "Password should not contain user name.";
		}
		return validationMsg;
	}

	/**
	 * @param user
	 * @return
	 */
	public UserOutputDAO getUserBoundaryDetail(UserOutputDAO user) {
		if (Objects.nonNull(user.getUserStateLGDCode())) {
			StateLgdMaster state = stateService.getStateByLGDCode(Long.valueOf(user.getUserStateLGDCode().longValue()));
			DistrictLgdMaster district = districtService
					.getDistrictByLGDCode(user.getUserDistrictLGDCode().longValue());
			SubDistrictLgdMaster subDistrict = subDistrictService
					.getSubDistrictByLGDCode(user.getUserTalukaLGDCode().longValue());
			VillageLgdMaster village = villageService.getVillageByLGDCode(user.getUserVillageLGDCode().longValue());
			user.setStateName(Objects.nonNull(state) ? state.getStateName() : "");
			user.setDistrictName(Objects.nonNull(district) ? district.getDistrictName() : "");
			user.setStateName(Objects.nonNull(subDistrict) ? subDistrict.getSubDistrictName() : "");
			user.setStateName(Objects.nonNull(village) ? village.getVillageName() : "");
		}

		return user;
	}

	/**
	 * @param user
	 * @return
	 */
	public UserOutputDAO getUserOutFromUserMaster(UserMaster user) {
		UserOutputDAO userOutputDAO = new UserOutputDAO();

		BeanUtils.copyProperties(user, userOutputDAO);
		getUserBoundaryDetail(userOutputDAO);
		if (Objects.nonNull(user.getDepartmentId())) {
			DepartmentMaster department = new DepartmentMaster(user.getDepartmentId().getDepartmentId(),
					user.getDepartmentId().getDepartmentName(), user.getDepartmentId().getDepartmentType());
			user.setDepartmentId(department);
		}
		user.setUserStatus(statusRepository.findByIsDeletedFalseAndStatusCode(user.getUserStatus()).getStatusCode());
		user.setIsAvailable(user.getIsAvailable());
		if (Objects.nonNull(user.getDesignationId())) {
			DesignationMaster designation = new DesignationMaster(user.getDesignationId().getDesignationId(),
					user.getDesignationId().getDesignationName());
			user.setDesignationId(designation);
		}
		UserBankDetail bankDetail = userBankDetailRepository.findByUserId_UserId(user.getUserId());
		if (Objects.nonNull(bankDetail)) {

			userOutputDAO.setBankId(bankDetail.getBankId().getBankId());
			userOutputDAO.setUserBankDetail(new UserBankDetail(bankDetail.getUserBankDetailId(),
					bankDetail.getUserBankName(), bankDetail.getUserBranchCode(), bankDetail.getUserIfscCode(),
					bankDetail.getUserBankAccountNumber(), bankDetail.getBankId().getBankId()));
		}
		userOutputDAO.setRoleId(user.getRoleId().getRoleId());
		userOutputDAO.setRoleName(user.getRoleId().getRoleName());
		return userOutputDAO;
	}

	/**
	 * Changes the password for a user.
	 *
	 * @param resetPassword The {@code ResetPasswordDAO} object containing the reset password data.
	 * @return The ResponseModel object representing the response of the password change operation.
	 */
	public ResponseModel changePassword(ResetPasswordDAO resetPassword) {
		ResponseModel responseModel = null;
		try {
			Optional<UserMaster> user = userMasterRepository.findByUserId(resetPassword.getUserId());
			System.out.println(" user "+user);
			System.out.println(resetPassword.getExistingPassword());
			if (encoder.matches(resetPassword.getExistingPassword(), user.get().getUserPassword())) {
				if (resetPassword.getNewPassword().equals(resetPassword.getConfirmPassword())) {

					if (checkLastThreePassword(user.get(), resetPassword.getNewPassword())) {
						return CustomMessages.makeResponseModel(true,
								"New password should not be same as old 3 password.", CustomMessages.GET_DATA_ERROR,
								CustomMessages.INVALID_INPUT);
					} else {

						String validationMsg = isValidPassword(resetPassword.getNewPassword(),
								user.get().getUserName());
						if (validationMsg.isEmpty()) {
							user.get().setUserPassword(encoder.encode(resetPassword.getNewPassword()));
							user.get().setModifiedBy(user.get().getUserId().toString());
							user.get().setIsPasswordChanged(true);
							user.get().setLastPasswordChangedDate(new Date());
							try {
								Gson gson = new Gson();
								JsonArray passwordHistory = new JsonArray();
								if (user.get().getPasswordHistory() != null) {
									passwordHistory = gson.fromJson(user.get().getPasswordHistory().toString(),
											JsonArray.class);
								}
								passwordHistory.add(user.get().getUserPassword());
								if (passwordHistory.size() > 3) {
									passwordHistory.remove(0);
								}
								user.get().setPasswordHistory(passwordHistory.toString());
							} catch (Exception e) {
								return CustomMessages.makeResponseModel(true, "Issue while saving passwordhistory",
										CustomMessages.GET_DATA_ERROR, CustomMessages.INVALID_INPUT);
							}

							userMasterRepository.save(user.get());
							Runnable r = () -> {
								messageConfigService.sendPasswordChangedEmail(user.get().getUserName(),
										user.get().getUserEmailAddress(), "changed");
							};
							Thread t = new Thread(r);
							t.start();

							return CustomMessages.makeResponseModel(true,
									"Password changed successfully. Please login with new password.",
									CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
						} else {
							return CustomMessages.makeResponseModel(true, validationMsg, CustomMessages.GET_DATA_ERROR,
									CustomMessages.INVALID_INPUT);
						}
					}

				} else {
					return CustomMessages.makeResponseModel(true,
							"New password and Confirmed password are not matched.", CustomMessages.GET_DATA_ERROR,
							CustomMessages.INVALID_INPUT);
				}
			} else {
				return CustomMessages.makeResponseModel(true, "Old password is incorrect.",
						CustomMessages.GET_DATA_ERROR, CustomMessages.INVALID_INPUT);
			}
		} catch (Exception e) {

			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), "Error while changing password.",
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Updates the mobile contact details of a user.
	 *
	 * @param userId The ID of the user.
	 * @param mobile The new mobile number.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response of the mobile contact update operation.
	 */
	public ResponseModel userContactDetailsUpdate(Long userId, String mobile, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			Optional<UserMaster> existingUser = userMasterRepository.findByUserId(userId);
			if (existingUser.isPresent()) {
				existingUser.get().setUserMobileNumber(mobile);
				userMasterRepository.save(existingUser.get());
				return CustomMessages.makeResponseModel(true, "User mobile number changed successfully.",
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(true, "User not found.", CustomMessages.GET_DATA_ERROR,
						CustomMessages.INVALID_INPUT);
			}

		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(),
					"Error while updating user contact details", CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Updates the email contact details of a user.
	 *
	 * @param userId The ID of the user.
	 * @param email The new email address.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response of the email contact update operation.
	 */
	public ResponseModel userEmailDetailsUpdate(Long userId, String email, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			Optional<UserMaster> existingUser = userMasterRepository.findByUserId(userId);
			if (existingUser.isPresent()) {
				existingUser.get().setUserEmailAddress(email);
				userMasterRepository.save(existingUser.get());
				return CustomMessages.makeResponseModel(true, "User email address changed successfully.",
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(true, "User not found.", CustomMessages.GET_DATA_ERROR,
						CustomMessages.INVALID_INPUT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(),
					"Error while updating user email address details", CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Sets the default page for a user.
	 *
	 * @param userId The ID of the user.
	 * @param defaultPageUrl
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response of the default page setting operation.
	 */
	public ResponseModel setDefaultPage(Long userId, String defaultPageUrl, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			Optional<UserMaster> existingUser = userMasterRepository.findByUserId(userId);
			if (existingUser.isPresent()) {
				existingUser.get().setDefaultPage(defaultPageUrl);
				userMasterRepository.save(existingUser.get());
				return CustomMessages.makeResponseModel(true, "Current page as a default page added successfully.",
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(true, "User not found.", CustomMessages.GET_DATA_ERROR,
						CustomMessages.INVALID_INPUT);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), "Error while setting default page",
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Checks if an email already exists for a user identified by their user ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user ID and email to check.
	 * @return The ResponseModel object representing the result of the email existence check.
	 */
	public ResponseModel checkEmailExistByUserId(HttpServletRequest request, UserInputDAO userInputDAO) {
		try {
			Optional<UserMaster> userNameOP = userMasterRepository
					.findByUserEmailAddressByUserId(userInputDAO.getUserEmailAddress(), userInputDAO.getUserId());
			if (userNameOP.isPresent()) {
				return CustomMessages.makeResponseModel(true, CustomMessages.USER_NAME_EXIST,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);

			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(true, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Checks if a mobile number already exists for a user identified by their user ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user ID and mobile number to check.
	 * @return The ResponseModel object representing the result of the mobile number existence check.
	 */
	public ResponseModel checkMobileExistByUserId(HttpServletRequest request, UserInputDAO userInputDAO) {
		try {
			Optional<UserMaster> userNameOP = userMasterRepository
					.findByUserMobileNumberByUserId(userInputDAO.getUserMobileNumber(), userInputDAO.getUserId());
			if (userNameOP.isPresent()) {
				return CustomMessages.makeResponseModel(true, CustomMessages.USER_MOBILE_NAME_EXIST,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(true, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Checks if a government ID already exists for a user identified by their user ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user ID and government ID to check.
	 * @return The ResponseModel object representing the result of the government ID existence check.
	 */
	public ResponseModel checkGovIdExistByUserId(HttpServletRequest request, UserInputDAO userInputDAO) {
		try {
			Optional<UserMaster> userNameOP = userMasterRepository
					.findByGovernmentIdByUserId(userInputDAO.getGovernmentId(), userInputDAO.getUserId());
			if (userNameOP.isPresent()) {
				return CustomMessages.makeResponseModel(true, CustomMessages.USER_MOBILE_NAME_EXIST,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(true, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Checks if an Aadhaar number already exists for a user identified by their user ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user ID and Aadhaar number to check.
	 * @return The ResponseModel object representing the result of the Aadhaar number existence check.
	 */
	public ResponseModel checkAadhaarExistByUserId(HttpServletRequest request, UserInputDAO userInputDAO) {
		try {
			Optional<UserMaster> userNameOP = userMasterRepository
					.findByGovernmentIdByUserId(userInputDAO.getGovernmentId(), userInputDAO.getUserId());
			if (userNameOP.isPresent()) {
				return CustomMessages.makeResponseModel(true, CustomMessages.USER_AADHAAR_EXIST,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(true, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * @param user
	 * @param newPassword
	 * @return
	 */
	public Boolean checkLastThreePassword(UserMaster user, String newPassword) {
		Boolean match = false;
		Gson gson = new Gson();
		if (user.getPasswordHistory() != null) {
			JsonArray passwordHistory = gson.fromJson(user.getPasswordHistory().toString(), JsonArray.class);
			for (JsonElement password : passwordHistory) {
				if (encoder.matches(newPassword, password.getAsString())) {
					match = true;
					break;
				}
			}
		}
		return match;
	}

	/**
	 * Sends an OTP (One-Time Password) to the user.
	 *
	 * @param otpRequestDAO The {@code OTPRequestDAO} object containing the OTP request data.
	 * @param httpServletRequest
	 * @return The ResponseModel object representing the response of the OTP generation operation.
	 */
	public ResponseModel generateOTP(OTPRequestDAO otpRequestDAO, HttpServletRequest httpServletRequest) {
		String verificationSource = otpRequestDAO.getVerificationSource();
		OTPRegistration existingRegistration = otpRepository
				.findByVerificationSource(otpRequestDAO.getVerificationSource());
		OTPRegistration otpRegistration = new OTPRegistration();
		if (existingRegistration != null) {
			if (existingRegistration.getOtpResendCount() >= 3) {
				Date cDate = new Date();

				long difference_In_Time = (existingRegistration.getCreatedOn().getTime()) - (cDate.getTime());

				long diffMinutes = difference_In_Time / (60 * 1000) % 60;

				diffMinutes = Math.abs(diffMinutes);

				if (diffMinutes < 10) {
					return new ResponseModel(true,
							"You've exceeded the maximum number of attempts to resend OTP, please try after 10 mins.",
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
				} else {
					otpRegistration.setOtpResendCount(1);
				}
			} else {
				otpRegistration.setOtpResendCount(existingRegistration.getOtpResendCount() + 1);
			}
		} else {
			otpRegistration.setOtpResendCount(1);
		}
		otpRepository.deleteByVerificationSource(verificationSource);

		String language = httpServletRequest.getHeader("language");
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("Content-Type", "application/json");
		headers.setAll(map);

		String otp = "";
		Random random = new Random();
		if (isTesting) {
			otp = "123456";
		} else {
			otp = String.format("%06d", random.nextInt(10000));
		}

		otpRegistration.setOtp(otp);
		otpRegistration.setVerificationSource(otpRequestDAO.getVerificationSource());
		otpRegistration.setVerificationType(otpRequestDAO.getVerificationType());
		otpRegistration.setCreatedOn(new Timestamp(new Date().getTime()));
		otpRegistration.setCreatedIp(httpServletRequest.getRemoteAddr());
		otpRegistration.setIsActive(Boolean.TRUE);
		otpRegistration.setIsDeleted(Boolean.FALSE);
		otpRegistration = otpRepository.save(otpRegistration);
		messageConfigurationService.sendUserUpdateOTPMobilEmail(otpRegistration);
		return new ResponseModel(otp, "OTP Generate Successfully", CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
	}

	/**
	 * Logs out the user by deleting their token.
	 *
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the result of the logout operation.
	 */
	public ResponseModel deleteToken(HttpServletRequest request) {
		try {
			String authToken = request.getHeader("Authorization").toString();
			String[] chunks = authToken.split(" ");
			LoginLogoutActivityLog existingLog = logReposiptory.findByAuthToken(chunks[1].toString());
			if (existingLog != null) {
				existingLog.setLogin(false);
				existingLog.setLogoutDate(new Date());

				if (existingLog.getLogInDate() != null) {
					LocalDateTime oldDateTime = LocalDateTime
							.from(Instant.ofEpochMilli(existingLog.getLogInDate().getTime())
									.atZone(ZoneId.systemDefault()).toLocalDateTime());

					LocalDateTime toDateTime = LocalDateTime.now();
					long minutes = oldDateTime.until(toDateTime, ChronoUnit.MINUTES);
					existingLog.setSessionDuration(minutes);
				}
				logReposiptory.save(existingLog);
			}
			return CustomMessages.makeResponseModel(false, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public Claims getTokenKeyFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
	}

	/**
	 * Retrieves a list of all active users in plain format.
	 *
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object containing the list of active users.
	 */
	public ResponseModel getAllActiveUserPlainList(HttpServletRequest httpServletRequest) {
		ResponseModel responseModel = null;
		try {

			List<Object[]> userList = userMasterRepository.getAllActiveUserPlainList();
			List<UserMasterPlainDAO> plainDAOList = new ArrayList<>();
			for (Object[] user : userList) {
				UserMasterPlainDAO userPlainObj = new UserMasterPlainDAO();

				userPlainObj.setUserId(Long.parseLong(user[0].toString()));
				userPlainObj.setUserName(user[1].toString());
				plainDAOList.add(userPlainObj);
			}
			return CustomMessages.makeResponseModel(plainDAOList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (

				Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * @param request
	 * @return
	 */
	public String getUserId(HttpServletRequest request) {

		String requestTokenHeader = request.getHeader("Authorization");
		String userId = null;

		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {

			String jwtToken = requestTokenHeader.substring(7);

			try {

				userId = jwtTokenUtil.getUsernameFromToken(jwtToken);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return userId;
				// logger.info("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				e.printStackTrace();
				return userId;
				// logger.info("JWT Token has expired");
			} catch (Exception e) {
				e.printStackTrace();
				return userId;
				// logger.info("JWT Token has expired" + e);
			}

		} else {

			// logger.warning("JWT Token does not begin with Bearer String");
		}
		return userId;

	}

	/**
	 * Creates a state admin user.
	 *
	 * @param httpServletRequest
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object indicating the status of the operation.
	 */
	public ResponseModel createStateAdminUser(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		try {
			if (userInputDAO.getStateCodeToBeAssign() == null) {
				return CustomMessages.makeResponseModel(true, "Please enter state lgd code.",
						CustomMessages.NO_DATA_FOUND, CustomMessages.INVALID_INPUT);
			}

			List<StateLgdMaster> stateList = stateLgdMasterRepository.findAll();
			//					findByStateLgdCode(userInputDAO.getStateCodeToBeAssign());

			//			if (state == null) {
			//				return CustomMessages.makeResponseModel(true, "Please enter valid state lgd code.",
			//						CustomMessages.NO_DATA_FOUND, CustomMessages.INVALID_INPUT);
			//			}

			//			stateList.stream().forEach(state ->{
			RoleMaster role = roleMasterRepository.findByCodeAndIsDefaultTrue("STATEADMIN");

			//				List<Long> menuIds = new ArrayList<>();
			//				menuIds.add((long) 6);
			//				menuIds.add((long) 19);
			//
			//				//Only for Configuration-> crops menu
			//				Set<MenuMaster> menuList = menuMasterRepository.findByIsActiveTrueAndIsDeletedFalseAndMenuIdIn(menuIds);

			List<MenuMaster> menuList = menuMasterRepository // for all menu
					.findByIsActiveTrueAndIsDeletedFalseAndMenuType(MenuTypeEnum.valueOf("WEB"));
			Set<MenuMaster> setMenuList = new HashSet<MenuMaster>(menuList);

			if (setMenuList != null && setMenuList.size() > 0) {
				role.setMenu(setMenuList);
			}
			// role.setModifiedBy("1");
			// role.setCreatedBy("1");
			roleMasterRepository.save(role);

			if (role != null) {

				userInputDAO.setUserName("SADM_1_S_"+userInputDAO.getStateCodeToBeAssign()); //user name
				userInputDAO.setUserEmailAddress("stateAdmin@gmail.com");

				Optional<UserMaster> userNameOP = userMasterRepository.findByUserName(userInputDAO.getUserName());
				if (!userNameOP.isPresent()) {
					UserMaster userMaster = CustomMessages.returnUserMasterFromUserInputDAO(userInputDAO);
					userMaster.setUserPassword(encoder.encode(userInputDAO.getUserPassword()));
					try {
						Gson gson = new Gson();
						JsonArray passwordHistory = new JsonArray();
						if (userMaster.getPasswordHistory() != null) {
							passwordHistory = gson.fromJson(userMaster.getPasswordHistory().toString(), JsonArray.class);
						}
						passwordHistory.add(userMaster.getUserPassword());
						if (passwordHistory.size() > 3) {
							passwordHistory.remove(0);
						}
						userMaster.setPasswordHistory(passwordHistory.toString());
					} catch (Exception e) {
						//							return CustomMessages.makeResponseModel(true, "Issue while saving passwordhistory",
						//									CustomMessages.GET_DATA_ERROR, CustomMessages.INVALID_INPUT);
					}
					userMaster.setRoleId(role);
					userMaster.setIsActive(true);
					userMaster.setIsDeleted(false);
					userMaster.setIsPasswordChanged(false);
					userMaster.setUserStatus(StatusEnum.APPROVED.getValue());
					userMaster.setUserStateLGDCode(userInputDAO.getStateCodeToBeAssign().intValue());

					userMaster.setCreatedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));
					userMaster.setModifiedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));

					UserMaster savedUser = userMasterRepository.save(userMaster);
					System.out.println(savedUser.getUserId());

					new Thread() {
						@Override
						public void run() {
							try {
								List<UserVillageMapping> userVillageList = new ArrayList<>();
								//						List<Long> villageCodes = getAccessibleVillageUM(roleInputDAO.getUserId());
								//						List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
								//								.findByVillageLgdCodeIn(villageCodes);
								//						System.out.println("Village count :" + villageLgdMaster.size());

								List<DistrictLgdMaster> districtCode =
										districtLgdMasterRepository.findByStateLgdCode_StateLgdCode(userInputDAO.getStateCodeToBeAssign());
								districtCode.forEach(district->{
									List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
											.findByDistrictLgdCode_DistrictLgdCode(district.getDistrictLgdCode());

									villageLgdMaster.forEach(village -> {
										UserVillageMapping userVillageMapping = new UserVillageMapping();

										userVillageMapping.setUserMaster(savedUser);
										userVillageMapping.setVillageLgdMaster(village);
										userVillageMapping.setCreatedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));
										userVillageMapping.setModifiedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));
										userVillageMapping.setIsActive(true);
										userVillageMapping.setIsDeleted(false);
										//									userVillageList.add(userVillageMapping);
										userVillageMappingRepository.save(userVillageMapping);
									});
								});

								System.out.println("Inside thread  count:" + userVillageList.size());
								userVillageMappingRepository.saveAll(userVillageList);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();

					return CustomMessages.makeResponseModel(savedUser, CustomMessages.GET_RECORD,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				}
			}
			//			});
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);

		}
		return responseModel;
	}

	/**
	 * Retrieves the accessible boundary by a list of user inputs.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object containing the accessible boundary information.
	 */
	public ResponseModel getAccessibleBoundaryByList(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = CustomMessages.makeResponseModel(null, CustomMessages.GET_RECORD,
				CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		List<StateLgdMaster> stateList = new ArrayList<>();
		List<DistrictLgdMaster> accessibleDistricts = new ArrayList<>();
		List<SubDistrictLgdMaster> accessibleSubDistricts = new ArrayList<>();
		List<VillageLgdMaster> accessibleVillages = new ArrayList<>();
		try {
			String userId = getUserId(httpServletRequest);
			Long id = Long.parseLong(userId);
			switch (userInputDAO.getBoundaryType()) {
			case "state":
				//

				List<Long> stateCodes = userVillageMappingRepository.getStateCodesById(id);
				if (stateCodes != null && stateCodes.size() > 0) {

					stateList = stateLgdMasterRepository.findAllByStateLgdCodeIn(stateCodes);
					responseModel.setData(stateList);
				}

				break;
			case "district":

				if (userInputDAO.getStateLGDCodeList() == null) {
					List<Long> assignStates = userVillageMappingRepository.getStateCodesById(id);
					userInputDAO.setStateLGDCodeList(assignStates);
				}

				List<Long> disctrictCodes = userVillageMappingRepository.getDistrictCodesByStateCodesAndId(id,
						userInputDAO.getStateLGDCodeList());

				if (disctrictCodes != null && disctrictCodes.size() > 0) {
					accessibleDistricts = districtLgdMasterRepository.findAllByDistrictLgdCode(disctrictCodes);
				}
				// List<DistrictLgdMaster> finalList = new ArrayList<>();
				// accessibleDistricts =
				// districtLgdMasterRepository.findAllByDistrictLgdCode(userInputDAO.getDistrictLgdCodeList());
				// accessibleDistricts.forEach(action->{
				// userInputDAO.getStateLGDCodeList().forEach(action2->{
				// if(action.getStateLgdCode().getStateLgdCode().equals(action2)) {
				// finalList.add(action);
				// }
				// });
				// });
				responseModel.setData(accessibleDistricts);
				break;
			case "subDistrict":
				if (userInputDAO.getStateLGDCodeList() == null && userInputDAO.getDistrictLgdCodeList() == null) {
					List<Long> assignStates = userVillageMappingRepository.getDistrictCodesById(id);
					userInputDAO.setDistrictLgdCodeList(assignStates);
				}
				List<Long> subDisctrictCodes = userVillageMappingRepository.getSubDistrictCodesByDistrictCodesAndId(id,
						userInputDAO.getDistrictLgdCodeList());

				if (subDisctrictCodes != null && subDisctrictCodes.size() > 0) {
					accessibleSubDistricts = subDistrictLgdMasterRepository.findByLgdCodes(subDisctrictCodes);
				}
				// List<SubDistrictLgdMaster> finalSubDistrictList = new ArrayList<>();
				// accessibleSubDistricts =
				// subDistrictLgdMasterRepository.findByLgdCodes(userInputDAO.getSubDistrictLgdCodeList());
				// accessibleSubDistricts.forEach(action->{
				// userInputDAO.getDistrictLgdCodeList().forEach(action2->{
				//
				// if(action.getDistrictLgdCode().getDistrictLgdCode().equals(action2)) {
				// finalSubDistrictList.add(action);
				// }
				// });
				// });

				responseModel.setData(accessibleSubDistricts);
				break;
			case "village":

				List<UserVillageMapping> villagelgdCodes = userVillageMappingRepository
				.findByUserMaster_UserIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeInOrderByVillageLgdMaster_VillageNameAsc(
						id, userInputDAO.getSubDistrictLgdCodeList());

				if (villagelgdCodes != null && villagelgdCodes.size() > 0) {

					accessibleVillages = villagelgdCodes.stream().map(x -> x.getVillageLgdMaster())
							.collect(Collectors.toList());
				}
				// List<VillageLgdMaster> finalVillageList = new ArrayList<>();
				// accessibleVillages =
				// villageLgdMasterRepository.findByVillageLgdCodeInOrderByVillageName(userInputDAO.getVillageLgdCodeList());
				// accessibleVillages.forEach(action->{
				// userInputDAO.getSubDistrictLgdCodeList().forEach(action2->{
				//
				// if(action.getSubDistrictLgdCode().getSubDistrictLgdCode().equals(action2)) {
				// finalVillageList.add(action);
				// }
				// });
				// });
				responseModel.setData(accessibleVillages);
				break;
			}
			return responseModel;

		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}


	/**
	 * Retrieves the village assigned tree for a user by user ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user ID.
	 * @return The ResponseModel object containing the village assigned tree information.
	 */
	public ResponseModel getUserVillageAssignedTreeByUserId(HttpServletRequest request, UserInputDAO userInputDAO) {
		try {
			Long userId = userInputDAO.getUserId();

			String fnName = "agri_stack.fn_web_get_user_assigned_tree";
			SqlData[] params = { new SqlData(1, userId.toString(), "int") };
			String response = dbUtils.executeStoredProcedure(fnName, "[]", params).toString();

			ObjectMapper mapper = new ObjectMapper();
			TypeFactory typeFactory = mapper.getTypeFactory();
			List<UserAssignedTreeDAO> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, UserAssignedTreeDAO.class));

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {

			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Changes the user's password.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user details.
	 * @return The ResponseModel object indicating the success of changing the password.
	 */
	public ResponseModel changePassword(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {
		try {
			Optional<UserMaster> op = userMasterRepository.findByUserIdAndIsDeletedAndIsActive(userInputDAO.getUserId(),
					false, true);
			if (op.isPresent()) {
				UserMaster userMaster = op.get();
				if (encoder.matches(userInputDAO.getOldPassword(), userMaster.getUserPassword())) {
					if (checkLastThreePassword(userMaster, userInputDAO.getNewPassword())) {
						return CustomMessages.makeResponseModel(true,
								"New password should not be same as old 3 password.", CustomMessages.GET_DATA_ERROR,
								CustomMessages.INVALID_INPUT);
					} else {
						String validationMsg = isValidPassword(userInputDAO.getNewPassword(), userMaster.getUserName());
						if (validationMsg.isEmpty()) {
							userMaster.setUserPassword(encoder.encode(userInputDAO.getNewPassword()));
							userMaster.setIsPasswordChanged(true);
							userMaster.setUserToken(null);
							userMaster.setLastPasswordChangedDate(new Date());
							String userId = getUserId(request);
							if (userId != null) {
								userMaster.setModifiedBy(userId);
							}
							try {
								Gson gson = new Gson();
								JsonArray passwordHistory = new JsonArray();
								if (userMaster.getPasswordHistory() != null) {
									passwordHistory = gson.fromJson(userMaster.getPasswordHistory().toString(),
											JsonArray.class);
								}
								passwordHistory.add(userMaster.getUserPassword());
								if (passwordHistory.size() > 3) {
									passwordHistory.remove(0);
								}
								userMaster.setPasswordHistory(passwordHistory.toString());
							} catch (Exception e) {
								return CustomMessages.makeResponseModel(true, "Issue while saving password history",
										CustomMessages.GET_DATA_ERROR, CustomMessages.INVALID_INPUT);
							}
							userMasterRepository.save(userMaster);
							Runnable r = () -> {
								messageConfigService.sendPasswordChangedEmail(userMaster.getUserName(),
										userMaster.getUserEmailAddress(), "changed");
							};
							Thread t = new Thread(r);
							t.start();

							return new ResponseModel(null, CustomMessages.UPDATE_PASSWORD,
									CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
									CustomMessages.METHOD_POST);
						} else {
							return CustomMessages.makeResponseModel(true, validationMsg, CustomMessages.GET_DATA_ERROR,
									CustomMessages.INVALID_INPUT);
						}
					}
				} else {
					return CustomMessages.makeResponseModel(null, CustomMessages.INVALID_PASSWORD2,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}
			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

		} catch (Exception e) {

			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Updates the user profile.
	 *
	 * @param request The HttpServletRequest object.
	 * @param inputDAO
	 * @param profileImage
	 * @return The ResponseModel object indicating the success of updating the user profile.
	 */
	public ResponseModel updateUserProfileMob(HttpServletRequest request, SurveyorInputDAO inputDAO,
			MultipartFile profileImage) {
		ResponseModel responseModel = null;
		try {
			String userId = getUserId(request);
			Optional<UserMaster> userOptional = userMasterRepository
					.findByUserIdAndIsDeletedAndIsActive(Long.valueOf(userId), Boolean.FALSE, Boolean.TRUE);
			if (userOptional.isPresent()) {
				UserMaster dbObject = userOptional.get();
				// check user role
				RoleMaster userRole = dbObject.getRoleId();
				if (userRole.getCode().equals(RoleEnum.SURVEYOR.toString())) {
					dbObject.setUserFirstName(inputDAO.getUserFirstName());
					dbObject.setUserLastName(inputDAO.getUserLastName());
					dbObject.setUserFullName(inputDAO.getUserFirstName() + " " + inputDAO.getUserLastName());
					dbObject.setUserMobileNumber(inputDAO.getUserMobileNumber());
					dbObject.setUserEmailAddress(inputDAO.getUserEmailAddress());
					dbObject.setModifiedBy(getUserId(request));
					dbObject.setModifiedOn(new Timestamp(new Date().getTime()));
					dbObject.setModifiedIp(request.getRemoteAddr());
					if (dbObject.getDepartmentId().getDepartmentType()
							.equals(DepartmentEnum.PRIVATE_RESIDENT.getValue())) {
						updateSurveyorBankDetail(dbObject, inputDAO);
					}
				}
				UserMaster savedUser = userMasterRepository.save(dbObject);
				if (Objects.nonNull(profileImage)) {
					deleteUserProfile(dbObject);
					MediaMaster mediaMaster = mediaService.storeFile(profileImage, "agristack", folderImage,
							ActivityCodeEnum.USER_PROFILE_IMAGE.getValue());
					savedUser.setMediaId(mediaMaster.getMediaId());
					savedUser.setUserImage(mediaMaster.getMediaUrl());
				}
				userMasterRepository.save(dbObject);
				UserOutputDAO userOutputDAO = getUserOutFromUserMaster(dbObject);
				StatusMaster status = statusRepository.findByIsDeletedFalseAndStatusCode(dbObject.getUserStatus());
				userOutputDAO.setUserStatus(Objects.isNull(status) ? null : status.getStatusName());

				LanguageMaster defaultLanguageMaster = dbObject.getDefaultLanguageMaster();
				if(Objects.isNull(defaultLanguageMaster) && !Objects.isNull(dbObject.getUserStateLGDCode())) {
					StateDefaultLanguageMapping defaultLanguageMapping = stateDefaultLanguageMappingRepository.findByIsActiveTrueAndIsDeletedFalseAndStateLgdMasterStateLgdCode(dbObject.getUserStateLGDCode().longValue());
					if(!Objects.isNull(defaultLanguageMapping)) {
						defaultLanguageMaster = defaultLanguageMapping.getLanguageId();
					}
				}

				if (!Objects.isNull(defaultLanguageMaster)) {
					userOutputDAO.setDefaultLanguageMaster(defaultLanguageMaster);
					userOutputDAO.setDefaultLanguageId(defaultLanguageMaster.getLanguageId());
				}
				//				userOutputDAO
				//				.setDefaultLanguageId(userDefLanMapService.getDefaultLanguageByUserId(dbObject.getUserId()));

				userOutputDAO.setUserType(dbObject.getRoleId().getCode());
				if (Objects.nonNull(dbObject.getMediaId())) {
					MediaMaster media = mediaService.getMediaDetail(dbObject.getMediaId());
					if (Objects.nonNull(media)) {
						userOutputDAO.setMediaMaster(media);
					}
				}
				return CustomMessages.makeResponseModel(userOutputDAO, CustomMessages.USER_UPDATED_SUCCESS,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.USER_ID_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * @param surveyor
	 * @param surveyorInput
	 * @return
	 */
	@Transactional
	private UserMaster updateSurveyorBankDetail(UserMaster surveyor, SurveyorInputDAO surveyorInput) {
		UserBankDetail userBankDetail = userBankDetailRepository.findByUserId_UserId(surveyor.getUserId());
		Optional<BankMaster> bankOp = bankRepository.findByBankIdAndIsDeletedFalse(surveyorInput.getUserBankId());
		if (Objects.nonNull(userBankDetail.getBankId()) && bankOp.isPresent() && Objects.nonNull(surveyorInput.getUserBankId())) {
			BankMaster bankMaster = bankOp.get();
			BeanUtils.copyProperties(surveyorInput, userBankDetail, "userBankDetailId", "createdBy", "createdIp",
					"createdOn", "isDeleted", "isActive");
			userBankDetail.setBankId(bankMaster);
			userBankDetail.setUserBankName(bankMaster.getBankName());
			userBankDetail.setModifiedIp(surveyor.getModifiedIp());
			userBankDetail.setModifiedBy(surveyor.getModifiedBy());
			userBankDetail.setModifiedOn(new Timestamp(new Date().getTime()));
			userBankDetailRepository.save(userBankDetail);
		}
		return surveyor;
	}

	@Transactional
	protected void deleteUserProfile(UserMaster user) {
		MediaMaster mediaMaster = mediaMasterRepository.getMediaByID(user.getMediaId());
		if (Objects.nonNull(mediaMaster)) {
			mediaService.deleteMedia(mediaMaster.getMediaId());
		}
	}

	/**
	 * Updates the token for a user.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user information and the new token.
	 * @return The ResponseModel object indicating the success or failure of the token update.
	 */
	public ResponseModel updateToken(HttpServletRequest request, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		try {
			Long userId = Long.parseLong(getUserId(request));

			Optional<UserMaster> opUser = userMasterRepository.findByIsDeletedAndUserId(Boolean.FALSE, userId);
			if (opUser.isPresent()) {
				UserMaster userMaster = opUser.get();
				userMaster.setDeviceToken(userInputDAO.getDeviceToken());
				userMasterRepository.save(userMaster);
			}
			responseModel = CustomMessages.makeResponseModel(null, CustomMessages.STATUS_UPDATED_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * Retrieves all accessible boundaries based on the provided list.
	 *
	 * @param httpServletRequest
	 * @param userInputDAO The {@code UserInputDAO} object containing the list of boundaries.
	 * @return The ResponseModel object containing the accessible boundaries.
	 */
	public ResponseModel getAllAccessibleBoundaryByList(HttpServletRequest httpServletRequest,
			UserInputDAO userInputDAO) {
		ResponseModel responseModel = CustomMessages.makeResponseModel(null, CustomMessages.GET_RECORD,
				CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		// List<StateLgdMaster> stateList = new ArrayList<>();
		// List<DistrictLgdMaster> accessibleDistricts = new ArrayList<>();
		// List<SubDistrictLgdMaster> accessibleSubDistricts = new ArrayList<>();
		List<BoundaryDto> boundaryList = new ArrayList<>();
		try {
			String userId = getUserId(httpServletRequest);
			Long id = Long.parseLong(userId);
			switch (userInputDAO.getBoundaryType()) {
			case "state":
				List<Object[]> stateList = userVillageMappingRepository.getStateList(id);
				for (Object[] state : stateList) {
					BoundaryDto boundaryDto = new BoundaryDto();
					boundaryDto.setBoundaryLgdCode(Long.parseLong(state[0].toString()));
					boundaryDto.setBoundaryName(state[1].toString());
					boundaryList.add(boundaryDto);
				}
				responseModel.setData(boundaryList);
				break;
			case "district":

				List<Object[]> districtList = userVillageMappingRepository.getDistrictList(id);
				for (Object[] district : districtList) {
					BoundaryDto boundaryDto = new BoundaryDto();
					boundaryDto.setBoundaryLgdCode(Long.parseLong(district[0].toString()));
					boundaryDto.setBoundaryName(district[1].toString());
					boundaryList.add(boundaryDto);
				}
				responseModel.setData(boundaryList);

				break;
			case "subDistrict":
				List<Object[]> subDistrictList = userVillageMappingRepository.getSubDistrictList(id);
				for (Object[] subDistrict : subDistrictList) {
					BoundaryDto boundaryDto = new BoundaryDto();
					boundaryDto.setBoundaryLgdCode(Long.parseLong(subDistrict[0].toString()));
					boundaryDto.setBoundaryName(subDistrict[1].toString());
					boundaryList.add(boundaryDto);
				}
				responseModel.setData(boundaryList);

				break;
			case "village":

				List<Object[]> villageList = userVillageMappingRepository.getVillageList(id);
				for (Object[] village : villageList) {
					BoundaryDto boundaryDto = new BoundaryDto();
					boundaryDto.setBoundaryLgdCode(Long.parseLong(village[0].toString()));
					boundaryDto.setBoundaryName(village[1].toString());
					boundaryList.add(boundaryDto);
				}
				responseModel.setData(boundaryList);
				break;
			}
			return responseModel;

		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}


	/**
	 * Retrieves the verification survey sub-district by its ID.
	 *
	 * @param httpServletRequest
	 * @param id The ID of the verification survey sub-district.
	 * @return The ResponseModel object containing the verification survey sub-district.
	 */
	public ResponseModel getVerficationSurveySubDistrictById(HttpServletRequest httpServletRequest, Long id) {
		ResponseModel responseModel = null;
		try {
			List<SubDistrictLgdMaster> finalData = new ArrayList<>();
			Optional<UserMaster> usop = userMasterRepository.findByIsDeletedAndUserId(Boolean.FALSE, id);
			UserMaster userMaster = null;
			if (usop.isPresent()) {
				userMaster = usop.get();
				if (userMaster.getVerificationDistrictLGDCode() != null) {
					finalData.add(subDistrictLgdMasterRepository
							.findBySubDistrictLgdCode(userMaster.getVerificationTalukaLGDCode().longValue()));
				}

			}

			return CustomMessages.makeResponseModel(finalData, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (

				Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * Downloads the user template based on the provided role, territory level, and LGD codes.
	 *
	 * @param role The ID of the role.
	 * @param territoryLevel The territory level.
	 * @param lgdCodes The list of LGD codes.
	 * @return The ResponseEntity object containing the user template file.
	 */
	public ResponseEntity<?> downloadUsersByRoleAndTerritory(Long role,	String territoryLevel,List<Integer> lgdCodes) {
		try {

			List<UserMaster> users = new ArrayList<>();

			//Get user based on role and territory
			switch(territoryLevel.toLowerCase()) {
			case("state"):
				users = userMasterRepository.
				findByRoleId_RoleIdAndUserStateLGDCodeInAndIsDeletedFalseAndIsActiveTrue
				(role, lgdCodes);
			break;
			case("district"):
				users = userMasterRepository.
				findByRoleId_RoleIdAndUserDistrictLGDCodeInAndIsDeletedFalseAndIsActiveTrue
				(role, lgdCodes);
			break;
			case("subdistrict"):
				users = userMasterRepository.
				findByRoleId_RoleIdAndUserTalukaLGDCodeInAndIsDeletedFalseAndIsActiveTrue
				(role, lgdCodes);
			break;
			case("village"):
				users = userMasterRepository.
				findByRoleId_RoleIdAndUserVillageLGDCodeInAndIsDeletedFalseAndIsActiveTrue
				(role, lgdCodes);
			break;
			}

			//Create excel
			Workbook userWorkBook = new XSSFWorkbook();
			XSSFSheet userDetailsSheet = (XSSFSheet) userWorkBook.createSheet("User Details");
			userDetailsSheet.protectSheet("password");
			//Set header and its style
			Row header = userDetailsSheet.createRow(0);

			CellStyle headerStyle = userWorkBook.createCellStyle();
			headerStyle.setWrapText(true);
			headerStyle.setLocked(true);
			XSSFFont font = ((XSSFWorkbook) userWorkBook).createFont();
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 11);
			font.setBold(true);
			headerStyle.setFont(font);


			List<String> userDetailsHeader = Constants.USER_HEADERS;

			for (int i = 0; i < userDetailsHeader.size(); i++) {
				Cell headerCell = header.createCell(i);
				headerCell.setCellValue(userDetailsHeader.get(i));
				headerCell.setCellStyle(headerStyle);
			}

			CellStyle unlockedCellStyle = userWorkBook.createCellStyle();
			unlockedCellStyle.setLocked(false);

			int counter = 1;
			for (UserMaster user : users) {
				Row content = userDetailsSheet.createRow(counter);
				for (int i = 0; i < userDetailsHeader.size(); i++) {

					Cell contentCell = content.createCell(i);

					switch (userDetailsHeader.get(i)) {
					case "User ID":
						contentCell.setCellValue(user.getUserName());
						break;
					case "User Name":
						contentCell.setCellValue(user.getUserFullName());
						contentCell.setCellStyle(unlockedCellStyle);
						break;
					case "Mobile Number":
						contentCell.setCellValue(user.getUserMobileNumber());
						contentCell.setCellStyle(unlockedCellStyle);
						break;
					case "Email Address":
						contentCell.setCellValue(user.getUserEmailAddress());
						contentCell.setCellStyle(unlockedCellStyle);
						break;
					default:
						break;
					}
				}
				counter++;
			}

			userDetailsSheet.autoSizeColumn(0);
			userDetailsSheet.autoSizeColumn(1);
			userDetailsSheet.autoSizeColumn(2);
			userDetailsSheet.autoSizeColumn(3);

			String UserTemplatePath = null;
			if (storageType == 1) {
				UserTemplatePath = localStoragePath+File.separator + "\\ExcelTemplate";
				File f = new File(UserTemplatePath);
				f.mkdir();
			}
			File userDetailsFile = new File(UserTemplatePath,
					"UserDetailsTemplate" + new Date().getTime() + ".xlsx");
			FileOutputStream outputStream;

			try {
				outputStream = new FileOutputStream(userDetailsFile);
				userWorkBook.write(outputStream);
				return ResponseEntity.ok().contentLength(Files.readAllBytes(userDetailsFile.toPath()).length)
						.contentType(MediaType.valueOf(MediaType.APPLICATION_OCTET_STREAM_VALUE))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + userDetailsFile.getName())
						.body(Files.readAllBytes(userDetailsFile.toPath()));

			}catch(Exception e) {
				return new ResponseEntity<String>(CustomMessages.getMessage(CustomMessages.INTERNAL_SERVER_ERROR),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(CustomMessages.getMessage(CustomMessages.INTERNAL_SERVER_ERROR),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * Bulk uploads users to update their name, email, and mobile.
	 *
	 * @param file The MultipartFile object containing the user data.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object indicating the success of the bulk update.
	 */
	public ResponseModel bulkUpdateUser(MultipartFile file, HttpServletRequest request) {
		try {
			String extension = FilenameUtils.getExtension(file.getOriginalFilename());

			if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
				return bulkUpdateUserExcel(file, request);
			} else {
				return CustomMessages.makeResponseModel("File type " + extension + "is not supported",
						CustomMessages.TYPE_NOT_SUPPORTED, CustomMessages.UNSUPPORTED_MEDIA_TYPE,
						CustomMessages.FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * @param file
	 * @param request
	 * @return
	 */
	private ResponseModel bulkUpdateUserExcel(MultipartFile file, HttpServletRequest request) throws Exception {

		// Get user registry sheet
		Workbook workbook = getWorkBook(file);
		org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

		// Header validation
		boolean isHeaderValid = validateExcelHeader(sheet, Constants.USER_HEADERS);
		if (!isHeaderValid) {
			return CustomMessages.makeResponseModel(null, CustomMessages.INVALID_FARMER_EXCEL_HEADER,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

		Iterator<Row> rows = sheet.iterator();
		rows.next(); // skip header

		// User Details Empty Sheet validation
		if (!rows.hasNext()) {
			return CustomMessages.makeResponseModel(null, CustomMessages.USER_EMPTY_EXCEL,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

		StringBuilder message = new StringBuilder();
		Boolean isUserValid = true;
		//		List<UserMaster> userList = new ArrayList<>();
		while (rows.hasNext()) {

			Row row = rows.next();
			isUserValid = true;
			if (row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
				Optional<UserMaster> user = userMasterRepository.findByUserName(row.getCell(0).getStringCellValue());

				if (user.isPresent()) {

					// username
					if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
						if (ValidationService.isStringOnlyAlphabet(row.getCell(1).getStringCellValue())) {
							user.get().setUserFullName(row.getCell(1).getStringCellValue());
						} else {
							isUserValid = false;
							message.append(
									"Only alphabets is allowed in user name " + row.getCell(1).getStringCellValue());
						}
					} else {
						isUserValid = false;
						message.append("Invalid user name " + row.getCell(1).getStringCellValue());
					}

					// Mobile
					if (row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC) {
						String mobileNumber = NumberToTextConverter.toText(row.getCell(2).getNumericCellValue());

						if (mobileNumber.length() == 10) { // 10 digit validation
							Optional<UserMaster> isMobilePresent =  userMasterRepository.findByUserMobileNumber(mobileNumber);

							if(isMobilePresent.isPresent()) { // If mobile number already exists
								isUserValid =false;
								message.append(" , Mobile number already exists "+ mobileNumber);
							} else {
								user.get().setUserMobileNumber(mobileNumber);
							}
						} else {
							isUserValid = false;
							message.append(" ,Not a valid 10 digit mobile number " + mobileNumber + " of user "
									+ row.getCell(0).getStringCellValue());
						}
					} else {
						isUserValid = false;
						message.append(", Invalid mobile number of user " + row.getCell(0).getStringCellValue());
					}

					// Email
					if (row.getCell(3).getCellType() == Cell.CELL_TYPE_STRING) {
						if (ValidationService.emailValidator(row.getCell(3).getStringCellValue())) { //email format validation
							Optional<UserMaster> isEmailPresent =  userMasterRepository.findByUserEmailAddress(row.getCell(3).getStringCellValue());
							if(isEmailPresent.isPresent()) {
								isUserValid =false;
								message.append(", Email already exists "+ row.getCell(3).getStringCellValue());
							}else {
								user.get().setUserEmailAddress(row.getCell(3).getStringCellValue());
							}

						} else {
							isUserValid = false;
							message.append(", Invalid email " + row.getCell(3).getStringCellValue() + " of user "
									+ row.getCell(0).getStringCellValue());
						}
					} else {
						isUserValid = false;
						message.append(", Invalid email type of user " + row.getCell(0).getStringCellValue());
					}

					if(isUserValid) {
						//						userList.add(user.get());
						UserMaster savedUser = userMasterRepository.save(user.get());
						if (savedUser.getUserMobileNumber() != null ) {
							sendMobileMessage(savedUser); //Send mobile notification
						}

						if( savedUser.getUserEmailAddress() != null) {
							sendEmailMessage(savedUser); //Send email notification
						}
					}
				} else {
					message.append(", User does not exists :" + row.getCell(0).getStringCellValue());
				}
			} else {
				message.append(", Invalid user type " + row.getCell(0).getStringCellValue());
			}
		}
		//		userMasterRepository.saveAll(userList);
		if(message.equals(null)) {
			message.append("User updated successfully.");
		}
		return CustomMessages.makeResponseModel(message, CustomMessages.EXCEL_UPLAOD_SUCCESS,
				CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
	}

	/**
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private Workbook getWorkBook(MultipartFile file) throws Exception {
		Workbook workbook = null;
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());

		if (extension.equalsIgnoreCase("xlsx")) {
			workbook = new XSSFWorkbook(file.getInputStream());
		} else if (extension.equalsIgnoreCase("xls")) {
			workbook = new HSSFWorkbook(file.getInputStream());
		}

		return workbook;
	}

	/**
	 * @param sheet
	 * @param mandatoryHeaders
	 * @return
	 */
	private boolean validateExcelHeader(Sheet sheet, List<String> mandatoryHeaders) {
		List<String> excelHeader = new ArrayList<>();

		sheet.getRow(0).forEach(cell -> { // Get excel header name
			excelHeader.add(cell.getStringCellValue());
		});

		if (!excelHeader.containsAll(mandatoryHeaders)) { // Validate required headers are in excel
			System.out.println("Required headers are not in excel.");
			return false;
		}

		Map<String, Integer> headerIndexMap = IntStream.range(0, excelHeader.size()).boxed()
				.collect(Collectors.toMap(i -> excelHeader.get(i), i -> i));

		// Check if the manadatory headers are in order
		Integer result = mandatoryHeaders.stream().map(headerIndexMap::get).reduce(-1,
				(x, hi) -> x < hi ? hi : excelHeader.size());

		if (result == excelHeader.size()) {
			System.out.println("Headers are not in order.");
			return false;
		}
		return true;

	}


	/**
	 * Adds a CR state admin user.
	 *
	 * @param httpServletRequest
	 * @param userInputDAO The {@code UserInputDAO} object containing the user details.
	 * @return The ResponseModel object indicating the success of adding the CR state admin user.
	 */
	public ResponseModel createCRStateAdminUser(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		try {
			if (userInputDAO.getStateCodeToBeAssign() == null) {
				return CustomMessages.makeResponseModel(true, "Please enter state lgd code.",
						CustomMessages.NO_DATA_FOUND, CustomMessages.INVALID_INPUT);
			}

			RoleMaster role = roleMasterRepository.findByCodeAndIsDefaultTrue("CR_STATE_ADMIN");
			if (role != null) {

				Optional<UserMaster> userNameOP = userMasterRepository.findByUserName(userInputDAO.getUserName());
				if (!userNameOP.isPresent()) {
					UserMaster userMaster = CustomMessages.returnUserMasterFromUserInputDAO(userInputDAO);
					userMaster.setUserPassword(encoder.encode(userInputDAO.getUserPassword()));
					try {
						Gson gson = new Gson();
						JsonArray passwordHistory = new JsonArray();
						if (userMaster.getPasswordHistory() != null) {
							passwordHistory = gson.fromJson(userMaster.getPasswordHistory().toString(), JsonArray.class);
						}
						passwordHistory.add(userMaster.getUserPassword());
						if (passwordHistory.size() > 3) {
							passwordHistory.remove(0);
						}
						userMaster.setPasswordHistory(passwordHistory.toString());
					} catch (Exception e) {
						//							return CustomMessages.makeResponseModel(true, "Issue while saving passwordhistory",
						//									CustomMessages.GET_DATA_ERROR, CustomMessages.INVALID_INPUT);
					}
					userMaster.setRoleId(role);
					userMaster.setIsActive(true);
					userMaster.setIsDeleted(false);
					userMaster.setIsPasswordChanged(false);
					userMaster.setUserStatus(StatusEnum.APPROVED.getValue());
					userMaster.setUserStateLGDCode(userInputDAO.getStateCodeToBeAssign().intValue());

					userMaster.setCreatedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));
					userMaster.setModifiedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));

					UserMaster savedUser = userMasterRepository.save(userMaster);
					System.out.println(savedUser.getUserId());

					new Thread() {
						@Override
						public void run() {
							try {
								List<UserVillageMapping> userVillageList = new ArrayList<>();

								List<DistrictLgdMaster> districtCode =
										districtLgdMasterRepository.findByStateLgdCode_StateLgdCode(userInputDAO.getStateCodeToBeAssign());
								districtCode.forEach(district->{
									List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
											.findByDistrictLgdCode_DistrictLgdCode(district.getDistrictLgdCode());

									villageLgdMaster.forEach(village -> {
										UserVillageMapping userVillageMapping = new UserVillageMapping();

										userVillageMapping.setUserMaster(savedUser);
										userVillageMapping.setVillageLgdMaster(village);
										userVillageMapping.setCreatedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));
										userVillageMapping.setModifiedBy(CustomMessages.getUserId(httpServletRequest, jwtTokenUtil));
										userVillageMapping.setIsActive(true);
										userVillageMapping.setIsDeleted(false);
										//									userVillageList.add(userVillageMapping);
										userVillageMappingRepository.save(userVillageMapping);
									});
								});

								System.out.println("Inside thread  count:" + userVillageList.size());
								userVillageMappingRepository.saveAll(userVillageList);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();

					return CustomMessages.makeResponseModel(savedUser, CustomMessages.GET_RECORD,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				} else {
					return CustomMessages.makeResponseModel(null, "User name already exists",
							CustomMessages.ALREADY_EXIST, CustomMessages.FAILED);
				}
			}
			//			});
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);

		}
		return responseModel;
	}



	/**
	 * Retrieves user details by role with pagination.
	 *
	 * @param httpServletRequest
	 * @param paginationDao The {@code PaginationDao} object containing the pagination details.
	 * @return The ResponseModel object containing the user details.
	 */
	public ResponseModel getUserDetailByRole(HttpServletRequest httpServletRequest, PaginationDao paginationDao) {
		ResponseModel responseModel = null;
		try {
			Long userId = Long.parseLong(getUserId(httpServletRequest));
			Pageable pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
					Sort.by(paginationDao.getSortField()).descending());

			if (paginationDao.getSortOrder().equals("asc")) {
				pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
						Sort.by(paginationDao.getSortField()).ascending());
			}

			Page<UserMaster> list = null;
			if(paginationDao.getRoleId()!= null && paginationDao.getBoundaryType() == null) {
				if (paginationDao.getSearch() != null && !paginationDao.getSearch().equals("")) {
//					list = userMasterRepository
//							.findByIsDeletedAndRoleId_RoleIdAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrRoleId_RoleNameIgnoreCaseContains
//							(Boolean.FALSE, paginationDao.getRoleId(),paginationDao.getSearch(), paginationDao.getSearch(), paginationDao.getSearch(), paginationDao.getSearch(), paginationDao.getSearch(), pageable);
//					
					list = userMasterRepository
							.findByIsDeletedAndRoleId_RoleIdAndCreatedByAndUserNameIgnoreCaseContainsOrUserFullNameIgnoreCaseContainsOrUserEmailAddressIgnoreCaseContainsOrUserMobileNumberIgnoreCaseContainsOrRoleId_RoleNameIgnoreCaseContains
							(Boolean.FALSE, paginationDao.getRoleId(),userId+"",paginationDao.getSearch(), paginationDao.getSearch(), paginationDao.getSearch(), paginationDao.getSearch(), paginationDao.getSearch(), pageable);
					
					
//					list =  userMasterRepository.findByIsDeletedAndRoleId_RoleIdAndUserNameContains(Boolean.FALSE, paginationDao.getRoleId(),paginationDao.getSearch(),pageable);
				}else {
//					list = userMasterRepository
//							.findByIsDeletedAndRoleId_RoleId
//							(Boolean.FALSE, paginationDao.getRoleId(), pageable);
					
					list = userMasterRepository
							.findByIsDeletedAndRoleId_RoleIdAndCreatedBy(Boolean.FALSE, paginationDao.getRoleId(),userId+"", pageable);
					
				}
				
			} else if(paginationDao.getRoleId() == null && paginationDao.getBoundaryType() != null) {
//				list = userMasterRepository
//						.findByIsDeletedAndRolePatternMappingId_TerritoryLevelOrderByCreatedOnDesc
//						(Boolean.FALSE, paginationDao.getBoundaryType().toLowerCase(), pageable);
				
				list = userMasterRepository
						.findByIsDeletedAndRolePatternMappingId_TerritoryLevelAndCreatedByOrderByCreatedOnDesc(Boolean.FALSE, paginationDao.getBoundaryType().toLowerCase(),userId+"", pageable);

			} else if(paginationDao.getRoleId() != null && paginationDao.getBoundaryType() != null) {
//				list = userMasterRepository
//						.findByIsDeletedAndRoleId_RoleIdAndRolePatternMappingId_TerritoryLevelOrderByCreatedOnDesc
//						(Boolean.FALSE, paginationDao.getRoleId(),paginationDao.getBoundaryType().toLowerCase(), pageable);
				
				list = userMasterRepository
						.findByIsDeletedAndRoleId_RoleIdAndRolePatternMappingId_TerritoryLevelAndCreatedByOrderByCreatedOnDesc
						(Boolean.FALSE, paginationDao.getRoleId(),paginationDao.getBoundaryType().toLowerCase(),userId+"", pageable);
			}

			if (paginationDao.getUserId() != null) {
				if (paginationDao.getSearch() != null) {
					list = userMasterRepository
							.findByIsDeletedAndRoleId_IsDefaultAndCreatedByAndUserNameContainsOrderByCreatedOnDesc(
									Boolean.FALSE, Boolean.FALSE, userId+"", paginationDao.getSearch(),
									pageable);
				} else {
					list = userMasterRepository.findByIsDeletedAndRoleId_IsDefaultAndCreatedByOrderByCreatedOnDesc(
							Boolean.FALSE, Boolean.FALSE, userId+"", pageable);
				}
			}
			List<UserMasterReturnDAO> finalResultList = new ArrayList<>();
			if (list != null && list.getSize() > 0) {
				list.getContent().forEach(action -> {
					UserMasterReturnDAO userMasterReturnDAO = new UserMasterReturnDAO();
					userMasterReturnDAO.setUserId(action.getUserId());
					userMasterReturnDAO.setUserName(action.getUserName());
					userMasterReturnDAO.setUserToken(action.getUserToken());

					userMasterReturnDAO.setUserFirstName(action.getUserFirstName());
					userMasterReturnDAO.setUserLastName(action.getUserLastName());
					userMasterReturnDAO.setUserFullName(action.getUserFullName());

					userMasterReturnDAO.setUserCountryLGDCode(action.getUserCountryLGDCode());
					userMasterReturnDAO.setUserStateLGDCode(action.getUserStateLGDCode());
					userMasterReturnDAO.setUserDistrictLGDCode(action.getUserDistrictLGDCode());
					userMasterReturnDAO.setUserTalukaLGDCode(action.getUserTalukaLGDCode());
					userMasterReturnDAO.setUserVillageLGDCode(action.getUserVillageLGDCode());

					userMasterReturnDAO.setUserAadhaarHash(action.getUserAadhaarHash());
					userMasterReturnDAO.setUserAadhaarVaultRefCentral(action.getUserAadhaarVaultRefCentral());
					userMasterReturnDAO.setUserStatus(action.getUserStatus());

					userMasterReturnDAO.setUserRejectionReason(action.getUserRejectionReason());

					userMasterReturnDAO.setUserMobileNumber(action.getUserMobileNumber());
					userMasterReturnDAO.setUserAlternateMobileNumber(action.getUserAlternateMobileNumber());
					userMasterReturnDAO.setUserEmailAddress(action.getUserEmailAddress());

					userMasterReturnDAO.setRoleId(action.getRoleId().getRoleId());
					userMasterReturnDAO.setRoleName(action.getRoleId().getRoleName());

					userMasterReturnDAO.setAddressLine1(action.getAddressLine1());
					userMasterReturnDAO.setAddressLine2(action.getAddressLine2());
					userMasterReturnDAO.setUserDeviceToken(action.getUserDeviceToken());
					userMasterReturnDAO.setAppVersion(action.getAppVersion());

					userMasterReturnDAO.setUserDeviceName(action.getUserDeviceName());

					userMasterReturnDAO.setUserOs(action.getUserOs());

					userMasterReturnDAO.setUserLocalLangauge(action.getUserLocalLangauge());

					userMasterReturnDAO.setIsEmailVerified(action.getIsEmailVerified());
					userMasterReturnDAO.setIsMobileVerified(action.getIsMobileVerified());
					userMasterReturnDAO.setUserPrId(action.getUserPrId());

					userMasterReturnDAO.setPinCode(action.getPinCode());

					userMasterReturnDAO.setIsAadhaarVerified(action.getIsAadhaarVerified());
					userMasterReturnDAO.setFarmAssignCount(action.getFarmAssignCount());
					userMasterReturnDAO.setFarmPendingCount(action.getFarmPendingCount());

					userMasterReturnDAO.setIsAvailable(action.getIsAvailable());
					userMasterReturnDAO.setIsPasswordChanged(action.getIsPasswordChanged());
					userMasterReturnDAO.setLastPasswordChangedDate(action.getLastPasswordChangedDate());
					userMasterReturnDAO.setDefaultPage(action.getDefaultPage());

					userMasterReturnDAO.setPasswordHistory(action.getPasswordHistory());

					GeographicalAreaOutputDAO geographicalAreaOutputDAO = new Gson().fromJson(action.getGeographicalArea(),
							GeographicalAreaOutputDAO.class);
					userMasterReturnDAO.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);
					// finalResultList.add(userMasterReturnDAO);
					action.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);
				});
			}

			Map<String, Object> responseData = new HashMap();
			responseData.put("data", list.getContent());
			responseData.put("total", list.getTotalElements());
			responseData.put("page", paginationDao.getPage());
			responseData.put("limit", paginationDao.getLimit());
			responseData.put("sortField", paginationDao.getSortField());
			responseData.put("sortOrder", paginationDao.getSortOrder());

			return CustomMessages.makeResponseModel(responseData, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}


	/**
	 * Resets the password of a user by their ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userId The ID of the user.
	 * @return The ResponseModel object indicating the success of resetting the password.
	 */
	public ResponseModel resetPasswordByMail(Long userId,HttpServletRequest request) {
		try {
			Optional<UserMaster> userOP = userMasterRepository.findByIsDeletedAndUserId(Boolean.FALSE,userId);

			if (userOP.isPresent()) {
				UserMaster usermaster = userOP.get();

//				String randomPassword = CommonUtil.GeneratePassword(8);
				usermaster.setUserPasswordText("Admin@123");
				usermaster.setUserPassword(encoder.encode("Admin@123"));
				usermaster.setIsPasswordChanged(false);
				usermaster.setLastPasswordChangedDate(new Date());

				Gson gson = new Gson();
				JsonArray passwordHistory = new JsonArray();
				if (usermaster.getPasswordHistory() != null) {
					passwordHistory = gson.fromJson(usermaster.getPasswordHistory().toString(), JsonArray.class);
				}
				passwordHistory.add(usermaster.getUserPassword());
				if (passwordHistory.size() > 3) {
					passwordHistory.remove(0);
				}
				usermaster.setPasswordHistory(passwordHistory.toString());

				usermaster.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				UserMaster savedUser = userMasterRepository.save(usermaster);

				if (savedUser.getUserEmailAddress() != null) {		//Sent email
					sendEmailMessage(savedUser);
				} else {
					return CustomMessages.makeResponseModel(null, "Email not found.", CustomMessages.GET_DATA_ERROR,
							CustomMessages.INVALID_INPUT);
				}
			} else {
				return CustomMessages.makeResponseModel(null, "User not found.", CustomMessages.GET_DATA_ERROR,
						CustomMessages.INVALID_INPUT);
			}
			return CustomMessages.makeResponseModel(null, CustomMessages.RESET_PWD_EMAIL_SENT_MSG,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		}catch(Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * @param tokenDAO
	 * @return
	 */
	public ResponseModel  getToken(TokenDAO tokenDAO) {
		// TODO Auto-generated method stub
		try {
			List<UserMaster> userList=new ArrayList<>();
			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(tokenDAO.getUserName());

			if (jwtTokenUtil.validateToken(tokenDAO.getToken(), userDetails) && userDetails.getPassword() != null
					&& userDetails.getPassword().equals(tokenDAO.getToken())) {
				userList=userMasterRepository.findByUserToken(tokenDAO.getToken());
			}
			
			return CustomMessages.makeResponseModel(userList, CustomMessages.LOGIN_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}catch(Exception e) {
				e.printStackTrace();
				return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
						CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			}
	}

	public ResponseModel getExternalToken(UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		try {

			Optional<UserMaster> userOptional = null;
			userOptional = userMasterRepository.getUserWithoutStatus(userInputDAO.getUserName());

			if (userOptional.isPresent()) {

				if(!userOptional.get().getIsActive()) {
					return CustomMessages.makeResponseModel(null, CustomMessages.INACTIVE_USER,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}

				UserMaster usermaster = userOptional.get();

				if (encoder.matches(userInputDAO.getUserPassword(), usermaster.getUserPassword())) {

					UserDetails userDetails = jwtUserDetailsService.loadUserByUsername((usermaster.getUserId() + ""));
					String token = jwtTokenUtil.generateToken(userDetails, false);
					usermaster.setUserToken(token);

					userMasterRepository.save(usermaster);
					
					try {
						ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
						String userDataJson = ow.writeValueAsString(usermaster);
					
						
						String userKey=redisAppName+"_"+Constants.REDIS_USER_KEY+usermaster.getUserId();
						
						//Set user data into redis
						redisService.setValue(userKey, userDataJson);
						System.out.println(redisService.getValue(userKey));
						System.out.println(userKey);
					
					
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					responseModel = CustomMessages.makeResponseModel(token, CustomMessages.SUCCESS,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				} else {
					responseModel = CustomMessages.makeResponseModel(null, CustomMessages.INVALID_PASSWORD,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}

			} else {
				responseModel = CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Assign All Jurisdiction To User.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing user input data.
	 * @return The ResponseModel object representing the response.
	 */
	public ResponseModel assignAllJurisdictionToUser(HttpServletRequest request,UserInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		try {
			Long userId = userInputDAO.getUserId();
			Integer lgdCode = 0;
			String territory = null;
			Optional<UserMaster> user = userMasterRepository.findByUserId(userId);
			if (user.isPresent()) {
				territory = user.get().getRolePatternMappingId().getTerritoryLevel();
				if (territory.equals("state")) {
					lgdCode = user.get().getUserStateLGDCode();
				} else if (territory.equals("district")) {
					lgdCode = user.get().getUserDistrictLGDCode();
				} else if (territory.equals("subdistrict")) {
					lgdCode = user.get().getUserTalukaLGDCode();
				} else if (territory.equals("village")) {
					lgdCode = user.get().getUserVillageLGDCode();
				}

				JSONArray userDetailsList = new JSONArray();

				JSONObject userDetails = new JSONObject();
				userDetails.put("userId", userId);
				userDetails.put("lgdCode", lgdCode);
				userDetails.put("territory", territory);

				
				if(userDetails!=null) {
					
					userDetailsList.put(userDetails);
					
					if(userDetailsList!=null ) {
						String data = userDetailsList.toString();
						userMasterRepository.userAssignVillageByTerritory(data);		
					}
					
				}

				
				responseModel = CustomMessages.makeResponseModel(null, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			} else {
				responseModel = CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

	
			return responseModel;
		} catch (Exception e) {
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
     * fetch user count.
     *
     * @param user type
     * @param request The HttpServletRequest object.
     * @return  The ResponseModel object representing the response.
     */
	public ResponseModel getUserCount(HttpServletRequest request,String userType) {
		// TODO Auto-generated method stub
		ResponseModel responseModel = null;
			try {	
				Long userId = Long.parseLong(getUserId(request));
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
				System.out.println("userId "+userId);
				System.out.println("userType "+userType);
				SowingSeason sowingSeason = generalService.getCurrentSeason();
				System.out.println("sowingSeason "+simpleDateFormat.parse(sowingSeason.getStartingMonth())+sowingSeason.getEndingMonth());
				// Parse dates from the sowingSeason object
				Date startDate = simpleDateFormat.parse(sowingSeason.getStartingMonth());
				Date endDate = simpleDateFormat.parse(sowingSeason.getEndingMonth());

				// Convert java.util.Date to java.sql.Timestamp if needed
				Timestamp startTimestamp = new Timestamp(startDate.getTime());
				Timestamp endTimestamp = new Timestamp(endDate.getTime());
				System.out.println("startTimestamp "+startTimestamp);

				String result=userMasterRepository.getUserCount(userId, userType,startTimestamp,endTimestamp);
				System.out.println("result "+result);
				  ObjectMapper jacksonObjMapper = new ObjectMapper();
				return responseModel = CustomMessages.makeResponseModel(jacksonObjMapper.readTree(result), CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			} catch (Exception e) {
				responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
						CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
				return responseModel;
			}
	}
	
	
	/**
	 * Retrieves all active users with pagination.
	 *
	 * @param request The HttpServletRequest object.
	 * @param paginationDao The {@code PaginationDao} object containing pagination parameters.
	 * @return The ResponseModel object representing the response containing the active users.
	 */
	public ResponseModel getAllActiveUserByPaginationNew(HttpServletRequest httpServletRequest,
			PaginationDao paginationDao) {
		ResponseModel responseModel = null;
		try {
			String search=paginationDao.getSearch()!=null?paginationDao.getSearch():"";
			Long userId = Long.parseLong(getUserId(httpServletRequest));
			
			String result=userMasterRepository.getUserDetailsCountFilter(userId, "Users",search,(paginationDao.getRoleId()!=null?paginationDao.getRoleId():0l));
			  ObjectMapper jacksonObjMapper = new ObjectMapper();
			  jacksonObjMapper.readTree(result);
			  
			  
			String data=userMasterRepository.getUserDetailsFilter(userId, "Users", paginationDao.getPage(),paginationDao.getLimit(), paginationDao.getSortField(), paginationDao.getSortOrder(),search,(paginationDao.getRoleId()!=null?paginationDao.getRoleId():0l));
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<UserReturnDAO> finalResult = mapper.readValue(data,
					typeFactory.constructCollectionType(List.class, UserReturnDAO.class));
			Map<String, Object> responseData = new HashMap();
			responseData.put("data", finalResult);
			responseData.put("total", 10);
			responseData.put("page", paginationDao.getPage());
			responseData.put("limit", paginationDao.getLimit());
			responseData.put("sortField", paginationDao.getSortField());
			responseData.put("sortOrder", paginationDao.getSortOrder());
			responseData.put("countDetails", jacksonObjMapper.readTree(result));
			

			return CustomMessages.makeResponseModel(responseData, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (

				Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}
	

	/**
	 * Retrieves the accessible boundary by a list of user inputs.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The {@code UserInputDAO} object containing the user input data.
	 * @return The ResponseModel object containing the accessible boundary information.
	 */
	public ResponseModel getAccessibleBoundaryByListNew(HttpServletRequest httpServletRequest, UserInputDAO userInputDAO) {
		ResponseModel responseModel = CustomMessages.makeResponseModel(null, CustomMessages.GET_RECORD,
				CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		List<StateLgdMaster> stateList = new ArrayList<>();
		List<DistrictLgdMaster> accessibleDistricts = new ArrayList<>();
		List<SubDistrictLgdMaster> accessibleSubDistricts = new ArrayList<>();
		List<VillageLgdMaster> accessibleVillages = new ArrayList<>();
		try {
			String userId = getUserId(httpServletRequest);
			Long id = Long.parseLong(userId);
			List<Long> codeList=null;
			switch (userInputDAO.getBoundaryType()) {
			case "state":
//				String stateList = getJoinedValuefromList(requestDAO.getStateLgdCodeList());
				
				break;
			case "district":
				codeList=userInputDAO.getStateLGDCodeList();
				break;
			case "subDistrict":
				
				codeList=userInputDAO.getDistrictLgdCodeList();
				break;
			case "village":
				codeList=userInputDAO.getSubDistrictLgdCodeList();
				break;
			}
			String codes = getJoinedValuefromList(codeList);
			String response=userVillageMappingRepository.getUserWiseBoundaryDetails(id, userInputDAO.getBoundaryType(), codes);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));
			responseModel.setData(resultObject);
			return responseModel;

		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}
	
	private String getJoinedValuefromList(List<Long> list) {
		try {
			return list.stream().map(i -> i.toString()).collect(Collectors.joining(", "));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * @param request
	 * @param userInputDAO
	 * @return
	 */
	public ResponseModel getAccessibleBoundaryByListV3(HttpServletRequest request, UserInputDAO userInputDAO) {
		// TODO Auto-generated method stub
		
		ResponseModel responseModel = CustomMessages.makeResponseModel(null, CustomMessages.GET_RECORD,
				CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);


		try {
			String userId = getUserId(request);
			
			Long id = Long.parseLong(userId);
			
			Boolean isRedis=false;
			String userKey=redisAppName+"_"+Constants.REDIS_USER_KEY+userId;
			Object userDataObj = redisService.getValue(userKey);
			List<Long> codeList=null;
			if (userDataObj != null) {
				JSONObject responseJson = new JSONObject(userDataObj.toString());
				

				
				switch (userInputDAO.getBoundaryType()) {
				case "state":
					if (responseJson.has("stateLGDCodeList")) {
						ObjectMapper mapper = new ObjectMapper(); 
						List<Long> stateListRedis = Arrays.asList(mapper.readValue(responseJson.get("stateLGDCodeList").toString(), Long[].class)); 
						String stateData = (String) redisService.getValue(Constants.STATE_LGD_MASTER);
						List<RedisBoundaryDAO> redisStateLgdMasterList = Arrays.asList(mapper.readValue(stateData, RedisBoundaryDAO[].class));
						List<RedisBoundaryDAO> tmpList =new ArrayList<>();
						if(redisStateLgdMasterList!=null && redisStateLgdMasterList.size()>0) {
							
							tmpList=redisStateLgdMasterList.stream()
					           .filter(e -> stateListRedis.stream().anyMatch(name -> name.equals(e.getStateLgdCode())))
					           .collect(Collectors.toList());
						}
						
//						responseModel.setData(tmpList);
						if(tmpList!=null && tmpList.size()>0) {
							responseModel.setData(tmpList.stream()
									.sorted(Comparator.comparing(RedisBoundaryDAO::getStateName))
											.collect(Collectors.toList()));	
						}
						isRedis=true;
						
					}else {
						isRedis=false;
					}
					
					break;
				case "district":
					if (responseJson.has("districtLgdCodeList")) {
						ObjectMapper mapper = new ObjectMapper(); 
						List<Long> districtListRedis = Arrays.asList(mapper.readValue(responseJson.get("districtLgdCodeList").toString(), Long[].class)); 
						String districtData = (String) redisService.getValue(Constants.DISTRICT_LGD_MASTER);
						List<RedisBoundaryDAO> tmpList =new ArrayList<>();
						List<RedisBoundaryDAO> redisDistrictLgdMasterList = Arrays.asList(mapper.readValue(districtData, RedisBoundaryDAO[].class));
						
						if(redisDistrictLgdMasterList!=null && redisDistrictLgdMasterList.size()>0) {
							
							tmpList=redisDistrictLgdMasterList.stream()
					           .filter(e -> districtListRedis.stream().anyMatch(name -> name.equals(e.getDistrictLgdCode())) &&
					        		   userInputDAO.getStateLGDCodeList().stream().anyMatch(name -> name.equals(e.getStateLgdCode())))
					           .collect(Collectors.toList());
						}
						
							
						if(tmpList!=null && tmpList.size()>0) {
							responseModel.setData(tmpList.stream()
									.sorted(Comparator.comparing(RedisBoundaryDAO::getDistrictName))
											.collect(Collectors.toList()));	
						}
						isRedis=true;
						
					}else {
						codeList=userInputDAO.getStateLGDCodeList();
						isRedis=false;
					}
					

					break;
				case "subDistrict":
					
					if (responseJson.has("subDistrictLgdCodeList")) {
						ObjectMapper mapper = new ObjectMapper(); 
						List<Long> subdistrictListRedis = Arrays.asList(mapper.readValue(responseJson.get("subDistrictLgdCodeList").toString(), Long[].class)); 
						List<RedisBoundaryDAO> tmpList =new ArrayList<>();
						userInputDAO.getDistrictLgdCodeList().forEach(action->{
							String subDistrictData;
							try {
								subDistrictData = (String) redisService.getValue(Constants.SUBDISTRICT_LGD_MASTER + "_" + action);
								List<RedisBoundaryDAO> redisDistrictLgdMasterList = Arrays.asList(mapper.readValue(subDistrictData, RedisBoundaryDAO[].class));
								
								if(redisDistrictLgdMasterList!=null && redisDistrictLgdMasterList.size()>0) {
									tmpList.addAll(redisDistrictLgdMasterList.stream()
									           .filter(e -> subdistrictListRedis.stream().anyMatch(value -> value.equals(e.getSubDistrictLgdCode())))
									           .collect(Collectors.toList()));
									
								}
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}	

						});
						if(tmpList!=null && tmpList.size()>0) {
							responseModel.setData(tmpList.stream()
									.sorted(Comparator.comparing(RedisBoundaryDAO::getSubDistrictName))
											.collect(Collectors.toList()));	
						}
							
						isRedis=true;
					}else {
						codeList=userInputDAO.getDistrictLgdCodeList();	
						isRedis=false;
					}
					
					
					break;
				case "village":
					if (responseJson.has("villageLgdCodeList")) {
						
						ObjectMapper mapper = new ObjectMapper(); 
						List<Long> villageListRedis = Arrays.asList(mapper.readValue(responseJson.get("villageLgdCodeList").toString(), Long[].class)); 
						List<RedisBoundaryDAO> tmpList =new ArrayList<>();
						userInputDAO.getSubDistrictLgdCodeList().forEach(action->{
							
							try {
								String villageData = (String) redisService.getValue(Constants.VILLAGE_LGD_MASTER + "_" + action);
								List<RedisBoundaryDAO> redisDistrictLgdMasterList = Arrays.asList(mapper.readValue(villageData, RedisBoundaryDAO[].class));
								
								if(redisDistrictLgdMasterList!=null && redisDistrictLgdMasterList.size()>0) {
									tmpList.addAll(redisDistrictLgdMasterList.stream()
									           .filter(e -> villageListRedis.stream().anyMatch(value -> value.equals(e.getVillageLgdCode())))
									           .collect(Collectors.toList()));
									
								}
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}	

						});
					
						
						if(tmpList!=null && tmpList.size()>0) {
							responseModel.setData(tmpList.stream()
									.sorted(Comparator.comparing(RedisBoundaryDAO::getVillageName))
											.collect(Collectors.toList()));	
						}
						isRedis=true;
					}else {
						codeList=userInputDAO.getSubDistrictLgdCodeList();
						isRedis=false;
					}
					break;
				}

			}else {
				isRedis=false;
				switch (userInputDAO.getBoundaryType()) {
				case "state":
//					String stateList = getJoinedValuefromList(requestDAO.getStateLgdCodeList());
					
					break;
				case "district":
					codeList=userInputDAO.getStateLGDCodeList();
					break;
				case "subDistrict":
					
					codeList=userInputDAO.getDistrictLgdCodeList();
					break;
				case "village":
					codeList=userInputDAO.getSubDistrictLgdCodeList();
					break;
				}
			}
			
			if(isRedis.equals(Boolean.FALSE)) {
				String codes = getJoinedValuefromList(codeList);
				String response=userVillageMappingRepository.getUserWiseBoundaryDetails(id, userInputDAO.getBoundaryType(), codes);
				
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				TypeFactory typeFactory = mapper.getTypeFactory();
				List<Object> resultObject = mapper.readValue(response,
						typeFactory.constructCollectionType(List.class, Object.class));
				responseModel.setData(resultObject);
			}

			return responseModel;

		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public ResponseModel getUserInformation(HttpServletRequest request) {
		try {
			SurveyorOutputDAO userOutput = new SurveyorOutputDAO();

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);
			
			Optional<UserMaster> opUser = userMasterRepository.findByUserId(Long.valueOf(userId));
			if (opUser.isPresent()) {
				UserMaster surveyor = opUser.get();
				BeanUtils.copyProperties(surveyor, userOutput);
				if (Objects.nonNull(surveyor.getDepartmentId())) {
					DepartmentMaster department = new DepartmentMaster(surveyor.getDepartmentId().getDepartmentId(),
							surveyor.getDepartmentId().getDepartmentName(),
							surveyor.getDepartmentId().getDepartmentType(),
							surveyor.getDepartmentId().getDepartmentCode());
					surveyor.setDepartmentId(department);
				}
				if (Objects.nonNull(surveyor.getDesignationId())) {
					DesignationMaster designation = new DesignationMaster(
							surveyor.getDesignationId().getDesignationId(),
							surveyor.getDesignationId().getDesignationName());
					surveyor.setDesignationId(designation);
				}
				UserBankDetail bankDetail = userBankDetailRepository.findByUserId_UserId(surveyor.getUserId());
				if (Objects.nonNull(bankDetail)) {
					userOutput.setUserBankDetail(new UserBankDetail(bankDetail.getUserBankName(),
							bankDetail.getUserBranchCode(), bankDetail.getUserIfscCode(),
							bankDetail.getUserBankAccountNumber(), bankDetail.getBankId().getBankId()));
				}
				
				userOutput.setUserRoleId(surveyor.getRoleId().getRoleId());
				userOutput.setUserRoleName(surveyor.getRoleId().getRoleName());
				
				userOutput.setGovernmentId(surveyor.getGovernmentId());
				
				if (Objects.nonNull(userOutput.getUserVillageLGDCode())) {
					VillageLgdMaster village = villageService
							.getVillageByLGDCode(userOutput.getUserVillageLGDCode().longValue());
					if (Objects.nonNull(village)) {
						userOutput.setStateName(Objects.nonNull(village.getStateLgdCode().getStateId())
								? village.getStateLgdCode().getStateName()
								: "");
						userOutput.setDistrictName(Objects.nonNull(village.getDistrictLgdCode().getDistrictId())
								? village.getDistrictLgdCode().getDistrictName()
								: "");
						userOutput.setTalukaName(Objects.nonNull(village.getSubDistrictLgdCode().getSubDistrictId())
								? village.getSubDistrictLgdCode().getSubDistrictName()
								: "");
						userOutput.setVillageName(Objects.nonNull(village) ? village.getVillageName() : "");
					}
				}

				return new ResponseModel(userOutput, "User " + CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			} else {
				return new ResponseModel(null, CustomMessages.USER_ID_NOT_FOUND, CustomMessages.NO_DATA_FOUND,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	public ResponseModel updateUserBankDetails(SurveyorInputDAO surveyorInput, HttpServletRequest request) {
		ResponseModel responseModel = new ResponseModel();
		String userId = CustomMessages.getUserId(request, jwtTokenUtil);
		try {
			if (Objects.nonNull(surveyorInput)) {
				Optional<UserMaster> opUser = userMasterRepository.findByUserId(Long.valueOf(userId));
				if (opUser.isPresent()) {

					UserBankDetail userBankDetail = userBankDetailRepository.findByUserId_UserId(Long.valueOf(userId));
					Optional<BankMaster> bankOp = bankRepository
							.findByBankIdAndIsDeletedFalse(surveyorInput.getUserBankId());

					if (bankOp.isPresent()) {
						BankMaster bankMaster = bankOp.get();
						if (userBankDetail != null) {
 
							userBankDetail.setBankId(bankMaster);
							userBankDetail.setUserBankName(bankMaster.getBankName());
							userBankDetail.setUserBankAccountNumber(surveyorInput.getUserBankAccountNumber());
							userBankDetail.setUserBranchCode(surveyorInput.getUserBranchCode());
							userBankDetail.setUserIfscCode(surveyorInput.getUserIfscCode());

							userBankDetail.setModifiedBy(userId);
							userBankDetail.setModifiedOn(new Timestamp(new Date().getTime()));
							userBankDetailRepository.save(userBankDetail);
						} else {

							userBankDetail = new UserBankDetail();
							
							userBankDetail.setUserId(opUser.get());
							userBankDetail.setBankId(bankMaster);
							userBankDetail.setUserBankName(bankMaster.getBankName());
							userBankDetail.setUserBankAccountNumber(surveyorInput.getUserBankAccountNumber());
							userBankDetail.setUserBranchCode(surveyorInput.getUserBranchCode());
							userBankDetail.setUserIfscCode(surveyorInput.getUserIfscCode());
							userBankDetail.setCreatedBy(userId);
							userBankDetail.setCreatedOn(new Timestamp(new Date().getTime()));
							userBankDetailRepository.save(userBankDetail);
						}
						
						return new ResponseModel(surveyorInput.getUserId(), "User " + CustomMessages.RECORD_UPDATE,
								CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
					}
				}
			}
			
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(null, CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public Map<String, String> getExternalTokenForCentralCore(CentralCoreAuthInputDAO userInputDAO) {
		ResponseModel responseModel = null;
		Map<String,String> outputObj = new HashMap<>();
		try {

			Optional<UserMaster> userOptional = null;
			userOptional = userMasterRepository.getUserWithoutStatus(userInputDAO.getUsername());

			if (userOptional.isPresent()) {

				if(!userOptional.get().getIsActive()) {
					outputObj.put("message", CustomMessages.INACTIVE_USER);
					return outputObj;
				}

				UserMaster usermaster = userOptional.get();

				if (encoder.matches(userInputDAO.getPassword(), usermaster.getUserPassword())) {

					UserDetails userDetails = jwtUserDetailsService.loadUserByUsername((usermaster.getUserId() + ""));
					String token = jwtTokenUtil.generateToken(userDetails, false);
					usermaster.setUserToken(token);

					userMasterRepository.save(usermaster);

					try {
						ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
						String userDataJson = ow.writeValueAsString(usermaster);


						String userKey=redisAppName+"_"+Constants.REDIS_USER_KEY+usermaster.getUserId();

						//Set user data into redis
						redisService.setValue(userKey, userDataJson);
						System.out.println(redisService.getValue(userKey));
						System.out.println(userKey);
//						Map<String,String> outputObj = new HashMap<>();
						outputObj.put("accessToken",token);
						outputObj.put("tokenType","Bearer");


					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					responseModel = CustomMessages.makeResponseModel(outputObj, CustomMessages.SUCCESS,
//							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				} else {
//					responseModel = CustomMessages.makeResponseModel(null, CustomMessages.INVALID_PASSWORD,
//							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
					outputObj.put("message", CustomMessages.INVALID_PASSWORD);
				}

			} else {
				outputObj.put("message", CustomMessages.USER_NOT_FOUND);
//				responseModel = CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
//						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

			return outputObj;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			outputObj.put("message",e.getMessage());
			return outputObj;
		}
	}

	public ResponseModel genrateToken(CheckIntegrityDAO checkIntegrityDAO) {
		ResponseModel responseModel = null;
		try {
			
			Instant currentTimestamp = Instant.now();
			checkIntegrityDAO.getDeviceToken();
			   int min = 10000000;  // Smallest 8-digit number
		        int max = 99999999;  // Largest 8-digit number

		        Random random = new Random();
		        int randomValue= random.nextInt(max - min + 1) + min;
		        
		        
//		        String tokenValue=org.apache.commons.codec.digest.DigestUtils.sha256Hex(randomValue+checkIntegrityDAO.getDeviceToken()+checkIntegrityDAO.getSecretKey()+currentTimestamp.toEpochMilli());
//		        String tokenValue=Blake2bHashUtility.calculateBlake2bHexHash(randomValue+checkIntegrityDAO.getDeviceToken()+checkIntegrityDAO.getSecretKey()+currentTimestamp.toEpochMilli());
		        String tokenValue=Blake2bHashUtility.calculateBlake2bHexHash(randomValue+checkIntegrityDAO.getDeviceToken()+checkIntegrityDAO.getSecretKey()+String.valueOf(System.nanoTime()));
		        if(checkIntegrityDAO.getDeviceToken()!=null) {
		        	
			       
			        Optional<UserDeviceIntegrityDetails> userOp=userDeviceIntegrityDetailsRepository.findByDeviceId(checkIntegrityDAO.getDeviceToken());
			        if(userOp.isPresent()) {
			        	userOp.get().setUniqueKey(tokenValue);
			        	
			        	userDeviceIntegrityDetailsRepository.save(userOp.get());
			        	//History store
			        	UserDeviceIntegrityDetailsHistory userDeviceIntegrityDetailsHistory=new UserDeviceIntegrityDetailsHistory();
			        	userDeviceIntegrityDetailsHistory.setDeviceId(userOp.get().getDeviceId());
			        	userDeviceIntegrityDetailsHistory.setUniqueKey(userOp.get().getUniqueKey());
			        	userDeviceIntegrityDetailsHistory.setIsActive(true);
			        	userDeviceIntegrityDetailsHistory.setIsDeleted(false);
			        	userDeviceIntegrityDetailsHistory.setDecodeIntegrityTokenResponse(userOp.get().getDecodeIntegrityTokenResponse());
			        	userDeviceIntegrityDetailsHistoryRepository.save(userDeviceIntegrityDetailsHistory);
			        	//History store end
			        }else {
			        	UserDeviceIntegrityDetails userDeviceIntegrityDetails=new UserDeviceIntegrityDetails();
			        	userDeviceIntegrityDetails.setDeviceId(checkIntegrityDAO.getDeviceToken());
			        	userDeviceIntegrityDetails.setUniqueKey(tokenValue);
			        	userDeviceIntegrityDetails.setIsActive(true);
			        	userDeviceIntegrityDetails.setIsDeleted(false);
			        	userDeviceIntegrityDetailsRepository.save(userDeviceIntegrityDetails);
			        	
			        	//History store start
			        	UserDeviceIntegrityDetailsHistory userDeviceIntegrityDetailsHistory=new UserDeviceIntegrityDetailsHistory();
			        	userDeviceIntegrityDetailsHistory.setDeviceId(checkIntegrityDAO.getDeviceToken());
			        	userDeviceIntegrityDetailsHistory.setUniqueKey(tokenValue);
			        	userDeviceIntegrityDetailsHistory.setIsActive(true);
			        	userDeviceIntegrityDetailsHistory.setIsDeleted(false);
			        	userDeviceIntegrityDetailsHistoryRepository.save(userDeviceIntegrityDetailsHistory);
			        	//History store end
			        }
			        responseModel=new ResponseModel(tokenValue, "User " + CustomMessages.GET_RECORD,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		        }else {
		        	return CustomMessages.makeResponseModel(null,"Device details not found. Please reinstall the app and try again. (Error: 116)",
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
//		        	responseModel=new ResponseModel(tokenValue, "User " + CustomMessages.GET_RECORD,
//							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		        }
		        
			
			} catch (Exception e) {
				e.printStackTrace();
		
				responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
						CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
				
			}
		return responseModel;
	}

	public ResponseModel integrityValidation(CheckIntegrityDAO checkIntegrityDAO) {
		// TODO Auto-generated method stub
		ResponseModel responseModel = null;
		googlePlayIntegrityService.appIntegrity(checkIntegrityDAO.getToken(),0l);
		try {	responseModel=new ResponseModel(checkIntegrityDAO, "User " + CustomMessages.GET_RECORD,
				CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
	
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			
		}
			return responseModel;
	}

	/**
     * fetch de active user.
     *
     *@param request The HttpServletRequest object.
     * @return  The ResponseModel object representing the response.
     */
	public ResponseModel deActiveUser(HttpServletRequest request) {
		ResponseModel responseModel = null;
		Long userId = Long.parseLong(getUserId(request));
		try {	
			Optional<UserMaster> userOp=userMasterRepository.findByUserId(userId);
//			&& userOp.get().getUserMobileNumber().equals("6358873724")
			if(userOp.isPresent()) {
				UserMaster userMaster=userOp.get();
				userMaster.setModifiedBy(userId+"");
				userMaster.setIsActive(false);
				userMaster.setIsDeleted(true);
				userMaster.setUserToken(null);
				userMasterRepository.save(userMaster);
				responseModel=new ResponseModel(null,  CustomMessages.INACTIVE_USER_MESSAGE,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			}else {
				return CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
	
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			
		}
			return responseModel;
	}

}
