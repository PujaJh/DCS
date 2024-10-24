package com.amnex.agristack.config;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.amnex.agristack.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.dao.RedisBoundaryDAO;
import com.amnex.agristack.dao.YearDao;
import com.amnex.agristack.entity.ConfigurationMaster;
import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.entity.MenuMaster;
import com.amnex.agristack.entity.RoleMaster;
import com.amnex.agristack.entity.RoleMenuMasterMapping;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.SubDistrictLgdMaster;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.entity.UserVillageMapping;
import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.entity.YearMaster;
import com.amnex.agristack.repository.ConfigurationRepository;
import com.amnex.agristack.repository.CropMasterRepository;
import com.amnex.agristack.repository.CropVarietyRepository;
import com.amnex.agristack.repository.DistrictLgdMasterRepository;
import com.amnex.agristack.repository.MenuMasterRepository;
import com.amnex.agristack.repository.RoleMasterRepository;
import com.amnex.agristack.repository.RoleMenuMasterMappingRepository;
import com.amnex.agristack.repository.StateLgdMasterRepository;
import com.amnex.agristack.repository.SubDistrictLgdMasterRepository;
import com.amnex.agristack.repository.UserMasterRepository;
import com.amnex.agristack.repository.UserVillageMappingRepository;
import com.amnex.agristack.repository.VillageLgdMasterRepository;
import com.amnex.agristack.repository.YearRepository;
import com.amnex.agristack.utils.Constants;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DefaultCall implements ApplicationRunner {

	@Autowired
	private RoleMasterRepository roleMasterRepository;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;
	@Autowired
	private UserMasterRepository userMasterRepository;
	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;
	@Autowired
	private MenuMasterRepository menuMasterRepository;
	@Autowired
	private CropVarietyRepository cropVarietyRepository;
	@Autowired
	private CropMasterRepository cropMasterRepository;
	@Autowired
	private RoleMenuMasterMappingRepository roleMenuMasterMappingRepository;

	
	@Autowired
	private ConfigurationRepository configurationRepository;

	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private StateLgdMasterRepository stateLgdMasterRepository;
	
	@Autowired
	private DistrictLgdMasterRepository districtLgdMasterRepository;
	
	@Autowired
	private SubDistrictLgdMasterRepository subDistrictLgdMasterRepository;
	
	@Autowired
	private YearRepository yearRepository;


	@Override
	public void run(ApplicationArguments args) throws Exception {

		try {
			boundryRedisDataStoreCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		roleMasterCall();

	}

	void roleMenuMasterCall() {
		List<RoleMaster> roleList=roleMasterRepository.findByIsDeleted(false);
		
		if(roleList!=null && roleList.size()>0) {
			
			List<RoleMenuMasterMapping> finalList=new ArrayList<RoleMenuMasterMapping>();
			roleList.forEach(action->{
				if(action.getMenu()!=null) {
						
					action.getMenu().forEach(action2->{
						
						RoleMenuMasterMapping roleMenuMasterMapping=new RoleMenuMasterMapping();
						roleMenuMasterMapping.setMenu(action2);
						roleMenuMasterMapping.setRole(action);
						roleMenuMasterMapping.setIsActive(true);
						roleMenuMasterMapping.setIsDeleted(false);
						roleMenuMasterMapping.setCreatedOn(new Timestamp(new Date().getTime()));
						roleMenuMasterMapping.setModifiedOn(new Timestamp(new Date().getTime()));
						roleMenuMasterMapping.setIsAdd(true);
						roleMenuMasterMapping.setIsEdit(true);
						roleMenuMasterMapping.setIsView(true);
						roleMenuMasterMapping.setIsDelete(true);
						finalList.add(roleMenuMasterMapping);
						System.out.println("/"+roleList.size());
					
					});

				}
			});
			if(finalList!=null && finalList.size()>0) {
				roleMenuMasterMappingRepository.saveAll(finalList);
				System.out.println("done");
			}
		}
	}
	void uploadAllVillagetoUser() {
		Optional<UserMaster> op = userMasterRepository.findByRoleId_CodeAndIsDeletedAndIsActive("CENTRALADMIN", false, true);
		if (op.isPresent()) {
			UserMaster user = op.get();
			List<VillageLgdMaster> villageMasterList = villageLgdMasterRepository.findAll();
			if (villageMasterList != null && villageMasterList.size() > 0) {

				System.out.println("in IF OP");
				
				villageMasterList.forEach(action -> {
					System.out.println("LGD " + action.getVillageLgdCode());
					UserVillageMapping data = new UserVillageMapping();
					data.setUserMaster(user);
					data.setVillageLgdMaster(action);
					data.setIsActive(true);
					data.setIsDeleted(false);
					userVillageMappingRepository.save(data);
				});
			}

		}
	}

	void userCall() {
		RoleMaster role = roleMasterRepository.findByIsDefaultAndRoleName(true, "SuperAdmin");
		if (role != null) {
			Optional<UserMaster> op = userMasterRepository.findByUserNameAndIsDeletedAndIsActive("SuperAdmin", false,
					true);
			if (!op.isPresent()) {
				UserMaster user = new UserMaster();
				user.setUserName("SuperAdmin");
				user.setRoleId(role);
				user.setUserEmailAddress("admin@gmail.com");
				user.setUserPassword("$2a$10$SaxqH9/YdXVcVrprpmn9ueb5X5wLITZbni0iRD.UpVELRcTV/jyVe");
				user.setUserMobileNumber("+917788445577");
				userMasterRepository.save(user);

				Set<MenuMaster> menuList = menuMasterRepository.findByIsDeletedFalseAndIsActiveTrue();
				if (menuList != null && menuList.size() > 0) {
					role.setMenu(menuList);
					roleMasterRepository.save(role);
					
					if(role!=null && role.getMenu().size()>0) {
						
						List<RoleMenuMasterMapping> finalList=new ArrayList<RoleMenuMasterMapping>();
				
								role.getMenu().forEach(action2->{
									
									RoleMenuMasterMapping roleMenuMasterMapping=new RoleMenuMasterMapping();
									roleMenuMasterMapping.setMenu(action2);
									roleMenuMasterMapping.setRole(role);
									roleMenuMasterMapping.setIsActive(true);
									roleMenuMasterMapping.setIsDeleted(false);
									roleMenuMasterMapping.setCreatedOn(new Timestamp(new Date().getTime()));
									roleMenuMasterMapping.setModifiedOn(new Timestamp(new Date().getTime()));
									roleMenuMasterMapping.setIsAdd(true);
									roleMenuMasterMapping.setIsEdit(true);
									roleMenuMasterMapping.setIsView(true);
									roleMenuMasterMapping.setIsDelete(true);
									finalList.add(roleMenuMasterMapping);
								
								});						
						if(finalList!=null && finalList.size()>0) {
							roleMenuMasterMappingRepository.saveAll(finalList);
							System.out.println("done");
						}
					}
					
				}

				List<VillageLgdMaster> villageMasterList = villageLgdMasterRepository.findAll();
				if (villageMasterList != null && villageMasterList.size() > 0) {
					if (user != null) {
						System.out.println("in IF OP");

						villageMasterList.forEach(action -> {
							System.out.println("LGD " + action.getVillageLgdCode());
							UserVillageMapping data = new UserVillageMapping();
							data.setUserMaster(user);
							data.setVillageLgdMaster(action);
							data.setIsActive(true);
							data.setIsDeleted(false);
							userVillageMappingRepository.save(data);
						});
					}

				}

			}

		}

	}

	void roleMasterCall() {
		List<String> roleNames = Arrays.asList("SuperAdmin", "Surveyor", "Verifier", "StateAdmin","Farmer","CR_State_Admin","Guest");

		List<RoleMaster> roles = roleMasterRepository.findByIsDefaultAndRoleNameInIgnoreCase(true, roleNames);
		List<RoleMaster> saveRoleData = new ArrayList<>();
		if (roles != null && roles.size() > 0) {
			List<String> roleCodes = roles.stream().map(data -> data.getRoleName()).collect(Collectors.toList());

			ArrayList<String> uniques = new ArrayList<String>(roleNames);
			uniques.removeAll(roleCodes);

			if (uniques != null && uniques.size() > 0) {
				uniques.forEach(action -> {

					saveRoleData.add(CustomMessages.addRoleMasterDefault(action, action.toUpperCase()));
				});
				roleMasterRepository.saveAll(saveRoleData);
			}

		} else {
			Long i = 1l;

			roleNames.forEach(action -> {

				saveRoleData.add(CustomMessages.addRoleMasterDefault(i + 1l, action, action.toUpperCase()));
			});
			roleMasterRepository.saveAll(saveRoleData);
		}
		userCall();
	}
	
	public void boundryRedisDataStoreCall() {
		
			new Thread() {
				@Override
				public void run() {
					try {			
	
							String configKey = ConfigCode.STATE_LGD_CODE.name();
							Optional<ConfigurationMaster> configKeyValue = configurationRepository.findByConfigKey(configKey);
							System.out.println("=============Start Redis state===================="+new Date());
							setStateDataToRedis(configKeyValue);
							
							System.out.println("=============Redis district===================="+new Date());
							List<Long> districtLgdCode = setDistrictDataToRedis(configKeyValue);
							
							System.out.println("=============Redis sub district===================="+new Date());
							List<Long> subDistrictLgdCode = setSubDistrictDataToRedis(districtLgdCode);
				
							System.out.println("=============Redis village===================="+new Date());
							setVillageDataToRedis(subDistrictLgdCode);
							System.out.println("=============End Redis===================="+new Date());
					
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		}.start();



	}
	
	private void setStateDataToRedis(Optional<ConfigurationMaster> configKeyValue) throws Exception {

		String stateKey = Constants.STATE_LGD_MASTER;
		String stateData = (String) redisService.getValue(stateKey);
		

		if (stateData == null) {
			
			if (configKeyValue.isPresent() && !configKeyValue.get().getConfigValue().equals("0")) {
				List<Long> stateCodeList = new ArrayList<Long>();
				stateCodeList.add(Long.valueOf(configKeyValue.get().getConfigValue()));

				List<StateLgdMaster> stateList = stateLgdMasterRepository.findAllByStateLgdCodeIn(stateCodeList);

				List<RedisBoundaryDAO> stateRedisList = new ArrayList<>();
//				.parallelStream()
				stateList.forEach((state) -> {

					RedisBoundaryDAO stateRedisDao = new RedisBoundaryDAO();
					stateRedisDao.setStateId(state.getStateId());
					stateRedisDao.setStateName(state.getStateName());
					stateRedisDao.setStateLgdCode(state.getStateLgdCode());

					stateRedisList.add(stateRedisDao);

				});

				if (stateRedisList != null&& stateRedisList.size()>0) {
					stateRedisList.removeAll(Collections.singleton(null));
					try {
						ObjectMapper objectMapper = new ObjectMapper();
						String stateCode = objectMapper.writeValueAsString(stateRedisList);
						redisService.setValue(stateKey, stateCode);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
		}
	}
	
	
	private List<Long> setDistrictDataToRedis(Optional<ConfigurationMaster> configKeyValue) throws Exception {

		
		List<Long> districtLgdCodes = new ArrayList<Long>();
		String districtData = (String) redisService.getValue(Constants.DISTRICT_LGD_MASTER);
		if (districtData == null) {
		ObjectMapper objectMapper = new ObjectMapper();

		if (configKeyValue.isPresent() && !configKeyValue.get().getConfigValue().equals("0")) {
			List<Long> stateCodeList = new ArrayList<Long>();
			stateCodeList.add(Long.valueOf(configKeyValue.get().getConfigValue()));
			
			List<DistrictLgdMaster> districtList = districtLgdMasterRepository.findAllByStateLgdCode(stateCodeList);

			List<RedisBoundaryDAO> districtRedisList = new ArrayList<RedisBoundaryDAO>();
//			.parallelStream()
			districtList.removeAll(Collections.singleton(null));
			districtList.forEach((district) -> {

				RedisBoundaryDAO boundaryDAO = new RedisBoundaryDAO();
				boundaryDAO.setDistrictId(district.getDistrictId());
				boundaryDAO.setDistrictName(district.getDistrictName());
				boundaryDAO.setDistrictLgdCode(district.getDistrictLgdCode());
				boundaryDAO.setStateLgdCode(district.getStateLgdCode().getStateLgdCode());

				districtRedisList.add(boundaryDAO);

				
				districtLgdCodes.add(district.getDistrictLgdCode());
			});

			if (districtRedisList != null) {
				try {
					String districtCode = objectMapper.writeValueAsString(districtRedisList);
					redisService.setValue(Constants.DISTRICT_LGD_MASTER, districtCode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		}
		return districtLgdCodes;
	}
	
	private List<Long> setSubDistrictDataToRedis(List<Long> districtLgdCode) throws Exception {
		
		String subDistrictData = (String) redisService.getValue(Constants.SUBDISTRICT_LGD_MASTER);
		List<Long> subDistrictCodes = new ArrayList<Long>();
		if (subDistrictData == null) {
		
				ObjectMapper objectMapper = new ObjectMapper();
		
				
				if (districtLgdCode != null && districtLgdCode.size() > 0) {
//					parallelStream().
					districtLgdCode.forEach((districtCode) -> {
		
						if (districtCode != null && districtCode > 0) {
		
		
							List<SubDistrictLgdMaster> subDistrictList = subDistrictLgdMasterRepository
									.findByDistrictLgdCode_DistrictLgdCode(districtCode);
		
							List<RedisBoundaryDAO> boundaryDAOs = new ArrayList<RedisBoundaryDAO>();
//							.parallelStream()
							subDistrictList.removeAll(Collections.singleton(null));
							subDistrictList.forEach((subDistrict) -> {
		
								RedisBoundaryDAO boundaryDAO = new RedisBoundaryDAO();
								boundaryDAO.setSubDistrictId(subDistrict.getSubDistrictId());
								boundaryDAO.setSubDistrictName(subDistrict.getSubDistrictName());
								boundaryDAO.setSubDistrictLgdCode(subDistrict.getSubDistrictLgdCode());
								boundaryDAO.setDistrictLgdCode(subDistrict.getDistrictLgdCode().getDistrictLgdCode());
								boundaryDAO.setStateLgdCode(subDistrict.getStateLgdCode().getStateLgdCode());
		
								boundaryDAOs.add(boundaryDAO);
		
								subDistrictCodes.add(subDistrict.getSubDistrictLgdCode());
		
							});
		
							if (boundaryDAOs != null) {
								try {
									String subDistrictCode = objectMapper.writeValueAsString(boundaryDAOs);
									redisService.setValue(Constants.SUBDISTRICT_LGD_MASTER + "_" + districtCode,
											subDistrictCode);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
		
						}
		
					});
				}
		}

		return subDistrictCodes;
	}


	
	private void setVillageDataToRedis(List<Long> subDistrictLgdCodes) throws Exception {
		
		String villageData = (String) redisService.getValue(Constants.VILLAGE_LGD_MASTER);
		
		if(villageData==null) {
			
		
		
				ObjectMapper objectMapper = new ObjectMapper();
				if (subDistrictLgdCodes != null && subDistrictLgdCodes.size() > 0) {
//					.parallelStream()
					subDistrictLgdCodes.forEach((subDistrictCode) -> {
		
						
		
						List<VillageLgdMaster> villageList = villageLgdMasterRepository.findBySubDistrictLgdCode_SubDistrictLgdCode(subDistrictCode);

							
						List<RedisBoundaryDAO> boundaryDAOs = new ArrayList<RedisBoundaryDAO>();
						villageList.removeAll(Collections.singleton(null));
//						parallelStream().
						villageList.forEach((village) -> {
		
							RedisBoundaryDAO boundaryDAO = new RedisBoundaryDAO();
							boundaryDAO.setVillageId(village.getVillageId());
							boundaryDAO.setVillageName(village.getVillageName());
							boundaryDAO.setVillageLgdCode(village.getVillageLgdCode());
							boundaryDAO.setSubDistrictLgdCode(village.getSubDistrictLgdCode().getSubDistrictLgdCode());
							boundaryDAO.setDistrictLgdCode(village.getDistrictLgdCode().getDistrictLgdCode());
							boundaryDAO.setStateLgdCode(village.getStateLgdCode().getStateLgdCode());
		
							boundaryDAOs.add(boundaryDAO);
		
						});
		
						if (boundaryDAOs != null) {
							try {
								String villageCode = objectMapper.writeValueAsString(boundaryDAOs);
								redisService.setValue(Constants.VILLAGE_LGD_MASTER + "_" + subDistrictCode, villageCode);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
		
					});
		
				}
		}

	}


}
