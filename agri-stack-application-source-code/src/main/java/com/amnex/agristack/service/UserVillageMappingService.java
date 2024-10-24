package com.amnex.agristack.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.TerritoriesDto;
import com.amnex.agristack.dao.UserDistrictDAO;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.dao.UserStateDAO;
import com.amnex.agristack.dao.UserSubDistrictDAO;
import com.amnex.agristack.dao.UserVillageDAO;
import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.SubDistrictLgdMaster;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.entity.UserVillageMapping;
import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.repository.SubDistrictLgdMasterRepository;
import com.amnex.agristack.repository.UserMasterRepository;
import com.amnex.agristack.repository.UserVillageMappingRepository;
import com.amnex.agristack.repository.VillageLgdMasterRepository;
import com.amnex.agristack.utils.Constants;
import com.amnex.agristack.utils.CustomMessages;

@Service
public class UserVillageMappingService {

	@Autowired
	UserService userService;

	@Autowired
	UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	SubDistrictLgdMasterRepository subDistrictLgdMasterRepository;

	@Autowired
	UserDefaultLanguageMappingService userDefaultLanMapService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${app.redis.name}")
	private String redisAppName;
	@Autowired
	private RedisService redisService;
	@Autowired
	private EntityManager entityManager;
	public void addUserVillage(UserMaster user, UserInputDAO userInputDAO) {
		List<UserVillageDAO> totalVillageDAO = new ArrayList<>();
		List<UserStateDAO> stateDAOList = userService.getuserStateList(userInputDAO.getGeographicalArea());
		stateDAOList.forEach(state -> {
			List<UserDistrictDAO> userDistrictDAOList = state.getDistrict();
			userDistrictDAOList.forEach(district -> {
				List<UserSubDistrictDAO> userSubDistrictDAOList = district.getSubDistrict();
				userSubDistrictDAOList.forEach(subDistrict -> {
					totalVillageDAO.addAll(subDistrict.getVillage());
				});

			});

		});
		List<UserVillageMapping> list = new ArrayList<>();
		totalVillageDAO.forEach(village -> {
			UserVillageMapping data = new UserVillageMapping();
			data.setUserMaster(user);
			data.setVillageLgdMaster(villageLgdMasterRepository.findByVillageLgdCode(village.getVillageLgdCode()));
			data.setIsActive(true);
			data.setIsDeleted(false);
			list.add(data);
		});
		userVillageMappingRepository.saveAll(list);
		Runnable r = () -> {
			userDefaultLanMapService.setDefaultLanguageByUserId(user);
		};
		Thread defaultLanguage = new Thread(r);
		defaultLanguage.start();
	}

	public ResponseModel getAssignedGeometryByUserId(Long userId) {
		try {
//			old code start
//			List<UserVillageMapping> userVillageList = userVillageMappingRepository
//					.findByUserMaster(userMasterRepository.findByUserId(userId).get());
//			Set<Long> stateSet = new HashSet<>();
//			Set<Long> subDistrictSet = new HashSet<>();
//			Set<Long> districtSet = new HashSet<>();
//			List<Long> villageList = new ArrayList<>();
//			userVillageList.forEach(userVillage -> {
//				VillageLgdMaster villageMaster = userVillage.getVillageLgdMaster();
//				stateSet.add(villageMaster.getStateLgdCode().getStateLgdCode());
//				districtSet.add(villageMaster.getDistrictLgdCode().getDistrictLgdCode());
//				subDistrictSet.add(villageMaster.getSubDistrictLgdCode().getSubDistrictLgdCode());
//				villageList.add(villageMaster.getVillageLgdCode());
//			});
//			List<Long> stateList = new ArrayList<>();
//			List<Long> subDistrictList = new ArrayList<>();
//			List<Long> districtList = new ArrayList<>();
//			stateList.addAll(stateSet);
//			subDistrictList.addAll(subDistrictSet);
//			districtList.addAll(districtSet);
//			old code end
			
			
			
			Map<String, List<Long>> map = new HashMap<String, List<Long>>();
//			Start Redis implementation
			Object states=redisService.getValue(redisAppName+"_"+Constants.REDIS_ASSIGN_STATE+userId);
			if(states!=null) {
				map.put("state", Arrays.asList(((String) states).split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()));
			}else {
				map.put("state", userVillageMappingRepository.getStateCodesById(userId));	
			}
			
			Object districts=redisService.getValue(redisAppName+"_"+Constants.REDIS_ASSIGN_DISTRICT+userId);
			if(districts!=null) {
				 
				map.put("district", Arrays.asList(((String) districts).split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()));
			}else {
				map.put("district", userVillageMappingRepository.getDistrictCodesById(userId));	
			}
			
			Object subDistricts=redisService.getValue(redisAppName+"_"+Constants.REDIS_ASSIGN_SUBDISTRICT+userId);
			if(subDistricts!=null) {
				
				map.put("subDistrict", Arrays.asList(((String) subDistricts).split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()));
			}else {
				map.put("subDistrict", userVillageMappingRepository.getSubDistrictCodesById(userId));	
			}
			
			Object villages=redisService.getValue(redisAppName+"_"+Constants.REDIS_ASSIGN_VILLAGE+userId);
			if(villages!=null) {
				 
				map.put("village", Arrays.asList(((String) villages).split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList()));
			}else {
				map.put("village", userVillageMappingRepository.getVillageCodesByUserId(userId));	
			}
			
//			End Redis implementation			
			
			return new ResponseModel(map, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	public ResponseModel getAssignedTreeGeometryByUserId(Long userId) {
		// TODO Auto-generated method stub
		try {
			
			List<Long> villageCodes=userVillageMappingRepository.getVillageCodesByUserId(userId);

			List<VillageLgdMaster> villageList = new ArrayList<>();
			if (villageCodes.size() < 2100) {
				 villageList=villageLgdMasterRepository.findByVillageLgdCodeIn(villageCodes);
			} else {
				Integer parts = 2100;
				Integer startIndex = 0;
				Integer totalSize = villageCodes.size();

				while (startIndex < totalSize) {
					List<Long> idList = new ArrayList<>();

					if ((startIndex + parts) < totalSize) {
						idList = villageCodes.subList(startIndex, (startIndex + parts));
					} else {
						idList = villageCodes.subList(startIndex, totalSize);
					}
					villageList
							.addAll(villageLgdMasterRepository.findByVillageLgdCodeIn(idList));

					startIndex += parts;
				}

			}
//			List<VillageLgdMaster> villageList=villageLgdMasterRepository.findByVillageLgdCodeIn(villageCodes);
//			List<UserVillageMapping> userVillageList = userVillageMappingRepository.findByUserMaster_UserId(userId);
			List<UserStateDAO> userStateDaoList = new ArrayList<UserStateDAO>();
			if (villageList != null && villageList.size() > 0) {
				Set<StateLgdMaster> stateLgdCodes = villageList.stream()
						.map(x -> x.getStateLgdCode()).collect(Collectors.toSet());
				Set<DistrictLgdMaster> districtLgdCodes = villageList.stream()
						.map(x -> x.getDistrictLgdCode()).collect(Collectors.toSet());
				Set<SubDistrictLgdMaster> subDistrictLgdCodes = villageList.stream()
						.map(x -> x.getSubDistrictLgdCode()).collect(Collectors.toSet());
				Set<VillageLgdMaster> villageLgdCodes = villageList.stream().map(x -> x)
						.collect(Collectors.toSet());

				List<StateLgdMaster> sortedStateLgdCodesList = stateLgdCodes.stream()
						.sorted(Comparator.comparing(StateLgdMaster::getStateName))
						.collect(Collectors.toList());
				List<DistrictLgdMaster> sortedDistrictLgdCodesList = districtLgdCodes.stream()
						.sorted(Comparator.comparing(DistrictLgdMaster::getDistrictName))
						.collect(Collectors.toList());
				List<SubDistrictLgdMaster> sortedSubDistrictLgdCodesList = subDistrictLgdCodes.stream()
						.sorted(Comparator.comparing(SubDistrictLgdMaster::getSubDistrictName))
						.collect(Collectors.toList());

				List<VillageLgdMaster> sortedVillageLgdCodesList = villageLgdCodes.stream()
						.sorted(Comparator.comparing(VillageLgdMaster::getVillageName))
						.collect(Collectors.toList());

				if (stateLgdCodes != null && stateLgdCodes.size() > 0) {
					sortedStateLgdCodesList.forEach(state -> {
						UserStateDAO userStateDao = new UserStateDAO();
						userStateDao.setStateLgdCode(state.getStateLgdCode());
						userStateDao.setMenuId(state.getStateLgdCode());
						userStateDao.setLabel(state.getStateName());
						// List<UserSubDistrictDAO> tempSubDistrictList = new
						// ArrayList<UserSubDistrictDAO>();
						List<UserDistrictDAO> tempdistrictList = new ArrayList<UserDistrictDAO>();
						sortedDistrictLgdCodesList.forEach(district -> {
							if (district.getStateLgdCode().getStateLgdCode().equals(state.getStateLgdCode())) {
								UserDistrictDAO userDistrictDAO = new UserDistrictDAO();
								userDistrictDAO.setLabel(district.getDistrictName());
								userDistrictDAO.setMenuId(district.getDistrictLgdCode());
								List<UserSubDistrictDAO> tempSubDistrictList = new ArrayList<UserSubDistrictDAO>();
								sortedSubDistrictLgdCodesList.forEach(subdistrict -> {
									if (subdistrict.getDistrictLgdCode().getDistrictLgdCode()
											.equals(district.getDistrictLgdCode())) {
										UserSubDistrictDAO userSubDistrictDAO = new UserSubDistrictDAO();
										userSubDistrictDAO.setMenuId(subdistrict.getSubDistrictLgdCode());
										userSubDistrictDAO.setLabel(subdistrict.getSubDistrictName());
										List<UserVillageDAO> tempVillageList = new ArrayList<UserVillageDAO>();

										sortedVillageLgdCodesList.forEach(village -> {
											if (village.getSubDistrictLgdCode().getSubDistrictLgdCode()
													.equals(subdistrict.getSubDistrictLgdCode())) {

												UserVillageDAO userVillageDAO = new UserVillageDAO();
												userVillageDAO.setMenuId(village.getVillageLgdCode());
												userVillageDAO.setLabel(village.getVillageName());
												userVillageDAO
														.setStateLgdCode(village.getStateLgdCode().getStateLgdCode());
												userVillageDAO.setDistrictLgdCode(
														village.getDistrictLgdCode().getDistrictLgdCode());
												userVillageDAO.setSubDistrictLgdCode(
														village.getSubDistrictLgdCode().getSubDistrictLgdCode());
												userVillageDAO.setVillageLgdCode(village.getVillageLgdCode());
												tempVillageList.add(userVillageDAO);
											}
										});
										userSubDistrictDAO.setChildren(tempVillageList);
										userSubDistrictDAO.setLeaf(false);
										userSubDistrictDAO
												.setStateLgdCode(subdistrict.getStateLgdCode().getStateLgdCode());
										userSubDistrictDAO.setDistrictLgdCode(
												subdistrict.getDistrictLgdCode().getDistrictLgdCode());
										userSubDistrictDAO.setSubDistrictLgdCode(subdistrict.getSubDistrictLgdCode());
										tempSubDistrictList.add(userSubDistrictDAO);
									}
								});
								userDistrictDAO.setLeaf(false);
								userDistrictDAO.setChildren(tempSubDistrictList);
								userDistrictDAO.setStateLgdCode(district.getStateLgdCode().getStateLgdCode());
								userDistrictDAO.setDistrictLgdCode(district.getDistrictLgdCode());
								tempdistrictList.add(userDistrictDAO);
							}
						});
						userStateDao.setLeaf(false);
						userStateDao.setChildren(tempdistrictList);
						userStateDaoList.add(userStateDao);
					});
				}
			}
			return new ResponseModel(userStateDaoList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Retrieves territories by territory level and user.
	 *
	 * @param territoryLevel The territory level parameter.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object containing the territories information.
	 */
	public List<TerritoriesDto> getTerritoriesByTerritoryLevelAndUser(String territoryLevel,
			HttpServletRequest request) {
		String userId = CustomMessages.getUserId(request, jwtTokenUtil);
		List<Object[]> territories = userVillageMappingRepository.getTerritoriesByTerritoryLevelAndUser(territoryLevel.toUpperCase(),
				Long.valueOf(userId));
		List<TerritoriesDto> territoriesDtos = new ArrayList<>();
		for (Object[] territory : territories) {
			territoriesDtos.add(new TerritoriesDto(Long.valueOf(territory[0].toString()),
					territory[1].toString()));
		}
		return territoriesDtos;
	}
}
