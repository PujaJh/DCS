/**
 *
 */
package com.amnex.agristack.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.FarmlandOwnershipDto;
import com.amnex.agristack.dao.UpApiResponseDto;
import com.amnex.agristack.dao.UploadLandAndOwnershipDetailsDto;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.geotools.data.DataStore;
//import org.geotools.data.shapefile.ShapefileDataStore;
//import org.geotools.feature.FeatureIterator;
import org.json.JSONArray;
//import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.UploadLandDetailsExcelFileColumnDao;
import com.amnex.agristack.service.MediaMasterService;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.Verhoeff;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
//import org.opengis.feature.Property;
//import org.opengis.feature.type.Name;

/**
 * @author kinnari.soni
 *
 */

@Service
public class FarmerLandOwnershipService {

	@Autowired
	private FarmerLandOwnershipRegistryRepository farmerLandOwnershipRegistryRepository;

	@Autowired
	private FarmerRegistryRepository farmerRegistryRepository;

	@Autowired
	private FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	private DummyLandOwnerShipMasterRepository dummyLandOwnerShipMasterRepository;

	@Autowired
	private OwnerTypeRepository ownerTypeRepository;
	@Autowired
	private DummyLandRecordREpo dummyLandRecordREpo;
	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	private CommonUtil commonUtil;
	@Autowired
	private StateUnitTypeRepository stateUnitTypeRepository;
	@Autowired
	private IdentifierTypeRepository identifierTypeRepository;
	@Autowired
	private MediaMasterService mediaMasterService;
	@Autowired
	private RoleMasterRepository roleMasterRepository;
	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${media.folder.document}")
	private String folderDocument;

	@Value("${file.upload-dir}")
	private String path;

	@Autowired
	private UploadLandAndOwnershipFileHistoryRepo uploadLandAndOwnershipFileHistoryRepo;

	// @PostConstruct
	public void test() {
		generateUserForFarmer(625251L);
	}

	/**
	 * add farmer land ownership mapping
	 * 
	 * @param farmlandOwnershipDto
	 * @return
	 */
	public FarmerLandOwnershipRegistry addFarmlandOwnerShipDetails(FarmlandOwnershipDto farmlandOwnershipDto) {
		try {
			FarmerLandOwnershipRegistry farmerLandOwnership = new FarmerLandOwnershipRegistry();
			if (farmlandOwnershipDto.getFarmLandOwnerShipId() != null) {
				farmerLandOwnership.setFarmerLandOwnershipRegistryId(farmlandOwnershipDto.getFarmLandOwnerShipId());
			}

			if (farmlandOwnershipDto.getFarmerRegistryId() != null) {
				Optional<FarmerRegistry> farmerData = farmerRegistryRepository
						.findById(farmlandOwnershipDto.getFarmerRegistryId());
				if (!farmerData.isPresent()) {
					// return new ResponseEntity<Mono<?>>(
					// Mono.just(ResponseMessages.Toast(ResponseMessages.NOT_FOUND,"Farmer registry
					// id not found", null)),
					// HttpStatus.NOT_FOUND);
				}
				farmerLandOwnership.setFarmerRegistryId(farmerData.get());
			}

			if (farmlandOwnershipDto.getFarmlandPlotRegistryId() != null) {
				Optional<FarmlandPlotRegistry> plotData = farmlandPlotRegistryRepository
						.findById(farmlandOwnershipDto.getFarmlandPlotRegistryId());
				if (!plotData.isPresent()) {
					// return new ResponseEntity<Mono<?>>(
					// Mono.just(ResponseMessages.Toast(ResponseMessages.NOT_FOUND,"Plot land id not
					// found", null)),
					// HttpStatus.NOT_FOUND);
				}
				farmerLandOwnership.setFarmlandPlotRegistryId(plotData.get());
			}

			farmerLandOwnership.setOwnerNoAsPerRor(
					farmlandOwnershipDto.getOwnerNoAsPerRor() != null ? farmlandOwnershipDto.getOwnerNoAsPerRor()
							: null);
			farmerLandOwnership.setOwnerNamePerRor(
					farmlandOwnershipDto.getOwnerNamePerRor() != null ? farmlandOwnershipDto.getOwnerNamePerRor()
							: null);
			farmerLandOwnership.setOwnerCleanedUpName(
					farmlandOwnershipDto.getOwnerCleanedUpName() != null ? farmlandOwnershipDto.getOwnerCleanedUpName()
							: null);
			farmerLandOwnership.setOwnershipShareExtent(farmlandOwnershipDto.getOwnershipShareExtent() != null
					? farmlandOwnershipDto.getOwnershipShareExtent()
					: null);
			farmerLandOwnership.setOwnerIdentifierNamePerRor(farmlandOwnershipDto.getOwnerIdentifierNamePerRor() != null
					? farmlandOwnershipDto.getOwnerIdentifierNamePerRor()
					: null);
			farmerLandOwnership.setOwnerIdentifierTypePerRor(farmlandOwnershipDto.getOwnerIdentifierTypePerRor() != null
					? farmlandOwnershipDto.getOwnerIdentifierTypePerRor()
					: null);
			farmerLandOwnership.setExtentAssignedArea(
					farmlandOwnershipDto.getExtentAssignedArea() != null ? farmlandOwnershipDto.getExtentAssignedArea()
							: null);
			farmerLandOwnership.setExtentTotalArea(
					farmlandOwnershipDto.getExtentTotalArea() != null ? farmlandOwnershipDto.getExtentTotalArea()
							: null);
			// farmerLandOwnership.setUtUnitTypeMasterId();
			// farmerLandOwnership.setUpdatedDate(new Date());
			// farmerLandOwnership.setSynchronisationDate(new Date());
			// farmerLandOwnership.setPlotStatusMasterId();
			farmerLandOwnership.setFarmerNameMatchScoreRor(farmlandOwnershipDto.getFarmerNameMatchScoreRor() != null
					? farmlandOwnershipDto.getFarmerNameMatchScoreRor()
					: null);
			farmerLandOwnership.setMainOwnerNoAsPerRor(farmlandOwnershipDto.getMainOwnerNoAsPerRor() != null
					? farmlandOwnershipDto.getMainOwnerNoAsPerRor()
					: null);
			farmerLandOwnership.setOwnerType(
					farmlandOwnershipDto.getOwnerType() != null ? farmlandOwnershipDto.getOwnerType() : null);
			farmerLandOwnership.setOwnershipShareType(
					farmlandOwnershipDto.getOwnershipShareType() != null ? farmlandOwnershipDto.getOwnershipShareType()
							: null);
			if (farmlandOwnershipDto.getOwnershipShareType() != null) {
				farmerLandOwnership.setOwnershipShareType(farmlandOwnershipDto.getOwnershipShareType());
			}
			farmerLandOwnership = farmerLandOwnershipRegistryRepository.save(farmerLandOwnership);

			// return new
			// ResponseEntity<Mono<?>>(Mono.just(ResponseMessages.Toast(ResponseMessages.SUCCESS,
			// "Farmland ownership details saved Successfully", farmerLandOwnership)),
			// HttpStatus.OK);
			return farmerLandOwnership;
		} catch (Exception e) {
			e.printStackTrace();
			// return new ResponseEntity<Mono<?>>(
			// Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR,
			// e.getMessage(), null)),
			// HttpStatus.INTERNAL_SERVER_ERROR);
			return null;
		}
	}

	/**
	 * fetch farmer land owner ship mapping by farmer id
	 * 
	 * @param farmerRegistryId
	 * @return List of FarmerLandOwnershipRegistry
	 */
	public List<FarmerLandOwnershipRegistry> getFarmerLandOwnerShipByFarmerRegistryId(Long farmerRegistryId) {
		return farmerLandOwnershipRegistryRepository
				.findByFarmerRegistryIdFarmerRegistryIdAndIsDeletedIsFalse(farmerRegistryId);
	}

	/*
	 * public List<FarmerRegistry>
	 * getFarmerLandOwnershipRegistriesByPlotVillageCode(Long villageCode) { return
	 * farmerLandOwnershipRegistryRepository.getLandOwnerShipDetailsWithFarmer(
	 * villageCode); }
	 */
	/**
	 * read excel file and upload land data
	 */
	public void readExcelAndUploadDataOfOwner() {
		List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters = dummyLandOwnerShipMasterRepository
				.findByVillageLgdCode(80254L);
		/*
		 * List<DummyLandOwnerShipMaster> dummyLandOwnerSameOwner = new ArrayList<>();
		 * List<DummyLandOwnerShipMaster> dummyLandOwnerDifferentOwner = new
		 * ArrayList<>();
		 * 
		 * // String dummyLandOwnerSameOwner =
		 * dummyLandOwnerShipMasters.parallelStream().filter(dummyLandDetails ->
		 * dummyLandDetails
		 * .getMainOwnerNo().trim().equalsIgnoreCase(dummyLandDetails.getOwnerNo().trim(
		 * ))) .collect(Collectors.toList());
		 * 
		 * dummyLandOwnerDifferentOwner = dummyLandOwnerShipMasters
		 * .parallelStream().filter(dummyLandDetails -> dummyLandDetails != null &&
		 * !(dummyLandDetails
		 * .getMainOwnerNo().trim().equalsIgnoreCase(dummyLandDetails.getOwnerNo().trim(
		 * )))) .collect(Collectors.toList());
		 * 
		 * saveAllLandDetails(dummyLandOwnerSameOwner); if
		 * (!CollectionUtils.isEmpty(dummyLandOwnerDifferentOwner)) {
		 * saveAllLandDetails(dummyLandOwnerDifferentOwner); }
		 */
		// uploadTelengaData(dummyLandOwnerShipMasters);
		saveAllLandDetailsSurveyNumberWise(dummyLandOwnerShipMasters);
	}

	/**
	 * read excel file and upload data
	 * 
	 * @param villageCode
	 * @param mediaMaster
	 */
	public void readExcelAndUploadDataOfOwnerWithVillageCode(Long villageCode, MediaMaster mediaMaster) {
		List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters = dummyLandOwnerShipMasterRepository
				.findByVillageLgdCode(villageCode);
		List<DummyLandOwnerShipMaster> dummyLandOwnerSameOwner = new ArrayList<>();
		List<DummyLandOwnerShipMaster> dummyLandOwnerDifferentOwner = new ArrayList<>();

		// String
		dummyLandOwnerSameOwner = dummyLandOwnerShipMasters.parallelStream().filter(dummyLandDetails -> dummyLandDetails
				.getMainOwnerNo().trim().equalsIgnoreCase(dummyLandDetails.getOwnerNo().trim()))
				.collect(Collectors.toList());

		dummyLandOwnerDifferentOwner = dummyLandOwnerShipMasters
				.parallelStream().filter(dummyLandDetails -> dummyLandDetails != null && !(dummyLandDetails
						.getMainOwnerNo().trim().equalsIgnoreCase(dummyLandDetails.getOwnerNo().trim())))
				.collect(Collectors.toList());

		saveAllLandDetails(dummyLandOwnerSameOwner);
		if (!CollectionUtils.isEmpty(dummyLandOwnerDifferentOwner)) {
			saveAllLandDetails(dummyLandOwnerDifferentOwner);
		}
		UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo
				.findByMediaMasterMediaId(mediaMaster.getMediaId());

		uploadLandAndOwnershipFileHistory.setIsread(true);
		uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo
				.save(uploadLandAndOwnershipFileHistory);

	}

	public void readExcelAndUploadDataOfOwnerForUpState(Long villageLgdCode) {
		List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters = dummyLandOwnerShipMasterRepository
				.findByVillageLgdCode(villageLgdCode);
		List<DummyLandOwnerShipMaster> dummyLandOwnerSameOwner = new ArrayList<>();
		List<DummyLandOwnerShipMaster> dummyLandOwnerDifferentOwner = new ArrayList<>();

		// String
		dummyLandOwnerSameOwner = dummyLandOwnerShipMasters.parallelStream().filter(dummyLandDetails -> dummyLandDetails
				.getMainOwnerNo().trim().equalsIgnoreCase(dummyLandDetails.getOwnerNo().trim()))
				.collect(Collectors.toList());

		dummyLandOwnerDifferentOwner = dummyLandOwnerShipMasters
				.parallelStream().filter(dummyLandDetails -> dummyLandDetails != null && !(dummyLandDetails
						.getMainOwnerNo().trim().equalsIgnoreCase(dummyLandDetails.getOwnerNo().trim())))
				.collect(Collectors.toList());

		saveAllLandDetails(dummyLandOwnerSameOwner);
		if (!CollectionUtils.isEmpty(dummyLandOwnerDifferentOwner)) {
			saveAllLandDetails(dummyLandOwnerDifferentOwner);
		}

	}

	public void readExcelAndUploadDataOfOwnerForUpState() {
		List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters = dummyLandOwnerShipMasterRepository
				.findByVillageLgdCode(141206L);
		List<DummyLandOwnerShipMaster> dummyLandOwnerSameOwner = new ArrayList<>();
		List<DummyLandOwnerShipMaster> dummyLandOwnerDifferentOwner = new ArrayList<>();

		// String
		dummyLandOwnerSameOwner = dummyLandOwnerShipMasters.parallelStream().filter(
				dummyLandDetails -> dummyLandDetails.getMainOwnerNo().equalsIgnoreCase(dummyLandDetails.getOwnerNo()))
				.collect(Collectors.toList());

		dummyLandOwnerDifferentOwner = dummyLandOwnerShipMasters.parallelStream()
				.filter(dummyLandDetails -> !(dummyLandDetails.getMainOwnerNo()
						.equalsIgnoreCase(dummyLandDetails.getOwnerNo())))
				.collect(Collectors.toList());

		saveAllLandDetails(dummyLandOwnerSameOwner);
		saveAllLandDetails(dummyLandOwnerDifferentOwner);

	}

	/**
	 * save all land details
	 * 
	 * @param dummyLandOwnerShipMasters
	 */
	public void saveAllLandDetails(List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters) {
		for (DummyLandOwnerShipMaster dummyLandOwnerShipMaster : dummyLandOwnerShipMasters) {
			if (dummyLandOwnerShipMaster != null) {
				
				try {

					String surveyNumber = dummyLandOwnerShipMaster.getSurveyNumber();
					String subSurveyNumber = dummyLandOwnerShipMaster.getSubSurveyNumber();
					Long villageLgdCode = dummyLandOwnerShipMaster.getVillageLgdCode();
					 
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
						VillageLgdMaster villageLgdMaster = villageLgdMasterRepository
								.findByVillageLgdCode(plotList.get(0).getVillageLgdCode());
						StateUnitTypeMaster stateUnitTypeMaster = stateUnitTypeRepository
								.findByStateLgdCodeAndIsDefault(villageLgdMaster.getStateLgdCode(), true);

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
							farmlandPlotRegistry.setFarmlandId(
									commonUtil.genrateLandParcelId(villageLgdMaster.getStateLgdCode().getStateLgdCode()));
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

//							saveFarmLandPlot(farmlandPlotRegistry);
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
								farmlandPlotRegistry.setFarmlandId(commonUtil
										.genrateLandParcelId(villageLgdMaster.getStateLgdCode().getStateLgdCode()));
								farmlandPlotRegistry.setLandParcelId(plotList.get(0).getSurveyNumber());
								farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());
								// farmlandPlotRegistry.setUtUnitTypeMasterId(plotList.get(0).getUtUnitTypeMasterId());
								farmlandPlotRegistry.setVillageLgdMaster(villageLgdMaster);
								farmlandPlotRegistry.setPlotArea(Double.parseDouble(dummyLandOwnerShipMaster.getTotalHa()));
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
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private FarmlandPlotRegistry saveFarmLandPlot(FarmlandPlotRegistry farmlandPlotRegistry) {
		try {
			farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);
		} catch (Exception e) {
			farmlandPlotRegistry.setFarmlandId(
					commonUtil.genrateLandParcelIdV3("UP"));
			farmlandPlotRegistry = saveFarmLandPlot(farmlandPlotRegistry);
		}
		return farmlandPlotRegistry;
	}

	public void generateLandUniqueFarmId(Integer numberOfLand, Long stateLgdCode) {

		System.out.println(numberOfLand);
		File f = new File("D:\\generated.txt");
		for (int i = 0; i < numberOfLand; i++) {

			System.out.println(commonUtil.genrateLandParcelId(29L));
		}
	}

	public List<FarmerLandOwnershipRegistry> generateFarmer() {

		List<FarmerLandOwnershipRegistry> farmerLandOwnershipRegistries = farmerLandOwnershipRegistryRepository
				.findByFarmerRegistryIdFarmerRegistryIdAndIsDeletedIsFalse(150l);

		// farmerLandOwnershipRegistries.get(0);
		return farmerLandOwnershipRegistries;
	}

	public void readExcelAndUploadDataOfOwnerForUpStateFromApi() throws JsonMappingException, JsonProcessingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchPaddingException {
		String encryptedVillageLgdCode = CommonUtil.getEncryptedVillageCode(141215l);
		String url = "https://upbhulekh.gov.in/WS_AgriStack/villageDetail?vCode=" + encryptedVillageLgdCode;
		WebClient client = WebClient.create();
		WebClient.ResponseSpec responseSpec = client.get().uri(url).retrieve();
		String response = responseSpec.bodyToMono(String.class).block();
		JSONArray jsonObject = new JSONArray(response);
		System.out.println(jsonObject.toString());
		ObjectMapper objectMapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		// List<UpApiResponseDto> ownerShipList =
		// objectMapper.readValue(jsonObject.toString(), List.class);
		// System.out.println(ownerShipList);
		List<UpApiResponseDto> ownerShipList = objectMapper.readValue(response,
				new TypeReference<List<UpApiResponseDto>>() {
				});
		System.out.println(ownerShipList);

		if (!CollectionUtils.isEmpty(ownerShipList)) {
			ownerShipList = ownerShipList.parallelStream()
					.filter(ownerShip -> ownerShip.getLandUsageType().equalsIgnoreCase("Agriculture"))
					.collect(Collectors.toList());
			List<FarmerLandOwnershipRegistry> farmerLandOwnershipRegistrys = new ArrayList<>();

			for (UpApiResponseDto upApiResponseDto : ownerShipList) {
				FarmerLandOwnershipRegistry farmerLandOwnershipRegistry = new FarmerLandOwnershipRegistry();
				FarmerRegistry farmerRegistry = new FarmerRegistry();
				farmerRegistry.setFarmerNameLocal(upApiResponseDto.getOwnerName());
				farmerRegistry.setFarmerIdentifierNameLocal(upApiResponseDto.getIndentifierName());
				String uniqueNumber = Verhoeff.getFarmerUniqueIdWithChecksum();
				farmerRegistry.setFarmerRegistryNumber(uniqueNumber);
				// farmerRegistry.setFarmerRegistryNumber(commonUtil.);
				farmerLandOwnershipRegistry.setExtentAssignedArea(Double.parseDouble(upApiResponseDto.getTotalArea()));
				VillageLgdMaster villageLgdMaster = villageLgdMasterRepository
						.findByVillageLgdCode(Long.parseLong(upApiResponseDto.getVillageLGDCode()));
				// farmerLandOwnershipRegistry.
				StateUnitTypeMaster stateUnitTypeMaster = stateUnitTypeRepository
						.findByStateLgdCodeAndIsDefault(villageLgdMaster.getStateLgdCode(), true);
				farmerLandOwnershipRegistry.setOwnerIdentifierNamePerRor(upApiResponseDto.getIndentifierName());
				IdentifierTypeMaster identifierTypeMaster = identifierTypeRepository
						.findByIdentifierTypeDescEngIgnoreCase(upApiResponseDto.getIndentifierType().toLowerCase());
				if (identifierTypeMaster != null) {
					farmerLandOwnershipRegistry
							.setOwnerIdentifierTypePerRor(identifierTypeMaster.getIdentifierTypeMasterId().intValue());
				}
				farmerLandOwnershipRegistry.setOwnerNamePerRor(upApiResponseDto.getOwnerName());
				farmerLandOwnershipRegistry.setOwnerNoAsPerRor(upApiResponseDto.getOwnerNumber());
				OwnerType ownerType = null;
				if (Double.parseDouble(upApiResponseDto.getTotalArea()) > 0) {
					ownerType = ownerTypeRepository.findByOwnerTypeDescEng("Single");
				} else {
					ownerType = ownerTypeRepository.findByOwnerTypeDescEng("Joint");
				}
				farmerLandOwnershipRegistry.setOwnerType(Integer.parseInt(ownerType.getOwnerTypeId().toString()));
				farmerRegistry = farmerRegistryRepository.save(farmerRegistry);

				// List<FarmerLandOwnershipRegistry>

				FarmlandPlotRegistry farmlandPlotRegistry = new FarmlandPlotRegistry();
				farmlandPlotRegistry.setFarmlandId(
						commonUtil.genrateLandParcelId(villageLgdMaster.getStateLgdCode().getStateLgdCode()));
				farmlandPlotRegistry.setLandParcelId(upApiResponseDto.getKhasraNo());
				// farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());
				// farmlandPlotRegistry.setUtUnitTypeMasterId(plotList.get(0).getUtUnitTypeMasterId());
				farmlandPlotRegistry.setVillageLgdMaster(villageLgdMaster);
				farmlandPlotRegistry.setPlotArea(Double.parseDouble(upApiResponseDto.getTotalArea()));
				farmlandPlotRegistry.setSurveyNumber(upApiResponseDto.getKhasraNo());

				if (stateUnitTypeMaster != null) {
					farmlandPlotRegistry.setUtUnitTypeMasterId(stateUnitTypeMaster);
				}
				List<DummyLandRecords> plotList = dummyLandRecordREpo.findBySurveyNumberAndVillageLgdCode(
						upApiResponseDto.getKhasraNo(), villageLgdMaster.getVillageLgdCode());
				farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);
				// farmLandPl
				if (!CollectionUtils.isEmpty(plotList)) {
					farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());
				}
			}

		}

	}

	public List<String> getAllMainOwnerNumber(Long villageCode, String surveyNumber) {

		return farmerLandOwnershipRegistryRepository.getAllOwnerNumberWithSameSurveyNumberAndVillageCode(surveyNumber,
				villageCode);
	}

	/**
	 * read excel from upload module and save the land details in database
	 * 
	 * @param uploadLandAndOwnershipDetailsDto
	 * @return
	 */
	public ResponseModel uploadExcelFile(UploadLandAndOwnershipDetailsDto uploadLandAndOwnershipDetailsDto) {
		// UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = new
		// UploadLandAndOwnershipFileHistory();
		UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistoryObj = new UploadLandAndOwnershipFileHistory();

		if (!CollectionUtils.isEmpty(uploadLandAndOwnershipDetailsDto.getExcelFile())) {
			// UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = new
			// UploadLandAndOwnershipFileHistory();

			for (MultipartFile file : uploadLandAndOwnershipDetailsDto.getExcelFile()) {
				Set<Long> villageList = new HashSet<>();

				UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = new UploadLandAndOwnershipFileHistory();
				MediaMaster mediaMaster = mediaMasterService.storeFile(file, "agristack", folderDocument, 0);
				uploadLandAndOwnershipFileHistory.setMediaMaster(mediaMaster);
				uploadLandAndOwnershipFileHistory.setContainIssue(false);
				uploadLandAndOwnershipFileHistory.setIsread(false);
				uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo
						.save(uploadLandAndOwnershipFileHistory);

				new Thread() {
					public void run() {

						int indexOfextension = file.getOriginalFilename().lastIndexOf(".");
						String extension = file.getOriginalFilename().substring(indexOfextension + 1);
						InputStream in;
						if (extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("csv")) {
							try {
								// as per dicussion need to change to village
								dummyLandOwnerShipMasterRepository.deleteAll();
								dummyLandRecordREpo.deleteAll();
								in = file.getInputStream();
								Workbook workbook;

								workbook = WorkbookFactory.create(in);

								Sheet sheet = workbook.getSheetAt(0);
								// Create a DataFormatter to format and get each cell's value as String
								DataFormatter dataFormatter = new DataFormatter();

								// 1. You can obtain a rowIterator and columnIterator and iterate over them
								Iterator<Row> rowIterator = sheet.rowIterator();
								rowIterator.next();
								int rowCount = 0;
								while (rowIterator.hasNext()) {
									int count = 0;

									Row row = rowIterator.next();
									DummyLandOwnerShipMaster dummyLandOwnerShipMaster = new DummyLandOwnerShipMaster();
									DummyLandRecords dummyLandRecords = new DummyLandRecords();

									// Now let's iterate over the columns of the current row
									Iterator<Cell> cellIterator = row.cellIterator();
									while (cellIterator.hasNext()) {

										Cell cell = cellIterator.next();
										String cellValue = dataFormatter.formatCellValue(cell);
										String cells;
										// switch (cellValue) {

										if (count == 0) {
//											System.out.print(cellValue + "\t");
											// cell = cellIterator.next();
											// cells = dataFormatter.formatCellValue(cell);
											dummyLandRecords.setSurveyNumber(cellValue);
											dummyLandOwnerShipMaster.setSurveyNumber(cellValue);
											// break;
										}
										if (count == 1) {
											// case "sub_survey_number":
//											System.out.print(cellValue + "\t");
											// cell = cellIterator.next();
											// cells = dataFormatter.formatCellValue(cell);
											if (!cellValue.equalsIgnoreCase("na")) {
												dummyLandRecords.setSubSurveyNumber(cellValue);
												dummyLandOwnerShipMaster.setSubSurveyNumber(cellValue);
											} else {
												dummyLandRecords.setSubSurveyNumber("");
												dummyLandOwnerShipMaster.setSubSurveyNumber("");
											}
										}

										// break;
										// case "village_lgd_code":
										// owner number
										if (count == 2) {
//											System.out.print(cellValue + "\t");
											// cell = cellIterator.next();
											// cells = dataFormatter.formatCellValue(cell);
											if (!cellValue.equalsIgnoreCase("na")) {
												dummyLandOwnerShipMaster.setOwnerNo(cellValue);
											}
										}

										// owner name
										if (count == 3) {
//											System.out.print(cellValue + "\t");
											// cell = cellIterator.next();
											// cells = dataFormatter.formatCellValue(cell);
											if (!cellValue.equalsIgnoreCase("na")) {
												String ownerName = cellValue.substring(0,
														Math.min(cellValue.length(), 100));
												dummyLandOwnerShipMaster.setOwnerName(ownerName);
											}
										}
										// father or identifier name
										if (count == 4) {
//											System.out.print(cellValue + "\t");
											// cell = cellIterator.next();
											// cells = dataFormatter.formatCellValue(cell);
											if (!cellValue.equalsIgnoreCase("na")) {
												dummyLandOwnerShipMaster.setIdentifierName(cellValue);
											}
										}

										// identifier type
										if (count == 5) {
//											System.out.print(cellValue + "\t");
											// cell = cellIterator.next();
											// cells = dataFormatter.formatCellValue(cell);
											if (!cellValue.equalsIgnoreCase("na")) {
												dummyLandOwnerShipMaster.setIdentifierType(cellValue);
											}
										}
										// main owner number
										if (count == 6) {
//											System.out.print(cellValue + "\t");
											// cell = cellIterator.next();
											// cells = dataFormatter.formatCellValue(cell);
											if (!cellValue.equalsIgnoreCase("na")) {
												dummyLandOwnerShipMaster.setMainOwnerNo(cellValue);
											}
										}

										// total area ha
										if (count == 7) {
//											System.out.print(cellValue + "\t");
											// cell = cellIterator.next();
											// cells = dataFormatter.formatCellValue(cell);
											if (!cellValue.equalsIgnoreCase("na")) {
												dummyLandOwnerShipMaster.setTotalHa(cellValue);
											}
										}
										// total area ha
										/*
										 * if (count == 8) { System.out.print(cellValue + "\t"); // cell =
										 * cellIterator.next(); // cells = dataFormatter.formatCellValue(cell); if
										 * (!cellValue.equalsIgnoreCase("na")) {
										 * dummyLandRecords.setTotalArea(cellValue); } }
										 */
										if (count == 8) {
//											System.out.print(cellValue + "\t");
											// cell = cellIterator.next();
											// cells = dataFormatter.formatCellValue(cell);
//											System.out.print(cellValue + "\t");
											try {
												dummyLandRecords.setVillageLgdCode(Long.parseLong(cellValue));
												dummyLandOwnerShipMaster.setVillageLgdCode(Long.parseLong(cellValue));

												villageList.add(Long.parseLong(cellValue));
											} catch (Exception e) {

											}
										}

										// break;
										// case "geom_str":
										if (count == 9) {
											// cell = cellIterator.next();
											// cells = dataFormatter.formatCellValue(cell);
//											System.out.print(cellValue + "\t");

											WKTReader wktReader = new WKTReader();
											Geometry geometry = wktReader.read(cellValue);
											geometry.setSRID(4326);
											dummyLandRecords.setGeom(geometry);
											GeometryFactory geometryFactory = new GeometryFactory();
											geometryFactory.createGeometry(geometry);
											// geometryFactory.s
											dummyLandRecords.setGeom(geometry);
										}
										/*
										 * if(count==10) { System.out.print(cellValue + "\t");
										 * dummyLandOwnerShipMaster.setAadharNo(cellValue);
										 * 
										 * } if(count==11) { System.out.print(cellValue + "\t");
										 * dummyLandOwnerShipMaster.setMobileNo(cellValue);
										 * 
										 * }
										 */

										// break;
										// }

										count++;
									}
									rowCount++;
									System.err.println(rowCount);

									dummyLandOwnerShipMasterRepository.save(dummyLandOwnerShipMaster);
									dummyLandRecordREpo.save(dummyLandRecords);
								}

							} catch (InvalidFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						// return null;
//          return new UploadMediaResponse(mediaMaster.getMediaId(), mediaMaster.getMediaUrl(), fileDownloadUri,
						// file.getContentType(), file.getSize(), HttpStatus.OK);
						if (!CollectionUtils.isEmpty(villageList)) {
							// readExcelAndUploadDataOfOwnerForGjState();

							villageList.forEach(village -> {
								readExcelAndUploadDataOfOwnerWithVillageCode(village, mediaMaster);

							});

							// uploadLandAndOwnershipFileHistory.setIsread(true);
							// uploadLandAndOwnershipFileHistory=
							// uploadLandAndOwnershipFileHistoryRepo.save(uploadLandAndOwnershipFileHistory);

						}
					}
				}.start();
			}

		}
		// return null;
		return new ResponseModel(uploadLandAndOwnershipFileHistoryObj, CustomMessages.FILE_UPLOAD_SUCCESS,
				CustomMessages.ADD_SUCCESSFULLY, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.METHOD_POST);
	}

	public List<UploadLandAndOwnershipFileHistory> getAllLandAndOwnershipFileHistories() {

		return uploadLandAndOwnershipFileHistoryRepo.findAllByOrderByCreatedOnDesc();
	}

	public void generateUserForFarmer(Long villageLgdCode) {
		List<FarmerRegistry> farmerRegistries = farmerLandOwnershipRegistryRepository
				.getLandOwnerShipDetailsWithFarmer(villageLgdCode);

		if (!CollectionUtils.isEmpty(farmerRegistries)) {
			farmerRegistries.forEach(farmerRegistry -> {
				UserMaster userMaster = new UserMaster();
				RoleMaster roleMaster = roleMasterRepository.findByCodeAndIsDefaultTrue("FARMER");

				if (farmerRegistry.getFarmerNameEn().length() > 24) {
					userMaster.setUserFirstName(farmerRegistry.getFarmerNameEn().substring(0, 24));

				} else {
					userMaster.setUserFirstName(farmerRegistry.getFarmerNameEn());
				}

				if (farmerRegistry.getFarmerNameEn().length() > 24) {
					userMaster.setUserName(farmerRegistry.getFarmerNameEn().substring(0, 24));

				} else {
					userMaster.setUserName(farmerRegistry.getFarmerNameEn());
				}

				userMaster.setRoleId(roleMaster);
				userMaster.setUserMobileNumber(farmerRegistry.getFarmerMobileNumber());
				userMaster.setIsActive(true);
				userMaster.setIsDeleted(false);
				userMaster.setUserPassword(encoder.encode("Farmer@123"));
				if (farmerRegistry.getFarmerNameEn().length() > 24) {
					userMaster.setUserFullName(farmerRegistry.getFarmerNameEn().substring(0, 24));
				} else {
					userMaster.setUserFullName(farmerRegistry.getFarmerNameEn());
				}
				userMaster = userMasterRepository.save(userMaster);
				farmerRegistry.setUserMaster(userMaster);
				farmerRegistryRepository.save(farmerRegistry);
				UserVillageMapping userVillageMapping = new UserVillageMapping();
				VillageLgdMaster villageLgdMaster = villageLgdMasterRepository.findByVillageLgdCode(villageLgdCode);
				userVillageMapping.setVillageLgdMaster(villageLgdMaster);
				userVillageMapping.setUserMaster(userMaster);
				userVillageMappingRepository.save(userVillageMapping);

			});
		}
	}

	public void readExcelAndUploadDataOfOwnerForGjState() {
		List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters = dummyLandOwnerShipMasterRepository
				.findByVillageLgdCode(80254L);
		/*
		 * List<DummyLandOwnerShipMaster> dummyLandOwnerSameOwner = new ArrayList<>();
		 * List<DummyLandOwnerShipMaster> dummyLandOwnerDifferentOwner = new
		 * ArrayList<>();
		 * 
		 * // String dummyLandOwnerSameOwner =
		 * dummyLandOwnerShipMasters.parallelStream().filter( dummyLandDetails ->
		 * dummyLandDetails.getMainOwnerNo().equalsIgnoreCase(dummyLandDetails.
		 * getOwnerNo())) .collect(Collectors.toList());
		 * 
		 * dummyLandOwnerDifferentOwner = dummyLandOwnerShipMasters.parallelStream()
		 * .filter(dummyLandDetails -> !(dummyLandDetails.getMainOwnerNo()
		 * .equalsIgnoreCase(dummyLandDetails.getOwnerNo())))
		 * .collect(Collectors.toList());
		 * 
		 * uploadTelengaData(dummyLandOwnerSameOwner);
		 */
		// saveAllLandDetails(dummyLandOwnerSameOwner);
		// saveAllLandDetails(dummyLandOwnerDifferentOwner);
		saveAllLandDetailsSurveyNumberWise(dummyLandOwnerShipMasters);
	}

	private void saveAllLandDetailsSurveyNumberWise(List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters) {
		/*
		 * for (DummyLandOwnerShipMaster dummyLandOwnerShipMaster :
		 * dummyLandOwnerShipMasters) {
		 * 
		 * 
		 * }
		 */
		Map<String, List<DummyLandOwnerShipMaster>> dummyLandOwnerShipMastersMap = dummyLandOwnerShipMasters.stream()
				.collect(Collectors.groupingBy(DummyLandOwnerShipMaster::getSurveyNumber));

		System.out.println(dummyLandOwnerShipMastersMap.size());
		for (Entry<String, List<DummyLandOwnerShipMaster>> dummyLandOwnerShipMaster : dummyLandOwnerShipMastersMap
				.entrySet()) {
			Long mainOwnerId = 100L;
			// mainOwnerId=dummyLandOwnerShipMaster.getValue().stream().max(DummyLandOwnerShipMaster::getMainOwnerNo)
			// //Long ownerId=(long) 2;
			// mainOwnerShipMaster=new
			FarmerLandOwnershipRegistry farmerLandOwnershipRegistryMain = new FarmerLandOwnershipRegistry();
			farmerLandOwnershipRegistryMain.setIsActive(true);
			farmerLandOwnershipRegistryMain.setIsDeleted(false);
			// farmerLandOwnershipRegistry.setMainOwnerNoAsPerRor(mainOwnerId);
			farmerLandOwnershipRegistryMain.setOwnerNoAsPerRor(mainOwnerId.toString());
			// farmerLandOwnershipRegistry
			// .setOwnerIdentifierNamePerRor(dummyLandOwnerShipMaster.getIdentifierName());
			// farmerLandOwnershipRegistry.setMainOwnerName(dummyLandOwnerShipMaster.getOwnerName());
			OwnerType ownerType = ownerTypeRepository.findByOwnerTypeDescEng("Joint");
			farmerLandOwnershipRegistryMain.setOwnerType(Integer.parseInt(ownerType.getOwnerTypeId().toString()));
			farmerLandOwnershipRegistryMain.setMainOwnerNoAsPerRor(mainOwnerId.intValue());

			// farmerRegistry = farmerRegistryRepository.save(farmerRegistry);
			// List<FarmerLandOwnershipRegistry>
			// farmerLandOwnershipRegistries=farmerLandOwnershipRegistryRepository.findByFarmlandPlotRegistryFarmlandPlotRegistryId(plotList.get(0).getFarmlandPlotRegistryId());

			// if(farmerLandOwnershipRegistries.size()>1) {
			List<DummyLandRecords> plotList = dummyLandRecordREpo.findBySurveyNumberAndVillageLgdCode(
					dummyLandOwnerShipMaster.getKey(), dummyLandOwnerShipMaster.getValue().get(0).getVillageLgdCode());
			if (!CollectionUtils.isEmpty(plotList)) {
				VillageLgdMaster villageLgdMaster = villageLgdMasterRepository
						.findByVillageLgdCode(plotList.get(0).getVillageLgdCode());
				StateUnitTypeMaster stateUnitTypeMaster = stateUnitTypeRepository
						.findByStateLgdCodeAndIsDefault(villageLgdMaster.getStateLgdCode(), true);

				FarmlandPlotRegistry farmlandPlotRegistry = new FarmlandPlotRegistry();
				farmlandPlotRegistry.setFarmlandId(
						commonUtil.genrateLandParcelId(villageLgdMaster.getStateLgdCode().getStateLgdCode()));
				farmlandPlotRegistry.setLandParcelId(plotList.get(0).getSurveyNumber());
				farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());
				// farmlandPlotRegistry.setUtUnitTypeMasterId(plotList.get(0).getUtUnitTypeMasterId());
				farmlandPlotRegistry.setVillageLgdMaster(villageLgdMaster);
				farmlandPlotRegistry
						.setPlotArea(Double.parseDouble(dummyLandOwnerShipMaster.getValue().get(0).getTotalHa()));
				farmlandPlotRegistry.setSurveyNumber(plotList.get(0).getSurveyNumber());
				farmlandPlotRegistry.setSubSurveyNumber(" ");

				if (stateUnitTypeMaster != null) {
					farmlandPlotRegistry.setUtUnitTypeMasterId(stateUnitTypeMaster);
				}

				farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);
				// farmLandPl

				// }
				// if(Farmer)
				farmerLandOwnershipRegistryMain
						.setOwnerNamePerRor("The owner of " + farmlandPlotRegistry.getSurveyNumber());
				farmerLandOwnershipRegistryMain.setMainOwnerName(farmerLandOwnershipRegistryMain.getOwnerNamePerRor());
				farmerLandOwnershipRegistryMain.setFarmlandPlotRegistryId(farmlandPlotRegistry);
				farmerLandOwnershipRegistryMain.setExtentAssignedArea(farmlandPlotRegistry.getPlotArea());
				// farmerLandOwnershipRegistry.setFarmerRegistryId(farmerRegistry);
				farmerLandOwnershipRegistryMain = farmerLandOwnershipRegistryRepository
						.save(farmerLandOwnershipRegistryMain);
			}
			// Double totalArea=0.0;
			Long ownerId = 1L;
			for (DummyLandOwnerShipMaster dummyLandOwnerShip : dummyLandOwnerShipMaster.getValue()) {
				FarmerLandOwnershipRegistry farmerLandOwnershipRegistry = new FarmerLandOwnershipRegistry();

				// String surveyNumber = dummyLandOwnerShip.getSurveyNumber();
				// String subSurveyNumber = dummyLandOwnerShipMaster.getSubSurveyNumber();
				// Long villageLgdCode = dummyLandOwnerShip.getVillageLgdCode();
				farmerLandOwnershipRegistry.setOwnerNoAsPerRor(ownerId.toString());
				farmerLandOwnershipRegistry.setOwnerNoAsPerRor(dummyLandOwnerShip.getOwnerNo());
				// dummyLandOwnerShip.setOwnerName();
				farmerLandOwnershipRegistry.setOwnerType(Integer.parseInt(ownerType.getOwnerTypeId().toString()));
				farmerLandOwnershipRegistry.setMainOwnerNoAsPerRor(mainOwnerId.intValue());
				farmerLandOwnershipRegistry.setExtentAssignedArea(0.0);
				if (dummyLandOwnerShip.getOwnerName().length() >= 100) {
					farmerLandOwnershipRegistry.setOwnerNamePerRor(dummyLandOwnerShip.getOwnerName().substring(0, 99));

				} else {
					farmerLandOwnershipRegistry.setOwnerNamePerRor(dummyLandOwnerShip.getOwnerName());

				}
				farmerLandOwnershipRegistry
						.setFarmlandPlotRegistryId(farmerLandOwnershipRegistryMain.getFarmlandPlotRegistryId());
				farmerLandOwnershipRegistry.setMainOwnerName(farmerLandOwnershipRegistryMain.getOwnerNamePerRor());
				// totalArea+=farmerLandOwnershipRegistry.getExtentAssignedArea();
				farmerLandOwnershipRegistryRepository.save(farmerLandOwnershipRegistry);

				ownerId++;
			}
			// farmerLandOwnershipRegistryMain.setExtentAssignedArea(totalArea);
			// farmerLandOwnershipRegistryMain.getFarmlandPlotRegistryId().setPlotArea(totalArea);
			// farmerLandOwnershipRegistryRepository.save(farmerLandOwnershipRegistryMain);

		}

	}

	private void uploadTelengaData(List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters) {
		// OwnerType ownerType = ownerTypeRepository.findByOwnerTypeDescEng("Single");
		for (DummyLandOwnerShipMaster dummyLandOwnerShipMaster : dummyLandOwnerShipMasters) {
			if (dummyLandOwnerShipMaster != null) {
				String surveyNumber = dummyLandOwnerShipMaster.getSurveyNumber();
				String subSurveyNumber = dummyLandOwnerShipMaster.getSubSurveyNumber();
				Long villageLgdCode = dummyLandOwnerShipMaster.getVillageLgdCode();

				/*
				 * List<FarmlandPlotRegistry> plotList = farmlandPlotRegistryRepository
				 * .findByVillageLgdMasterVillageLgdCodeAndSurveyNumberAndSubSurveyNumber(
				 * villageLgdCode, surveyNumber, subSurveyNumber);
				 */
				List<DummyLandRecords> plotList = dummyLandRecordREpo
						.findBySurveyNumberAndSubSurveyNumberAndVillageLgdCodeLimit1(
								dummyLandOwnerShipMaster.getSurveyNumber(),
								dummyLandOwnerShipMaster.getSubSurveyNumber(), villageLgdCode);
				FarmerLandOwnershipRegistry farmerLandOwnershipRegistry = new FarmerLandOwnershipRegistry();
				farmerLandOwnershipRegistry.setIsActive(true);
				farmerLandOwnershipRegistry.setIsDeleted(false);
				farmerLandOwnershipRegistry
						.setMainOwnerNoAsPerRor(Integer.valueOf(dummyLandOwnerShipMaster.getMainOwnerNo()));
				farmerLandOwnershipRegistry.setOwnerNoAsPerRor(dummyLandOwnerShipMaster.getOwnerNo());

				// farmerLandOwnershipRegistry.setOwnerNamePerRor(dummyLandOwnerShipMaster.getOwnerName());
				if (dummyLandOwnerShipMaster.getOwnerName().length() >= 100) {
					farmerLandOwnershipRegistry
							.setOwnerNamePerRor(dummyLandOwnerShipMaster.getOwnerName().substring(0, 99));
					farmerLandOwnershipRegistry
							.setMainOwnerName(dummyLandOwnerShipMaster.getOwnerName().substring(0, 99));

				} else {
					farmerLandOwnershipRegistry.setOwnerNamePerRor(dummyLandOwnerShipMaster.getOwnerName());
					farmerLandOwnershipRegistry.setMainOwnerName(dummyLandOwnerShipMaster.getOwnerName());

				}
				// farmerLandOwnershipRegistry.setMainOwnerName();
				//
				if (!CollectionUtils.isEmpty(plotList)) {
					VillageLgdMaster villageLgdMaster = villageLgdMasterRepository
							.findByVillageLgdCode(plotList.get(0).getVillageLgdCode());
					StateUnitTypeMaster stateUnitTypeMaster = stateUnitTypeRepository
							.findByStateLgdCodeAndIsDefault(villageLgdMaster.getStateLgdCode(), true);

					if (dummyLandOwnerShipMaster.getOwnerNo()
							.equalsIgnoreCase(dummyLandOwnerShipMaster.getMainOwnerNo())) {

						// FarmerRegistry farmerRegistry = new FarmerRegistry();
						/*
						 * Optional<FarmerRegistry> farmerRegistryOp=
						 * farmerRegistryRepository.findByFarmerMobileNumber(dummyLandOwnerShipMaster.
						 * getMobileNo()); if(farmerRegistryOp.isPresent()) {
						 * farmerRegistry=farmerRegistryOp.get(); }else { String uniqueNumber =
						 * Verhoeff.getFarmerUniqueIdWithChecksum();
						 * farmerRegistry.setFarmerRegistryNumber(uniqueNumber);
						 * 
						 * }
						 */
						// farmerRegistry.setFarmerMobileNumber(dummyLandOwnerShipMaster.getMobileNo());
						// farmerRegistry.setFarmerAadhaarHash(Base64.getEncoder().encodeToString(dummyLandOwnerShipMaster.getAadharNo().getBytes()));
						// farmerRegistry.setFarmerNameLocal(dummyLandOwnerShipMaster.getOwnerName());
						// farmerRegistry.setFarmerIdentifierNameLocal(dummyLandOwnerShipMaster.getIdentifierName());
						if (dummyLandOwnerShipMaster.getIdentifierName().length() >= 100) {

							farmerLandOwnershipRegistry.setOwnerIdentifierNamePerRor(
									dummyLandOwnerShipMaster.getIdentifierName().substring(0, 99));
						} else {
							farmerLandOwnershipRegistry
									.setOwnerIdentifierNamePerRor(dummyLandOwnerShipMaster.getIdentifierName());
						}
						OwnerType ownerType = ownerTypeRepository.findByOwnerTypeDescEng("Single");
						farmerLandOwnershipRegistry
								.setOwnerType(Integer.parseInt(ownerType.getOwnerTypeId().toString()));
						// farmerRegistry = farmerRegistryRepository.save(farmerRegistry);
						// List<FarmerLandOwnershipRegistry>
						// farmerLandOwnershipRegistries=farmerLandOwnershipRegistryRepository.findByFarmlandPlotRegistryFarmlandPlotRegistryId(plotList.get(0).getFarmlandPlotRegistryId());

						// if(farmerLandOwnershipRegistries.size()>1) {

						FarmlandPlotRegistry farmlandPlotRegistry = new FarmlandPlotRegistry();
						farmlandPlotRegistry.setFarmlandId(
								commonUtil.genrateLandParcelId(villageLgdMaster.getStateLgdCode().getStateLgdCode()));
						farmlandPlotRegistry.setLandParcelId(plotList.get(0).getSurveyNumber());
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
						// farmerLandOwnershipRegistry.setFarmerRegistryId(farmerRegistry);
						farmerLandOwnershipRegistryRepository.save(farmerLandOwnershipRegistry);
					}
				}
			}
		}
	}

	public ResponseModel uploadExcelFileReadHeader(UploadLandAndOwnershipDetailsDto uploadLandAndOwnershipDetailsDto)
			throws IOException, InvalidFormatException {
		// UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = new
		// UploadLandAndOwnershipFileHistory();
		// UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistoryObj = new
		// UploadLandAndOwnershipFileHistory();
		List<UploadLandDetailsExcelFileColumnDao> columnHeader = new ArrayList<UploadLandDetailsExcelFileColumnDao>();

		if (!CollectionUtils.isEmpty(uploadLandAndOwnershipDetailsDto.getExcelFile())) {
			// UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = new
			// UploadLandAndOwnershipFileHistory();

			for (MultipartFile file : uploadLandAndOwnershipDetailsDto.getExcelFile()) {
				// Set<Long> villageList = new HashSet<>();

				UploadLandAndOwnershipFileHistory uploadLandAndOwnershipFileHistory = new UploadLandAndOwnershipFileHistory();
				MediaMaster mediaMaster = mediaMasterService.storeFile(file, "agristack", folderDocument, 0);
				uploadLandAndOwnershipFileHistory.setMediaMaster(mediaMaster);
				uploadLandAndOwnershipFileHistory.setContainIssue(false);
				uploadLandAndOwnershipFileHistory.setIsread(false);
				uploadLandAndOwnershipFileHistory = uploadLandAndOwnershipFileHistoryRepo
						.save(uploadLandAndOwnershipFileHistory);

				int indexOfextension = file.getOriginalFilename().lastIndexOf(".");
				String extension = file.getOriginalFilename().substring(indexOfextension + 1);
				InputStream in;
				if (extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("csv")) {
					in = file.getInputStream();
					Workbook workbook;

					workbook = WorkbookFactory.create(in);

					Sheet sheet = workbook.getSheetAt(0);
					// Create a DataFormatter to format and get each cell's value as String
					DataFormatter dataFormatter = new DataFormatter();

					// 1. You can obtain a rowIterator and columnIterator and iterate over them
					Iterator<Row> rowIterator = sheet.rowIterator();
					// rowIterator.next();
					Row row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					int indexValue = 0;
					while (cellIterator.hasNext()) {

						Cell cell = cellIterator.next();
						String cellValue = dataFormatter.formatCellValue(cell);
						String cells;
						UploadLandDetailsExcelFileColumnDao uploadLandDetailsExcelFileColumnDao = new UploadLandDetailsExcelFileColumnDao(
								cellValue, indexValue);
						columnHeader.add(uploadLandDetailsExcelFileColumnDao);
						System.out.println(cellValue);
						indexValue++;

					}
				}
			}
			// return null;
		}
		// return null;
		return new ResponseModel(columnHeader, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.ADD_SUCCESSFULLY,
				CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.METHOD_POST);
	}

	public ResponseModel uploadShpAndReadColumns(UploadLandAndOwnershipDetailsDto uploaadLandAndOwnershipDetailsDto)
			throws IOException {

		if (!CollectionUtils.isEmpty(uploaadLandAndOwnershipDetailsDto.getShpFile())) {
			Resource shpFilePath = null;
			String folderName = "shpFile" + new Date().getTime();
			String mainFolder = "agristack";
			for (MultipartFile file : uploaadLandAndOwnershipDetailsDto.getShpFile()) {
				// if()
				MediaMaster mediaMaster = mediaMasterService.storeFileWithSameName(file, "agristack", folderName, 0);
				int lastIndexOfDot = mediaMaster.getMediaUrl().lastIndexOf('.');

				if (mediaMaster.getMediaUrl().substring(lastIndexOfDot + 1).toLowerCase().equalsIgnoreCase("shp")) {
//					shpFilePath=mediaMasterService.loadFileAsResource(mediaMaster.getMediaUrl(), folderName, mediaMaster.getMediaType());
					Path filePath = Paths.get(path + File.separator + mainFolder + File.separator + folderName
							+ File.separator + mediaMaster.getMediaUrl()).toAbsolutePath().normalize();
					shpFilePath = new UrlResource(filePath.toUri());
				}
			}
			String filePath = shpFilePath.getFile().getAbsolutePath();
//			DataStore dataStore = openDataStore(filePath);
//			final String[] typeNames = dataStore.getTypeNames();
//			System.out.println(typeNames);
//			final FeatureIterator<SimpleFeature> iterator = dataStore.getFeatureSource(typeNames[0]).getFeatures().features();
//			SimpleFeature feature =  iterator.next();
//		List<Property> properties=	feature.getProperties().stream().toList();
//		List<Name> names=	properties.stream().map(Property::getName).toList();
			// dataStore.getFeatureSource(typeNames[0]).getFeatures().
//		System.out.println(names);
//		List<String>columns=names.stream().map(Name::getURI).toList();
//		return new ResponseModel(columns, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.ADD_SUCCESSFULLY,
//				CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.METHOD_POST);
			return new ResponseModel(null, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.ADD_SUCCESSFULLY,
					CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.METHOD_POST);
		} else {
			return null;
		}

		/*
		 * String filePath="D:\\chitoor_data_agri_stack\\madhyaPradesh\\358182.shp";
		 * DataStore dataStore = openDataStore(filePath); final String[] typeNames =
		 * dataStore.getTypeNames(); System.out.println(typeNames); final
		 * FeatureIterator<SimpleFeature> iterator =
		 * dataStore.getFeatureSource(typeNames[0]).getFeatures().features();
		 * SimpleFeature feature = iterator.next(); List<Property> properties=
		 * feature.getProperties().stream().toList(); List<Name> names=
		 * properties.stream().map(Property::getName).toList();
		 * //dataStore.getFeatureSource(typeNames[0]).getFeatures().
		 * System.out.println(names);
		 * List<String>columns=names.stream().map(Name::getURI).toList(); return new
		 * ResponseModel(columns, CustomMessages.FILE_UPLOAD_SUCCESS,
		 * CustomMessages.ADD_SUCCESSFULLY, CustomMessages.FILE_UPLOAD_SUCCESS,
		 * CustomMessages.METHOD_POST);
		 */
	}
//	protected static DataStore openDataStore(final String filePath) throws IOException {
//		final File file = new File(filePath);
//		return new ShapefileDataStore(file.toURI().toURL());

	// final Map<String, Object> params = new HashMap<String, Object>();
	// params.put(ShapefileDataStoreFactory.URLP.key, file.toURI().toURL());
	// params.put(ShapefileDataStoreFactory.CREATE_SPATIAL_INDEX.key, Boolean.TRUE);
	// return DataStoreFinder.getDataStore(params);
//	}

//	public ResponseModel UploadLandAndPlotDetails(UploadLandAndOwnershipDetailsDto uploadLandAndOwnershipDetailsDto) {
//
//		return null;
//	}

//	public void fetchUPLandDataByVillageCode(Long villageLgdCode) {
//		// TODO Auto-generated method stub
//
//	}

	public ResponseModel deleteFarmLandPlotWithOwner(Long villageLgdCode) {
		try {

			farmerLandOwnershipRegistryRepository.deleteAllByVillageLgdCode(villageLgdCode);
			farmlandPlotRegistryRepository.deleteAllByVillageLgdCode(villageLgdCode);
			
			return new ResponseModel(null, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

	}
	public List<UploadLandAndOwnershipFileHistory> getAllLandAndOwnershipFileHistoriesByUser(HttpServletRequest request) {

		Long userId = Long.valueOf(CustomMessages.getUserId(request, jwtTokenUtil));
		return uploadLandAndOwnershipFileHistoryRepo.findAllByUploadedByOrderByCreatedOnDesc(userId);
	}
}
