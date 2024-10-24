package com.amnex.agristack.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.UploadLandAndOwnershipDetailsDto;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.UPStateRoRDataResponseDAO;
import com.amnex.agristack.ror.dao.AssamRoRDataMainDAO;
import com.amnex.agristack.ror.dao.GJRoRDataMainDAO;
import com.amnex.agristack.ror.dao.ODRoRDataMainDAO;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.Verhoeff;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

import io.netty.util.internal.StringUtil;
import com.amnex.agristack.service.DummyLandRecordREpoService;

@Service
public class StateRoRDataDumpService {

	@Autowired
	private StatePrefixMasterRepository statePrefixMasterRepository;

	@Autowired
	GeneralService generalService;

	@Autowired
	private FarmerLandOwnershipService farmerLandOwnershipService;

	@Autowired
	private FarmerRegistryRepository farmerRegistryRepository;

	@Autowired
	private DummyLandOwnerShipMasterRepository dummyLandOwnerShipMasterRepository;

	@Autowired
	private DummyLandRecordREpo dummyLandRecordREpo;

	@Autowired
	private OwnerTypeRepository ownerTypeRepository;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	private CommonUtil commonUtil;
	@Autowired
	private StateUnitTypeRepository stateUnitTypeRepository;
	@Autowired
	private IdentifierTypeRepository identifierTypeRepository;

	@Autowired
	private FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	private  CustomMaterializedViewRepository customMaterializedViewRepository;

	@Autowired
	private FarmerLandOwnershipRegistryRepository farmerLandOwnershipRegistryRepository;

	@Autowired
	private MediaMasterService mediaMasterService;
	@Autowired
	private RoleMasterRepository roleMasterRepository;
	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	private UploadLandAndOwnershipFileHistoryRepo uploadLandAndOwnershipFileHistoryRepo;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UPRoRDataUploadLogRepository roRDataUploadLogRepository;

	@Autowired
	private UploadRoRDataService uploadRoRDataService;

	@Autowired
	private DummyLandRecordREpoService dummyLandRecordREpoService;

	@Value("${media.folder.document}")
	private String folderDocument;

	@Value("${file.upload-dir}")
	private String path;

	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("xlsx", "csv", "xls");
	private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv");
	private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);

	public ResponseModel uploadUPStateDataByVillageCode(Integer villageLgdCode, HttpServletRequest request) {
		try {
//			String key = (request.getHeader("enckey") != null) ? request.getHeader("enckey") : "UtaDataAgriData";
			String key = "UtaDataAgriData";

			List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository.findByIsUploadedIsNull();

			return fetchUPVillageDataByVillageCode(villageLgdCode, key, true);

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	@Transactional
	private ResponseModel fetchUPVillageDataByVillageCode(Integer villageLgdCode, String key, boolean fullUpload) {

		try {
			String encrypted = generalService.encryptVillageLgdCode(villageLgdCode.toString(), key);

			encrypted = URLEncoder.encode(encrypted, StandardCharsets.UTF_8.toString());

			RestTemplate restTemplate = new RestTemplate();
			String url = "https://agristack.gov.in/fetchUPRoRDataV1/" + encrypted;
//			String url = "https://upbhulekh.gov.in/WS_AGRIROR/agriWithWkt?villcode=" + encrypted;

			ResponseEntity<UPStateRoRDataResponseDAO[]> responseEntity = restTemplate.getForEntity(url,
					UPStateRoRDataResponseDAO[].class);

			/*
				// read Json file and comment IF condition of responseEntity
				String jsonFilePath = "up_json/"+villageLgdCode.toString()+".json";
				UPStateRoRDataResponseDAO[] stateRoRDAOs = readJsonFile(jsonFilePath);
			 */

			if (responseEntity != null) {

				UPStateRoRDataResponseDAO[] stateRoRDAOs = responseEntity.getBody();

				if (stateRoRDAOs != null && stateRoRDAOs.length > 0) {

					if (fullUpload == true) {
						farmerLandOwnershipRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(villageLgdCode));
						farmlandPlotRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(villageLgdCode));
					}

					dummyLandOwnerShipMasterRepository.deleteAllByVillageCode(Long.valueOf(villageLgdCode));
					dummyLandRecordREpo.deleteAllByVillageCode(Long.valueOf(villageLgdCode));

					List<UPStateRoRDataResponseDAO> plotDAOs = Arrays.asList(stateRoRDAOs);

					uploadAllDataToTempTable(plotDAOs, fullUpload);

				} else {
					List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository
							.findByVillageLgdCode(Long.valueOf(villageLgdCode));
					for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
						upRoRDataUploadLog.setIsUploaded(false);
						upRoRDataUploadLog.setCount(0);
						upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
						roRDataUploadLogRepository.save(upRoRDataUploadLog);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

		return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
	}

	private void uploadAllDataToTempTable(List<UPStateRoRDataResponseDAO> stateRoRDAOs, boolean fullUpload) {
		try {
//			new Thread() {
//				public void run() {

			Integer villageCode = stateRoRDAOs.get(0).getVillagecode();
			List<String> existingSurveyNo = farmlandPlotRegistryRepository
					.findAllByFprSurveyNumberByFprVillageLgdCode(Long.valueOf(villageCode));

			Set<Long> villageList = new HashSet<>();

			List<DummyLandOwnerShipMaster> finalLandOwners = new ArrayList<>();
			List<DummyLandRecords> finalPlots = new ArrayList<>();

			System.err.println("uploadAllDataToTempTable ::  " + new Timestamp(new Date().getTime()));

			stateRoRDAOs.stream().forEach((plotDAO) -> {

				if (!plotDAO.getOwner().isEmpty()) {
					plotDAO.getOwner().stream().forEach((owner) -> {

						DummyLandOwnerShipMaster dummyLandOwnerShipMaster = new DummyLandOwnerShipMaster();
						DummyLandRecords dummyLandRecords = new DummyLandRecords();

						dummyLandRecords.setSurveyNumber(owner.getKhasrano());
//						dummyLandRecords.setSubSurveyNumber("");
						dummyLandRecords.setSubSurveyNumber(owner.getUniquecode());
						dummyLandRecords.setVillageLgdCode(Long.valueOf(plotDAO.getVillagecode()));
						try {

							WKTReader wktReader = new WKTReader();
//									Geometry geometry = wktReader.read(plotDAO.getPlotgeometry().replace("\"", ""));
//									geometry.setSRID(4326);

							String wktString = dummyLandRecordREpo.getGeometryFromWKT(
									plotDAO.getPlotgeometry().replace("\"", ""), plotDAO.getProjectioncode(), 4326);
							Geometry geometry = wktReader.read(wktString);
							geometry.setSRID(4326);
							dummyLandRecords.setGeom(geometry);

							GeometryFactory geometryFactory = new GeometryFactory();
							geometryFactory.createGeometry(geometry);

							// geometryFactory.s
							dummyLandRecords.setGeom(geometry);

						} catch (Exception e) {
							System.err.println("Error In geometry :: " + plotDAO.getPlotgeometry());

						}

						villageList.add(Long.valueOf(plotDAO.getVillagecode()));

						dummyLandOwnerShipMaster.setVillageLgdCode(Long.valueOf(plotDAO.getVillagecode()));

						dummyLandOwnerShipMaster.setSurveyNumber(owner.getKhasrano());
//						dummyLandOwnerShipMaster.setSubSurveyNumber("");
						dummyLandOwnerShipMaster.setSubSurveyNumber(owner.getUniquecode());
						dummyLandOwnerShipMaster.setOwnerNo(owner.getOwner_Number().toString());

						String ownerName = owner.getOwner_Name().substring(0,
								Math.min(owner.getOwner_Name().length(), 100));

						dummyLandOwnerShipMaster.setOwnerName(ownerName);
						dummyLandOwnerShipMaster.setIdentifierName(owner.getIndentifier_Name());
						dummyLandOwnerShipMaster.setIdentifierType(owner.getIndentifier_type());
						dummyLandOwnerShipMaster.setMainOwnerNo("1");
//								dummyLandOwnerShipMaster.setTotalHa(owner.getExtend().toString());
						dummyLandOwnerShipMaster.setTotalHa(owner.getTotal_Area().toString());

						if (dummyLandRecords.getGeom() != null
								&& owner.getLand_Usage_type().equalsIgnoreCase("Agriculture")
								&& owner.getOwnership_type().equalsIgnoreCase("Private")
								&& dummyLandOwnerShipMaster != null && dummyLandRecords != null) {

							Boolean uploadFlag = true;
							if (!fullUpload && (existingSurveyNo.contains(dummyLandRecords.getSurveyNumber()))) {
								uploadFlag = false;
							}

							if (Boolean.TRUE.equals(uploadFlag)) {
								finalLandOwners.add(dummyLandOwnerShipMaster);
								finalPlots.add(dummyLandRecords);
							}
						}

					});
				}
			});

			if (!finalLandOwners.isEmpty() && !finalPlots.isEmpty()) {
				dummyLandOwnerShipMasterRepository.saveAll(finalLandOwners);
				dummyLandRecordREpo.saveAll(finalPlots);
			}

			if (!CollectionUtils.isEmpty(villageList)) {

				villageList.forEach(village -> {
//					uploadDataAtSurveyLevel(village);
					uploadRoRDataService.uploadDataAtSurveyLevelV2(village);

					List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository.findByVillageLgdCode(village);

					for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
						upRoRDataUploadLog.setIsUploaded(true);
						if (fullUpload == false) {
							upRoRDataUploadLog.setIsReUploadDone(true);
						}
						upRoRDataUploadLog.setCount(stateRoRDAOs.size());
						upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
						roRDataUploadLogRepository.save(upRoRDataUploadLog);

						System.out.println(village + " :: Completed Re Upload ");
					}
				});

			}
//				}
//			}.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void uploadDataAtOwnerLevel1(Long villageCode) {
		try {

			List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters = dummyLandOwnerShipMasterRepository
					.findByVillageLgdCode(villageCode);

			List<DummyLandOwnerShipMaster> dummyLandOwnerSameOwner = new ArrayList<>();
			List<DummyLandOwnerShipMaster> dummyLandOwnerDifferentOwner = new ArrayList<>();

			// String
			dummyLandOwnerSameOwner = dummyLandOwnerShipMasters.parallelStream()
					.filter(dummyLandDetails -> dummyLandDetails.getMainOwnerNo().trim()
							.equalsIgnoreCase(dummyLandDetails.getOwnerNo().trim()))
					.collect(Collectors.toList());

			dummyLandOwnerDifferentOwner = dummyLandOwnerShipMasters
					.parallelStream().filter(dummyLandDetails -> dummyLandDetails != null && !(dummyLandDetails
							.getMainOwnerNo().trim().equalsIgnoreCase(dummyLandDetails.getOwnerNo().trim())))
					.collect(Collectors.toList());

			saveAllOwnerLandDetails(dummyLandOwnerSameOwner);
			if (!CollectionUtils.isEmpty(dummyLandOwnerDifferentOwner)) {
				saveAllOwnerLandDetails(dummyLandOwnerDifferentOwner);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void uploadDataAtSurveyLevel1(Long villageCode) {
		try {
			List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters = dummyLandOwnerShipMasterRepository
					.findByVillageLgdCode(villageCode);

			Map<String, List<DummyLandOwnerShipMaster>> dummyLandOwnerShipMastersMap = dummyLandOwnerShipMasters
					.stream().collect(Collectors.groupingBy(DummyLandOwnerShipMaster::getSurveyNumber));

			System.out.println(villageCode + " :: uploadDataAtSurveyLevel :: " + dummyLandOwnerShipMastersMap.size()
					+ " :: " + new Timestamp(new Date().getTime()));

			VillageLgdMaster villageLgdMaster = villageLgdMasterRepository.findByVillageLgdCode(villageCode);
			StateUnitTypeMaster stateUnitTypeMaster = stateUnitTypeRepository
					.findByStateLgdCodeAndIsDefault(villageLgdMaster.getStateLgdCode(), true);

//			List<StateUnitTypeMaster> stateUnitTypeMasters = stateUnitTypeRepository
//					.findByUnitTypeDescEngIgnoreCaseAndStateLgdCode("Lessa",villageLgdMaster.getStateLgdCode());
//			
//			if(stateUnitTypeMasters.size() > 0) {
//				stateUnitTypeMaster = stateUnitTypeMasters.get(0);
//			}
//			
			Optional<StatePrefixMaster> statePrefix = statePrefixMasterRepository
					.findById(villageLgdMaster.getStateLgdCode().getStateLgdCode());
			String stateShortName = statePrefix.get().getStateShortName().toString();

			OwnerType ownerType = ownerTypeRepository.findByOwnerTypeDescEng("Joint");

			List<FarmerLandOwnershipRegistry> finalFarmerLandOwnershipRegistryList = new ArrayList<>();

			for (Entry<String, List<DummyLandOwnerShipMaster>> dummyLandOwnerShipMaster : dummyLandOwnerShipMastersMap
					.entrySet()) {
				try {

					Long mainOwnerId = 1L;

					List<DummyLandOwnerShipMaster> landOwners = dummyLandOwnerShipMaster.getValue();

					Double totalPlotArea = landOwners.stream().mapToDouble(x -> Double.valueOf(x.getTotalHa()))
							.findFirst().getAsDouble();
//					Double totalPlotArea = landOwners.stream().mapToDouble(x -> Double.valueOf(x.getTotalHa())).sum();

					FarmerLandOwnershipRegistry farmerLandOwnershipRegistryMain = new FarmerLandOwnershipRegistry();
					farmerLandOwnershipRegistryMain.setIsActive(true);
					farmerLandOwnershipRegistryMain.setIsDeleted(false);

					farmerLandOwnershipRegistryMain.setOwnerNoAsPerRor(mainOwnerId.toString());

					farmerLandOwnershipRegistryMain
							.setOwnerType(Integer.parseInt(ownerType.getOwnerTypeId().toString()));
					farmerLandOwnershipRegistryMain.setMainOwnerNoAsPerRor(mainOwnerId.intValue());

					// if(farmerLandOwnershipRegistries.size()>1) {
					List<DummyLandRecords> plotList = dummyLandRecordREpo.findBySurveyNumberAndVillageLgdCode(
							dummyLandOwnerShipMaster.getKey(),
							dummyLandOwnerShipMaster.getValue().get(0).getVillageLgdCode());
					if (!CollectionUtils.isEmpty(plotList)) {

						FarmlandPlotRegistry farmlandPlotRegistry = new FarmlandPlotRegistry();
//						farmlandPlotRegistry.setFarmlandId(
//								commonUtil.genrateLandParcelId(villageLgdMaster.getStateLgdCode().getStateLgdCode()));

						farmlandPlotRegistry.setFarmlandId(commonUtil.genrateLandParcelIdV2(
								villageLgdMaster.getStateLgdCode().getStateLgdCode(), stateShortName));

						farmlandPlotRegistry.setLandParcelId(plotList.get(0).getSurveyNumber());
						farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());
						// farmlandPlotRegistry.setUtUnitTypeMasterId(plotList.get(0).getUtUnitTypeMasterId());
						farmlandPlotRegistry.setVillageLgdMaster(villageLgdMaster);

//						farmlandPlotRegistry
//								.setPlotArea(Double.parseDouble(dummyLandOwnerShipMaster.getValue().get(0).getTotalHa()));

						farmlandPlotRegistry.setPlotArea(totalPlotArea);

						farmlandPlotRegistry.setSurveyNumber(plotList.get(0).getSurveyNumber());
						farmlandPlotRegistry.setSubSurveyNumber(plotList.get(0).getSubSurveyNumber());

						if (stateUnitTypeMaster != null) {
							farmlandPlotRegistry.setUtUnitTypeMasterId(stateUnitTypeMaster);
						}

//						farmlandPlotRegistry = saveFarmLandPlotRegistry(farmlandPlotRegistry,stateShortName);
						farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);

						farmerLandOwnershipRegistryMain
								.setOwnerNamePerRor("The owner of Survey No " + farmlandPlotRegistry.getSurveyNumber());
						farmerLandOwnershipRegistryMain
								.setMainOwnerName(farmerLandOwnershipRegistryMain.getOwnerNamePerRor());
						farmerLandOwnershipRegistryMain.setFarmlandPlotRegistryId(farmlandPlotRegistry);
						farmerLandOwnershipRegistryMain.setExtentAssignedArea(farmlandPlotRegistry.getPlotArea());

						farmerLandOwnershipRegistryMain = farmerLandOwnershipRegistryRepository
								.save(farmerLandOwnershipRegistryMain);
					}
					// Double totalArea=0.0;
					Long ownerId = 1L;

					for (DummyLandOwnerShipMaster dummyLandOwnerShip : dummyLandOwnerShipMaster.getValue()) {
						FarmerLandOwnershipRegistry farmerLandOwnershipRegistry = new FarmerLandOwnershipRegistry();

//						ownerId = ownerId + 1;
						ownerId = Long.valueOf(dummyLandOwnerShip.getOwnerNo()) + 1;

						farmerLandOwnershipRegistry.setOwnerNoAsPerRor(ownerId.toString());
//						farmerLandOwnershipRegistry.setOwnerNoAsPerRor(dummyLandOwnerShip.getOwnerNo());

						farmerLandOwnershipRegistry
								.setOwnerType(Integer.parseInt(ownerType.getOwnerTypeId().toString()));
						farmerLandOwnershipRegistry.setMainOwnerNoAsPerRor(mainOwnerId.intValue());
						farmerLandOwnershipRegistry.setExtentAssignedArea(0.0);
						if (dummyLandOwnerShip.getOwnerName().length() >= 100) {
							farmerLandOwnershipRegistry
									.setOwnerNamePerRor(dummyLandOwnerShip.getOwnerName().substring(0, 99));

						} else {
							farmerLandOwnershipRegistry.setOwnerNamePerRor(dummyLandOwnerShip.getOwnerName());

						}
						farmerLandOwnershipRegistry
								.setFarmlandPlotRegistryId(farmerLandOwnershipRegistryMain.getFarmlandPlotRegistryId());
						farmerLandOwnershipRegistry
								.setMainOwnerName(farmerLandOwnershipRegistryMain.getOwnerNamePerRor());

						farmerLandOwnershipRegistry
								.setOwnerIdentifierNamePerRor(dummyLandOwnerShip.getIdentifierName());

						IdentifierTypeMaster identifierTypeMaster = identifierTypeRepository
								.findByIdentifierTypeDescEngIgnoreCase(
										dummyLandOwnerShip.getIdentifierType().toLowerCase());
						if (identifierTypeMaster != null) {
							farmerLandOwnershipRegistry.setOwnerIdentifierTypePerRor(
									identifierTypeMaster.getIdentifierTypeMasterId().intValue());
						}

						// totalArea+=farmerLandOwnershipRegistry.getExtentAssignedArea();
						finalFarmerLandOwnershipRegistryList.add(farmerLandOwnershipRegistry);
//						farmerLandOwnershipRegistryRepository.save(farmerLandOwnershipRegistry);

						ownerId++;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			farmerLandOwnershipRegistryRepository.saveAll(finalFarmerLandOwnershipRegistryList);

			System.out.println("SurveyLevel Done::  " + new Timestamp(new Date().getTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private FarmlandPlotRegistry saveFarmLandPlotRegistry(FarmlandPlotRegistry farmlandPlotRegistry, String stateCode) {
		try {
			farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);

		} catch (Exception e) {
			farmlandPlotRegistry.setFarmlandId(commonUtil.genrateLandParcelIdV3(stateCode));
			farmlandPlotRegistry = saveFarmLandPlotRegistry(farmlandPlotRegistry, stateCode);
		}
		return farmlandPlotRegistry;
	}

	private void saveAllOwnerLandDetails(List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters) {

		VillageLgdMaster villageLgdMaster = villageLgdMasterRepository
				.findByVillageLgdCode(dummyLandOwnerShipMasters.get(0).getVillageLgdCode());
		StateUnitTypeMaster stateUnitTypeMaster = stateUnitTypeRepository
				.findByStateLgdCodeAndIsDefault(villageLgdMaster.getStateLgdCode(), true);

		Optional<StatePrefixMaster> statePrefix = statePrefixMasterRepository
				.findById(villageLgdMaster.getStateLgdCode().getStateLgdCode());
		String stateShortName = statePrefix.get().getStateShortName().toString();

		for (DummyLandOwnerShipMaster dummyLandOwnerShipMaster : dummyLandOwnerShipMasters) {
			try {
				if (dummyLandOwnerShipMaster != null) {
					String surveyNumber = dummyLandOwnerShipMaster.getSurveyNumber();
					String subSurveyNumber = dummyLandOwnerShipMaster.getSubSurveyNumber();
					Long villageLgdCode = dummyLandOwnerShipMaster.getVillageLgdCode();

					/*
					 * List<FarmlandPlotRegistry> plotList = farmlandPlotRegistryRepository
					 * .findByVillageLgdMasterVillageLgdCodeAndSurveyNumberAndSubSurveyNumber(
					 * villageLgdCode, surveyNumber, subSurveyNumber);
					 */
					List<DummyLandRecords> plotList = dummyLandRecordREpo.findBySurveyNumberAndVillageLgdCode(
							dummyLandOwnerShipMaster.getSurveyNumber(), villageLgdCode);
					FarmerLandOwnershipRegistry farmerLandOwnershipRegistry = new FarmerLandOwnershipRegistry();
					farmerLandOwnershipRegistry.setIsActive(true);
					farmerLandOwnershipRegistry.setIsDeleted(false);
					farmerLandOwnershipRegistry
							.setMainOwnerNoAsPerRor(Integer.valueOf(dummyLandOwnerShipMaster.getMainOwnerNo()));
					farmerLandOwnershipRegistry.setOwnerNoAsPerRor(dummyLandOwnerShipMaster.getOwnerNo());
					farmerLandOwnershipRegistry.setOwnerNamePerRor(dummyLandOwnerShipMaster.getOwnerName());
					// farmerLandOwnershipRegistry.setMainOwnerName();
					//
					if (!CollectionUtils.isEmpty(plotList)) {

						if (dummyLandOwnerShipMaster.getOwnerNo()
								.equalsIgnoreCase(dummyLandOwnerShipMaster.getMainOwnerNo())) {

							FarmerRegistry farmerRegistry = new FarmerRegistry();
							String uniqueNumber = Verhoeff.getFarmerUniqueIdWithChecksum();
							farmerRegistry.setFarmerRegistryNumber(uniqueNumber);
							farmerRegistry.setFarmerNameLocal(dummyLandOwnerShipMaster.getOwnerName());
							farmerRegistry.setFarmerIdentifierNameLocal(dummyLandOwnerShipMaster.getIdentifierName());
							farmerLandOwnershipRegistry
									.setOwnerIdentifierNamePerRor(dummyLandOwnerShipMaster.getIdentifierName());
							farmerLandOwnershipRegistry.setMainOwnerName(dummyLandOwnerShipMaster.getOwnerName());
							OwnerType ownerType = ownerTypeRepository.findByOwnerTypeDescEng("Single");
							farmerLandOwnershipRegistry
									.setOwnerType(Integer.parseInt(ownerType.getOwnerTypeId().toString()));
							farmerRegistry = farmerRegistryRepository.save(farmerRegistry);
							// List<FarmerLandOwnershipRegistry>
							// farmerLandOwnershipRegistries=farmerLandOwnershipRegistryRepository.findByFarmlandPlotRegistryFarmlandPlotRegistryId(plotList.get(0).getFarmlandPlotRegistryId());

							// if(farmerLandOwnershipRegistries.size()>1) {

							FarmlandPlotRegistry farmlandPlotRegistry = new FarmlandPlotRegistry();

//							farmlandPlotRegistry.setFarmlandId(
//									commonUtil.genrateLandParcelId(villageLgdMaster.getStateLgdCode().getStateLgdCode()));

							farmlandPlotRegistry.setFarmlandId(commonUtil.genrateLandParcelIdV2(
									villageLgdMaster.getStateLgdCode().getStateLgdCode(), stateShortName));

							farmlandPlotRegistry.setLandParcelId(plotList.get(0).getSurveyNumber());
							// farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());
							farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());
							// farmlandPlotRegistry.setUtUnitTypeMasterId(plotList.get(0).getUtUnitTypeMasterId());
							farmlandPlotRegistry.setVillageLgdMaster(villageLgdMaster);
							farmlandPlotRegistry.setPlotArea(Double.parseDouble(dummyLandOwnerShipMaster.getTotalHa()));
							farmlandPlotRegistry.setSurveyNumber(plotList.get(0).getSurveyNumber());
							if (subSurveyNumber != null) {
								farmlandPlotRegistry.setSubSurveyNumber(subSurveyNumber);
							}
							if (stateUnitTypeMaster != null) {
								farmlandPlotRegistry.setUtUnitTypeMasterId(stateUnitTypeMaster);
							}

							farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);
							// farmLandPl

							// }
							// if(Farmer)
							farmerLandOwnershipRegistry
									.setExtentAssignedArea((Double.parseDouble(dummyLandOwnerShipMaster.getTotalHa())));
							farmerLandOwnershipRegistry.setFarmlandPlotRegistryId(farmlandPlotRegistry);
							farmerLandOwnershipRegistry.setFarmerRegistryId(farmerRegistry);
							farmerLandOwnershipRegistryRepository.save(farmerLandOwnershipRegistry);
						} else {
							FarmerRegistry farmerRegistry = new FarmerRegistry();
							farmerRegistry.setFarmerNameLocal(dummyLandOwnerShipMaster.getOwnerName());
							farmerRegistry.setFarmerIdentifierNameLocal(dummyLandOwnerShipMaster.getIdentifierName());
							farmerLandOwnershipRegistry.setMainOwnerName(dummyLandOwnerShipMaster.getOwnerName());
							OwnerType ownerType = ownerTypeRepository.findByOwnerTypeDescEng("Joint");
							farmerLandOwnershipRegistry
									.setOwnerType(Integer.parseInt(ownerType.getOwnerTypeId().toString()));
							farmerRegistry = farmerRegistryRepository.save(farmerRegistry);
							// farmerLandOwnershipRegistry.setFarmlandPlotRegistryId(plotList.get(0));
							farmerLandOwnershipRegistry
									.setExtentAssignedArea(Double.parseDouble(dummyLandOwnerShipMaster.getTotalHa()));
							farmerLandOwnershipRegistry
									.setOwnerIdentifierNamePerRor(dummyLandOwnerShipMaster.getIdentifierName());
							farmerLandOwnershipRegistry.setFarmerRegistryId(farmerRegistry);
							List<FarmerLandOwnershipRegistry> farmerLandOwnershipRegistries = farmerLandOwnershipRegistryRepository
									.findByMainOwnerNoAsPerRorAndFarmlandPlotRegistryIdSurveyNumberAndFarmlandPlotRegistryIdSubSurveyNumberAndFarmlandPlotRegistryIdVillageLgdMasterVillageLgdCode(
											Integer.parseInt(dummyLandOwnerShipMaster.getMainOwnerNo()), surveyNumber,
											subSurveyNumber, villageLgdCode);
							IdentifierTypeMaster identifierTypeMaster = identifierTypeRepository
									.findByIdentifierTypeDescEngIgnoreCase(
											dummyLandOwnerShipMaster.getIdentifierType().toLowerCase());
							if (identifierTypeMaster != null) {
								farmerLandOwnershipRegistry.setOwnerIdentifierTypePerRor(
										identifierTypeMaster.getIdentifierTypeMasterId().intValue());
							}
							if (!CollectionUtils.isEmpty(farmerLandOwnershipRegistries)) {
								farmerLandOwnershipRegistries.forEach(farmerLandOwnership -> {
									farmerLandOwnership
											.setOwnerType(Integer.parseInt(ownerType.getOwnerTypeId().toString()));
								});
								farmerLandOwnershipRegistryRepository.saveAll(farmerLandOwnershipRegistries);
								farmerLandOwnershipRegistry.setFarmlandPlotRegistryId(
										farmerLandOwnershipRegistries.get(0).getFarmlandPlotRegistryId());
								farmerLandOwnershipRegistry
										.setMainOwnerName(farmerLandOwnershipRegistries.get(0).getOwnerNamePerRor());

							} else {
								FarmlandPlotRegistry farmlandPlotRegistry = new FarmlandPlotRegistry();

//								farmlandPlotRegistry.setFarmlandId(commonUtil
//										.genrateLandParcelId(villageLgdMaster.getStateLgdCode().getStateLgdCode()));

								farmlandPlotRegistry.setFarmlandId(commonUtil.genrateLandParcelIdV2(
										villageLgdMaster.getStateLgdCode().getStateLgdCode(), stateShortName));

								farmlandPlotRegistry.setLandParcelId(plotList.get(0).getSurveyNumber());
								farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());
								// farmlandPlotRegistry.setUtUnitTypeMasterId(plotList.get(0).getUtUnitTypeMasterId());
								farmlandPlotRegistry.setVillageLgdMaster(villageLgdMaster);
								farmlandPlotRegistry
										.setPlotArea(Double.parseDouble(dummyLandOwnerShipMaster.getTotalHa()));
								farmlandPlotRegistry.setSurveyNumber(plotList.get(0).getSurveyNumber());
								/*
								 * if (plotList.get(0).getSubSurveyNumber() != null) {
								 * farmlandPlotRegistry.setSubSurveyNumber(plotList.get(0).getSubSurveyNumber())
								 * ; }
								 */
								if (subSurveyNumber != null) {
									farmlandPlotRegistry.setSubSurveyNumber(subSurveyNumber);
								}
								if (stateUnitTypeMaster != null) {
									farmlandPlotRegistry.setUtUnitTypeMasterId(stateUnitTypeMaster);
								}
								farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);
								farmerLandOwnershipRegistry.setFarmlandPlotRegistryId(farmlandPlotRegistry);

							}
							farmerLandOwnershipRegistryRepository.save(farmerLandOwnershipRegistry);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * read excel from upload module and save the land details in database
	 * 
	 * @param uploadLandAndOwnershipDetailsDto
	 * @return
	 */
//	public ResponseModel uploadExcelFile(UploadLandAndOwnershipDetailsDto uploadLandAndOwnershipDetailsDto,
//										 HttpServletRequest request) {
//
//		//Define allowed extensions and MIME types
//        List<String> allowedExtensions = Arrays.asList("xlsx", "csv", "xls");
//        List<String> allowedMimeTypes = Arrays.asList("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv");
//
//        // Define maximum file size in bytes (100MB)
//        long maxFileSize = 100 * 1024 * 1024; // 100MB in bytes
//
//		String userId = CustomMessages.getUserId(request, jwtTokenUtil);
//		System.out.println("userId "+userId);
//		UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistoryObj = new UploadLandAndOwnershipFileHistory();
//
//		if (!CollectionUtils.isEmpty(uploadLandAndOwnershipDetailsDto.getExcelFile())) {
//			// UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = new
//			// UploadLandAndOwnershipFileHistory();
//
//			for (MultipartFile file : uploadLandAndOwnershipDetailsDto.getExcelFile()) {
//
//				// Check file size
//				if(file.getSize() > maxFileSize) {
//					return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED,
//							CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_SIZE_LIMIT, CustomMessages.METHOD_POST);
//				}
//
//				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
//				// Check file extension
//				if(!allowedExtensions.contains(fileExtension.toLowerCase())) {
//					return new ResponseModel(null,CustomMessages.FILE_UPLOAD_FAILED,
//							CustomMessages.GET_DATA_ERROR,CustomMessages.FILE_EXTENSION_NOT_ALLOWED,CustomMessages.METHOD_POST);
//				}
//				// Check MIME type
//				String fileContentType = file.getContentType();
//				if(!allowedMimeTypes.contains(fileContentType)) {
//					return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED,
//							CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_TYPE_NOT_ALLOWED, CustomMessages.METHOD_POST);
//				}
//
//				// Check for invalid characters in filename
//				String fileName = file.getOriginalFilename();
//				if(Pattern.compile("[\\\\/:*?\"<>|]").matcher(fileName).find()){
//					return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED,
//							CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_INVALID_CHARACTERS, CustomMessages.METHOD_POST);
//				}
//
//				// Check for double extensions or double dots
//				if(fileName.split("\\.").length > 2) {
//					 return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED,
//							 CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_CONTAINS_EXTENSION, CustomMessages.METHOD_POST);
//				}
//
//				// Check for null bytes
//				if(fileName.contains("\0")) {
//					return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED,
//							CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_CONTAINS_NULL_BYTE, CustomMessages.METHOD_POST);
//				}
//
//				Set<Long> villageList = new HashSet<>();
//
//				UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = new UploadLandAndOwnershipFileHistory();
//				MediaMaster mediaMaster = mediaMasterService.storeFile(file, "agristack", folderDocument, 0);
//
//				System.out.println("mediaMaster "+mediaMaster);
//				System.out.println("mediaMaster "+mediaMaster.getMediaId());
//				uploadLandAndOwnershipFileHistory.setUploadedBy(Long.valueOf(userId));
//				uploadLandAndOwnershipFileHistory.setFileName(file.getOriginalFilename());
//				uploadLandAndOwnershipFileHistory.setMediaMaster(mediaMaster);
//				uploadLandAndOwnershipFileHistory.setContainIssue(false);
//				uploadLandAndOwnershipFileHistory.setIsread(true);
//				uploadLandAndOwnershipFileHistory.setIsDeletedExistingData(uploadLandAndOwnershipDetailsDto.getIsDeleteExistingData());
//				uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo
//						.save(uploadLandAndOwnershipFileHistory);
//
//				new Thread() {
//					public void run() {
//
//						int indexOfextension = file.getOriginalFilename().lastIndexOf(".");
//						String extension = file.getOriginalFilename().substring(indexOfextension + 1);
//						InputStream in;
//						if (extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("csv")) {
//							try {
//								// as per dicussion need to change to village
////								dummyLandOwnerShipMasterRepository.deleteAll();
////								dummyLandRecordREpo.deleteAll();
//								in = file.getInputStream();
//								Workbook workbook;
//
//								workbook = WorkbookFactory.create(in);
//
//								Sheet sheet = workbook.getSheetAt(0);
//								// Create a DataFormatter to format and get each cell's value as String
//								DataFormatter dataFormatter = new DataFormatter();
//
//								// 1. You can obtain a rowIterator and columnIterator and iterate over them
//								Iterator<Row> rowIterator = sheet.rowIterator();
//								rowIterator.next();
//								int rowCount = 0;
//								while (rowIterator.hasNext()) {
//									int count = 0;
//
//									Row row = rowIterator.next();
//									DummyLandOwnerShipMaster dummyLandOwnerShipMaster = new DummyLandOwnerShipMaster();
//									DummyLandRecords dummyLandRecords = new DummyLandRecords();
//
//									// Now let's iterate over the columns of the current row
//									Iterator<Cell> cellIterator = row.cellIterator();
//									while (cellIterator.hasNext()) {
//
//										Cell cell = cellIterator.next();
//										String cellValue = dataFormatter.formatCellValue(cell);
//										String cells;
//										// switch (cellValue) {
//
//										if (count == 0) {
//											dummyLandRecords.setSurveyNumber(cellValue);
//											dummyLandOwnerShipMaster.setSurveyNumber(cellValue);
//											// break;
//										}
//										if (count == 1) {
//											if (!cellValue.equalsIgnoreCase("na")) {
//												dummyLandRecords.setSubSurveyNumber(cellValue);
//												dummyLandOwnerShipMaster.setSubSurveyNumber(cellValue);
//											} else {
//												dummyLandRecords.setSubSurveyNumber("");
//												dummyLandOwnerShipMaster.setSubSurveyNumber("");
//											}
//										}
//
//										// break;
//										// case "village_lgd_code":
//										// owner number
//										if (count == 2) {
//											if (!cellValue.equalsIgnoreCase("na")) {
//												dummyLandOwnerShipMaster.setOwnerNo(cellValue);
//											}
//										}
//
//										// owner name
//										if (count == 3) {
//											if (!cellValue.equalsIgnoreCase("na")) {
//												String ownerName = cellValue.substring(0,
//														Math.min(cellValue.length(), 100));
//												dummyLandOwnerShipMaster.setOwnerName(ownerName);
//											}
//										}
//										// father or identifier name
//										if (count == 4) {
//											if (!cellValue.equalsIgnoreCase("na")) {
//												dummyLandOwnerShipMaster.setIdentifierName(cellValue);
//											}
//										}
//
//										// identifier type
//										if (count == 5) {
//											if (!cellValue.equalsIgnoreCase("na")) {
//												dummyLandOwnerShipMaster.setIdentifierType(cellValue);
//											}
//										}
//										// main owner number
//										if (count == 6) {
//											if (!cellValue.equalsIgnoreCase("na")) {
//												dummyLandOwnerShipMaster.setMainOwnerNo(cellValue);
//											}
//										}
//
//										// total area ha
//										if (count == 7) {
////
//											if (!cellValue.equalsIgnoreCase("na")) {
//												dummyLandOwnerShipMaster.setTotalHa(cellValue);
//											}
//										}
//										// total area ha
//										/*
//										 * if (count == 8) { System.out.print(cellValue + "\t"); // cell =
//										 * cellIterator.next(); // cells = dataFormatter.formatCellValue(cell); if
//										 * (!cellValue.equalsIgnoreCase("na")) {
//										 * dummyLandRecords.setTotalArea(cellValue); } }
//										 */
//										if (count == 8) {
//											try {
//												dummyLandRecords.setVillageLgdCode(Long.parseLong(cellValue));
//												dummyLandOwnerShipMaster.setVillageLgdCode(Long.parseLong(cellValue));
//
//												if (!villageList.contains(Long.parseLong(cellValue))) {
//													villageList.add(Long.parseLong(cellValue));
//													dummyLandOwnerShipMasterRepository
//															.deleteAllByVillageCode(Long.parseLong(cellValue));
//													dummyLandRecordREpo
//															.deleteAllByVillageCode(Long.parseLong(cellValue));
//												}
//											} catch (Exception e) {
//
//											}
//										}
//
//										// break;
//										// case "geom_str":
//										if (count == 9) {
//											try {
//
//												WKTReader wktReader = new WKTReader();
////												Geometry geometry = wktReader.read(plotDAO.getPlotgeometry().replace("\"", ""));
////												geometry.setSRID(4326);
//
//												String wktString = "";
//												if ("GEOJSON".equalsIgnoreCase(
//														uploadLandAndOwnershipDetailsDto.getGeometryType())) {
//
//													System.out.println("cellValue "+cellValue);
//													System.out.println(uploadLandAndOwnershipDetailsDto.getProjectionCode());
//													// wktString = dummyLandRecordREpoService.getWKTFromGeoJson1(cellValue,
//													// 		uploadLandAndOwnershipDetailsDto.getProjectionCode(), 4326);
//
//													wktString = dummyLandRecordREpo.getWKTFromGeoJson(cellValue,
//															uploadLandAndOwnershipDetailsDto.getProjectionCode(), 4326);
//													System.out.println("wktString "+wktString);
////													geometry = dummyLandRecordREpo.getGeometryFromGeoJson(cellValue,
////															uploadLandAndOwnershipDetailsDto.getProjectionCode(), 4326);
//												} else if ("WKT".equalsIgnoreCase(
//														uploadLandAndOwnershipDetailsDto.getGeometryType())) {
//
//													wktString = dummyLandRecordREpo.getGeometryFromWKT(cellValue,
//															uploadLandAndOwnershipDetailsDto.getProjectionCode(), 4326);
//												}
//
//												Geometry geometry = wktReader.read(wktString);
//											//	System.out.println("geometry "+geometry);
//												geometry.setSRID(4326);
//												dummyLandRecords.setGeom(geometry);
//
//												GeometryFactory geometryFactory = new GeometryFactory();
//												geometryFactory.createGeometry(geometry);
//
//												dummyLandRecords.setGeom(geometry);
//
//											} catch (Exception e) {
//												e.printStackTrace();
//											}
//
////											WKTReader wktReader = new WKTReader();
////											Geometry geometry = wktReader.read(cellValue);
////											geometry.setSRID(4326);
////											dummyLandRecords.setGeom(geometry);
////											GeometryFactory geometryFactory = new GeometryFactory();
////											geometryFactory.createGeometry(geometry);
////											dummyLandRecords.setGeom(geometry);
//										}
//										count++;
//									}
//									rowCount++;
//									dummyLandOwnerShipMasterRepository.save(dummyLandOwnerShipMaster);
//								//	System.out.println("dummyLandRecords "+dummyLandRecords);
//									dummyLandRecordREpo.save(dummyLandRecords);
//								}
//
//							} catch (Exception e) {
//								e.printStackTrace();
//
//								UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo
//										.findByMediaMasterMediaId(mediaMaster.getMediaId());
//
//								uploadLandAndOwnershipFileHistory.setContainIssue(true);
//								uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo
//										.save(uploadLandAndOwnershipFileHistory);
//							}
//
//						}
//						System.out.println("villageList "+villageList);
//
//						if (!CollectionUtils.isEmpty(villageList)) {
//							// readExcelAndUploadDataOfOwnerForGjState();
//
//							villageList.forEach(village -> {
////								readExcelAndUploadDataOfOwnerWithVillageCode(village, mediaMaster);
//								if(uploadLandAndOwnershipDetailsDto.getIsDeleteExistingData()) {
//									farmerLandOwnershipRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(village));
//									farmlandPlotRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(village));
//								}
//
//								UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo
//										.findByMediaMasterMediaId(mediaMaster.getMediaId());
//										System.out.println("uploadLandAndOwnershipFileHistory 1"+uploadLandAndOwnershipFileHistory);
//
//								try {
//									if ("OWNERLEVEL".equalsIgnoreCase(
//											uploadLandAndOwnershipDetailsDto.getPlotGenerationType())) {
////										uploadDataAtOwnerLevel(village);
//										uploadRoRDataService.uploadDataAtOwnerLevelV2(village);
//									} else if ("SURVEYLEVEL".equalsIgnoreCase(
//											uploadLandAndOwnershipDetailsDto.getPlotGenerationType())) {
////										uploadDataAtSurveyLevel(village);
//										uploadRoRDataService.uploadDataAtSurveyLevelV2(village);
//									}
//
//									uploadLandAndOwnershipFileHistory.setIsread(true);
//
//								} catch (Exception e) {
//									System.out.println("uploadLandAndOwnershipFileHistory 2"+uploadLandAndOwnershipFileHistory);
//									uploadLandAndOwnershipFileHistory.setContainIssue(true);
//								}
//
//								System.out.println("uploadLandAndOwnershipFileHistory "+uploadLandAndOwnershipFileHistory);
//								uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo
//										.save(uploadLandAndOwnershipFileHistory);
//							});
//						}
//					}
//				}.start();
//			}
//
//		}
//		return new ResponseModel(uploadLandAndOwnershipFileHistoryObj, CustomMessages.FILE_UPLOAD_SUCCESS,
//				CustomMessages.ADD_SUCCESSFULLY, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.METHOD_POST);
//	}

//	public ResponseModel uploadExcelFile(UploadLandAndOwnershipDetailsDto uploadLandAndOwnershipDetailsDto, HttpServletRequest request) {
//		// Define allowed extensions and MIME types
//		List<String> allowedExtensions = Arrays.asList("xlsx", "csv", "xls");
//		List<String> allowedMimeTypes = Arrays.asList("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv");
//
//		// Define maximum file size in bytes (100MB)
//		long maxFileSize = 100 * 1024 * 1024; // 100MB in bytes
//
//		String userId = CustomMessages.getUserId(request, jwtTokenUtil);
//		UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistoryObj = new UploadLandAndOwnershipFileHistory();
//
//		if (!CollectionUtils.isEmpty(uploadLandAndOwnershipDetailsDto.getExcelFile())) {
//			for (MultipartFile file : uploadLandAndOwnershipDetailsDto.getExcelFile()) {
//				// Check file size
//				if (file.getSize() > maxFileSize) {
//					return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_SIZE_LIMIT, CustomMessages.METHOD_POST);
//				}
//
//				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
//				// Check file extension
//				if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
//					return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_EXTENSION_NOT_ALLOWED, CustomMessages.METHOD_POST);
//				}
//
//				// Check MIME type
//				String fileContentType = file.getContentType();
//				if (!allowedMimeTypes.contains(fileContentType)) {
//					return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_TYPE_NOT_ALLOWED, CustomMessages.METHOD_POST);
//				}
//
//				// Check for invalid characters in filename
//				String fileName = file.getOriginalFilename();
//				if (Pattern.compile("[\\\\/:*?\"<>|]").matcher(fileName).find()) {
//					return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_INVALID_CHARACTERS, CustomMessages.METHOD_POST);
//				}
//
//				// Check for double extensions or double dots
//				if (fileName.split("\\.").length > 2) {
//					return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_CONTAINS_EXTENSION, CustomMessages.METHOD_POST);
//				}
//
//				// Check for null bytes
//				if (fileName.contains("\0")) {
//					return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_CONTAINS_NULL_BYTE, CustomMessages.METHOD_POST);
//				}
//
//				// Save file and create history entry
//				MediaMaster mediaMaster = mediaMasterService.storeFile(file, "agristack", folderDocument, 0);
//				UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = new UploadLandAndOwnershipFileHistory();
//				uploadLandAndOwnershipFileHistory.setUploadedBy(Long.valueOf(userId));
//				uploadLandAndOwnershipFileHistory.setFileName(file.getOriginalFilename());
//				uploadLandAndOwnershipFileHistory.setMediaMaster(mediaMaster);
//				uploadLandAndOwnershipFileHistory.setContainIssue(false);
//				uploadLandAndOwnershipFileHistory.setIsread(true);
//				uploadLandAndOwnershipFileHistory.setIsDeletedExistingData(uploadLandAndOwnershipDetailsDto.getIsDeleteExistingData());
//				uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo.save(uploadLandAndOwnershipFileHistory);
//
//				// Use a separate thread for file processing to avoid blocking the main thread
//				UploadLandAndOwnershipFileHistory finalUploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistory;
//				new Thread(() -> processFile(file, uploadLandAndOwnershipDetailsDto, finalUploadLandAndOwnershipFileHistory)).start();
//			}
//		}
//
//		return new ResponseModel(uploadLandAndOwnershipFileHistoryObj, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.ADD_SUCCESSFULLY, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.METHOD_POST);
//	}
//
//	private void processFile(MultipartFile file, UploadLandAndOwnershipDetailsDto dto, UploadLandAndOwnershipFileHistory history) {
//		Set<Long> villageList = new HashSet<>();
//		try (InputStream in = file.getInputStream(); Workbook workbook = WorkbookFactory.create(in)) {
//			Sheet sheet = workbook.getSheetAt(0);
//			DataFormatter dataFormatter = new DataFormatter();
//
//			Iterator<Row> rowIterator = sheet.rowIterator();
//			if (rowIterator.hasNext()) rowIterator.next(); // Skip header row
//
//			while (rowIterator.hasNext()) {
//				Row row = rowIterator.next();
//				DummyLandOwnerShipMaster ownershipMaster = new DummyLandOwnerShipMaster();
//				DummyLandRecords landRecords = new DummyLandRecords();
//				Iterator<Cell> cellIterator = row.cellIterator();
//
//				int count = 0;
//				while (cellIterator.hasNext()) {
//					Cell cell = cellIterator.next();
//					String cellValue = dataFormatter.formatCellValue(cell);
//
//					switch (count) {
//						case 0:
//							ownershipMaster.setSurveyNumber(cellValue);
//							landRecords.setSurveyNumber(cellValue);
//							break;
//						case 1:
//							ownershipMaster.setSubSurveyNumber("na".equalsIgnoreCase(cellValue) ? "" : cellValue);
//							landRecords.setSubSurveyNumber("na".equalsIgnoreCase(cellValue) ? "" : cellValue);
//							break;
//						case 2:
//							ownershipMaster.setOwnerNo("na".equalsIgnoreCase(cellValue) ? null : cellValue);
//							break;
//						case 3:
//							ownershipMaster.setOwnerName(cellValue.length() > 100 ? cellValue.substring(0, 100) : cellValue);
//							break;
//						case 4:
//							ownershipMaster.setIdentifierName("na".equalsIgnoreCase(cellValue) ? null : cellValue);
//							break;
//						case 5:
//							ownershipMaster.setIdentifierType("na".equalsIgnoreCase(cellValue) ? null : cellValue);
//							break;
//						case 6:
//							ownershipMaster.setMainOwnerNo("na".equalsIgnoreCase(cellValue) ? null : cellValue);
//							break;
//						case 7:
//							ownershipMaster.setTotalHa("na".equalsIgnoreCase(cellValue) ? null : cellValue);
//							break;
//						case 8:
//							try {
//								long villageCode = Long.parseLong(cellValue);
//								ownershipMaster.setVillageLgdCode(villageCode);
//								landRecords.setVillageLgdCode(villageCode);
//								if (!villageList.contains(villageCode)) {
//									villageList.add(villageCode);
//									dummyLandOwnerShipMasterRepository.deleteAllByVillageCode(villageCode);
//									dummyLandRecordREpo.deleteAllByVillageCode(villageCode);
//								}
//							} catch (NumberFormatException e) {
//								// Handle invalid village code format
//							}
//							break;
//						case 9:
//							handleGeometry(cellValue, dto, landRecords);
//							break;
//					}
//					count++;
//				}
//
//				dummyLandOwnerShipMasterRepository.save(ownershipMaster);
//				dummyLandRecordREpo.save(landRecords);
//			}
//
//			if (!CollectionUtils.isEmpty(villageList)) {
//				villageList.forEach(village -> {
//					if (dto.getIsDeleteExistingData()) {
//						farmerLandOwnershipRegistryRepository.deleteAllByVillageLgdCode(village);
//						farmlandPlotRegistryRepository.deleteAllByVillageLgdCode(village);
//					}
//
//					try {
//						if ("OWNERLEVEL".equalsIgnoreCase(dto.getPlotGenerationType())) {
//							uploadRoRDataService.uploadDataAtOwnerLevelV2(village);
//						} else if ("SURVEYLEVEL".equalsIgnoreCase(dto.getPlotGenerationType())) {
//							uploadRoRDataService.uploadDataAtSurveyLevelV2(village);
//						}
//						history.setIsread(true);
//					} catch (Exception e) {
//						history.setContainIssue(true);
//					}
//
//					uploadLandAndOwnershipFileHistoryRepo.save(history);
//				});
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			history.setContainIssue(true);
//			uploadLandAndOwnershipFileHistoryRepo.save(history);
//		}
//	}
//
//	private void handleGeometry(String cellValue, UploadLandAndOwnershipDetailsDto dto, DummyLandRecords landRecords) {
//		try {
//			WKTReader wktReader = new WKTReader();
//			String wktString = "";
//			if ("GEOJSON".equalsIgnoreCase(dto.getGeometryType())) {
//				wktString = dummyLandRecordREpo.getWKTFromGeoJson(cellValue, dto.getProjectionCode(), 4326);
//			} else if ("WKT".equalsIgnoreCase(dto.getGeometryType())) {
//				wktString = dummyLandRecordREpo.getGeometryFromWKT(cellValue, dto.getProjectionCode(), 4326);
//			}
//			Geometry geometry = wktReader.read(wktString);
//			geometry.setSRID(4326);
//			landRecords.setGeom(geometry);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public ResponseModel uploadExcelFile(UploadLandAndOwnershipDetailsDto uploadLandAndOwnershipDetailsDto, HttpServletRequest request) {
		List<String> allowedExtensions = Arrays.asList("xlsx", "csv", "xls");
		List<String> allowedMimeTypes = Arrays.asList("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "text/csv");
		long maxFileSize = 100 * 1024 * 1024; // 100MB in bytes

		String userId = CustomMessages.getUserId(request, jwtTokenUtil);
		UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistoryObj = new UploadLandAndOwnershipFileHistory();

		if (!CollectionUtils.isEmpty(uploadLandAndOwnershipDetailsDto.getExcelFile())) {
			for (MultipartFile file : uploadLandAndOwnershipDetailsDto.getExcelFile()) {
				ResponseModel validationResponse = validateFile(file, allowedExtensions, allowedMimeTypes, maxFileSize);
				if (validationResponse != null) {
					return validationResponse;
				}

				MediaMaster mediaMaster = mediaMasterService.storeFile(file, "agristack", folderDocument, 0);
				UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = createFileHistory(file, userId, uploadLandAndOwnershipDetailsDto, mediaMaster);
				uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo.save(uploadLandAndOwnershipFileHistory);

				UploadLandAndOwnershipFileHistory finalUploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistory;
				processFileAsync(file, uploadLandAndOwnershipDetailsDto, finalUploadLandAndOwnershipFileHistory);
			}
		//	customMaterializedViewRepository.refreshMaterializedView();
				}

		return new ResponseModel(uploadLandAndOwnershipFileHistoryObj, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.ADD_SUCCESSFULLY, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.METHOD_POST);
	}

	private ResponseModel validateFile(MultipartFile file, List<String> allowedExtensions, List<String> allowedMimeTypes, long maxFileSize) {
		if (file.getSize() > maxFileSize) {
			return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_SIZE_LIMIT, CustomMessages.METHOD_POST);
		}

		String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (!allowedExtensions.contains(fileExtension.toLowerCase())) {
			return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_EXTENSION_NOT_ALLOWED, CustomMessages.METHOD_POST);
		}

		String fileContentType = file.getContentType();
		if (!allowedMimeTypes.contains(fileContentType)) {
			return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_TYPE_NOT_ALLOWED, CustomMessages.METHOD_POST);
		}

		String fileName = file.getOriginalFilename();
		if (Pattern.compile("[\\\\/:*?\"<>|]").matcher(fileName).find()) {
			return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_INVALID_CHARACTERS, CustomMessages.METHOD_POST);
		}

		if (fileName.split("\\.").length > 2) {
			return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_CONTAINS_EXTENSION, CustomMessages.METHOD_POST);
		}

		if (fileName.contains("\0")) {
			return new ResponseModel(null, CustomMessages.FILE_UPLOAD_FAILED, CustomMessages.GET_DATA_ERROR, CustomMessages.FILE_UPLOAD_CONTAINS_NULL_BYTE, CustomMessages.METHOD_POST);
		}

		return null;
	}

	private UploadLandAndOwnershipFileHistory createFileHistory(MultipartFile file, String userId, UploadLandAndOwnershipDetailsDto uploadLandAndOwnershipDetailsDto, MediaMaster mediaMaster) {
		UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = new UploadLandAndOwnershipFileHistory();
		uploadLandAndOwnershipFileHistory.setUploadedBy(Long.valueOf(userId));
		uploadLandAndOwnershipFileHistory.setFileName(file.getOriginalFilename());
		uploadLandAndOwnershipFileHistory.setMediaMaster(mediaMaster);
		uploadLandAndOwnershipFileHistory.setContainIssue(false);
		uploadLandAndOwnershipFileHistory.setIsread(true);
		uploadLandAndOwnershipFileHistory.setIsDeletedExistingData(uploadLandAndOwnershipDetailsDto.getIsDeleteExistingData());
		return uploadLandAndOwnershipFileHistory;
	}

	private void processFileAsync(MultipartFile file, UploadLandAndOwnershipDetailsDto dto, UploadLandAndOwnershipFileHistory history) {
		// Using a thread pool for better resource management
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		executorService.submit(() -> processFile(file, dto, history));
		executorService.shutdown();
	}

	private void processFile(MultipartFile file, UploadLandAndOwnershipDetailsDto dto, UploadLandAndOwnershipFileHistory history) {
		Set<Long> villageList = new HashSet<>();
		try (InputStream in = file.getInputStream(); Workbook workbook = WorkbookFactory.create(in)) {
			Sheet sheet = workbook.getSheetAt(0);
			DataFormatter dataFormatter = new DataFormatter();
			Iterator<Row> rowIterator = sheet.rowIterator();
			if (rowIterator.hasNext()) rowIterator.next(); // Skip header row

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				DummyLandOwnerShipMaster ownershipMaster = new DummyLandOwnerShipMaster();
				DummyLandRecords landRecords = new DummyLandRecords();
				Iterator<Cell> cellIterator = row.cellIterator();
				int count = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					String cellValue = dataFormatter.formatCellValue(cell);
					mapCellValueToEntity(count, cellValue, ownershipMaster, landRecords, villageList, dto);
					count++;
				}

				dummyLandOwnerShipMasterRepository.save(ownershipMaster);
				dummyLandRecordREpo.save(landRecords);
			}

			processVillageData(villageList, dto, history);

		} catch (Exception e) {
			e.printStackTrace();
			history.setContainIssue(true);
			uploadLandAndOwnershipFileHistoryRepo.save(history);
		}
	}

	private void mapCellValueToEntity(int count, String cellValue, DummyLandOwnerShipMaster ownershipMaster, DummyLandRecords landRecords, Set<Long> villageList, UploadLandAndOwnershipDetailsDto dto) {
		switch (count) {
			case 0:
				ownershipMaster.setSurveyNumber(cellValue);
				landRecords.setSurveyNumber(cellValue);
				break;
			case 1:
				ownershipMaster.setSubSurveyNumber("na".equalsIgnoreCase(cellValue) ? "" : cellValue);
				landRecords.setSubSurveyNumber("na".equalsIgnoreCase(cellValue) ? "" : cellValue);
				break;
			case 2:
				ownershipMaster.setOwnerNo("na".equalsIgnoreCase(cellValue) ? null : cellValue);
				break;
			case 3:
				ownershipMaster.setOwnerName(cellValue.length() > 100 ? cellValue.substring(0, 100) : cellValue);
				break;
			case 4:
				ownershipMaster.setIdentifierName("na".equalsIgnoreCase(cellValue) ? null : cellValue);
				break;
			case 5:
				ownershipMaster.setIdentifierType("na".equalsIgnoreCase(cellValue) ? null : cellValue);
				break;
			case 6:
				ownershipMaster.setMainOwnerNo("na".equalsIgnoreCase(cellValue) ? null : cellValue);
				break;
			case 7:
				ownershipMaster.setTotalHa("na".equalsIgnoreCase(cellValue) ? null : cellValue);
				break;
			case 8:
				try {
					long villageCode = Long.parseLong(cellValue);
					ownershipMaster.setVillageLgdCode(villageCode);
					landRecords.setVillageLgdCode(villageCode);
					if (!villageList.contains(villageCode)) {
						villageList.add(villageCode);
						dummyLandOwnerShipMasterRepository.deleteAllByVillageCode(villageCode);
						dummyLandRecordREpo.deleteAllByVillageCode(villageCode);
					}
				} catch (NumberFormatException e) {
					// Handle invalid village code format
				}
				break;
			case 9:
				handleGeometry(cellValue, dto, landRecords);
				break;
		}
	}

	private void processVillageData(Set<Long> villageList, UploadLandAndOwnershipDetailsDto dto, UploadLandAndOwnershipFileHistory history) {
		if (!CollectionUtils.isEmpty(villageList)) {
			villageList.forEach(village -> {
				if (dto.getIsDeleteExistingData()) {
					farmerLandOwnershipRegistryRepository.deleteAllByVillageLgdCode(village);
					farmlandPlotRegistryRepository.deleteAllByVillageLgdCode(village);
				}

				try {
					if ("OWNERLEVEL".equalsIgnoreCase(dto.getPlotGenerationType())) {
						uploadRoRDataService.uploadDataAtOwnerLevelV2(village);

					} else if ("SURVEYLEVEL".equalsIgnoreCase(dto.getPlotGenerationType())) {
						uploadRoRDataService.uploadDataAtSurveyLevelV2(village);
					}
					history.setIsread(true);
				} catch (Exception e) {
					history.setContainIssue(true);
				}

				uploadLandAndOwnershipFileHistoryRepo.save(history);
			});
		}
	}

	private void handleGeometry(String cellValue, UploadLandAndOwnershipDetailsDto dto, DummyLandRecords landRecords) {
		try {
			WKTReader wktReader = new WKTReader();
			String wktString = "";
			if ("GEOJSON".equalsIgnoreCase(dto.getGeometryType())) {
				wktString = dummyLandRecordREpo.getWKTFromGeoJson(cellValue, dto.getProjectionCode(), 4326);
			} else if ("WKT".equalsIgnoreCase(dto.getGeometryType())) {
				wktString = dummyLandRecordREpo.getGeometryFromWKT(cellValue, dto.getProjectionCode(), 4326);
			}
			Geometry geometry = wktReader.read(wktString);
			geometry.setSRID(4326);
			landRecords.setGeom(geometry);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/***
	 * UPLOAD DATA FOR UP STATE
	 **/

	public ResponseModel uploadUPStateDataBulk() {
		try {
			String key = "UtaDataAgriData";

			new Thread() {
				public void run() {

					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository.findByVillageLgdCode(171841L);

//					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository
//							.findByIsUploadedIsNullAndIsActiveTrue();

					System.out.println("RoR data Upload Started for count :: " + allVillageLgd.size());

					allVillageLgd.stream().forEach(x -> {
						System.out.println("Initiated for :: " + x.getVillageLgdCode());
						fetchUPVillageDataByVillageCode(Integer.valueOf(x.getVillageLgdCode().toString()), key, true);
					});

					System.out.println("RoR data Upload Completed");

				}
			}.start();

//			return fetchUPVillageDataByVillageCode(villageLgdCode, key);
			return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/***
	 * UPLOAD DATA FOR ODISHA STATE
	 **/

	public ResponseModel uploadODStateDataBulk() {
		try {
			String key = "24b2c55f86d6a06f25f8a2f96b8123e9";

			Long stateLgdCode = 21L;
			new Thread() {
				public void run() {

//					fetchODVillageDataByVillageCode(394754, 1602, key);

					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository
							.findByIsUploadedIsNullAndIsActiveTrueAndStateLgdCode(stateLgdCode);

//					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository.findByVillageLgdCode(395255L);

					System.out.println("RoR data Upload Started for count :: " + allVillageLgd.size());

					allVillageLgd.stream().forEach(x -> {
						System.out.println("Initiated for :: " + x.getVillageLgdCode());
						fetchODVillageDataByVillageCode(Integer.valueOf(x.getVillageLgdCode().toString()),
								Integer.valueOf(x.getTalukCode().toString()), key);
					});

					System.out.println("RoR data Upload Completed");

				}
			}.start();

			return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel fetchODVillageDataByVillageCode(Integer villageLgdCode, Integer subDistrictLgdCode,
			String key) {
		try {

			RestTemplate restTemplate = new RestTemplate();
			String url = "https://odisha4kgeo.in/index.php/mapview/getVillagePlotsDCS";

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", key);
			headers.add("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("village_code", villageLgdCode.toString());
			map.add("tahasil_code", subDistrictLgdCode.toString());

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);

			if (responseEntity != null) {

				Gson gson = new Gson();
				ODRoRDataMainDAO[] stateRoRDAOs = gson.fromJson(responseEntity.getBody(), ODRoRDataMainDAO[].class);

				if (stateRoRDAOs != null && stateRoRDAOs.length > 0) {

					farmerLandOwnershipRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(villageLgdCode));
					farmlandPlotRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(villageLgdCode));
					dummyLandOwnerShipMasterRepository.deleteAllByVillageCode(Long.valueOf(villageLgdCode));
					dummyLandRecordREpo.deleteAllByVillageCode(Long.valueOf(villageLgdCode));

					List<ODRoRDataMainDAO> plotDAOs = Arrays.asList(stateRoRDAOs);

					uploadAllOdishaDataToTempTable(plotDAOs);

				} else {
					List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository
							.findByVillageLgdCode(Long.valueOf(villageLgdCode));
					for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
						upRoRDataUploadLog.setIsUploaded(false);
						upRoRDataUploadLog.setCount(0);
						upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
						roRDataUploadLogRepository.save(upRoRDataUploadLog);
					}
				}
			}
		} catch (Exception e) {

			List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository
					.findByVillageLgdCode(Long.valueOf(villageLgdCode));
			for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
				upRoRDataUploadLog.setIsUploaded(false);
				upRoRDataUploadLog.setCount(-1);
				upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
				roRDataUploadLogRepository.save(upRoRDataUploadLog);
			}

			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

		return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
	}

	private void uploadAllOdishaDataToTempTable(List<ODRoRDataMainDAO> stateRoRDAO) {
		try {

			Set<Long> villageList = new HashSet<>();

			List<DummyLandOwnerShipMaster> finalLandOwners = new ArrayList<>();
			List<DummyLandRecords> finalPlots = new ArrayList<>();

			System.err.println("uploadAllDataToTempTable ::  " + new Timestamp(new Date().getTime()));

			List<ODRoRDataMainDAO> stateRoRDAOs = stateRoRDAO.stream().filter(x -> x.getPlotArea() != null)
					.collect(Collectors.toList());

			stateRoRDAOs.stream().forEach((plotDAO) -> {

				try {
					if (!plotDAO.getOwners().isEmpty()) {
						plotDAO.getOwners().stream().forEach((owner) -> {

							DummyLandOwnerShipMaster dummyLandOwnerShipMaster = new DummyLandOwnerShipMaster();
							DummyLandRecords dummyLandRecords = new DummyLandRecords();

							dummyLandRecords.setSurveyNumber(plotDAO.getSurveyNo());
							String subSurveyNo = plotDAO.getSubSurveyNo() != null ? plotDAO.getSubSurveyNo() : "";
							dummyLandRecords.setSubSurveyNumber(subSurveyNo);
							dummyLandRecords.setVillageLgdCode(Long.valueOf(plotDAO.getVillageLgdCode()));

							try {

								WKTReader wktReader = new WKTReader();

								Integer projectionCode = Integer
										.valueOf(plotDAO.getProjectionCode().replaceAll("EPSG:", ""));
								String wktString = dummyLandRecordREpo.getGeometryFromWKT(
										plotDAO.getPlotGeometry().replace("\"", ""), projectionCode, 4326);
								Geometry geometry = wktReader.read(wktString);
								geometry.setSRID(4326);
								dummyLandRecords.setGeom(geometry);

								GeometryFactory geometryFactory = new GeometryFactory();
								geometryFactory.createGeometry(geometry);

								dummyLandRecords.setGeom(geometry);

							} catch (Exception e) {
								System.err.println("Error In geometry :: " + plotDAO.getPlotGeometry());

							}

							villageList.add(Long.valueOf(plotDAO.getVillageLgdCode()));

							dummyLandOwnerShipMaster.setVillageLgdCode(Long.valueOf(plotDAO.getVillageLgdCode()));

							dummyLandOwnerShipMaster.setSurveyNumber(plotDAO.getSurveyNo());
							dummyLandOwnerShipMaster.setSubSurveyNumber(subSurveyNo);
							dummyLandOwnerShipMaster.setOwnerNo(owner.getOwnerNo().toString());

							String ownerName = owner.getOwnerName().substring(0,
									Math.min(owner.getOwnerName().length(), 100));

							dummyLandOwnerShipMaster.setOwnerName(ownerName);
							dummyLandOwnerShipMaster.setIdentifierName(owner.getIndetifierName());
							dummyLandOwnerShipMaster.setIdentifierType(owner.getIndetifierType());
							dummyLandOwnerShipMaster.setMainOwnerNo("1");
//									dummyLandOwnerShipMaster.setTotalHa(owner.getExtend().toString());
							dummyLandOwnerShipMaster.setTotalHa(plotDAO.getPlotArea());

							if (dummyLandRecords.getGeom() != null && dummyLandOwnerShipMaster != null
									&& dummyLandRecords != null) {

								finalLandOwners.add(dummyLandOwnerShipMaster);
								finalPlots.add(dummyLandRecords);
							}

						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			if (!finalLandOwners.isEmpty() && !finalPlots.isEmpty()) {
				dummyLandOwnerShipMasterRepository.saveAll(finalLandOwners);
				dummyLandRecordREpo.saveAll(finalPlots);
			}

			if (!CollectionUtils.isEmpty(villageList)) {

				villageList.forEach(village -> {
//					uploadDataAtOwnerLevel(village);
					uploadRoRDataService.uploadDataAtOwnerLevelV2(village);

					List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository.findByVillageLgdCode(village);

					for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
						upRoRDataUploadLog.setIsUploaded(true);
						upRoRDataUploadLog.setCount(stateRoRDAOs.size());
						upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
						roRDataUploadLogRepository.save(upRoRDataUploadLog);

						System.out.println(village + " :: Completed ");
					}
				});

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***
	 * UPLOAD DATA FOR GJ STATE
	 **/

	public ResponseModel uploadGJStateDataBulk() {
		try {

			Long stateLgdCode = 24L;
			new Thread() {
				public void run() {

					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository
							.findByIsUploadedIsNullAndIsActiveTrueAndStateLgdCode(stateLgdCode);

//					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository.findByVillageLgdCode(510174L);

					System.out.println("RoR data Upload Started for count :: " + allVillageLgd.size());

					allVillageLgd.parallelStream().forEach(x -> {
						System.out.println("Initiated for :: " + x.getVillageLgdCode());
						fetchGJVillageDataByVillageCode(Integer.valueOf(x.getVillageLgdCode().toString()),
								Integer.valueOf(x.getSubDistrictLgdCode().toString()),
								Integer.valueOf(x.getDistrictLgdCode().toString()), true);
					});

					System.out.println("RoR data Upload Completed");

				}
			}.start();

			return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel fetchGJVillageDataByVillageCode(Integer villageLgdCode, Integer subDistrictLgdCode,
			Integer districtLgdCode, boolean fullUpload) {
		try {

			RestTemplate restTemplate = new RestTemplate();
			String token = getTokenForGJRoRDataUpload();

			String url = "https://anyror.gujarat.gov.in/agristack_api/ror_detail";

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + token);
			headers.add("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("villagelgcode", villageLgdCode.toString());
			map.add("subdistrictlgcode", subDistrictLgdCode.toString());
			map.add("districtlgcode", districtLgdCode.toString());

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);

			if (responseEntity != null) {

				Gson gson = new Gson();
				GJRoRDataMainDAO[] stateRoRDAOs = gson.fromJson(responseEntity.getBody(), GJRoRDataMainDAO[].class);

				if (stateRoRDAOs != null && stateRoRDAOs.length > 0) {

					if (fullUpload == true) {
						farmerLandOwnershipRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(villageLgdCode));
						farmlandPlotRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(villageLgdCode));
					}
					dummyLandOwnerShipMasterRepository.deleteAllByVillageCode(Long.valueOf(villageLgdCode));
					dummyLandRecordREpo.deleteAllByVillageCode(Long.valueOf(villageLgdCode));

					List<GJRoRDataMainDAO> plotDAOs = Arrays.asList(stateRoRDAOs);

					uploadAllGJDataToTempTable(plotDAOs, fullUpload);

				} else {
					List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository
							.findByVillageLgdCode(Long.valueOf(villageLgdCode));
					for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
						upRoRDataUploadLog.setIsUploaded(false);
						upRoRDataUploadLog.setCount(0);
						upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
						roRDataUploadLogRepository.save(upRoRDataUploadLog);
					}
				}
			}
		} catch (Exception e) {

			List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository
					.findByVillageLgdCode(Long.valueOf(villageLgdCode));
			for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
				upRoRDataUploadLog.setIsUploaded(false);
				upRoRDataUploadLog.setCount(-1);
				upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
				roRDataUploadLogRepository.save(upRoRDataUploadLog);
			}

			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

		return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
	}

	private void uploadAllGJDataToTempTable(List<GJRoRDataMainDAO> stateRoRDAO, boolean fullUpload) {
		try {
			Set<Long> villageList = new HashSet<>();

			String villageLgdCode = stateRoRDAO.get(0).getVillageLgdCode();
			List<String> existingSurveyNo = farmlandPlotRegistryRepository
					.findAllByFprSurveyNumberByFprVillageLgdCode(Long.valueOf(villageLgdCode));

			List<DummyLandOwnerShipMaster> finalLandOwners = new ArrayList<>();
			List<DummyLandRecords> finalPlots = new ArrayList<>();

			System.err.println("uploadAllDataToTempTable ::  " + new Timestamp(new Date().getTime()));

			List<GJRoRDataMainDAO> stateRoRDAOs = stateRoRDAO.stream()
					.filter(x -> (!StringUtil.isNullOrEmpty(x.getPlotGeometry()))).collect(Collectors.toList());

			stateRoRDAOs.stream().forEach((plotDAO) -> {

				if (!plotDAO.getOwners().isEmpty()) {
					plotDAO.getOwners().stream().forEach((owner) -> {

						DummyLandOwnerShipMaster dummyLandOwnerShipMaster = new DummyLandOwnerShipMaster();
						DummyLandRecords dummyLandRecords = new DummyLandRecords();

						dummyLandRecords.setSurveyNumber(plotDAO.getSurveyNo());
						dummyLandRecords.setSubSurveyNumber(plotDAO.getSubSurveyNo());
						dummyLandRecords.setVillageLgdCode(Long.valueOf(plotDAO.getVillageLgdCode()));
						try {

							WKTReader wktReader = new WKTReader();
//									Geometry geometry = wktReader.read(plotDAO.getPlotgeometry().replace("\"", ""));
//									geometry.setSRID(4326);

							Integer sourceSRS = !StringUtil.isNullOrEmpty(plotDAO.getProjectionCode())
									? Integer.valueOf(plotDAO.getProjectionCode())
									: 4326;

							String wktString = dummyLandRecordREpo
									.getGeometryFromWKT(plotDAO.getPlotGeometry().replace("\"", ""), sourceSRS, 4326);
							Geometry geometry = wktReader.read(wktString);
							geometry.setSRID(4326);
							dummyLandRecords.setGeom(geometry);

							GeometryFactory geometryFactory = new GeometryFactory();
							geometryFactory.createGeometry(geometry);

							// geometryFactory.s
							dummyLandRecords.setGeom(geometry);

						} catch (Exception e) {
							System.err.println("Error In geometry :: " + plotDAO.getPlotGeometry());

						}

						villageList.add(Long.valueOf(plotDAO.getVillageLgdCode()));

						dummyLandOwnerShipMaster.setVillageLgdCode(Long.valueOf(plotDAO.getVillageLgdCode()));

						dummyLandOwnerShipMaster.setSurveyNumber(plotDAO.getSurveyNo());
						dummyLandOwnerShipMaster.setSubSurveyNumber(plotDAO.getSubSurveyNo());
						dummyLandOwnerShipMaster.setOwnerNo(owner.getOwnerNo().toString());

						String ownerName = owner.getOwnerName().substring(0,
								Math.min(owner.getOwnerName().length(), 100));

						dummyLandOwnerShipMaster.setOwnerName(ownerName);
						dummyLandOwnerShipMaster.setIdentifierName(owner.getIdentifierName());
						dummyLandOwnerShipMaster.setIdentifierType(owner.getIdentifierType());
						dummyLandOwnerShipMaster.setMainOwnerNo("1");
//								dummyLandOwnerShipMaster.setTotalHa(owner.getExtend().toString());
						dummyLandOwnerShipMaster.setTotalHa(plotDAO.getPlotArea().toString());

						if (dummyLandRecords.getGeom() != null && dummyLandOwnerShipMaster != null
								&& dummyLandRecords != null) {

							Boolean uploadFlag = true;
							if (!fullUpload && (existingSurveyNo.contains(dummyLandRecords.getSurveyNumber()))) {
								uploadFlag = false;
							}

							if (Boolean.TRUE.equals(uploadFlag)) {
								finalLandOwners.add(dummyLandOwnerShipMaster);
								finalPlots.add(dummyLandRecords);
							}

//							finalLandOwners.add(dummyLandOwnerShipMaster);
//							finalPlots.add(dummyLandRecords);
						}

					});
				}
			});

			if (!finalLandOwners.isEmpty() && !finalPlots.isEmpty()) {
				dummyLandOwnerShipMasterRepository.saveAll(finalLandOwners);
				dummyLandRecordREpo.saveAll(finalPlots);
			}

			if (!CollectionUtils.isEmpty(villageList)) {

				villageList.forEach(village -> {
//					uploadDataAtSurveyLevel(village);
					uploadRoRDataService.uploadDataAtSurveyLevelV2(village);

					List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository.findByVillageLgdCode(village);

					for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
						upRoRDataUploadLog.setIsUploaded(true);
						upRoRDataUploadLog.setCount(stateRoRDAOs.size());
						upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
						roRDataUploadLogRepository.save(upRoRDataUploadLog);

						System.out.println(village + " :: Completed ");
					}
				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getTokenForGJRoRDataUpload() {
		try {
			String tokenURL = "https://anyror.gujarat.gov.in/agristack_api/get_token";
			String token = null;

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("user_name", "pmkisan-guj");

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(tokenURL, entity, String.class);

			if (responseEntity != null) {
				Gson gson = new Gson();
				JsonObject[] objects = gson.fromJson(responseEntity.getBody(), JsonObject[].class);
				if (objects.length > 0) {
					JsonObject tokenObj = objects[0];
					token = tokenObj.get("ResMsg") != null ? tokenObj.get("ResMsg").getAsString() : null;
				}
			}

			return token;
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * UPLOAD DATA FOR Assam STATE
	 **/

	public ResponseModel uploadASStateDataBulk() {
		try {

			Long stateLgdCode = 18L;
			new Thread() {
				public void run() {

//					fetchASVillageDataByVillageCode(301355);
					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository
							.findByIsUploadedIsNullAndIsActiveTrueAndStateLgdCode(stateLgdCode);

					System.out.println("RoR data Upload Started for count :: " + allVillageLgd.size());

					allVillageLgd.stream().forEach(x -> {
						System.out.println("Initiated for :: " + x.getVillageLgdCode());
						fetchASVillageDataByVillageCode(Integer.valueOf(x.getVillageLgdCode().toString()), true);
					});

					System.out.println("RoR data Upload Completed");

				}
			}.start();

			return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	@Transactional
	private ResponseModel fetchASVillageDataByVillageCode(Integer villageLgdCode, boolean fullUpload) {

		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = "https://landhub.assam.gov.in/api/index.php/NicApi/pattadarInfo_lgdcode";

			HttpHeaders headers = new HttpHeaders();
			headers.add("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("lgdcode", villageLgdCode.toString());
			map.add("apikey", "agristack_team");

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);

			if (responseEntity != null) {

				Gson gson = new Gson();
				JsonObject object = gson.fromJson(responseEntity.getBody(), JsonObject.class);
				JsonArray jsonArray = null;
				if (object != null) {
					jsonArray = object.get("plot_wise_data") != null ? (object.get("plot_wise_data")).getAsJsonArray()
							: null;
				}

				AssamRoRDataMainDAO[] stateRoRDAOs = gson.fromJson(jsonArray, AssamRoRDataMainDAO[].class);

				if (stateRoRDAOs != null && stateRoRDAOs.length > 0) {

					if (fullUpload == true) {
						farmerLandOwnershipRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(villageLgdCode));
						farmlandPlotRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(villageLgdCode));
					}

					dummyLandOwnerShipMasterRepository.deleteAllByVillageCode(Long.valueOf(villageLgdCode));
					dummyLandRecordREpo.deleteAllByVillageCode(Long.valueOf(villageLgdCode));

					List<AssamRoRDataMainDAO> plotDAOs = Arrays.asList(stateRoRDAOs);

					uploadAllAssamDataToTempTable(plotDAOs, villageLgdCode, fullUpload);

				} else {
					List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository
							.findByVillageLgdCode(Long.valueOf(villageLgdCode));
					for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
						upRoRDataUploadLog.setIsUploaded(false);
						upRoRDataUploadLog.setCount(0);
						upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
						roRDataUploadLogRepository.save(upRoRDataUploadLog);
					}
				}
			}
		} catch (Exception e) {
			List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository
					.findByVillageLgdCode(Long.valueOf(villageLgdCode));
			for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
				upRoRDataUploadLog.setIsUploaded(false);
				upRoRDataUploadLog.setCount(0);
				upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
				roRDataUploadLogRepository.save(upRoRDataUploadLog);
			}
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

		return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
	}

	private void uploadAllAssamDataToTempTable(List<AssamRoRDataMainDAO> stateRoRDAO, Integer villageLgdCode,
			boolean fullUpload) {
		try {
			Set<Long> villageList = new HashSet<>();

			List<String> existingSurveyNo = farmlandPlotRegistryRepository
					.findAllByFprSurveyNumberByFprVillageLgdCode(Long.valueOf(villageLgdCode));

			List<DummyLandOwnerShipMaster> finalLandOwners = new ArrayList<>();
			List<DummyLandRecords> finalPlots = new ArrayList<>();

			System.err.println("uploadAllAssamDataToTempTable ::  " + new Timestamp(new Date().getTime()));

			stateRoRDAO.parallelStream().forEach((x) -> {
				String geometry = null;
				String projection_code = null;
				try {
					JsonArray features = x.getPlot_geometry().get("features").getAsJsonArray();
					if (features.size() > 0) {
						JsonObject feature = (JsonObject) features.get(0);
						geometry = feature.get("geometry").toString();

						JsonObject property = (JsonObject) feature.get("properties");
						projection_code = property.get("projection_code").getAsString();
					}

				} catch (Exception e) {
				}
				x.setGeometry(geometry);
				x.setProjection_code(projection_code);
			});

			List<AssamRoRDataMainDAO> stateRoRDAOs = stateRoRDAO.stream()
					.filter(x -> (!StringUtil.isNullOrEmpty(x.getGeometry()))).collect(Collectors.toList());

			stateRoRDAOs.stream().forEach((plotDAO) -> {

				if (!plotDAO.getOwners().isEmpty()) {
					plotDAO.getOwners().stream().forEach((owner) -> {

						DummyLandOwnerShipMaster dummyLandOwnerShipMaster = new DummyLandOwnerShipMaster();
						DummyLandRecords dummyLandRecords = new DummyLandRecords();

						dummyLandRecords.setSurveyNumber(plotDAO.getSurveyNo());
						dummyLandRecords.setSubSurveyNumber("");
						dummyLandRecords.setVillageLgdCode(Long.valueOf(villageLgdCode));
						try {

							WKTReader wktReader = new WKTReader();
//									Geometry geometry = wktReader.read(plotDAO.getPlotgeometry().replace("\"", ""));
//									geometry.setSRID(4326);

							Integer sourceSRS = !StringUtil.isNullOrEmpty(plotDAO.getProjection_code())
									? Integer.valueOf(plotDAO.getProjection_code().replaceAll("EPSG:", ""))
									: 32646;

//							String wktString = dummyLandRecordREpo
//									.getGeometryFromWKT(plotDAO.getGeometry().replace("\"", ""), sourceSRS, 4326);

							String wktString = dummyLandRecordREpo.getWKTStringFromGeoJson(plotDAO.getGeometry(),
									sourceSRS, 4326);

							Geometry geometry = wktReader.read(wktString);
							geometry.setSRID(4326);
							dummyLandRecords.setGeom(geometry);

							GeometryFactory geometryFactory = new GeometryFactory();
							geometryFactory.createGeometry(geometry);

							// geometryFactory.s
							dummyLandRecords.setGeom(geometry);

						} catch (Exception e) {
							System.err.println("Error In geometry :: " + plotDAO.getGeometry());

						}

						villageList.add(Long.valueOf(villageLgdCode));

						dummyLandOwnerShipMaster.setVillageLgdCode(Long.valueOf(villageLgdCode));

						dummyLandOwnerShipMaster.setSurveyNumber(plotDAO.getSurveyNo());
						dummyLandOwnerShipMaster.setSubSurveyNumber("");
						dummyLandOwnerShipMaster.setOwnerNo(owner.getOwnerNo().toString());

						String ownerName = owner.getOwnerName().substring(0,
								Math.min(owner.getOwnerName().length(), 100));

						dummyLandOwnerShipMaster.setOwnerName(ownerName);
						dummyLandOwnerShipMaster.setIdentifierName(owner.getIndetifierName());
						dummyLandOwnerShipMaster.setIdentifierType(owner.getIndetifierType());
						dummyLandOwnerShipMaster.setMainOwnerNo("1");
//								dummyLandOwnerShipMaster.setTotalHa(owner.getExtend().toString());

						Double plotArea = Double.valueOf(plotDAO.getPlotArea());

						if (plotDAO.getPlotAreaUnit().equalsIgnoreCase("Sq. feet")) {
							// convert sq. feet to Lessa
							plotArea = (plotArea) / 144;
						}
//						dummyLandOwnerShipMaster.setTotalHa(plotDAO.getPlotArea().toString());
						dummyLandOwnerShipMaster.setTotalHa(plotArea.toString());

						if (dummyLandRecords.getGeom() != null && dummyLandOwnerShipMaster != null
								&& dummyLandRecords != null) {

							Boolean uploadFlag = true;
							if (!fullUpload && (existingSurveyNo.contains(dummyLandRecords.getSurveyNumber()))) {
								uploadFlag = false;
							}

							if (Boolean.TRUE.equals(uploadFlag)) {
								finalLandOwners.add(dummyLandOwnerShipMaster);
								finalPlots.add(dummyLandRecords);
							}
						}

					});
				}
			});

			if (!finalLandOwners.isEmpty() && !finalPlots.isEmpty()) {
				dummyLandOwnerShipMasterRepository.saveAll(finalLandOwners);
				dummyLandRecordREpo.saveAll(finalPlots);
			}

			if (!CollectionUtils.isEmpty(villageList)) {

				villageList.forEach(village -> {
//					uploadDataAtSurveyLevel(village);
					uploadRoRDataService.uploadDataAtSurveyLevelV2(village);

					List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository.findByVillageLgdCode(village);

					for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
						upRoRDataUploadLog.setIsUploaded(true);
						if (fullUpload == false) {
							upRoRDataUploadLog.setIsReUploadDone(true);
						}
						upRoRDataUploadLog.setCount(stateRoRDAOs.size());
						upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
						roRDataUploadLogRepository.save(upRoRDataUploadLog);

						System.out.println(village + " :: Completed ");
					}
				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void uploadUPStateDataReUpload() {

		String key = "UtaDataAgriData";

		new Thread() {
			public void run() {

//				List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository.findByVillageLgdCode(171841L);

				List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository
						.findByIsUploadedIsNullAndIsActiveTrueAndReUploadTrueAndIsReUploadDoneIsNull();

				System.out.println("RoR data Upload Started for count :: " + allVillageLgd.size());

				allVillageLgd.forEach(x -> {
					System.out.println("Re Upload Initiated for :: " + x.getVillageLgdCode());
					fetchUPVillageDataByVillageCode(Integer.valueOf(x.getVillageLgdCode().toString()), key, false);
				});

				System.out.println("RoR data Upload Completed");
			}
		}.start();

	}

	public void uploadASStateDataReUpload() {

		try {
			Long stateLgdCode = 18L;
			new Thread() {
				public void run() {

//					fetchASVillageDataByVillageCode(301355, false);
					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository
							.findByReUploadTrueAndIsReUploadDoneIsNull();

					System.out.println("Re RoR data Upload Started for count :: " + allVillageLgd.size());

					allVillageLgd.stream().forEach(x -> {
						System.out.println("Re Initiated for :: " + x.getVillageLgdCode());
						fetchASVillageDataByVillageCode(Integer.valueOf(x.getVillageLgdCode().toString()),false);
					});

					System.out.println("RoR data Upload Completed");

				}
			}.start();

		} catch (Exception e) {
		}

	}

	public static UPStateRoRDataResponseDAO[] readJsonFile(String filePath) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			// Load the resource from the "resource" folder
			InputStream inputStream = new ClassPathResource(filePath).getInputStream();

			return objectMapper.readValue(inputStream, UPStateRoRDataResponseDAO[].class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
