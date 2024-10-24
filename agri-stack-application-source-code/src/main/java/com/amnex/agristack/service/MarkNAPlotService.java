package com.amnex.agristack.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.amnex.agristack.dao.FarmlandPlotRegistryDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.FarmlandPlotRegistry;
import com.amnex.agristack.repository.FarmlandPlotRegistryRepository;
import com.amnex.agristack.repository.UserLandAssignmentRepository;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.config.JwtUserDetailsService;
import com.amnex.agristack.dao.NAPlotInputDAO;
import com.amnex.agristack.dao.SeasonMasterOutputDAO;
import com.amnex.agristack.entity.NonAgriPlotMapping;
import com.amnex.agristack.entity.SowingSeason;
import com.amnex.agristack.repository.NonAgriPlotMappingRepository;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;

@Service
public class MarkNAPlotService {

	@Autowired
	ExceptionLogService exceptionLogService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	NonAgriPlotMappingRepository nonAgriPlotMappingRepository;

	@Autowired
	FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	GeneralService generalService;

	@Autowired
	private UserLandAssignmentRepository userLandAssignmentRepository;

	/**
	 * Add details of non-agricultural plots.
	 *
	 * @param inputDAO The input data containing plot details
	 * @param request  The HTTP servlet request
	 * @return A response model indicating the result of the operation
	 */
	@Transactional
	public ResponseModel addNAPlotDetails(NAPlotInputDAO inputDAO, HttpServletRequest request) {
		ResponseModel responseModel = new ResponseModel();
		try {
			SowingSeason currentSeason = generalService.getCurrentSeason();

			// **DELETE ALL PREVIOUSLY MARKED NA PLOTS BY SEASON,START YEAR , END YEAR AND
			// VILLAGE CODE**//
			if (inputDAO.getSeasonId() == null || inputDAO.getSeasonId() == 0) {
				inputDAO.setSeasonId(Long.valueOf(currentSeason.getSeasonId()));
			}
			if (inputDAO.getEndYear() == null || inputDAO.getEndYear() == 0) {
				inputDAO.setEndYear(currentSeason.getEndingYear());
			}
			if (inputDAO.getStartYear() == null || inputDAO.getStartYear() == 0) {
				inputDAO.setStartYear(currentSeason.getStartingYear());
			}

			nonAgriPlotMappingRepository.deleteBySeasonIdStartYearEndYearAndVillageLGDCode(inputDAO.getSeasonId(),
					inputDAO.getStartYear(), inputDAO.getEndYear(), inputDAO.getVillageLgdCode());
			
			// **DELETE ALL PREVIOUSLY MARKED NA PLOTS BY SEASON,START YEAR , END YEAR AND

			// VILLAGE CODE**//
			List<NonAgriPlotMapping> nonAgriPlots = new ArrayList<>();

			if (inputDAO.getLandParcelIds() != null && inputDAO.getLandParcelIds().size() > 0) {
				List<FarmlandPlotRegistry> farmLandPlots = farmlandPlotRegistryRepository
						.findAllByFarmLandPlotIds(inputDAO.getLandParcelIds());

				farmLandPlots.forEach(landPlot -> {
					NonAgriPlotMapping nonAgriPlot = new NonAgriPlotMapping();
					nonAgriPlot.setStartYear(currentSeason.getStartingYear());
					nonAgriPlot.setEndYear(currentSeason.getEndingYear());
					nonAgriPlot.setSeason(currentSeason);
					nonAgriPlot.setLandParcelId(landPlot);
					nonAgriPlot.setIsActive(Boolean.TRUE);
					nonAgriPlot.setIsDeleted(Boolean.FALSE);
					nonAgriPlot.setCreatedOn(new Timestamp(new Date().getTime()));
					nonAgriPlot.setCreatedIp(CommonUtil.getRequestIp(request));
					nonAgriPlot.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
					nonAgriPlots.add(nonAgriPlot);
				});

				nonAgriPlotMappingRepository.saveAll(nonAgriPlots);
			}

			return new ResponseModel(inputDAO.getUserId(), CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Get details of non-agricultural plots.
	 *
	 * @param inputDAO The input data containing filter criteria
	 * @param request  The HTTP servlet request
	 * @return A response model containing the non-agricultural plot details
	 */
	public ResponseModel getNAPlotDetails(NAPlotInputDAO inputDAO, HttpServletRequest request) {
		ResponseModel responseModel = new ResponseModel();
		try {
//			List<NonAgriPlotMapping> nonAgriPlots = nonAgriPlotMappingRepository
//					.findAllBySeasonIdStartYearEndYearAndSubDistrictLGDCode(inputDAO.getSeasonId(),
//							inputDAO.getStartYear(), inputDAO.getEndYear(), inputDAO.getSubDistrictLgdCodes(),
//							inputDAO.getVillageLgdCodes());
			
			/* Pagination Start */
			Pageable pageable = PageRequest.of(inputDAO.getPage(), inputDAO.getLimit(),
					Sort.by(inputDAO.getSortField()).descending());

			if (inputDAO.getSortOrder().equals("asc")) {
				pageable = PageRequest.of(inputDAO.getPage(), inputDAO.getLimit(),
						Sort.by(inputDAO.getSortField()).ascending());
			}
			String searchKeyword = Objects.nonNull(inputDAO.getSearch()) ? inputDAO.getSearch().toLowerCase() : null;
			
			/* Pagination end*/
			
			Page<NonAgriPlotMapping> nonAgriPlots =  nonAgriPlotMappingRepository.findAllBySeasonIdStartYearEndYearAndSubDistrictLGDCodeWithPagination(searchKeyword, inputDAO.getSeasonId(),
							inputDAO.getStartYear(), inputDAO.getEndYear(), inputDAO.getSubDistrictLgdCodes(),
							inputDAO.getVillageLgdCodes(),pageable);

			List<NAPlotInputDAO> finalList = new ArrayList<>();

			nonAgriPlots.getContent().forEach(action -> {
				FarmlandPlotRegistryDAO obj = new FarmlandPlotRegistryDAO();
				NAPlotInputDAO naPlot = new NAPlotInputDAO();
				FarmlandPlotRegistry landParcel = action.getLandParcelId();
				
				naPlot.setNaPlotId(action.getNaPlotId());
				naPlot.setEndYear(action.getEndYear());
				naPlot.setStartYear(action.getStartYear());

				SeasonMasterOutputDAO seasonDao = new SeasonMasterOutputDAO();

				try {
					BeanUtils.copyProperties(seasonDao, action.getSeason());
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}

				naPlot.setSeason(seasonDao);

				obj.setVillageLgdCode(landParcel.getVillageLgdMaster().getVillageLgdCode());
				obj.setVillageName(landParcel.getVillageLgdMaster().getVillageName());
				obj.setSubDistrictName(landParcel.getVillageLgdMaster().getSubDistrictLgdCode().getSubDistrictName());
				obj.setSubDistrictLgdCode(
						landParcel.getVillageLgdMaster().getSubDistrictLgdCode().getSubDistrictLgdCode());
				obj.setFarmlandId(landParcel.getFarmlandId());
				obj.setLandParcelId(landParcel.getLandParcelId());
				obj.setFarmlandPlotRegistryId(landParcel.getFarmlandPlotRegistryId());
				
				obj.setSurveyNumber(landParcel.getSurveyNumber());
				obj.setSubSurveyNumber(landParcel.getSubSurveyNumber());

				naPlot.setLandParcelId(obj);

				finalList.add(naPlot);
			});
			
			Map<String, Object> responseData = new HashMap();
			responseData.put("data", finalList);
			responseData.put("total", nonAgriPlots.getTotalElements());
			responseData.put("page", inputDAO.getPage());
			responseData.put("limit", inputDAO.getLimit());
			responseData.put("sortField", inputDAO.getSortField());
			responseData.put("sortOrder", inputDAO.getSortOrder());

			return new ResponseModel(responseData, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Get farmland plot details.
	 *
	 * @param inputDAO The input data containing filter criteria
	 * @param request  The HTTP servlet request
	 * @return A response model containing the farmland plot details
	 */
	public ResponseModel getFarmlandPlots(NAPlotInputDAO inputDAO, HttpServletRequest request) {
		ResponseModel responseModel = null;
		Long villageCode = inputDAO.getVillageLgdCode();
		try {
			SowingSeason currentSeason = generalService.getCurrentSeason();
			List<FarmlandPlotRegistry> details = farmlandPlotRegistryRepository
					.findByVillageLgdMasterVillageLgdCode(Long.valueOf(villageCode));

			List<NonAgriPlotMapping> naPlots = nonAgriPlotMappingRepository
					.findAllBySeasonIdStartYearEndYearAndVillageLgdCode(currentSeason.getSeasonId(),
							currentSeason.getStartingYear(), currentSeason.getEndingYear(),
							inputDAO.getVillageLgdCode());

			List<Long> naPlotIds = naPlots.stream()
					.map(obj -> obj.getLandParcelId().getFarmlandPlotRegistryId()).collect(Collectors.toList());

			List<FarmlandPlotRegistryDAO> finalList = new ArrayList<>();

			details.forEach(action -> {
				FarmlandPlotRegistryDAO obj = new FarmlandPlotRegistryDAO();
				obj.setVillageLgdCode(action.getVillageLgdMaster().getVillageLgdCode());
				obj.setFarmlandId(action.getFarmlandId());
				obj.setLandParcelId(action.getLandParcelId());
				obj.setFarmlandPlotRegistryId(action.getFarmlandPlotRegistryId());
				
				obj.setSurveyNumber(action.getSurveyNumber());
				obj.setSubSurveyNumber(action.getSubSurveyNumber());

				if (currentSeason != null) {
					obj.setSeason(currentSeason);
				}
				if (naPlotIds.contains(action.getFarmlandPlotRegistryId())) {
					obj.setIsNAPlot(Boolean.TRUE);
				} else {
					obj.setIsNAPlot(Boolean.FALSE);
				}
				finalList.add(obj);
			});

			return CustomMessages.makeResponseModel(finalList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Delete NA Plot Details.
	 *
	 * @param inputDao The input data containing filter criteria
	 * @param request  The HTTP servlet request
	 * @return A response model containing the non-agricultural plot details
	 */
	public ResponseModel deleteNAPlotDetails(NAPlotInputDAO inputDao, HttpServletRequest request) {
		
		ResponseModel responseModel = new ResponseModel();
		try {
			
			List<Long> naPlotIds = inputDao.getNaPlotIds();
			nonAgriPlotMappingRepository.deleteByNAPlotIds(naPlotIds);
				
			return new ResponseModel(inputDao.getUserId(), CustomMessages.RECORD_DELETE, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST); 
		} catch (Exception e) {
			exceptionLogService.addException(e.getStackTrace()[0].getClassName() ,e.getStackTrace()[0].getMethodName(), e, null);
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE, 
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}	

	}
}
