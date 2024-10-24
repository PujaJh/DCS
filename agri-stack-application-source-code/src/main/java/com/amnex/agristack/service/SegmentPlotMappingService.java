package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.CadastralLinePointDTO;
import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.dao.SegmentPlotMappingDAO;
import com.amnex.agristack.entity.SegmentOwnerMapping;
import com.amnex.agristack.entity.SegmentPlotMapping;
import com.amnex.agristack.repository.SegmentOwnerMappingRepository;
import com.amnex.agristack.repository.SegmentPlotMappingRepository;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.GeometryUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Strings;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

@Service
public class SegmentPlotMappingService {

	@Autowired
	SegmentPlotMappingRepository segmentPlotMappingRepository;

	@Autowired
	SegmentOwnerMappingRepository segmentOwnerMappingRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private GeometryUtils geomUtil;

	public ResponseModel getPlotDetailsByUserId(HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

			String response = segmentPlotMappingRepository.getPlotDetailsByUserId(Long.valueOf(userId));

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<SegmentPlotMappingDAO> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, SegmentPlotMappingDAO.class));

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {

			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

//	public ResponseModel getSegmentsBySurveyNumber(SegmentPlotMappingDAO requestDAO, HttpServletRequest request) {
//		try {
//
//			String surveyNo = !Strings.isNullOrEmpty(requestDAO.getSurveyNumber()) ? requestDAO.getSurveyNumber() : "";
//			String response = segmentPlotMappingRepository.getSegmentsBySurveyNumber(requestDAO.getVillageLgdCode(),
//					surveyNo, requestDAO.getCurrentLat(), requestDAO.getCurrentLong());
//
//			ObjectMapper mapper = new ObjectMapper();
//			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//			TypeFactory typeFactory = mapper.getTypeFactory();
//			List<SegmentPlotMappingDAO> resultObject = mapper.readValue(response,
//					typeFactory.constructCollectionType(List.class, SegmentPlotMappingDAO.class));
//
//			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
//					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
//
//		} catch (Exception e) {
//
//			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
//					CustomMessages.FAILED);
//		}
//	}

	public ResponseModel addSegmentPlotMappingDetails(List<SegmentPlotMappingDAO> mappingDAO,
			HttpServletRequest request) {
		try {

			Long userId = Long.valueOf(CustomMessages.getUserId(request, jwtTokenUtil));

			mappingDAO.forEach((segment) -> {
				SegmentPlotMapping plotMapping = new SegmentPlotMapping();
				plotMapping.setCreatedBy(userId.toString());
				plotMapping.setCreatedOn(new Timestamp(new Date().getTime()));
				plotMapping.setFarmlandPlotRegistryId(segment.getFarmlandPlotRegistryId());
				plotMapping.setSegmentId(segment.getSegmentId());
				plotMapping.setSegmentNumber(segment.getSegmentNumber());
				plotMapping.setSegmentUniqueId(segment.getSegmentUniqueId());
				plotMapping.setValidGeometry(segment.getValidGeometry());

				plotMapping.setFarmlandId(segment.getFarmlandId());
				plotMapping.setVillageLgdCode(segment.getVillageLgdCode());
				plotMapping.setSurveyNumber(segment.getSurveyNumber());

				plotMapping = segmentPlotMappingRepository.save(plotMapping);

				if (!segment.getOwners().isEmpty()) {

					for (SegmentOwnerMappingDAO owner : segment.getOwners()) {
						SegmentOwnerMapping ownerMapping = new SegmentOwnerMapping();
						ownerMapping.setCreatedBy(userId.toString());
						ownerMapping.setCreatedOn(new Timestamp(new Date().getTime()));

						ownerMapping.setOwnerName(owner.getOwnerName());
						ownerMapping.setOwnerNumber(owner.getOwnerId());

						ownerMapping.setSegmentPlotMappingId(plotMapping.getSegmentPlotMappingId());

						segmentOwnerMappingRepository.save(ownerMapping);
					}
				}

			});

			return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {

			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public ResponseModel getSegmentPlotMappingReport(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String villageCodes = requestDAO.getVillageLgdCodeList().stream().map(i -> i.toString())
					.collect(Collectors.joining(", "));
			String response = segmentPlotMappingRepository.getSegmentPlotMappingReport(villageCodes);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {

			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public ResponseModel getNearestSegments(SegmentPlotMappingDAO requestDAO, HttpServletRequest request) {
		try {

			String surveyNo = !Strings.isNullOrEmpty(requestDAO.getSurveyNumber()) ? requestDAO.getSurveyNumber() : "";
			String response = segmentPlotMappingRepository.getNearestSegments(requestDAO.getVillageLgdCode(), surveyNo,
					requestDAO.getCurrentLat(), requestDAO.getCurrentLong());

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<SegmentPlotMappingDAO> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, SegmentPlotMappingDAO.class));

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {

			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public ResponseModel getSegmentBasedOnCurrentLocation(SegmentPlotMappingDAO requestDAO,
			HttpServletRequest request) {
		try {

			String segmentUniqueIds = requestDAO.getSegmentUniqueIds().stream().map(i -> i.toString())
					.collect(Collectors.joining(","));

			String response = segmentPlotMappingRepository.getSegmentBasedOnCurrentLocation(
					requestDAO.getVillageLgdCode(), segmentUniqueIds, requestDAO.getFlag(), requestDAO.getCurrentLat(),
					requestDAO.getCurrentLong(), "");

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<SegmentPlotMappingDAO> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, SegmentPlotMappingDAO.class));

			if (requestDAO.getFlag().equals("ON_BOUNDARY")) {

				if (!resultObject.isEmpty()) {
					SegmentPlotMappingDAO segment = resultObject.get(0);

					try {

						WKTReader wktReader = new WKTReader();

						Geometry geometry = wktReader.read(segment.getGeometry());
						geometry.setSRID(4326);

						GeometryFactory geometryFactory = new GeometryFactory();
						geometryFactory.createGeometry(geometry);

						CadastralLinePointDTO nearestPoint = geomUtil.findPerpendicularPointFromLatLong(geometry,
								requestDAO.getCurrentLat(), requestDAO.getCurrentLong());

						String nearestLineWKT = !Strings.isNullOrEmpty(nearestPoint.getLineStringGeom().toText())
								? nearestPoint.getLineStringGeom().toText()
								: "";
						response = segmentPlotMappingRepository.getSegmentBasedOnCurrentLocation(
								requestDAO.getVillageLgdCode(), segmentUniqueIds, "ON_BOUNDARY_INTERSECT",
								requestDAO.getCurrentLat(), requestDAO.getCurrentLong(), nearestLineWKT);

						resultObject = mapper.readValue(response,
								typeFactory.constructCollectionType(List.class, SegmentPlotMappingDAO.class));
 
					} catch (Exception e) {
						return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
								CustomMessages.FAILED);
					}

				}
			}
			if(!resultObject.isEmpty()) {
				return new ResponseModel(resultObject, CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

			}else {
				return CustomMessages.makeResponseModel(null, CustomMessages.NOT_FOUND, CustomMessages.NO_RECORD_FOUND,
						CustomMessages.FAILED);
			}
		
		} catch (Exception e) {

			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

}
