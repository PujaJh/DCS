package com.amnex.agristack.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.ResponseModel;
import com.google.common.io.BaseEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.repository.FarmerRegistryRepository;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;

@Service
public class FarmerGoldenRecordService {
	
	@Autowired
	private FarmerRegistryRepository farmerRegistryRepository;

	@Autowired
	private PmKisanService pmKisanService;
	
	public ResponseModel getFarmerDetailByVillageLgdCode(FarmerGoldenRecordDao dao, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
            FarmerGoldenRecordDao daos = new FarmerGoldenRecordDao();
            List<Object[]> response = farmerRegistryRepository.getFarmerDetailByVillageLgdCodes(dao.getVillageLgdCode().intValue());
            List<FarmerGoldenRecordDao> response1 = response.stream().map((Object[] mapper) ->{
            	 String farmerNameLocal = (mapper[0] != null) ? mapper[0].toString() : null;
                 String farmerNameEnglish = (mapper[1] != null) ? mapper[1].toString() : null;
                 Long farmerId = (mapper[2] != null) ? Long.parseLong(mapper[2].toString()) : null;
                 return new FarmerGoldenRecordDao(farmerNameLocal,farmerNameEnglish, farmerId);
            }).collect(Collectors.toList());
            
            
          
            
            return new ResponseModel(response1, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
                    CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
        } catch (Exception e) {
            responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
            return responseModel;    
        }
		
	}

//	public ResponseModel getSurveyNoDetailByFarmerId(FarmerGoldenRecordDao dao, HttpServletRequest request) {
//		ResponseModel responseModel = null;
//		try {
//            FarmerGoldenRecordDao daos = new FarmerGoldenRecordDao();
//            List<Object[]> response = farmerRegistryRepository.getSurveyNoDetailByFarmerId(dao.getFarmerId());
//            List<FarmerGoldenRecordDao> response1 = response.stream().map((Object[] mapper) -> {
//                String surveyNumber = (mapper[0] != null) ? mapper[0].toString() : null;
//                String subSurveyNumber = (mapper[1] != null) ? mapper[1].toString() : null;
//                Long farmLandPlotId = (mapper[2] != null) ? Long.parseLong(mapper[2].toString()) : null;
//                return new FarmerGoldenRecordDao(farmLandPlotId,surveyNumber, subSurveyNumber);
//            }).collect(Collectors.toList());
//            return new ResponseModel(response1, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
//                    CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
//        } catch (Exception e) {
//            responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
//                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
//            return responseModel;    
//        }
//		
//	}

	public ResponseModel geFarmerGoldenRecordDetailsByPlot(FarmerGoldenRecordDao farmerGoldenRecordDao,
			HttpServletRequest request) {
		
		try {
			String response = farmerRegistryRepository.getFarmerGoldenRecords(farmerGoldenRecordDao.getFarmerId(), farmerGoldenRecordDao.getFarmLandPlotId(),farmerGoldenRecordDao.getStartYear(), farmerGoldenRecordDao.getEndYear(), farmerGoldenRecordDao.getSeasonId());
			Gson g = new Gson();
			Object finalObject = g.fromJson(response, Object.class);
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//			TypeFactory typeFactory = mapper.getTypeFactory();
//			List<FarmerGoldenRecordOutputDao> resultObject = mapper.readValue(response, typeFactory.constructCollectionType(List.class, FarmerGoldenRecordOutputDao.class));
			
			return new ResponseModel(finalObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getFarmerProfileAndLandDetails(FarmerGoldenRecordDao dao, HttpServletRequest request) {
		try {
			String response = farmerRegistryRepository.getFarmerProfileAndLandDetails(dao.getFarmerId());

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();

			List<FarmerProfileLandDAO> resultObject = mapper.readValue(response, typeFactory.constructCollectionType(List.class, FarmerProfileLandDAO.class));

			FarmerProfileLandDAO farmerProfileLandDAO = !resultObject.isEmpty() ? resultObject.get(0): null;


			byte[] contentInBytes = BaseEncoding.base64().decode(farmerProfileLandDAO.getFarmerDetails().getFarmerAadharHash());
			String newstr = new String(contentInBytes, "UTF-8");
			PmKisanDAO pmKisanDAO = pmKisanService.getPmKisanDetail(newstr);

			if(Objects.nonNull(pmKisanDAO) && Objects.nonNull(pmKisanDAO.getMessage())){
				String pmKisanStatus = pmKisanDAO.getRsponce().equals("True") ? pmKisanDAO.getMessage() :  "Not Registered in PM-Kisan";
				farmerProfileLandDAO.getFarmerDetails().setPmKisanStatus(pmKisanStatus);
			} else {
				farmerProfileLandDAO.getFarmerDetails().setPmKisanStatus("-");
			}
			farmerProfileLandDAO.getFarmerDetails().setPmfbyStatus("-");

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		}catch(Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
		
	}
	

}
