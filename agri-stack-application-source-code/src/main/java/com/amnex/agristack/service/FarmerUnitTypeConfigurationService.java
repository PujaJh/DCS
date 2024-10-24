/**
 *
 */
package com.amnex.agristack.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.StateUnitTypeDTO;
import com.amnex.agristack.dao.SubDistrictLandUnitTypeDTO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.StateUnitTypeMaster;
import com.amnex.agristack.entity.SubDistrictLandUnitTypeMapping;
import com.amnex.agristack.repository.StateUnitTypeRepository;
import com.amnex.agristack.repository.SubDistrictLandUnitTypeMappingRepository;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.SubDistrictLgdMaster;
import com.amnex.agristack.repository.StateLgdMasterRepository;
import com.amnex.agristack.repository.SubDistrictLgdMasterRepository;
import com.amnex.agristack.utils.CustomMessages;

/**
 * @author kinnari.soni
 *
 *         22 Feb 2023 3:25:23 pm
 */

@Service
public class FarmerUnitTypeConfigurationService {

	@Autowired
	private StateLgdMasterRepository stateLgdMasterRepository;

	@Autowired
	private StateUnitTypeRepository stateUnitTypeRepository;

	@Autowired
	private SubDistrictLandUnitTypeMappingRepository subDistrictLandUnitTypeMappingRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtils;

	@Autowired
	private SubDistrictLgdMasterRepository subDistrictLgdMasterRepository;

	/**
	 * Adds a unit type.
	 *
	 * @param stateUnitType The state unit type to add.
	 * @return ResponseModel containing the result of the operation.
	 */
	public ResponseModel addUnitType(StateUnitTypeDTO stateUnitType) {
		ResponseModel responseModel = null;
		try {
			StateUnitTypeMaster unitType = new StateUnitTypeMaster();

			//Check for duplicate name
			List<StateUnitTypeMaster> existingUnitType = stateUnitTypeRepository.findByUnitTypeDescEngIgnoreCaseAndStateLgdCode(stateUnitType.getUnitTypeDescEng(),
					stateLgdMasterRepository.findByStateLgdCode(stateUnitType.getStateLgdCode()));

			//			StateUnitTypeMaster existingUnitType = stateUnitTypeRepository
			//					.findByUnitTypeDescEngAndUnitValueAndStateLgdCode(stateUnitType.getUnitTypeDescEng(),
			//							stateUnitType.getUnitValue(),
			//							stateLgdMasterRepository.findByStateLgdCode(stateUnitType.getStateLgdCode()));

			if (existingUnitType.size() > 0 ) {
				return CustomMessages.makeResponseModel(true, CustomMessages.DUPLICATE_UNIT_TYPE,
						CustomMessages.ALREADY_EXIST, CustomMessages.FAILED);
			} else {
				unitType.setStateLgdCode(stateLgdMasterRepository.findByStateLgdCode(stateUnitType.getStateLgdCode()));
				unitType.setUnitTypeDescEng(stateUnitType.getUnitTypeDescEng());
				unitType.setUnitTypeDescLl(stateUnitType.getUnitTypeDescLl() != null ? stateUnitType.getUnitTypeDescLl()
						: stateUnitType.getUnitTypeDescEng());
				unitType.setUnitValue(stateUnitType.getUnitValue());
				unitType.setIsActive(true);
				unitType.setIsDeleted(false);
				unitType.setDefault(false);
				return CustomMessages.makeResponseModel(stateUnitTypeRepository.save(unitType),
						"Unit type " + CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Adds a unit type mapping.
	 *
	 * @param subDistrictLandUnitTypeDTO The sub-district land unit type DTO.
	 * @param request                    The HTTP servlet request.
	 * @return ResponseModel containing the result of the operation.
	 */
	public ResponseModel addUnitTypeMapping(SubDistrictLandUnitTypeDTO subDistrictLandUnitTypeDTO,
											HttpServletRequest request) {
		try {

			StateUnitTypeMaster unitType = stateUnitTypeRepository
					.findByUnitTypeMasterId(subDistrictLandUnitTypeDTO.getUnitTypeId());

			subDistrictLandUnitTypeDTO.getSubDistrictLgdCodeList().forEach(subDistrictCode -> {
				SubDistrictLgdMaster subDistrictObj = subDistrictLgdMasterRepository
						.findBySubDistrictLgdCode(subDistrictCode);

				// If state data not available insert data, else mapping will be updated based
				// on state
				SubDistrictLandUnitTypeMapping subDistrictUnitMapping = subDistrictLandUnitTypeMappingRepository
						.findBySubDistrictLgdCode(subDistrictObj);
				String userId = CustomMessages.getUserId(request, jwtTokenUtils);

				if (subDistrictUnitMapping == null) {
					subDistrictUnitMapping = new SubDistrictLandUnitTypeMapping();
					subDistrictUnitMapping.setCreatedIp(request.getRemoteAddr());
					subDistrictUnitMapping.setCreatedBy(userId);
				} else {
					subDistrictUnitMapping.setModifiedIp(request.getRemoteAddr());
					subDistrictUnitMapping.setModifiedBy(userId);
				}

				subDistrictUnitMapping.setSubDistrictLgdCode(subDistrictObj);
				subDistrictUnitMapping.setUnitTypeMasterId(unitType);
				subDistrictLandUnitTypeMappingRepository.save(subDistrictUnitMapping);
			});

			return new ResponseModel("Farmer sub district unit type set successfully.", CustomMessages.ASSIGNED_DEFAULT_LAND_UNIT,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Retrieves unit types by state.
	 *
	 * @param stateLgdCode The LGD code of the state.
	 * @return ResponseModel containing the unit types.
	 */
	public ResponseModel getUnitTypeByState(Long stateLgdCode) {
		try {
			// Get state
			StateLgdMaster state = stateLgdMasterRepository.findByStateLgdCode(stateLgdCode);
			if (state == null) {
				return new ResponseModel(null, "State lgd code " + stateLgdCode + " not found",
						CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED, CustomMessages.METHOD_GET);
			} else {

				List<StateUnitTypeMaster> stateLandUnitTypeList = stateUnitTypeRepository.findByStateLgdCode(state);
				if (stateLandUnitTypeList == null) {
					return new ResponseModel(null, "State lgd code " + stateLgdCode + " unit type not found",
							CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED, CustomMessages.METHOD_GET);
				} else {
					return new ResponseModel(stateLandUnitTypeList, CustomMessages.GET_RECORD,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
				}
			}

		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves unit type mapping.
	 *
	 * @param subDistrictLandUnitTypeDTO The sub-district land unit type DTO.
	 * @param request                    The HTTP servlet request.
	 * @return ResponseModel containing the unit type mapping.
	 */
	public ResponseModel getUnitTypeMapping(SubDistrictLandUnitTypeDTO subDistrictLandUnitTypeDTO,
			HttpServletRequest request) {
		try {
//			return new ResponseModel(
//					subDistrictLandUnitTypeMappingRepository.findBySubDistrictLgdCodeList(subDistrictLgdMasterRepository
//							.findByLgdCodes(subDistrictLandUnitTypeDTO.getSubDistrictLgdCodeList())),
//					CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
//					CustomMessages.METHOD_POST);
			
			
			List<SubDistrictLandUnitTypeMapping> mappings = subDistrictLandUnitTypeMappingRepository.findBySubDistrictLgdCodeList(subDistrictLgdMasterRepository
					.findByLgdCodes(subDistrictLandUnitTypeDTO.getSubDistrictLgdCodeList()));
	
			// Set createdIp and modifiedIp to null for each mapping
			for(SubDistrictLandUnitTypeMapping mapping : mappings) {
				mapping.setCreatedIp(null);
				mapping.setModifiedIp(null);
				}

	return new ResponseModel(
			mappings,
			CustomMessages.GET_RECORD, 
			CustomMessages.GET_DATA_SUCCESS, 
			CustomMessages.SUCCESS,
			CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Retrieves unit type by sub-district code.
	 *
	 * @param subDistrictLgdCode The LGD code of the sub-district.
	 * @return ResponseModel containing the unit type.
	 */
	public ResponseModel getUnitTypeBySubDistrictCode(Long subDistrictLgdCode) {
		try {
			Map<String, Object> data = new HashedMap();
			// Get state
			SubDistrictLgdMaster subDistrictLgdMaster = subDistrictLgdMasterRepository
					.findBySubDistrictLgdCode(subDistrictLgdCode);
			if (subDistrictLgdMaster == null) {
				return new ResponseModel(null, "SubDistrict lgd code " + subDistrictLgdCode + " not found",
						CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED, CustomMessages.METHOD_GET);
			} else {

				SubDistrictLandUnitTypeMapping subDistrictLandUnitTypeMapping = subDistrictLandUnitTypeMappingRepository
						.findBySubDistrictLgdCode(subDistrictLgdMaster);
				if (subDistrictLandUnitTypeMapping == null) {
					List<StateUnitTypeMaster> stateLandUnitTypeList = stateUnitTypeRepository
							.findByStateLgdCode(subDistrictLgdMaster.getStateLgdCode());
					if (stateLandUnitTypeList.size() <= 0) {
						return new ResponseModel(null,
								"SubDistrict lgd code " + subDistrictLgdCode + " unit type not found",
								CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED, CustomMessages.METHOD_GET);
					} else {
						data.put("id", stateLandUnitTypeList.get(0).getUnitTypeMasterId());
						data.put("name", stateLandUnitTypeList.get(0).getUnitTypeDescEng());
						return new ResponseModel(data, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
								CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
					}
				} else {
					data.put("id", subDistrictLandUnitTypeMapping.getUnitTypeMasterId().getUnitTypeMasterId());
					data.put("name", subDistrictLandUnitTypeMapping.getUnitTypeMasterId().getUnitTypeDescEng());
					return new ResponseModel(data, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
							CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
				}
			}

		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}
}
