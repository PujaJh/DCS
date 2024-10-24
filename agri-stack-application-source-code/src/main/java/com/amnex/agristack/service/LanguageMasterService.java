package com.amnex.agristack.service;

import java.util.List;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.stereotype.Service;

import com.amnex.agristack.entity.LanguageMaster;
import com.amnex.agristack.repository.LanguageMasterRepository;
import com.amnex.agristack.utils.CustomMessages;

/**
 * @author janmaijaysingh.bisen
 *
 */
@Service
public class LanguageMasterService {

	private LanguageMasterRepository languageMasterRepository;

	public LanguageMasterService(LanguageMasterRepository languageMasterRepository) {
		this.languageMasterRepository = languageMasterRepository;
	}

	public List<LanguageMaster> getAllLanguageMaster() {
		return languageMasterRepository.findByIsActiveAndIsDeleted(true, false);
	}

	public ResponseModel getLanguageById(Long languageId) {
		try {
			return new ResponseModel(languageMasterRepository.findById(languageId).get(), CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

}
