/**
 * 
 */
package com.amnex.agristack.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.PrCountDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.dao.PRPaymentReleaseDAO;
import com.amnex.agristack.entity.ConfigurationMaster;
import com.amnex.agristack.entity.LandParcelSurveyMaster;
import com.amnex.agristack.entity.PRPaymentRelease;
import com.amnex.agristack.entity.SowingSeason;
import com.amnex.agristack.entity.StatusMaster;
import com.amnex.agristack.entity.SurveyPaymentMapping;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;

/**
 * @author majid.belim
 *
 */

@Service
public class PRPaymentReleaseService {

	@Autowired
	private PRPaymentReleaseRepository prPaymentReleaseRepository;
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	@Autowired
	private SeasonMasterRepository seasonMasterRepository;

	@Autowired
	private UserMasterRepository userMasterRepository;
	@Autowired
	private UserService userService;

	@Autowired
	private UserLandAssignmentRepository userLandAssignmentRepository;
	
	@Autowired
	private LandParcelSurveyMasterRespository landParcelSurveyMasterRespository;

	@Autowired
	private ConfigurationRepository configurationRepository;
	@Autowired
	private SurveyPaymentMappingRepository surveyPaymentMappingRepository;
	/**
	 * Endpoint for adding or updating a payment.
	 * 
	 * @param request             The HttpServletRequest object.
	 * @param prPaymentReleaseDAO The {@code PRPaymentReleaseDAO} object containing the
	 *                            payment details.
	 * @return The ResponseModel object representing the response.
	 */
	public ResponseModel addOrUpdatePayment(HttpServletRequest request, PRPaymentReleaseDAO prPaymentReleaseDAO) {
		ResponseModel responseModel = null;
		try {
			PRPaymentRelease prPaymentRelease = null;
			if (prPaymentReleaseDAO.getPrId() != null) {
				Optional<PRPaymentRelease> op = prPaymentReleaseRepository
						.findByPrIdAndIsActiveAndIsDeleted(prPaymentReleaseDAO.getPrId(), true, false);
				if (op.isPresent()) {

					prPaymentRelease = op.get();
					prPaymentRelease.setModifiedBy(userService.getUserId(request));
					prPaymentRelease.setModifiedIp(CommonUtil.getRequestIp(request));
				}
			} else {
				prPaymentRelease = new PRPaymentRelease();
				prPaymentRelease.setCreatedIp(CommonUtil.getRequestIp(request));
				SowingSeason season = seasonMasterRepository.findBySeasonId(prPaymentReleaseDAO.getSeasonId());
				if (season != null) {
					prPaymentRelease.setSeason(season);
				}
				prPaymentRelease.setIsActive(true);
				prPaymentRelease.setIsDeleted(false);

				prPaymentRelease.setStartYear(prPaymentReleaseDAO.getStartYear());
				prPaymentRelease.setEndYear(prPaymentReleaseDAO.getEndYear());

				StatusMaster status = statusMasterRepository.findByIsDeletedFalseAndStatusCode(
						StatusEnum.valueOf(prPaymentReleaseDAO.getStatus()).getValue());
//				.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue());

				prPaymentRelease.setModifiedBy(userService.getUserId(request));
				prPaymentRelease.setPaymentStatus(status);
			}

			Long userId = Long.parseLong(userService.getUserId(request));
			prPaymentRelease.setCreatedBy(userService.getUserId(request));
			Optional<UserMaster> reviewByOp = userMasterRepository.findByUserId(userId);
			if (reviewByOp.isPresent()) {
				prPaymentRelease.setReviewBy(reviewByOp.get());
			}

			Optional<UserMaster> userOP = userMasterRepository.findByUserId(prPaymentReleaseDAO.getSurveyorId());
			if (userOP.isPresent()) {
				prPaymentRelease.setSurveyorId(userOP.get());
			}
			prPaymentRelease.setRemarks(prPaymentReleaseDAO.getRemarks());

//			prPaymentRelease.setTotalPlotForPayment(prPaymentReleaseDAO.getTotalPlotForPayment().intValue());
			prPaymentRelease.setCalculatedAmount(prPaymentReleaseDAO.getCalculatedAmount());

			PRPaymentRelease savePrPayment=prPaymentReleaseRepository.save(prPaymentRelease);
			List<ConfigCode> configCodes=new ArrayList<>();
			configCodes.add(ConfigCode.MIN_AMOUNT);
			configCodes.add(ConfigCode.MAX_AMOUNT);
			configCodes.add(ConfigCode.NA_AMOUNT);
			configCodes.add(ConfigCode.FALLOW_AMOUNT);
			configCodes.add(ConfigCode.PER_CROP_AMOUNT);
			
			List<ConfigurationMaster> configList=configurationRepository.findByIsActiveTrueAndIsDeletedFalseAndConfigCodeIn(configCodes);
			
			if (prPaymentReleaseDAO.getStatus().equals("APPROVED")) {
				UserMaster userMaster=prPaymentRelease.getSurveyorId();
				List<PrCountDAO> prData = userLandAssignmentRepository.getPrPaymentCountsOld(
						prPaymentReleaseDAO.getSeasonId(), prPaymentReleaseDAO.getStartYear(),
						prPaymentReleaseDAO.getEndYear(), userMaster.getUserId());
				if (prData != null && prData.size() > 0) {
					List<Long> lpsdIds = prData.stream().map(x -> x.getLpsdId()).collect(Collectors.toList());
					List<LandParcelSurveyMaster> lpsmList=landParcelSurveyMasterRespository.findByLpsmIdIn(lpsdIds);
					List<SurveyPaymentMapping> surveyPaymentMappingList=new ArrayList<>();
		
					
					Long minAmount=Long.valueOf(configList.stream().filter(x->x.getConfigKey().equals(ConfigCode.MIN_AMOUNT.toString())).findFirst().get().getConfigValue());
					Long maxAmount=Long.valueOf(configList.stream().filter(x->x.getConfigKey().equals(ConfigCode.MAX_AMOUNT.toString())).findFirst().get().getConfigValue());
					Long naAmount=Long.valueOf(configList.stream().filter(x->x.getConfigKey().equals(ConfigCode.NA_AMOUNT.toString())).findFirst().get().getConfigValue());
					Long fallowAmount=Long.valueOf(configList.stream().filter(x->x.getConfigKey().equals(ConfigCode.FALLOW_AMOUNT.toString())).findFirst().get().getConfigValue());
					Long perCropAmount=Long.valueOf(configList.stream().filter(x->x.getConfigKey().equals(ConfigCode.PER_CROP_AMOUNT.toString())).findFirst().get().getConfigValue());
					
					prData.forEach(action->{
						SurveyPaymentMapping surveyPaymentMapping=new SurveyPaymentMapping();
						surveyPaymentMapping.setIsActive(true);
						surveyPaymentMapping.setIsDeleted(false);
						surveyPaymentMapping.setUserId(userMaster);
						List<LandParcelSurveyMaster> filterData=lpsmList.stream().filter(x->x.getLpsmId().equals(action.getLpsdId())).collect(Collectors.toList());
						if(filterData!=null && filterData.size()>0) {
							surveyPaymentMapping.setLpsmId(filterData.get(0));
						}
						surveyPaymentMapping.setPrId(savePrPayment);

						surveyPaymentMapping.setMinAmount(minAmount);
						surveyPaymentMapping.setMaxAmount(maxAmount);
						surveyPaymentMapping.setNaAmount(naAmount);
						surveyPaymentMapping.setFalllowAmount(fallowAmount);
						surveyPaymentMapping.setPerCropAmount(perCropAmount);
						surveyPaymentMapping.setNaCount(action.getNaCount());
						surveyPaymentMapping.setFalllowCount(action.getFallowCount());
						surveyPaymentMapping.setCropCount(action.getCropCount());
						surveyPaymentMappingList.add(surveyPaymentMapping);
					});
					if(surveyPaymentMappingList!=null && surveyPaymentMappingList.size()>0) {
						surveyPaymentMappingRepository.saveAll(surveyPaymentMappingList);
						
					}
				}
			}
			return CustomMessages.makeResponseModel(null, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;

		}

	}

	/**
	 * Endpoint for updating the payment status.
	 * 
	 * @param request             The HttpServletRequest object.
	 * @param  prPaymentReleaseDAO The {@code PRPaymentReleaseDAO} object containing the
	 *                            payment details.
	 * @return The ResponseModel object representing the response.
	 */
	public ResponseModel updatePaymentStatus(HttpServletRequest request, PRPaymentReleaseDAO prPaymentReleaseDAO) {
		ResponseModel responseModel = null;
		try {
			if (prPaymentReleaseDAO.getPrIds() != null && prPaymentReleaseDAO.getPrIds().size() > 0) {
				List<PRPaymentRelease> prList = prPaymentReleaseRepository
						.findByPrIdInAndIsActiveAndIsDeleted(prPaymentReleaseDAO.getPrIds(), true, false);
				if (prList != null && prList.size() > 0) {
					StatusMaster paymentStatus = statusMasterRepository
							.findByIsDeletedFalseAndStatusCode(StatusEnum.APPROVED.getValue());
					prList.forEach(action -> {

						action.setModifiedBy(userService.getUserId(request));
						action.setModifiedIp(CommonUtil.getRequestIp(request));
						action.setPaymentStatus(paymentStatus);
					});
					prPaymentReleaseRepository.saveAll(prList);
				}
			}

			return CustomMessages.makeResponseModel(null, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;

		}
	}

}
