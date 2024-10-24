package com.amnex.agristack.service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.config.JwtUserDetailsService;
import com.amnex.agristack.dao.FarmlandPlotDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.UPRoRDataUploadLog;
import com.amnex.agristack.repository.*;
import com.amnex.agristack.ror.dao.ODRoRDataMainDAO;
import com.amnex.agristack.utils.CustomMessages;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatewiseNAUpdateService {

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

	@Autowired
	private UPRoRDataUploadLogRepository roRDataUploadLogRepository;

	@Autowired
	private FarmerLandOwnershipRegistryRepository farmerLandOwnershipRegistryRepository;

	@Autowired
	private LandParcelSurveyMasterRespository landParcelSurveyMasterRepository;

	@Autowired
	private UnableToSurveyRepository unableToSurveyRepository;


	public ResponseModel markODStateNAPlot() {
		try {
			String key = "24b2c55f86d6a06f25f8a2f96b8123e9";

			new Thread() {
				public void run() {
                        // check isNA NUll remove isUploaded
					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository
							.findByIsMarkNADoneIsNullAndIsActiveTrueAndIsNaMarkIsTrue();

					System.out.println("RoR NA data Upload Started for count :: " + allVillageLgd.size());

//					List<UPRoRDataUploadLog> tempVillageLgd = allVillageLgd.stream().filter(e-> e.getVillageLgdCode().equals(382973L) && e.getTalukCode().equals(2903)).collect(Collectors.toList());
//						allVillageLgd = new ArrayList<>();
//						allVillageLgd.addAll(tempVillageLgd);
						allVillageLgd.stream().forEach(x -> {
						System.out.println("Initiated for :: " + x.getVillageLgdCode());
						Map<String, Integer> mapResult = updateOdishaNADetail(Integer.valueOf(x.getVillageLgdCode().toString()),Integer.valueOf(x.getTalukCode().toString()), key);
						if(mapResult.get("error") == null) {
							x.setIsMarkNADone(true);
							x.setNaPlotCount(mapResult.get("farmlandPlotRegistryCount"));
							x.setLandOwnershipCount(mapResult.get("landOwnershipCount"));
							x.setUserLandAssignmentCount(mapResult.get("userLandAssignmentCount"));
							x.setSurveyConductCount(mapResult.get("surveyConductedCount"));
							x.setNaPlotMarkOn(new Timestamp(new Date().getTime()));
							roRDataUploadLogRepository.save(x);
						} else {
							x.setNaPlotMarkOn(new Timestamp(new Date().getTime()));
							roRDataUploadLogRepository.save(x);
						}
						System.out.println("Completed for :: " + x.getVillageLgdCode());
					});
					System.out.println("RoR NA data Upload Completed");
				}
			}.start();

			return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}


	/**
	 * update NA Plot Details for odisha
	 *
	 * @param villageLgdCode
	 * @param subDistrictLgdCode
	 * @param key
	 * @return A response model containing the non-agricultural plot details
	 */
	public Map<String, Integer> updateOdishaNADetail(Integer villageLgdCode, Integer subDistrictLgdCode,
													 String key) {
		Map<String, Integer> updateCount = new HashMap<>();
		try {

			RestTemplate restTemplate = new RestTemplate();
			String url = "https://odisha4kgeo.in/index.php/mapview/getVillagePlotsStatusDCS";

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", key);
			headers.add("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("village_code", villageLgdCode.toString());
			map.add("tahasil_code", subDistrictLgdCode.toString());

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
			if (responseEntity != null) {
				List<FarmlandPlotDAO> fprList = new ArrayList<>();
				Gson gson = new Gson();
				ODRoRDataMainDAO[] stateRoRDAOs = gson.fromJson(responseEntity.getBody(), ODRoRDataMainDAO[].class);
				if (stateRoRDAOs != null && stateRoRDAOs.length > 0) {
					List<ODRoRDataMainDAO> platData = Arrays.asList(stateRoRDAOs);
					Set<ODRoRDataMainDAO> naPlotSet = new HashSet<>();

					naPlotSet = platData.stream().filter(ele ->
							ele.getKissamStatus().equals("NA")).collect(Collectors.toSet());

					List<ODRoRDataMainDAO> naPlotList = naPlotSet.stream().collect(Collectors.toList());
					naPlotList.sort(Comparator.comparing(ODRoRDataMainDAO::getSurveyNo));

					// check village and plot available in farm land plot registry
					List<FarmlandPlotDAO> dataList = farmlandPlotRegistryRepository.findPlotRegistryForVillage(Long.valueOf(villageLgdCode));

					naPlotList.stream().forEach(ele -> {
						List<FarmlandPlotDAO> matchedObjList = dataList.stream().filter(farm-> farm.getSurveyNumber().equals(ele.getSurveyNo()) && farm.getSubSurveyNumber().equals(ele.getSubSurveyNo())).collect(Collectors.toList());
						if(!matchedObjList.isEmpty()){
							fprList.addAll(matchedObjList);
						}
					});
					System.out.println("Total Plot registry count: "+ fprList.size());
					if(!fprList.isEmpty()) {
						List<Long> fprPrimaryIds = fprList.stream().map(FarmlandPlotDAO::getFarmlandPlotRegistryId).collect(Collectors.toList());

						// find survey conducted against plots
						List<Long> parcelIds = landParcelSurveyMasterRepository.getParcelIdsFromLandParcelSurveyMaster(fprPrimaryIds);

						// find unable to conduct survey
						List<Long> unableToSurvey = unableToSurveyRepository.getParcelIdsFromUnableTOSurvey(fprPrimaryIds);
						parcelIds.addAll(unableToSurvey);
						updateCount.put("surveyConductedCount", parcelIds.size());

						// filter farmland plot registry list
						List<FarmlandPlotDAO> filterFarmPlotDAOList = fprList.stream().filter(ele -> !parcelIds.contains(ele.getFarmlandPlotRegistryId())).collect(Collectors.toList());

						System.out.println("Plot count with survey: " +parcelIds.size());
						System.out.println("Plot count with out survey: " +filterFarmPlotDAOList.size());

						if(!filterFarmPlotDAOList.isEmpty()) {
							List<Long> farmPlotPrimaryIds = filterFarmPlotDAOList.stream().map(FarmlandPlotDAO::getFarmlandPlotRegistryId).collect(Collectors.toList());
							List<String> farmLandIds = filterFarmPlotDAOList.stream().map(FarmlandPlotDAO::getFarmlandId).collect(Collectors.toList());

							// delete user assignment if any
							int assignmentCount = userLandAssignmentRepository.deleteUserLandAssignmentByVillageAndFarmlandId(Long.valueOf(villageLgdCode), farmLandIds);
							updateCount.put("userLandAssignmentCount", assignmentCount);

							// delete Ownership
							int ownershipCount = farmerLandOwnershipRegistryRepository.deleteAllByFarmlandPlotRegistryId(farmPlotPrimaryIds);
							updateCount.put("landOwnershipCount",ownershipCount);

							// delete farmland plot registry count
							int plotRegistryCount = farmlandPlotRegistryRepository.deleteAllByFarmlandPlotRegistryId(farmPlotPrimaryIds);
							updateCount.put("farmlandPlotRegistryCount", plotRegistryCount);
							updateCount.put("error", null);
						} else {
							updateCount.put("landOwnershipCount",0);
							updateCount.put("userLandAssignmentCount", 0);
							updateCount.put("farmlandPlotRegistryCount", 0);
							updateCount.put("error", null);
						}

					} else {
						updateCount.put("landOwnershipCount",0);
						updateCount.put("userLandAssignmentCount", 0);
						updateCount.put("farmlandPlotRegistryCount", 0);
						updateCount.put("error", null);
					}

				}
			}
		} catch (Exception e) {
			updateCount.put("error", 1);
			e.printStackTrace();
			return updateCount;
		}
		return updateCount;
	}
}
