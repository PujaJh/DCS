package com.amnex.agristack.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.entity.LogMaster;
import com.amnex.agristack.repository.LogRepository;

@Service
public class LogService {

	@Autowired
	LogRepository logRepository;

	/**
	 * Saves a log entry in the LogMaster table.
	 *
	 * @param moduleName         The name of the module.
	 * @param action             The action performed.
	 * @param originalDataIds    The IDs of the original data.
	 * @param originalDataNames  The names of the original data.
	 * @param newDataIds         The IDs of the new data.
	 * @param newDataNames       The names of the new data.
	 * @param userId             The ID of the user.
	 * @param userName           The name of the user.
	 */
	public void addLog(String moduleName, String action, String originalDataIds, String originalDataNames,
			String newDataIds, String newDataNames, String userId, String userName) {

		LogMaster logMaster = new LogMaster();
		logMaster.setModuleName(moduleName);
		logMaster.setAction(action);
		logMaster.setActionTime(new Date());
		logMaster.setNewDataIds(newDataIds);
		logMaster.setNewDataNames(newDataNames);
		logMaster.setOriginalDataIds(originalDataIds);
		logMaster.setOriginalDataNames(originalDataNames);
		logMaster.setUserId(userId);
		logMaster.setUserName(userName);
		logRepository.save(logMaster);

	}

}
