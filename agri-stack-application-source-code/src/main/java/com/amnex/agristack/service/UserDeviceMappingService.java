package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.UserDeviceMappingDAO;
import com.amnex.agristack.entity.StatusMaster;
import com.amnex.agristack.entity.UserDeviceMapping;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.repository.StatusMasterRepository;
import com.amnex.agristack.repository.UserDeviceMappingRepository;
import com.amnex.agristack.repository.UserMasterRepository;
import com.amnex.agristack.repository.VillageLgdMasterRepository;
import com.amnex.agristack.utils.CustomMessages;

@Service
public class UserDeviceMappingService {

	@Autowired
	private UserDeviceMappingRepository userDeviceRepo;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private VillageLgdMasterRepository villagelgdMasterRepo;
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	@Autowired
	private UserMasterRepository userMasterRepository;

	/**
	 * Retrieves the user device details provided input
	 * parameters.
	 *
	 * @param req The {@code UserDeviceMappingDAO} object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The result of executing the stored procedure.
	 */
	public ResponseModel getUserDeviceDetails(UserDeviceMappingDAO req, HttpServletRequest request) {
		try {

			Pageable pageable = PageRequest.of(req.getPage(), req.getLimit(), Sort.by(req.getSortField()).descending());

			if (req.getSortOrder().equals("asc")) {
				pageable = PageRequest.of(req.getPage(), req.getLimit(), Sort.by(req.getSortField()).ascending());
			}

			Map<String, Object> responseData = new HashMap();
			responseData.put("page", req.getPage());
			responseData.put("limit", req.getLimit());
			responseData.put("sortField", req.getSortField());
			responseData.put("sortOrder", req.getSortOrder());

			List<UserDeviceMappingDAO> getUserMappingDAOList = new ArrayList<UserDeviceMappingDAO>();
			List<Long> villageCodeList = new ArrayList<Long>();
			Page<UserDeviceMapping> getuserDeviceMappingList;

			if (req.getSearch() != null) {
				getuserDeviceMappingList = userDeviceRepo
						.findByUserId_UserNameOrUserId_UserMobileNumberOrUserId_UserEmailAddressOrImeiNumberAndCreatedOnBetween(
								req.getSearch(), req.getSearch(), req.getSearch(), req.getSearch(),req.getStartDate(),req.getEndDate(), pageable);

			} else {
				getuserDeviceMappingList = userDeviceRepo.findByUserId_UserVillageLGDCodeInAndCreatedOnBetween(req.getVillageLgdCodeList(),
						req.getStartDate(),req.getEndDate(),pageable);
			}

			responseData.put("totalRecords", getuserDeviceMappingList.getTotalElements());

			getuserDeviceMappingList.forEach(ele1 -> {
				long l = (long) ele1.getUserId().getUserVillageLGDCode();
				villageCodeList.add(l);
			});
			List<VillageLgdMaster> getVillageLgdMaster = villagelgdMasterRepo.findByVillageLgdCodeIn(villageCodeList);
			List<StatusMaster> getStatusList = statusMasterRepository.findAll();

			getuserDeviceMappingList.forEach(ele -> {

				UserDeviceMappingDAO userDeviceMappingDAO = new UserDeviceMappingDAO(ele.getUserDeviceMappingId(),
						ele.getUserId().getUserId(), ele.getUserId().getUserName(), ele.getImeiNumber(),
						ele.getUserId().getUserMobileNumber(), ele.getUserId().getUserEmailAddress(), null, null, null,
						null, null, null, null, ele.getRemarks(), ele.getIsActive(), null, null, null, null, null,
						ele.getCreatedOn(), ele.getCreatedBy());

				if (ele.getUserId().getUserVillageLGDCode() != null) {

					getVillageLgdMaster.forEach(action -> {
						long l = (long) ele.getUserId().getUserVillageLGDCode();
						if (action.getVillageLgdCode().equals(l)) {

							userDeviceMappingDAO.setStateName(action.getStateLgdCode().getStateName());
							userDeviceMappingDAO.setDistrictName(action.getDistrictLgdCode().getDistrictName());
							userDeviceMappingDAO
									.setSubDistrictName(action.getSubDistrictLgdCode().getSubDistrictName());
							userDeviceMappingDAO.setVillageName(action.getVillageName());
						}
					});
				}

				getStatusList.forEach(statusAction -> {
					if (ele.getStatusCode().equals(statusAction.getStatusCode())) {
						userDeviceMappingDAO.setStatusCode(statusAction.getStatusCode());
						userDeviceMappingDAO.setStatusName(statusAction.getStatusName());
					}
				});
				getUserMappingDAOList.add(userDeviceMappingDAO);
				responseData.put("data", getUserMappingDAOList);
			});
			return CustomMessages.makeResponseModel(responseData, CustomMessages.SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * update the user device mapping status provided input
	 * parameters.
	 *
	 * @param req The {@code UserDeviceMappingDAO} object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The response model containing the update the user device mapping status.
	 */
	public ResponseModel updateUserDeviceMappingStatus(UserDeviceMappingDAO req, HttpServletRequest request) {
		try {
			Optional<UserDeviceMapping> userDeviceMappingOP = userDeviceRepo
					.findByUserDeviceMappingId(req.getUserDeviceMappingId());

			if (userDeviceMappingOP.isPresent()) {

				UserDeviceMapping userDeviceMapping = userDeviceMappingOP.get();
				if (req.getStatusName().equals(StatusEnum.APPROVED.toString())) {
					userDeviceMapping.setStatusCode(StatusEnum.APPROVED.getValue());
					userDeviceMapping.setRemarks(null);
					UserMaster userMaster = userDeviceMapping.getUserId();
					userMaster.setImeiNumber(userDeviceMapping.getImeiNumber());
					userMasterRepository.save(userMaster);
				} else if (req.getStatusName().equals(StatusEnum.REJECTED.toString())) {
					userDeviceMapping.setStatusCode(StatusEnum.REJECTED.getValue());
				}
				userDeviceMapping.setModifiedBy(Long.valueOf(CustomMessages.getUserId(request, jwtTokenUtil)));
				userDeviceMapping.setModifiedOn(new Timestamp(new Date().getTime()));
				if (req.getRemarks() != null) {
					userDeviceMapping.setRemarks(req.getRemarks());
				}
				userDeviceRepo.save(userDeviceMapping);

				return new ResponseModel(req, CustomMessages.RECORD_UPDATE, CustomMessages.UPDATE_SUCCESSFULLY,
						CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(req, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}

	}

}
