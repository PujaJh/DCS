/**
 *
 */
package com.amnex.agristack.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.SurveyorInputDAO;
import com.amnex.agristack.dao.SurveyorOutputDAO;
import com.amnex.agristack.entity.DepartmentMaster;
import com.amnex.agristack.entity.DesignationMaster;
import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.SubDistrictLgdMaster;
import com.amnex.agristack.entity.UserBankDetail;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.repository.StatusMasterRepository;
import com.amnex.agristack.repository.UserAvailabilityRepository;
import com.amnex.agristack.repository.UserBankDetailRepository;
import com.amnex.agristack.repository.UserMasterRepository;
import com.amnex.agristack.repository.VillageLgdMasterRepository;
import com.amnex.agristack.utils.CustomMessages;

/**
 * @author kinnari.soni
 *
 * 25 Feb 2023 9:18:21 am
 */

@Service
public class SupervisorService {

	@Autowired
	UserMasterRepository userRepository;
	//
	//	@Autowired
	//	DepartmentRepository departmentRepository;
	//
	//	@Autowired
	//	DesignationRepository designationRepository;
	//
	//	@Autowired
	//	RoleMasterRepository roleRepository;
	//
	//	@Autowired
	//	GeneralService generalService;

	@Autowired
	UserAvailabilityRepository userAvailabilityRepository;

	@Autowired
	private UserBankDetailRepository userBankDetailRepository;

	@Autowired
	private StatusMasterRepository statusRepository;

	@Autowired
	StateLgdMasterService stateService;

	@Autowired
	DistrictLgdMasterService districtService;
	@Autowired
	SubDistrictLgdMasterService subDistrictService;

	@Autowired
	VillageLgdMasterService villageService;

	@Autowired
	private VillageLgdMasterRepository villageMasterRepository;

	/**
	 * Retrieves a list of supervisors.
	 * 
	 * @param requestInput  The {@code SurveyorInputDAO} object containing the request parameters.
	 * @param request       The HttpServletRequest object.
	 * @return {@link SurveyorOutputDAO}  The ResponseModel object representing the response.
	 */
	public ResponseModel getAllSupervisor(SurveyorInputDAO requestInput, HttpServletRequest request) {
		ResponseModel responseModel = new ResponseModel();
		List<SurveyorOutputDAO> surveyorList = new ArrayList<>();
		try {

			List<UserMaster> userData  = userRepository.findAllByIsDeletedAndIsActiveAndRoleId_RoleIdAndUserPrIdNotNullOrderByCreatedOnDesc(false,true,new Long(26));
			surveyorList = getSurpervisorOutFromUserMaster(userData);
			return new ResponseModel(surveyorList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}
	
	/**
	 * Retrieves a list of SurveyorOutputDAO objects from a list of UserMaster objects.
	 * 
	 * @param users  The List of UserMaster objects.
	 * @return       The List of SurveyorOutputDAO objects representing the supervisors.
	 */
	public List<SurveyorOutputDAO> getSurpervisorOutFromUserMaster(List<UserMaster> users) {
		List<SurveyorOutputDAO> supervisorList = new ArrayList<>();
		for (UserMaster user : users) {
			if(!user.getIsDeleted()) {
				SurveyorOutputDAO  surveyor = new SurveyorOutputDAO();
				BeanUtils.copyProperties(user,surveyor);
				getSurveyorBoundaryDetail(surveyor);
				if(Objects.nonNull(surveyor.getDepartmentId())) {
					DepartmentMaster department = new DepartmentMaster(surveyor.getDepartmentId().getDepartmentId(), surveyor.getDepartmentId().getDepartmentName(), surveyor.getDepartmentId().getDepartmentType());
					surveyor.setDepartmentId(department);
				}
				surveyor.setUserStatus(statusRepository.findByIsDeletedFalseAndStatusCode(user.getUserStatus()).getStatusName());
				surveyor.setIsAvailable(user.getIsAvailable());
				if(Objects.nonNull(surveyor.getDesignationId())) {
					DesignationMaster designation = new DesignationMaster(surveyor.getDesignationId().getDesignationId(), surveyor.getDesignationId().getDesignationName());
					surveyor.setDesignationId(designation);
				}
				UserBankDetail bankDetail = userBankDetailRepository.findByUserId_UserId(user.getUserId());
				if(Objects.nonNull(bankDetail)) {
					surveyor.setUserBankDetail(new UserBankDetail(bankDetail.getUserBankName(), bankDetail.getUserBranchCode(), bankDetail.getUserIfscCode(), bankDetail.getUserBankAccountNumber(), bankDetail.getBankId().getBankId()));
				}
				//	                surveyor.setSeasonName(season.getSeasonName());
				surveyor.setCreatedOn(user.getCreatedOn());
				VillageLgdMaster villageMaster = villageMasterRepository.findByVillageLgdCode(new Long(user.getUserVillageLGDCode()));
				surveyor.setVillageMaster(villageMaster);
				supervisorList.add(surveyor);
			}
		}
		return supervisorList;
	}

	/**
	 * Retrieves the boundary details for a SurveyorOutputDAO object.
	 * 
	 * @param user  The SurveyorOutputDAO object.
	 * @return      The SurveyorOutputDAO object with boundary details set.
	 */
	public SurveyorOutputDAO getSurveyorBoundaryDetail(SurveyorOutputDAO user) {
		if(Objects.nonNull(user.getUserStateLGDCode())) {
			StateLgdMaster state = stateService.getStateByLGDCode(Long.valueOf(user.getUserStateLGDCode().longValue()));
			DistrictLgdMaster district = districtService.getDistrictByLGDCode(user.getUserDistrictLGDCode().longValue());
			SubDistrictLgdMaster subDistrict = subDistrictService.getSubDistrictByLGDCode(user.getUserTalukaLGDCode().longValue());
			VillageLgdMaster village = villageService.getVillageByLGDCode(user.getUserVillageLGDCode().longValue());
			user.setStateName(Objects.nonNull(state) ? state.getStateName() : "");
			user.setDistrictName(Objects.nonNull(district) ? district.getDistrictName() : "");
			user.setStateName(Objects.nonNull(subDistrict) ? subDistrict.getSubDistrictName() : "");
			user.setStateName(Objects.nonNull(village) ? village.getVillageName() : "");
		}

		return user;
	}



}
