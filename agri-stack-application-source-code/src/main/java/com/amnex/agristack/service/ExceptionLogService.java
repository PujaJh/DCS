package com.amnex.agristack.service;

import java.util.Date;
import java.util.List;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.ExceptionAuditDTO;
import com.amnex.agristack.entity.ExceptionAuditLog;
import com.amnex.agristack.repository.ExceptionAuditRepository;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Service
public class ExceptionLogService {

	@Autowired
	ExceptionAuditRepository exceptionAuditRepository;

	public void addException(String controller, String action, Exception exception, String exceptionOriginDetails) {

		ExceptionAuditLog exceptionAudit = new ExceptionAuditLog();

		exceptionAudit.setControllerName(controller);
		exceptionAudit.setActionName(action);
		exceptionAudit.setExceptionDescription(exception.getMessage());
		exceptionAudit.setExceptionType(exception.getClass().getSimpleName());
		exceptionAudit.setExceptionOriginDetails(exceptionOriginDetails);

		exceptionAudit.setCreatedOn(new Date());
		exceptionAudit.setActive(true);

		exceptionAuditRepository.save(exceptionAudit);
	}

	/**
	 * perform add exception
	 * @param {@code ExceptionAuditDTO}inputDao
	 * @return  The ResponseModel object representing the response.
  */
	public ResponseModel addExceptionFromMobile(ExceptionAuditDTO inputDao) {

		try {
			ExceptionAuditLog exceptionAudit = new ExceptionAuditLog();

			exceptionAudit.setExceptionCode(inputDao.getExceptionCode());
			exceptionAudit.setUserId(inputDao.getUserId());
			
			exceptionAudit.setControllerName(inputDao.getControllerName());
			exceptionAudit.setActionName(inputDao.getActionName());
			exceptionAudit.setExceptionDescription(inputDao.getExceptionDescription());
			exceptionAudit.setExceptionType(inputDao.getExceptionType());
			exceptionAudit.setExceptionOriginDetails(inputDao.getExceptionOriginDetails());

			exceptionAudit.setCreatedOn(new Date());
			exceptionAudit.setActive(true);

			exceptionAuditRepository.save(exceptionAudit);
			return new ResponseModel(null, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}

	}
}
