package com.amnex.agristack.service;

import java.util.Collections;
import java.util.List;

import com.amnex.agristack.dao.UserDefaultLanguageMappingDTO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.UserDefaultLanguageMapping;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.utils.CustomMessages;

@Service
public class UserDefaultLanguageMappingService {

	@Autowired
	UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	UserDefaultLanguageMappingRepository userDefLanMapRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	LanguageMasterRepository languageMasterRepository;

	/**
	 * Adds default language for a user.
	 *
	 * @param userDeLanMapDTO The UserDefaultLanguageMappingDTO containing the user and language information.
	 * @return The ResponseModel indicating the status of the operation.
	 */
	public ResponseModel addDefaultLanguage(UserDefaultLanguageMappingDTO userDeLanMapDTO) {

		List<Long> userList = userVillageMappingRepository.findUniqueUsersByClientId(userDeLanMapDTO.getUserId());
		userList.removeAll(Collections.singleton(null));
		System.out.println(userList.contains(userDeLanMapDTO.getUserId()));
		userList.forEach(userId -> {
			if(userId.equals(userDeLanMapDTO.getUserId())) {
				System.out.println("test");
			}
			UserMaster userMaster = userMasterRepository.findByUserId(userId).get();
			List<UserDefaultLanguageMapping> existingMapping = userDefLanMapRepository.findByUserMasterUserId(userMaster.getUserId());
			if (!CollectionUtils.isEmpty(existingMapping)) {
				for (UserDefaultLanguageMapping userDefaultLanguageMapping : existingMapping) {
					userDefaultLanguageMapping.setLanguageId(
							languageMasterRepository.findById(userDeLanMapDTO.getLanguageMasterId()).get());
					userDefaultLanguageMapping.setModifiedBy(userDeLanMapDTO.getUserId().toString());
					userDefLanMapRepository.save(userDefaultLanguageMapping);
				}

			} else {
				UserDefaultLanguageMapping input = new UserDefaultLanguageMapping();
				input.setCreatedBy(userDeLanMapDTO.getUserId().toString());
				input.setLanguageId(languageMasterRepository.findById(userDeLanMapDTO.getLanguageMasterId()).get());
				input.setUserMaster(userMaster);
				input.setIsActive(true);
				input.setIsDeleted(false);
				userDefLanMapRepository.save(input);
			}
		});

		return new ResponseModel(null, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

	}

	/**
	 * Retrieves the default language ID for a user.
	 *
	 * @param userId The ID of the user.
	 * @return The default language ID of the user, or null if not found.
	 */
	public Long getDefaultLanguageByUserId(Long userId) {

		List<UserDefaultLanguageMapping> result = userDefLanMapRepository
				.findByUserMasterUserId(userId);
		if (!CollectionUtils.isEmpty(result)) {
			return result.get(0).getLanguageId().getLanguageId();
		}
		return null;
	}

	/**
	 * Sets the default language for a user.
	 *
	 * @param user The UserMaster object representing the user.
	 */
	public void setDefaultLanguageByUserId(UserMaster user) {
		Long languageId = userDefLanMapRepository.findLangugaeIdByUserMaster(user.getUserId());
		UserDefaultLanguageMapping userDefaultLanguageMapping = userDefLanMapRepository.findByUserMasterUserId(user.getUserId()).get(0);
		if (languageId != null && userDefaultLanguageMapping == null) {
			UserDefaultLanguageMapping input = new UserDefaultLanguageMapping();
			input.setUserMaster(user);
			input.setIsActive(true);
			input.setIsDeleted(false);
			input.setLanguageId(languageMasterRepository.findById(languageId).get());
			userDefLanMapRepository.save(input);
		}
	}
}
