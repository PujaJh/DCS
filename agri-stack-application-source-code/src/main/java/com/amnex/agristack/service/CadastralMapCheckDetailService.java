package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.FarmlandPlotRegistry;
import com.amnex.agristack.repository.FarmlandPlotRegistryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.CadastralLinePointDTO;
import com.amnex.agristack.dao.CadastralMapDetailInput;
import com.amnex.agristack.entity.CadastralMapCheckDetail;
import com.amnex.agristack.repository.CadastralMapCheckDetailRepository;
import com.amnex.agristack.repository.CadastralMapGeometryRepository;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.DBUtils;
import com.amnex.agristack.utils.GeometryUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vividsolutions.jts.io.WKTWriter;

@Service
public class CadastralMapCheckDetailService {

	@Autowired
	UserService userService;

	@Autowired
	DBUtils dbUtils;

	@Autowired
	CadastralMapCheckDetailRepository cadastralMapDetailRepository;

	@Autowired
	CadastralMapGeometryRepository cadastralMapGeometryRepository;

	@Autowired
	FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	GeometryUtils geomUtils;

	public ResponseModel addCadastralMapDetail(CadastralMapDetailInput input, HttpServletRequest request) {
		CadastralMapCheckDetail cadastralMapDetail = new CadastralMapCheckDetail();
		try {

//			List<CadastralMapGeometry> geomList = cadastralMapGeometryRepository
//					.findByVillageLgdCodeAndSurveyNumberAndSubSurveyNumber(input.getVillageLgdCode(),
//							input.getSurveyNo(), input.getSubSurveyNo());
			
			List<FarmlandPlotRegistry> geomList = farmlandPlotRegistryRepository
					.findByVillageLgdCodeAndSurveyNumberAndSubSurveyNumber(Long.valueOf(input.getVillageLgdCode()),
							input.getSurveyNo(), input.getSubSurveyNo());

//			CadastralMapGeometry cadastralMapGeometry = new CadastralMapGeometry();
			FarmlandPlotRegistry cadastralMapGeometry = new FarmlandPlotRegistry();
			WKTWriter wktWriter = new WKTWriter();
			CadastralLinePointDTO nearestPoint = new CadastralLinePointDTO();
			
			if (!geomList.isEmpty()) {
				cadastralMapGeometry = geomList.get(0);
				nearestPoint = geomUtils.findPerpendicularPointFromLatLong(
						cadastralMapGeometry.getPlotGeometry(), input.getSurveyorLat(), input.getSurveyorLong());

			}
			String userId = userService.getUserId(request);

			List<CadastralMapCheckDetail> surveyCheck = cadastralMapDetailRepository
					.findByVillageLgdCodeAndSurveyNoAndSubSurveyNoAndNearestPolylineWkt(input.getVillageLgdCode(),
							input.getSurveyNo(), input.getSubSurveyNo(), wktWriter.write(nearestPoint.getLineStringGeom()));

			if (surveyCheck != null && !surveyCheck.isEmpty()) {
				return new ResponseModel(null, "Survey is already done from this side of boundary.",
						CustomMessages.ALREADY_EXIST, CustomMessages.FAILED, CustomMessages.METHOD_GET);
			}

			cadastralMapDetail.setFarmlandPlotId(input.getFarmlandPlotId());
			cadastralMapDetail.setSurveyNo(input.getSurveyNo());
			cadastralMapDetail.setSubSurveyNo(input.getSubSurveyNo());
			cadastralMapDetail.setVillageLgdCode(input.getVillageLgdCode());
			cadastralMapDetail.setSurveyorLat(input.getSurveyorLat());
			cadastralMapDetail.setSurveyorLong(input.getSurveyorLong());
			
			
			cadastralMapDetail.setNearestPolylineWkt(input.getNearestPolylineWkt());
			cadastralMapDetail.setNearestPointWkt(input.getNearestPointWkt());
			cadastralMapDetail.setNearestDistance(input.getNearestDistance());
			
//			Map<String, Object> msgWithData = new HashMap<String, Object>();
//			msgWithData.put("plotGeom", wktWriter.write(cadastralMapGeometry.getPlotGeometry()));
//			msgWithData.put("getLineStringGeom", wktWriter.write(nearestPoint.getLineStringGeom()));
//			msgWithData.put("getPerpPointGeom", wktWriter.write(nearestPoint.getPerpPointGeom()));
//			msgWithData.put("getNearestDistance", nearestPoint.getNearestDistance());

			
			cadastralMapDetail.setNearestPolylineWkt(wktWriter.write(nearestPoint.getLineStringGeom()));
			cadastralMapDetail.setNearestPointWkt(wktWriter.write(nearestPoint.getPerpPointGeom()));
			cadastralMapDetail.setNearestDistance(nearestPoint.getNearestDistance());
			
			cadastralMapDetail.setCreatedBy(input.getUserId());
			cadastralMapDetail.setCreatedOn(new Timestamp(new Date().getTime()));
			cadastralMapDetail.setIsActive(true);
			cadastralMapDetail.setIsDeleted(false);

			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
				Date parsedDate = formatter.parse(input.getSurveyorMobileDate());
				Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
				cadastralMapDetail.setSurveyorMobileDate(timestamp);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}

			cadastralMapDetailRepository.save(cadastralMapDetail);

		} catch (Exception e) {
			e.printStackTrace();
//			return new ResponseEntity<String>(CustomMessages.getMessage(CustomMessages.INTERNAL_SERVER_ERROR),
//					HttpStatus.INTERNAL_SERVER_ERROR);
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
		return new ResponseModel(cadastralMapDetail, "Cadastral detail added successfully.",
				CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

//		return new ResponseEntity<String>(CustomMessages.getMessageWithData(
//				CustomMessages.CADASTRAL_DETAIL_ADDED_SUCCSSFULLY, cadastralMapDetail), HttpStatus.OK);
	}

	public ResponseModel getCadastrialDataByUser(CadastralMapDetailInput input) {
		try {

			String response = cadastralMapDetailRepository.getCadastrialDataByUser(input.getUserId().intValue());

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

	}

	public ResponseModel getCadastrialSurveyReport(CadastralMapDetailInput input) {
		try {

			String response = cadastralMapDetailRepository.getCadastrialSurveyReport(input.getUserId().intValue());

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
}
