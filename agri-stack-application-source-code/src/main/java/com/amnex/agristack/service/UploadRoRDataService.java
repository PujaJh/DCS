package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.Verhoeff;

@Service
public class UploadRoRDataService {

	@Autowired
	private StatePrefixMasterRepository statePrefixMasterRepository;

	@Autowired
	GeneralService generalService;

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
	private FarmerLandOwnershipRegistryRepository farmerLandOwnershipRegistryRepository;

	public void uploadDataAtSurveyLevelV2(Long villageCode) {
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

			List<StateUnitTypeMaster> stateUnitTypeMasters = stateUnitTypeRepository
					.findByUnitTypeDescEngIgnoreCaseAndStateLgdCode(dummyLandOwnerShipMasters.get(0).getAreaUnit(),
							villageLgdMaster.getStateLgdCode());

			if (stateUnitTypeMasters.size() > 0) {
				stateUnitTypeMaster = stateUnitTypeMasters.get(0);
			} else {
				stateUnitTypeMasters = stateUnitTypeRepository.findByUnitTypeDescEngIgnoreCaseAndStateLgdCode("Hectare",
						villageLgdMaster.getStateLgdCode());
				stateUnitTypeMaster = stateUnitTypeMasters.get(0);
			}
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

						farmlandPlotRegistry = saveFarmLandPlotRegistry(farmlandPlotRegistry, stateShortName);
//						farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);

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

	public void uploadDataAtOwnerLevelV2(Long villageCode) {
		try {

			List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters = dummyLandOwnerShipMasterRepository
					.findByVillageLgdCode(villageCode);

			List<DummyLandOwnerShipMaster> dummyLandOwnerSameOwner = new ArrayList<>();
			List<DummyLandOwnerShipMaster> dummyLandOwnerDifferentOwner = new ArrayList<>();

			dummyLandOwnerSameOwner = dummyLandOwnerShipMasters.parallelStream()
					.filter(dummyLandDetails -> dummyLandDetails.getMainOwnerNo().trim()
							.equalsIgnoreCase(dummyLandDetails.getOwnerNo().trim()))
					.collect(Collectors.toList());

			dummyLandOwnerDifferentOwner = dummyLandOwnerShipMasters
					.parallelStream().filter(dummyLandDetails -> dummyLandDetails != null && !(dummyLandDetails
							.getMainOwnerNo().trim().equalsIgnoreCase(dummyLandDetails.getOwnerNo().trim())))
					.collect(Collectors.toList());

			saveAllOwnerLandDetailsV2(dummyLandOwnerSameOwner);
			if (!CollectionUtils.isEmpty(dummyLandOwnerDifferentOwner)) {
				saveAllOwnerLandDetailsV2(dummyLandOwnerDifferentOwner);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void saveAllOwnerLandDetailsV2(List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters) {
		VillageLgdMaster villageLgdMaster = new VillageLgdMaster();
		if(!CollectionUtils.isEmpty(dummyLandOwnerShipMasters))
		  villageLgdMaster = villageLgdMasterRepository
				.findByVillageLgdCode(dummyLandOwnerShipMasters.get(0).getVillageLgdCode());
		
		StateUnitTypeMaster stateUnitTypeMaster = stateUnitTypeRepository
				.findByStateLgdCodeAndIsDefault(villageLgdMaster.getStateLgdCode(), true);

	    
				Optional<StatePrefixMaster> statePrefix = Optional.empty();
		if(villageLgdMaster.getStateLgdCode() !=null)
		 statePrefix = statePrefixMasterRepository
				.findById(villageLgdMaster.getStateLgdCode().getStateLgdCode());

		String stateShortName = "";

		if (statePrefix.isPresent()) {
			stateShortName = statePrefix.get().getStateShortName();
		}

		for (DummyLandOwnerShipMaster dummyLandOwnerShipMaster : dummyLandOwnerShipMasters) {
			try {
				if (dummyLandOwnerShipMaster != null) {
					String surveyNumber = dummyLandOwnerShipMaster.getSurveyNumber();
					String subSurveyNumber = dummyLandOwnerShipMaster.getSubSurveyNumber();
					Long villageLgdCode = dummyLandOwnerShipMaster.getVillageLgdCode();

					List<DummyLandRecords> plotList = dummyLandRecordREpo
							.findBySurveyNumberAndSubSurveyNumberAndVillageLgdCodeLimit1(
									dummyLandOwnerShipMaster.getSurveyNumber(),
									dummyLandOwnerShipMaster.getSubSurveyNumber(),
									dummyLandOwnerShipMaster.getVillageLgdCode());
					FarmerLandOwnershipRegistry farmerLandOwnershipRegistry = new FarmerLandOwnershipRegistry();
					farmerLandOwnershipRegistry.setIsActive(true);
					farmerLandOwnershipRegistry.setIsDeleted(false);
					farmerLandOwnershipRegistry
							.setMainOwnerNoAsPerRor(Integer.valueOf(dummyLandOwnerShipMaster.getMainOwnerNo()));
					farmerLandOwnershipRegistry.setOwnerNoAsPerRor(dummyLandOwnerShipMaster.getOwnerNo());
					farmerLandOwnershipRegistry.setOwnerNamePerRor(dummyLandOwnerShipMaster.getOwnerName());
					SowingSeason sowingSeason =	generalService.getCurrentSeason();

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

							FarmlandPlotRegistry farmlandPlotRegistry = new FarmlandPlotRegistry();
							//System.out.println("villageLgdMaster.getStateLgdCode().getStateLgdCode() "+villageLgdMaster.getStateLgdCode().getStateLgdCode());
							//System.out.println("stateShortName" +stateShortName);
							//System.out.println(villageLgdMaster);
							//System.out.println(commonUtil.genrateLandParcelIdV2(
							//	villageLgdMaster.getStateLgdCode().getStateLgdCode(), stateShortName));

							farmlandPlotRegistry.setFarmlandId(commonUtil.genrateLandParcelIdV3(
									villageLgdMaster.getStateLgdCode().getStateLgdCode(), stateShortName));

							farmlandPlotRegistry.setLandParcelId(plotList.get(0).getSurveyNumber());
							farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());
							farmlandPlotRegistry.setVillageLgdMaster(villageLgdMaster);
							//System.out.println("villageLgdMaster "+villageLgdMaster);
							farmlandPlotRegistry.setPlotArea(Double.parseDouble(dummyLandOwnerShipMaster.getTotalHa()));
							farmlandPlotRegistry.setSurveyNumber(plotList.get(0).getSurveyNumber());
							if (subSurveyNumber != null) {
								farmlandPlotRegistry.setSubSurveyNumber(subSurveyNumber);
							}
							if (stateUnitTypeMaster != null) {
								farmlandPlotRegistry.setUtUnitTypeMasterId(stateUnitTypeMaster);
							}
							//System.out.println("farmlandPlotRegistry "+farmlandPlotRegistry);
							//System.out.println("subSurveyNumber "+subSurveyNumber);
							//farmlandPlotRegistry.setSeasonName(sowingSeason.getSeasonName());
							farmlandPlotRegistry.setYear(sowingSeason.getStartingYear());
							farmlandPlotRegistry.setSeasonId(sowingSeason.getSeasonId());

							farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);
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

								farmlandPlotRegistry.setFarmlandId(commonUtil.genrateLandParcelIdV2(
										villageLgdMaster.getStateLgdCode().getStateLgdCode(), stateShortName));

								farmlandPlotRegistry.setLandParcelId(plotList.get(0).getSurveyNumber());
								farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());

								farmlandPlotRegistry.setVillageLgdMaster(villageLgdMaster);
								farmlandPlotRegistry
										.setPlotArea(Double.parseDouble(dummyLandOwnerShipMaster.getTotalHa()));
								farmlandPlotRegistry.setSurveyNumber(plotList.get(0).getSurveyNumber());

								if (subSurveyNumber != null) {
									farmlandPlotRegistry.setSubSurveyNumber(subSurveyNumber);
								}
								if (stateUnitTypeMaster != null) {
									farmlandPlotRegistry.setUtUnitTypeMasterId(stateUnitTypeMaster);
								}
							//	farmlandPlotRegistry.setSeasonName(sowingSeason.getSeasonName());
								farmlandPlotRegistry.setYear(sowingSeason.getStartingYear());
								farmlandPlotRegistry.setSeasonId(sowingSeason.getSeasonId());


								farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);
								farmerLandOwnershipRegistry.setFarmlandPlotRegistryId(farmlandPlotRegistry);

							}
						//	System.out.println("farmerLandOwnershipRegistryRepository" +farmerLandOwnershipRegistry);
							farmerLandOwnershipRegistryRepository.save(farmerLandOwnershipRegistry);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	public void uploadDataByUniqueCodeForUP(Long villageCode) {
		try {
			List<DummyLandOwnerShipMaster> dummyLandOwnerShipMasters = dummyLandOwnerShipMasterRepository
					.findByVillageLgdCode(villageCode);

			Map<String, List<DummyLandOwnerShipMaster>> dummyLandOwnerShipMastersMap = dummyLandOwnerShipMasters
					.stream().collect(Collectors.groupingBy(DummyLandOwnerShipMaster::getUniqueCode));

			System.out.println(villageCode + " :: uploadDataByUniqueCodeForUP :: " + dummyLandOwnerShipMastersMap.size()
					+ " :: " + new Timestamp(new Date().getTime()));

			VillageLgdMaster villageLgdMaster = villageLgdMasterRepository.findByVillageLgdCode(villageCode);

			StateUnitTypeMaster stateUnitTypeMaster = stateUnitTypeRepository
					.findByStateLgdCodeAndIsDefault(villageLgdMaster.getStateLgdCode(), true);

			List<StateUnitTypeMaster> stateUnitTypeMasters = stateUnitTypeRepository
					.findByUnitTypeDescEngIgnoreCaseAndStateLgdCode(dummyLandOwnerShipMasters.get(0).getAreaUnit(),
							villageLgdMaster.getStateLgdCode());

			if (!stateUnitTypeMasters.isEmpty()) {
				stateUnitTypeMaster = stateUnitTypeMasters.get(0);
			} else {
				stateUnitTypeMasters = stateUnitTypeRepository.findByUnitTypeDescEngIgnoreCaseAndStateLgdCode("Hectare",
						villageLgdMaster.getStateLgdCode());
				stateUnitTypeMaster = stateUnitTypeMasters.get(0);
			}
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
					List<DummyLandRecords> plotList = dummyLandRecordREpo
							.findBySurveyNumberAndVillageLgdCodeAndUniqueCode(
									dummyLandOwnerShipMaster.getValue().get(0).getSurveyNumber(),
									dummyLandOwnerShipMaster.getValue().get(0).getVillageLgdCode(),
									dummyLandOwnerShipMaster.getKey());
					if (!CollectionUtils.isEmpty(plotList)) {

						FarmlandPlotRegistry farmlandPlotRegistry = new FarmlandPlotRegistry();

						farmlandPlotRegistry.setFarmlandId(commonUtil.genrateLandParcelIdV3(stateShortName));

						farmlandPlotRegistry.setLandParcelId(plotList.get(0).getSurveyNumber());
						farmlandPlotRegistry.setPlotGeometry(plotList.get(0).getGeom());
						farmlandPlotRegistry.setVillageLgdMaster(villageLgdMaster);

//						farmlandPlotRegistry
//								.setPlotArea(Double.parseDouble(dummyLandOwnerShipMaster.getValue().get(0).getTotalHa()));

						farmlandPlotRegistry.setPlotArea(totalPlotArea);

						farmlandPlotRegistry.setSurveyNumber(plotList.get(0).getSurveyNumber());
						farmlandPlotRegistry.setSubSurveyNumber(plotList.get(0).getSubSurveyNumber());

						farmlandPlotRegistry.setUniqueCode(plotList.get(0).getUniqueCode());

						if (stateUnitTypeMaster != null) {
							farmlandPlotRegistry.setUtUnitTypeMasterId(stateUnitTypeMaster);
						}

						farmlandPlotRegistry.setCreatedOn(new Timestamp(new Date().getTime()));

						farmlandPlotRegistry.setUpdateDate(new Timestamp(new Date().getTime()));
						farmlandPlotRegistry.setSynchronisationDate(new Timestamp(new Date().getTime()));
						farmlandPlotRegistry.setLandUsageType(plotList.get(0).getLandUsageType());
						farmlandPlotRegistry.setLandOwnershipType(plotList.get(0).getOwnershipType());

						farmlandPlotRegistry = saveFarmLandPlotRegistry(farmlandPlotRegistry, stateShortName);
//						farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);

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

						ownerId = ownerId + 1;
//						ownerId = Long.valueOf(dummyLandOwnerShip.getOwnerNo()) + 1;

						farmerLandOwnershipRegistry.setOwnerNoAsPerRor(ownerId.toString());
//						farmerLandOwnershipRegistry.setOwnerNoAsPerRor(dummyLandOwnerShip.getOwnerNo());

						farmerLandOwnershipRegistry
								.setOwnerType(Integer.parseInt(ownerType.getOwnerTypeId().toString()));
						farmerLandOwnershipRegistry.setMainOwnerNoAsPerRor(mainOwnerId.intValue());
//						farmerLandOwnershipRegistry.setExtentAssignedArea(0.0);
						farmerLandOwnershipRegistry.setExtentAssignedArea(dummyLandOwnerShip.getOwnerExtent());
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
//						ownerId++;
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

}
