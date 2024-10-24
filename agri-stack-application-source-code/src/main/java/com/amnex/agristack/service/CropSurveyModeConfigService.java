package com.amnex.agristack.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.repository.UserLandAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.Enum.CropSurveyModeEnum;
import com.amnex.agristack.dao.CropSurveyConfigModeRequestDTO;
import com.amnex.agristack.dao.CropSurveyModeConfigDao;
import com.amnex.agristack.dao.SurveyorInputDAO;
import com.amnex.agristack.entity.CropSurveyModeConfig;
import com.amnex.agristack.entity.CropSurveyModeMaster;
import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.repository.CropSurveyModeConfigRepository;
import com.amnex.agristack.repository.CropSurveyModeMasterRepository;
import com.amnex.agristack.repository.VillageLgdMasterRepository;
import com.amnex.agristack.utils.CustomMessages;

@Service
public class CropSurveyModeConfigService {

	@Autowired
	CropSurveyModeConfigRepository cropSurveyModeConfigRepository;

	@Autowired
	CropSurveyModeMasterRepository cropSurveyModeMasterRepository;

	@Autowired
	VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@Autowired
	UserLandAssignmentRepository userLandAssignmentRepository;

	/**
     * Updates the Crop Survey Mode configuration.
     *
     * @param payload the {@code CropSurveyConfigModeRequestDTO} object containing the updated configuration
     * @return a {@code CropSurveyModeConfig}
     */
	public List<CropSurveyModeConfig> updateCropSurveyModeConfig(CropSurveyConfigModeRequestDTO payload) {
		String territoryLevel = payload.getTerritoryLevel();
		List<VillageLgdMaster> villageList = new ArrayList<>();
		CropSurveyModeMaster mode = cropSurveyModeMasterRepository.findById(payload.getMode()).get();
		List<CropSurveyModeConfig> existing = new ArrayList<>();

		switch (territoryLevel.toLowerCase()) {
			case "state":
				villageList = villageLgdMasterRepository.findByStateLgdCode_StateLgdCodeIn(payload.getTerritoryCodes());
				existing = cropSurveyModeConfigRepository.findAllByStateLgdCodeIn(payload.getTerritoryCodes());
				break;
			case "district":
				villageList = villageLgdMasterRepository
						.findByDistrictLgdCode_DistrictLgdCodeIn(payload.getTerritoryCodes());
				existing = cropSurveyModeConfigRepository.findAllByDistrictLgdCodeIn(payload.getTerritoryCodes());
				break;
			case "subdistrict":
				villageList = villageLgdMasterRepository
						.findBySubDistrictLgdCode_SubDistrictLgdCodeIn(payload.getTerritoryCodes());
				existing = cropSurveyModeConfigRepository.findAllBySubDistrictLgdCodeIn(payload.getTerritoryCodes());
				break;
			case "village":
				villageList = villageLgdMasterRepository.findByVillageLgdCodeIn(payload.getTerritoryCodes());
				existing = cropSurveyModeConfigRepository.findAllByVillageLgdCodeIn(payload.getTerritoryCodes());
				break;
			default:
				break;
		}
		List<CropSurveyModeConfig> configs = new ArrayList<>();
		List<Long> existingVillageCode = new ArrayList<>();
		if (existing != null) {
			existing.parallelStream().forEach(config -> {
				config.setMode(mode);
				configs.add(config);
			});
			existingVillageCode.addAll(existing.stream().map((existingConfig) -> existingConfig.getVillageLgdCode())
					.collect(Collectors.toList()));
		}

		villageList.parallelStream().forEach((code) -> {
			if (!existingVillageCode.contains(code.getVillageLgdCode())) {
				CropSurveyModeConfig config = new CropSurveyModeConfig();
				config.setStateLgdCode(code.getStateLgdCode().getStateLgdCode());
				config.setDistrictLgdCode(code.getDistrictLgdCode().getDistrictLgdCode());
				config.setSubDistrictLgdCode(code.getSubDistrictLgdCode().getSubDistrictLgdCode());
				config.setVillageLgdCode(code.getVillageLgdCode());
				config.setMode(mode);
				configs.add(config);
			}
		});
		// configs.remove(null);
		cropSurveyModeConfigRepository.saveAll(configs);
		return configs;
	}

	/**
     * Retrieves the Crop Survey Mode configuration.
     *
     * @param payload the {@code CropSurveyConfigModeRequestDTO} object containing the request parameters
     * @param request the HttpServletRequest object representing the incoming request
     * @return a ResponseModel containing the Crop Survey Mode configuration {@link CropSurveyModeConfigDao}
     */
	public List<CropSurveyModeConfigDao> getConfig(CropSurveyConfigModeRequestDTO payload, HttpServletRequest request) {
		String userId = CustomMessages.getUserId(request, jwtTokenUtil);
		List<Object[]> objects = cropSurveyModeConfigRepository.getConfigs(Long.parseLong(userId));
		List<CropSurveyModeConfigDao> configs = new ArrayList<>();
		for (Object[] obj : objects) {
			CropSurveyModeConfigDao config = new CropSurveyModeConfigDao(Long.valueOf(obj[0].toString()),
					Long.valueOf(obj[1].toString()), Long.valueOf(obj[2].toString()), Long.valueOf(obj[3].toString()),
					Long.valueOf(obj[4].toString()), Long.valueOf(obj[5].toString()), (String) obj[6], (String) obj[7],
					(String) obj[8], (String) obj[9], (String) obj[10]);
			configs.add(config);
		}
		return configs;
	}

	/**
     * Retrieves the Crop Survey Mode configuration by surveyor.
     *
     * @param surveyorInput the {@code SurveyorInputDAO} object containing the surveyor details
     * @return a ResponseModel {@code CropSurveyModeConfigDao} containing the Crop Survey Mode configuration for the specified surveyor
     */
	public List<CropSurveyModeConfigDao> getCropSurveyConfigBySurveyor(SurveyorInputDAO input) {

		List<Long> villageCodes = userLandAssignmentRepository
				.getDistinctVillageLgdCodeByUserIdAndSeasonIdAndStartingYearAndEndingYear(input.getUserId(),
						input.getSeasonId(), Integer.valueOf(input.getStartingYear()),
						Integer.valueOf(input.getEndingYear()));

		List<CropSurveyModeConfig> configs = cropSurveyModeConfigRepository.findAllByVillageLgdCodeIn(villageCodes);

		List<CropSurveyModeConfigDao> finalConfig = new ArrayList<>();

		villageCodes.forEach((villageLgdCode) -> {

			CropSurveyModeConfig filteredConfig = new CropSurveyModeConfig();
			Boolean isFlexible = Boolean.FALSE;

			if (!configs.isEmpty()) {
				List<CropSurveyModeConfig> filteredConfigList = configs.stream()
						.filter((c) -> c.getVillageLgdCode().equals(villageLgdCode)).collect(Collectors.toList());

				if (!filteredConfigList.isEmpty()) {
					filteredConfig = filteredConfigList.get(0);
					isFlexible = Long.valueOf(CropSurveyModeEnum.FLEXIBLE.getValue()).equals(filteredConfig.getMode().getId())
							? Boolean.TRUE
							: Boolean.FALSE;
				}
			}

			CropSurveyModeConfigDao config = new CropSurveyModeConfigDao(villageLgdCode, isFlexible);
			finalConfig.add(config);

		});
		return finalConfig;
	}
}