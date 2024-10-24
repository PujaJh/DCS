package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.UserDefaultLanguageMappingDTO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.StateDefaultLanguageMapping;
import com.amnex.agristack.repository.StateDefaultLanguageMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.entity.LanguageMaster;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.repository.LanguageMasterRepository;
import com.amnex.agristack.repository.StateLgdMasterRepository;
import com.amnex.agristack.repository.UserMasterRepository;
import com.amnex.agristack.utils.CustomMessages;

@Service
public class StateDefaultLanguageMappingService {

	@Autowired
	StateDefaultLanguageMappingRepository stateDefaultLanguageMappingRepository;

	@Autowired
	StateLgdMasterRepository stateLgdMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	LanguageMasterRepository languageMasterRepository;

	/**
	 * Adds default language for a State.
	 *
	 * @param userDeLanMapDTO The UserDefaultLanguageMappingDTO containing the state
	 *                        and language information.
	 * @return The ResponseModel indicating the status of the operation.
	 */
	public ResponseModel addDefaultLanguage(UserDefaultLanguageMappingDTO userDeLanMapDTO, HttpServletRequest request) {
		UserMaster userMaster = userMasterRepository.findByUserId(userDeLanMapDTO.getUserId()).get();
		LanguageMaster languageMaster = languageMasterRepository.findById(userDeLanMapDTO.getLanguageMasterId()).get();

		if (userMaster.getRolePatternMappingId().getTerritoryLevel().equals("state")) {
			StateDefaultLanguageMapping stateDefaultLanguageMapping = stateDefaultLanguageMappingRepository
					.findByIsActiveTrueAndIsDeletedFalseAndStateLgdMasterStateLgdCode(
							userDeLanMapDTO.getStateLgdCode());
			if (Objects.isNull(stateDefaultLanguageMapping)) {
				stateDefaultLanguageMapping = new StateDefaultLanguageMapping();
				stateDefaultLanguageMapping.setStateLgdMaster(
						stateLgdMasterRepository.findByStateLgdCode(userDeLanMapDTO.getStateLgdCode()));
				stateDefaultLanguageMapping.setCreatedBy(userDeLanMapDTO.getUserId().toString());
				stateDefaultLanguageMapping.setCreatedIp(request.getRemoteAddr());
				stateDefaultLanguageMapping.setCreatedOn(new Timestamp(new Date().getTime()));
				stateDefaultLanguageMapping.setIsActive(true);
				stateDefaultLanguageMapping.setIsDeleted(false);
			}
			stateDefaultLanguageMapping.setModifiedBy(userDeLanMapDTO.getUserId().toString());
			stateDefaultLanguageMapping.setModifiedIp(request.getRemoteAddr());
			stateDefaultLanguageMapping.setModifiedOn(new Timestamp(new Date().getTime()));
			stateDefaultLanguageMapping.setLanguageId(languageMaster);
			stateDefaultLanguageMappingRepository.save(stateDefaultLanguageMapping);
		}

		userMaster.setDefaultLanguageMaster(languageMaster);
		userMasterRepository.save(userMaster);

		return new ResponseModel(languageMaster, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

	}

	/**
	 * Retrieves the default language ID for a state.
	 *
	 * @param userDeLanMapDTO The ID of the user.
	 * @return The default language ID of the user, or null if not found.
	 */
	public ResponseModel getDefaultLanguageByStateLgdCode(UserDefaultLanguageMappingDTO userDeLanMapDTO) {
		Long defaultLanguageId = null;
		StateDefaultLanguageMapping stateDefaultLanguageMapping = stateDefaultLanguageMappingRepository
				.findByIsActiveTrueAndIsDeletedFalseAndStateLgdMasterStateLgdCode(userDeLanMapDTO.getStateLgdCode());
		if (!Objects.isNull(stateDefaultLanguageMapping)) {
			defaultLanguageId = stateDefaultLanguageMapping.getLanguageId().getLanguageId();
		}
		return new ResponseModel(defaultLanguageId, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
	}
}
