/**
 * 
 */
package com.amnex.agristack.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.BoundaryLgdDAO;
import com.amnex.agristack.dao.CropMasterOutputDAO;
import com.amnex.agristack.dao.CropSequenceMappingDAO;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.entity.CropRegistry;
import com.amnex.agristack.entity.CropSequenceMapping;
import com.amnex.agristack.repository.CropMasterRepository;
import com.amnex.agristack.repository.CropSequenceMappingRepository;
import com.amnex.agristack.repository.SubDistrictLgdMasterRepository;
import com.amnex.agristack.repository.UserVillageMappingRepository;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;

/**
 * @author majid.belim
 *
 */
@Service
public class CropSequenceMappingService {

	@Autowired
	private SubDistrictLgdMasterRepository subDistrictLgdMasterRepository;

	@Autowired
	private UserService userService;
	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;
	@Autowired
	private CropSequenceMappingRepository cropSequenceMappingRepository;

	@Autowired
	private CropMasterRepository cropMasterRepository;

	/**
	 * add or update crop Sequence Mapping
	 * parameters.
	 *
	 * @param req The {@code CropSequenceMappingDAO} object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The ResponseModel object representing the response {@link CropSequenceMapping}
	 */
	public ResponseModel add(HttpServletRequest request, CropSequenceMappingDAO cropSequenceMappingDAO) {

		try {
			List<CropSequenceMapping> finalData = new ArrayList<CropSequenceMapping>();
			List<BoundaryLgdDAO> data = new ArrayList<BoundaryLgdDAO>();
			String userId = userService.getUserId(request);
			Long id = Long.parseLong(userId);
			List<Long> assignStates = userVillageMappingRepository.getStateCodesById(id);
			if (cropSequenceMappingDAO.getType().equals("subDistrict")) {
				cropSequenceMappingRepository.deleteBySubDistrictLgdCodeIn(cropSequenceMappingDAO.getSubDistrictCodes());
			}else {

				cropSequenceMappingRepository.deleteByStateLgdCodeIn(assignStates);
			}
			if (cropSequenceMappingDAO.getCropSequenceList() != null
					&& cropSequenceMappingDAO.getCropSequenceList().size() > 0
					&& cropSequenceMappingDAO.getType() != null) {
				
				if (cropSequenceMappingDAO.getType().equals("subDistrict")
						&& cropSequenceMappingDAO.getSubDistrictCodes() != null
						&& cropSequenceMappingDAO.getSubDistrictCodes().size() > 0) {

					
					data = subDistrictLgdMasterRepository
							.findBySubDistrictLgdCodeBySubDistrictCodes(cropSequenceMappingDAO.getSubDistrictCodes());

				} else {

					
					if (assignStates != null && assignStates.size() > 0) {
						data = subDistrictLgdMasterRepository.findBySubDistrictLgdCodeByStateCode(assignStates);
					}

				}

				if (data != null && data.size() > 0) {

					

							
					data.forEach(action -> {

						cropSequenceMappingDAO.getCropSequenceList().forEach(action2 -> {
							CropSequenceMapping cropSequenceMapping = new CropSequenceMapping();
							cropSequenceMapping.setCropId(action2.getCropId());
							cropSequenceMapping.setStateLgdCode(action.getStateLgdCode());
							cropSequenceMapping.setDistrictLgdCode(action.getDistrictLgdCode());
							cropSequenceMapping.setSubDistrictLgdCode(action.getSubDistrictLgdCode());
							cropSequenceMapping.setSeasonId(cropSequenceMappingDAO.getSeasonId());
							cropSequenceMapping.setSequenceNumber(action2.getSequenceNumber());
							cropSequenceMapping.setCreatedBy(userId);
							if (cropSequenceMappingDAO.getType().equals("state")) {
								cropSequenceMapping.setIsStateLevel(true);
							} else {
								cropSequenceMapping.setIsStateLevel(false);
							}
							try {
								cropSequenceMapping.setCreatedIp(CommonUtil.getRequestIp(request));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							finalData.add(cropSequenceMapping);
						});
					});

					if (finalData != null && finalData.size() > 0) {
						cropSequenceMappingRepository.saveAll(finalData);
					}
				}

			}

			return CustomMessages.makeResponseModel(finalData, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();

			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

		// TODO Auto-generated method stub

	}

	/**
	 * fetch crop detail list by user
	 * parameters.
	 *
	 * @param req The CropSequenceMappingDAO object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The ResponseModel object representing the response.
	 */
	public ResponseModel getCropDetailListByUser(HttpServletRequest request,
			CropSequenceMappingDAO cropSequenceMappingDAO) {
		try {
			String userId = userService.getUserId(request);
			Long id = Long.parseLong(userId);
			List<CropSequenceMapping> cropSequenceMappingList=new ArrayList<>();
			if(cropSequenceMappingDAO.getSubDistrictCode()!=null ) {
				
				cropSequenceMappingList=cropSequenceMappingRepository.findOneBySubDistrictLgdCodeAndSeasonIdOrderBySequenceNumberAsc(cropSequenceMappingDAO.getSubDistrictCode(),cropSequenceMappingDAO.getSeasonId());
				
			}else {
				
				
				List<Long> assignSubDistict = userVillageMappingRepository.getSubDistrictCodesById(id);
				
				if (assignSubDistict != null && assignSubDistict.size() > 0) {
					cropSequenceMappingList=cropSequenceMappingRepository.findOneBySubDistrictLgdCodeAndIsStateLevelAndSeasonId(assignSubDistict.get(0),true,cropSequenceMappingDAO.getSeasonId());
				}	
			}
			

			
			List<CropRegistry> crops = new ArrayList<>();
			List<Long> assignStates = userVillageMappingRepository.getStateCodesById(id);
//			if (assignSubDistict != null && assignSubDistict.size() > 0) {
				if(cropSequenceMappingList!=null && cropSequenceMappingList.size()>0) {
					List<Long> cropIds=cropSequenceMappingList.stream().map(x->x.getCropId()).collect(Collectors.toList());
					crops = cropMasterRepository.findStateCropsAndCropNotIn(assignStates, cropIds);
				}else {
					crops = cropMasterRepository.findStateCrops(assignStates);	
				}
				
//			}
			return CustomMessages.makeResponseModel(crops, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();

			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * fetch configure crop detail list by boundary
	 * parameters.
	 *
	 * @param req The CropSequenceMappingDAO object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The ResponseModel object representing the response.
	 */
	public ResponseModel getConfigureCropDetailListByBoundary(HttpServletRequest request,
			CropSequenceMappingDAO cropSequenceMappingDAO) {
		try {
			
			
			List<CropSequenceMapping> crops = new ArrayList<>();
			
			
			if(cropSequenceMappingDAO.getSubDistrictCode()!=null ) {
					crops=cropSequenceMappingRepository.findOneBySubDistrictLgdCodeAndSeasonIdOrderBySequenceNumberAsc(cropSequenceMappingDAO.getSubDistrictCode(),cropSequenceMappingDAO.getSeasonId());
					
			}else {
					String userId = userService.getUserId(request);
					Long id = Long.parseLong(userId);
					List<Long> assignSubDistict = userVillageMappingRepository.getSubDistrictCodesById(id);
					if (assignSubDistict != null && assignSubDistict.size() > 0) {
						
						crops=cropSequenceMappingRepository.findOneBySubDistrictLgdCodeAndIsStateLevelAndSeasonId(assignSubDistict.get(0),true,cropSequenceMappingDAO.getSeasonId());
					}
			}
			List<CropMasterOutputDAO> cropMasterOutputDAOList=new ArrayList<>();
			
			if(crops!=null && crops.size()>0) {
				
				List<Long> cropList=crops.stream().map(x->x.getCropId()).collect(Collectors.toList());
				List<CropRegistry> cropMasterList= cropMasterRepository.findByCropIds(cropList);
				
				crops.forEach(action->{
					
					List<CropRegistry> crop=cropMasterList.stream().filter(x->x.getCropId().equals(action.getCropId())).collect(Collectors.toList());
					if(crop!=null && crop.size()>0) {
						CropMasterOutputDAO cropMasterOutputDAO=new CropMasterOutputDAO();
						cropMasterOutputDAO.setSequenceNumber(action.getSequenceNumber());
						cropMasterOutputDAO.setCropName(crop.get(0).getCropName());
						cropMasterOutputDAO.setCropId(crop.get(0).getCropId());
						cropMasterOutputDAOList.add(cropMasterOutputDAO);
					}
					

				});
			}
			return CustomMessages.makeResponseModel(cropMasterOutputDAOList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
			
		} catch (Exception e) {
			e.printStackTrace();

			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

}
