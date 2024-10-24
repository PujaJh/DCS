package com.amnex.agristack.service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.EarlyLateVillageInputDAO;
import com.amnex.agristack.dao.EarlyLateVillageOutputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author krupali.jogi
 */
@Service
public class EarlyLateVillageService {
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private GeneralService generalService;


	@Autowired
	private YearRepository yearRepository;

	@Autowired
	private SeasonMasterRepository seasonRepository;

	@Autowired
	private EarlyLateVillageRepository earlyLateVillageRepository;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;


	/**
	 * Retrieves all village mappings.
	 *
	 * @param request The HTTP servlet request.
	 * @return The response model containing the list of village mappings.
	 */
	public ResponseModel getAllVillageMapping(HttpServletRequest request) {
		try {
			List<EarlyLateVillage> villageMappingList = earlyLateVillageRepository.findAll();

			return CustomMessages.makeResponseModel(villageMappingList, CustomMessages.GET_EARLY_LATE_VILLAGES_SUCCESSFULLY,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Retrieves the early and late village mappings based on the provided input.
	 *
	 * @param request               The HTTP servlet request.
	 * @param earlyLateVillageInputDAO The input data containing the necessary parameters.
	 * @return The response model containing the list of early and late village mappings.
	 */
	public ResponseModel getEarlyLateMarkedVillages(HttpServletRequest request, EarlyLateVillageInputDAO earlyLateVillageInputDAO) {
		try {
			List<EarlyLateVillageOutputDAO> outputList = getEarlyAndLateVillageMapping(earlyLateVillageInputDAO);
			return CustomMessages.makeResponseModel(outputList, CustomMessages.GET_SURVEY_ACTIVITY_SUCCESSFULLY,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
	/**
	 * Retrieves the list of early and late village mappings based on the provided input.
	 *
	 * @param earlyLateVillageInputDAO The input data containing the necessary parameters.
	 * @return The list of early and late village mappings.
	 */
	public List<EarlyLateVillageOutputDAO> getEarlyAndLateVillageMapping(EarlyLateVillageInputDAO earlyLateVillageInputDAO) {
		List<EarlyLateVillageOutputDAO> outputList = new ArrayList<>();
		List<EarlyLateVillage> list = earlyLateVillageRepository.getEarlyLateVillageList(earlyLateVillageInputDAO.getStartYear(),
				earlyLateVillageInputDAO.getEndYear(), earlyLateVillageInputDAO.getSeasonId(),
				earlyLateVillageInputDAO.getSubDistrictLgdCodeList());
		list.stream().forEach(ele-> {
			EarlyLateVillageOutputDAO outputDAO = new  EarlyLateVillageOutputDAO();
			outputDAO.setId(ele.getId());
			if(Objects.nonNull(ele.getSeasonId())) {
				outputDAO.setSeasonId(ele.getSeasonId());
				SowingSeason season = seasonRepository.findBySeasonId(ele.getSeasonId());
				if(Objects.nonNull(season.getSeasonId())) {
					outputDAO.setSeasonName(season.getSeasonName());
				}
			}
			if(Objects.nonNull(ele.getVillageLGDCodes())) {
				List<String> lgdCodesList = Arrays.asList(ele.getVillageLGDCodes().split(","));
				List<Long> villageCodes = lgdCodesList.stream()
						.map(Long::valueOf)
						.collect(Collectors.toList());
				outputDAO.setVillageLGDCodeList(villageCodes);
				outputDAO.setSubDistrictLGDCode(ele.getSubDistrictLGDCode());
				outputDAO.setDistrictLGDCode(ele.getDistrictLGDCode());
				outputDAO.setStateLGDCode(ele.getStateLGDCode());
				List<VillageLgdMaster> villages = villageLgdMasterRepository.findByVillageLgdCodeIn(villageCodes);
				outputDAO.setVillageList(villages);
			}

			if(outputDAO.getVillageList().size() >0) {
				VillageLgdMaster villageLgdMaster = outputDAO.getVillageList().get(0);
				outputDAO.setSubDistrictName(villageLgdMaster.getSubDistrictLgdCode().getSubDistrictName());
				outputDAO.setDistrictName(villageLgdMaster.getDistrictLgdCode().getDistrictName());
				outputDAO.setStateName(villageLgdMaster.getStateLgdCode().getStateName());
			}
			outputDAO.setIsActive(ele.getIsActive());
			outputDAO.setIsDeleted(ele.getIsDeleted());
			outputList.add(outputDAO);
		});
		return outputList;
	}

	/**
	 * Retrieves the village LGD codes based on the early and late village mappings.
	 *
	 * @param earlyLateVillageInputDAO The input data containing the necessary parameters.
	 * @return The list of village LGD codes.
	 */
	public 	List<Long> getVillageLGDCodesByEarlyAndLateVillageMapping(EarlyLateVillageInputDAO earlyLateVillageInputDAO) {
		List<Long> finalVillageCodes =new ArrayList<Long>();

		List<EarlyLateVillage> list = earlyLateVillageRepository.getEarlyLateVillageList(earlyLateVillageInputDAO.getStartYear(),
				earlyLateVillageInputDAO.getEndYear(), earlyLateVillageInputDAO.getSeasonId(),
				earlyLateVillageInputDAO.getSubDistrictLgdCodeList());

		list.stream().forEach(ele-> {
			if(Objects.nonNull(ele.getVillageLGDCodes())) {
				List<String> lgdCodesList = Arrays.asList(ele.getVillageLGDCodes().split(","));
				List<Long> villageCodes = lgdCodesList.stream()
						.map(Long::valueOf)
						.collect(Collectors.toList());
				if(villageCodes!=null && villageCodes.size()>0) {
					finalVillageCodes.addAll(villageCodes);
				}
			}
		});
		return finalVillageCodes;
	}

	/**
	 * Adds early/late villages.
	 *
	 * @param request      HTTP servlet request
	 * @param inputDAO     EarlyLateVillageInputDAO object containing input data
	 * @return             ResponseModel object with the result of the operation
	 */
	@Transactional
	public ResponseModel addEarlyLateVillages(HttpServletRequest request, EarlyLateVillageInputDAO inputDAO) {
		try {
			SowingSeason currentSeason = generalService.getCurrentSeason();

			if (inputDAO.getSeasonId() == null || inputDAO.getSeasonId() == 0) {
				inputDAO.setSeasonId(Long.valueOf(currentSeason.getSeasonId()));
			}
			if (inputDAO.getEndYear() == null || inputDAO.getEndYear() == 0) {
				inputDAO.setEndYear(currentSeason.getEndingYear());
			}
			if (inputDAO.getStartYear() == null || inputDAO.getStartYear() == 0) {
				inputDAO.setStartYear(currentSeason.getStartingYear());
			}
			EarlyLateVillage earlyLateVillage = new EarlyLateVillage();
			earlyLateVillage.setStartYear(inputDAO.getStartYear());
			earlyLateVillage.setEndYear(inputDAO.getEndYear());
			earlyLateVillage.setStateLGDCode(inputDAO.getStateLGDCode());
			earlyLateVillage.setDistrictLGDCode(inputDAO.getDistrictLGDCode());
			earlyLateVillage.setSubDistrictLGDCode(inputDAO.getSubDistrictLGDCode());
			String str = inputDAO.getVillageLGDCodeList().stream()
					.map(n -> String.valueOf(n))
					.collect(Collectors.joining(","));
			earlyLateVillage.setVillageLGDCodes(str);
			earlyLateVillage.setSeasonId(inputDAO.getSeasonId());
			earlyLateVillage.setIsActive(true);
			earlyLateVillage.setIsDeleted(false);
			earlyLateVillage.setCreatedOn(new Timestamp(new Date().getTime()));
			earlyLateVillage.setCreatedIp(CommonUtil.getRequestIp(request));
			earlyLateVillage.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			earlyLateVillageRepository.save(earlyLateVillage);
			return CustomMessages.makeResponseModel(earlyLateVillage, CustomMessages.ADD_EARLY_LATE_VILLAGES_SUCCESSFULLY,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}


	/**
	 * Updates early/late villages.
	 *
	 * @param request      HTTP servlet request
	 * @param inputDAO     EarlyLateVillageInputDAO object containing input data
	 * @return             ResponseModel object with the result of the operation
	 */
	@Transactional
	public ResponseModel updateEarlyLateVillages(HttpServletRequest request, EarlyLateVillageInputDAO inputDAO) {
		try {
			SowingSeason currentSeason = generalService.getCurrentSeason();

			if (inputDAO.getSeasonId() == null || inputDAO.getSeasonId() == 0) {
				inputDAO.setSeasonId(Long.valueOf(currentSeason.getSeasonId()));
			}
			if (inputDAO.getEndYear() == null || inputDAO.getEndYear() == 0) {
				inputDAO.setEndYear(currentSeason.getEndingYear());
			}
			if (inputDAO.getStartYear() == null || inputDAO.getStartYear() == 0) {
				inputDAO.setStartYear(currentSeason.getStartingYear());
			}

			EarlyLateVillage dbObject = earlyLateVillageRepository.findById(inputDAO.getId()).orElse(null);
			if(dbObject != null) {
				dbObject.setStartYear(inputDAO.getStartYear());
				dbObject.setEndYear(inputDAO.getEndYear());
				dbObject.setStateLGDCode(inputDAO.getStateLGDCode());
				dbObject.setDistrictLGDCode(inputDAO.getDistrictLGDCode());
				dbObject.setSubDistrictLGDCode(inputDAO.getSubDistrictLGDCode());
				String str = inputDAO.getVillageLGDCodeList().stream()
						.map(n -> String.valueOf(n))
						.collect(Collectors.joining(","));
				dbObject.setVillageLGDCodes(str);
				dbObject.setSeasonId(inputDAO.getSeasonId());
				dbObject.setModifiedOn(new Timestamp(new Date().getTime()));
				dbObject.setModifiedIp(CommonUtil.getRequestIp(request));
				dbObject.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			} else  {
				return CustomMessages.makeResponseModel(null, CustomMessages.EARLY_LATE_VILLAGES_NOT_FOUND,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
//			earlyLateVillageRepository.save(earlyLateVillage);
			return CustomMessages.makeResponseModel(dbObject, CustomMessages.UPDATE_EARLY_LATE_VILLAGES_SUCCESSFULLY,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Checks if early/late villages already exist.
	 *
	 * @param request                   HTTP servlet request
	 * @param earlyLateVillageInputDAO  EarlyLateVillageInputDAO object containing input data
	 * @return                          ResponseModel object with the result of the operation
	 */
	public ResponseModel checkMarkVillagesExist(HttpServletRequest request, EarlyLateVillageInputDAO earlyLateVillageInputDAO) {
		try {
			Optional<EarlyLateVillage> data = earlyLateVillageRepository.checkEntryExist(earlyLateVillageInputDAO.getStartYear(),earlyLateVillageInputDAO.getEndYear(),earlyLateVillageInputDAO.getSeasonId(), earlyLateVillageInputDAO.getSubDistrictLGDCode());
			if(data.isPresent()) {
				return CustomMessages.makeResponseModel(true, CustomMessages.EARLY_LATE_VILLAGES_ALREADY_EXIST,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.EARLY_LATE_VILLAGES_NOT_FOUND,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}


	/**
	 * Deletes an early/late village entry.
	 *
	 * @param request   HTTP servlet request
	 * @param id        ID of the EarlyLateVillage object to be deleted
	 * @return          ResponseModel object with the result of the operation
	 */
	public ResponseModel deleteEarlyLateVillages(HttpServletRequest request, Long id) {
		try {
			EarlyLateVillage dbObject = earlyLateVillageRepository.findById(id).orElse(null);
			if(dbObject != null) {
				dbObject.setIsDeleted(true);
				dbObject.setIsActive(false);
				dbObject.setModifiedOn(new Timestamp(new Date().getTime()));
				dbObject.setModifiedIp(CommonUtil.getRequestIp(request));
				dbObject.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				this.earlyLateVillageRepository.save(dbObject);
			} else  {
				return CustomMessages.makeResponseModel(null, CustomMessages.EARLY_LATE_VILLAGES_NOT_FOUND,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
			return CustomMessages.makeResponseModel(dbObject, CustomMessages.DELETE_EARLY_LATE_VILLAGES_SUCCESSFULLY,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}


	/**
	 * Retrieves the marked villages for a taluk.
	 *
	 * @param request                   HTTP servlet request
	 * @param earlyLateVillageInputDAO  EarlyLateVillageInputDAO object containing input data
	 * @return                          ResponseModel object with the result of the operation
	 */
	public ResponseModel getMarkedVillagesForTaluk(HttpServletRequest request, EarlyLateVillageInputDAO earlyLateVillageInputDAO) {
		try {
			Optional<EarlyLateVillage> data = earlyLateVillageRepository.checkEntryExist(earlyLateVillageInputDAO.getStartYear(),earlyLateVillageInputDAO.getEndYear(),earlyLateVillageInputDAO.getSeasonId(), earlyLateVillageInputDAO.getSubDistrictLGDCode());
			if(data.isPresent()) {
				EarlyLateVillage dbObject = data.get();
				EarlyLateVillageOutputDAO outputDAO = new EarlyLateVillageOutputDAO();
				if(Objects.nonNull(dbObject.getVillageLGDCodes())) {
					List<String> lgdCodesList = Arrays.asList(dbObject.getVillageLGDCodes().split(","));
					List<Long> villageCodes = lgdCodesList.stream()
							.map(Long::valueOf)
							.collect(Collectors.toList());
					outputDAO.setId(dbObject.getId());
					outputDAO.setVillageLGDCodeList(villageCodes);
					outputDAO.setSubDistrictLGDCode(dbObject.getSubDistrictLGDCode());
					outputDAO.setDistrictLGDCode(dbObject.getDistrictLGDCode());
					outputDAO.setStateLGDCode(dbObject.getStateLGDCode());
				}
				return CustomMessages.makeResponseModel(outputDAO, CustomMessages.EARLY_LATE_VILLAGES_ALREADY_EXIST,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.EARLY_LATE_VILLAGES_NOT_FOUND,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
}
