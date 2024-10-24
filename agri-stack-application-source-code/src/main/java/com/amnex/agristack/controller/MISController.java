package com.amnex.agristack.controller;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.FarmlandPlotRegistryDAO;
import com.amnex.agristack.dao.RoleInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.SummaryReportService;
import com.amnex.agristack.service.SurveyReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.dao.CropDistributionDTO;
import com.amnex.agristack.service.MISService;
import com.amnex.agristack.utils.CustomMessages;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.lang.GeoLocation;
//import org.apache.commons.imaging.common.IImageMetadata;


@RestController
@RequestMapping("/mis")
@Slf4j
public class MISController {

	@Autowired
	MISService misService;

	@Autowired
	SummaryReportService summaryReportService;
	@Autowired
	SurveyReviewService surveyReviewService;

	// Retrieves crop distribution data by crop
	/**
	 * Retrieves the crop distribution by crop
	 * @param cropDistributionDTO The CropDistributionDto containing the input params
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping("/getCropDistributionByCrop")
	public Object getCropDistributionByCrop(@RequestBody CropDistributionDTO cropDistributionDTO) {
		try {
			Object result = misService.getCropDistributionByCrop(cropDistributionDTO);
			// TODO:Return the result
			return new ResponseModel(result, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(e.getMessage(), CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Retrieves the crop distribution by crop variety
	 * @param cropDistributionDTO The CropDistributionDto containing the input params
	 * @return The ResponseModel object representing the response
	 */
	// Retrieves crop distribution data by crop variety
	@PostMapping("/getCropDistributionByCropVariety")
	public Object getCropDistributionByCropVariety(@RequestBody CropDistributionDTO cropDistributionDTO) {
		try {
			Object result = misService.getCropDistributionByCropVariety(cropDistributionDTO);
			// TODO:Return the result
			return new ResponseModel(result, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(e.getMessage(), CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Retrieves the crop distribution by territory
	 * @param cropDistributionDTO The CropDistributionDto containing the input params
	 * @return The ResponseModel object representing the response
	 */
	// Retrieves crop distribution data by territory
	@PostMapping("/getCropDistributionByTerritory")
	public Object getCropDistributionByTerritory(@RequestBody CropDistributionDTO cropDistributionDTO) {
		try {
			Object result = misService.getCropDistributionByTerritory(cropDistributionDTO);
			// TODO:Return the result
			return new ResponseModel(result, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(e.getMessage(), CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Retrieves the surveyor accuracy by territory
	 * @param cropDistributionDTO The CropDistributionDto containing the input params
	 * @return The ResponseModel object representing the response
	 */
	// Retrieves surveyor accuracy data by territory
	@PostMapping("/getSurveyorAccuracyByTerritory")
	public Object getSurveyorAccuracyByTerritory(@RequestBody CropDistributionDTO cropDistributionDTO) {
		try {
			Object result = misService.getSurveyorAccuracyByTerritory(cropDistributionDTO);
			// TODO:Return the result
			return new ResponseModel(result, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(e.getMessage(), CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Retrieves the farmer by territory
	 * @param cropDistributionDTO The CropDistributionDto containing the input params
	 * @return The ResponseModel object representing the response
	 */
	// Retrieves farmer data by territory
	@PostMapping("/getFarmerByTerritory")
	public Object getFarmerByTerritory(@RequestBody CropDistributionDTO cropDistributionDTO) {
		try {
			Object result = misService.getFarmerByTerritory(cropDistributionDTO);
			// TODO:Return the result
			return new ResponseModel(result, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(e.getMessage(), CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Retrieves the geom of the village
	 * @param farmlandPlotRegistryDAO The FarmlandPlotRegistryDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	// Retrieves village geometry data by village LGD code list
	@PostMapping(value = "/getVillageGeom")
	public ResponseEntity<?> addCropCategory(@RequestBody FarmlandPlotRegistryDAO farmlandPlotRegistryDAO,
			HttpServletRequest request) {
		try {
			return misService.getVillageGeom(farmlandPlotRegistryDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the village level crop details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getVillageLevelCropDetails")
	public ResponseModel getVillageLevelCropDetails(@RequestBody CommonRequestDAO requestDAO,
			HttpServletRequest request) {
		try {
			return misService.getVillageLevelCropDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the survey summary details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getSurveySummaryDetails")
	public ResponseModel getSurveySummaryDetails(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return misService.getSurveySummaryDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the surveyor survey details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getSurveyorSurveyDetails")
	public ResponseModel getSurveyorSurveyDetails(@RequestBody CommonRequestDAO requestDAO,
			HttpServletRequest request) {
		try {
			return misService.getSurveyorSurveyDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the surveyor activity details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getSurveyorActivityDetails")
	public ResponseModel getSurveyorActivityDetails(@RequestBody CommonRequestDAO requestDAO,
			HttpServletRequest request) {
		try {
			return misService.getSurveyorActivityDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the cadastral map survey details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getCadastralMapSurveyDetail")
	public ResponseModel getCadastralMapSurveyDetail(@RequestBody CommonRequestDAO requestDAO,HttpServletRequest request) {
		try {
			return misService.getCadastralMapSurveyDetail(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the survey data details of crop media
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/surveyDataDetailsOfCropMedia")
	public ResponseModel surveyDataDetailsOfCropMedia(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
		return misService.surveyDataDetailsOfCropMedia(requestDAO,request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}

	/**
	 * Retrieves the list of cultivated aggregated details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getCultivatedAggregatedDetails")
	public ResponseModel getCultivatedAggregatedDetails(@RequestBody CommonRequestDAO requestDAO,
			HttpServletRequest request) {
		try {
			return misService.getCultivatedAggregatedDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@PostMapping(value = "/scraper/getCultivatedAggregatedSummary")
	public ResponseModel getCultivatedAggregatedSummary(@RequestBody Map<String,Object>  requestDAO,
														HttpServletRequest request) {
		try {
			return misService.getCultivatedAggregatedSummary(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of cultivated summary details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getCultivatedSummaryDetails")
	public ResponseModel getCultivatedSummaryDetails(@RequestBody CommonRequestDAO requestDAO,
													 HttpServletRequest request) {
		try {
			return misService.getCultivatedSummaryDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

//	@PostMapping(value = "/getToken")
//	public ResponseModel getToken(){
//		Map<String, Object> claims = new HashMap<>();
//		claims.put("role", "admin");
//		claims.put("scope", "read");
//		String subject = "user#$123";
//		String token = JwtTokenUtil.generateSecureToken(claims, subject);
//		System.out.println("Generated Token: " + token);
//		return null;
//
//	}

	@GetMapping(value = "/scraper/token/renew")
	public ResponseModel getNewToken(HttpServletRequest request) throws Exception {
		System.out.println("request "+request);
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", "admin");
		claims.put("scope", "read");
		String subject = "user#$123";
		return  JwtTokenUtil.generateSecureToken(claims, subject,request);

	}


	@PostMapping(value = "/scraper/getCultivatedAreaSummaryReport")
	public ResponseModel getCultivatedSummaryReport(@RequestBody Map<String,Object> requestDAO,
													 HttpServletRequest request) {
		try {
			return misService.getCultivatedSummaryReport(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Retrieves the cultivated crop category details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getCultivatedCropCategoryDetails")
	public ResponseModel getCultivatedCropCategoryDetails(@RequestBody CommonRequestDAO requestDAO,
			HttpServletRequest request) {
		try {
			return misService.getCultivatedCropCategoryDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the user management report
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getUsermanagementReport")
	public ResponseModel getUsermanagementReport(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return misService.getUsermanagementReport(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the surveyor progress details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getSurveyorProgressDetails")
	public ResponseModel getSurveyorProgressDetails(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return misService.getSurveyorProgressDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@PostMapping(value = "/scraper/getCropAreaData")
	public ResponseModel getCropAreaData(@RequestBody Map<String,Object> requestDAO, HttpServletRequest request) {
		try {
			return misService.getCropAreaData(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@PostMapping(value = "/scraper/insertRecordWiseStatus")
	public Map<String,Object> insertRecordWiseStatus(@RequestBody String requestDAO, HttpServletRequest request) {
		try {
			return misService.insertRecordWiseStatus(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	@PostMapping(value = "/scraper/getSurveyorActivityReport")
	public Object getSurveyorActivityReport(@RequestBody Map<String,Object> requestDAO, HttpServletRequest request){
		try {
			return summaryReportService.getSurveyorActivityReport(requestDAO,request);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(e.getMessage(), CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}}


	@PostMapping(value = "/surveyWiseImage")
	public ResponseModel GetSurveyWiseImagesForAUser(@RequestBody CommonRequestDAO requestDTO){
		try{
			surveyReviewService.GetSurveyWiseImagesForAUser(requestDTO);
			return new ResponseModel(true,
					null, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
					CustomMessages.METHOD_GET);
		} catch (Exception e){
			return new ResponseModel(null,
					e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}


	@PostMapping(value = "/getDownloadedeImage")
	public ResponseModel GetDownloadedImages(@RequestBody CommonRequestDAO requestDTO){
		try{
			List<String> imagePathList = surveyReviewService.GetDownloadedImages(requestDTO);
			return new ResponseModel(imagePathList,
					null, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
					CustomMessages.METHOD_GET);
		} catch (Exception e){
			return new ResponseModel(null,
					e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}


	@PostMapping(value = "/getImageLatLon")
	public ResponseModel getImageLatLon(@RequestBody CommonRequestDAO requestDTO){
		try{
			//surveyReviewService.GetSurveyWiseImagesForAUser(requestDTO);
//			javaxt.io.Image image = new javaxt.io.Image("/home/pujakumari/Music/userImage/1184486/.trashed-1729441449-IMG_1184486Wed Sep 11 11_19_38 GMT+05_30 2024.jpg");
//			double[] gps = image.getGPSCoordinate();
//
//			try {
//				ImageInputStream inStream = ImageIO.createImageInputStream(new File("/home/pujakumari/Music/userImage/1184486/.trashed-1729441449-IMG_1184486Wed Sep 11 11_19_38 GMT+05_30 2024.jpg"));
//				Iterator<ImageReader> imgItr = ImageIO.getImageReaders(inStream);
//
//				while (imgItr.hasNext()) {
//					ImageReader reader = imgItr.next();
//					reader.setInput(inStream, true);
//					IIOMetadata metadata = reader.getImageMetadata(0);
//					System.out.println("metadata "+metadata);
//
//
//					String[] names = metadata.getMetadataFormatNames();
//					int length = names.length;
//					for (int i = 0; i < length; i++) {
//						System.out.println( "Format name: " + names[ i ] );
//					}
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			try {
//				// File path of the image
//				File imageFile = new File("/home/pujakumari/Music/userImage/1184486/.trashed-1729441449-IMG_1184486Wed Sep 11 11_19_38 GMT+05_30 2024.jpg");
//
//				// Extract metadata from the image file
//				Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
//
//				// Iterate over all metadata directories
//				for (Directory directory : metadata.getDirectories()) {
//					// Print all tags for debugging purposes
//					for (Tag tag : directory.getTags()) {
//						System.out.println(tag);
//					}
//					System.out.println(" directory "+directory);
//
//					// Look for GPS data
//					if (directory instanceof GpsDirectory) {
//						GpsDirectory gpsDirectory = (GpsDirectory) directory;
//
//						// Get latitude and longitude
//						GeoLocation geoLocation = gpsDirectory.getGeoLocation();
//						if (geoLocation != null) {
//							System.out.println("Latitude: " + geoLocation.getLatitude());
//							System.out.println("Longitude: " + geoLocation.getLongitude());
//						} else {
//							System.out.println("No GPS data found.");
//						}
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			try {
//				// Specify the image file
//				File imageFile = new File("/home/pujakumari/Music/userImage/1184486/.trashed-1729441449-IMG_1184486Wed Sep 11 11_19_38 GMT+05_30 2024.jpg");
//
//				// Extract metadata from the image
//				IImageMetadata metadata = Imaging.getMetadata(imageFile);
//
//				if (metadata != null) {
//					// Display metadata for debugging
//					List<? extends ImageMetadataItem> items = metadata.getItems();
//					for (ImageMetadataItem item : items) {
//						System.out.println(item); // For general metadata inspection
//					}
//
//					// Find the GPS metadata
//					// Unfortunately, the Apache Commons Imaging doesn't have as friendly GPS handling as MetadataExtractor.
//					// You can check for EXIF data and inspect relevant tags, including GPS ones.
//					// Example: metadata.getItems() may contain GPS info in EXIF tags.
//				} else {
//					System.out.println("No metadata found.");
//				}
//			} catch (ImageReadException | IOException e) {
//				e.printStackTrace();
//			}

			return new ResponseModel(true,
					null, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
					CustomMessages.METHOD_GET);
		} catch (Exception e){
			return new ResponseModel(null,
					e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}





}
