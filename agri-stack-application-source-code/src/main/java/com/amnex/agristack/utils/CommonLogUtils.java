package com.amnex.agristack.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.LogDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
public class CommonLogUtils {

	private static Logger logger = Logger.getLogger(CommonLogUtils.class.getName());

	@Autowired
	private Environment environment;

	@Autowired
	private ELKLogUtils elkLogService;

	public void addTransactionLog(HttpServletRequest request, String projectName, Integer userId, String page,
			String operation, String clientName, String recordChange, String parameter2, String parameter3,
			String parameter4) {

		new Thread() {
			@Override
			public void run() {
				try {

					LogDTO log = new LogDTO();
					log.setUserId(userId == null ? "" : userId.toString());
					log.setUserName("");
					log.setType("INFO");
					log.setDomainName(environment.getProperty("app.log.domain-name"));
					log.setModule(environment.getProperty("app.log.module-name"));
					log.setPage(page == null ? "" : page);
					log.setOperation(operation == null ? "" : operation);
					log.setClientName(clientName == null ? "" : clientName);
					log.setAttributes(parameter2 == null ? "" : parameter2);
					log.setErrorStacktrace("");
					log.setErrorMessage("");

					elkLogService.saveLog(log);

				} catch (Exception e) {
					logger.log(Level.SEVERE, ":: Error while adding transaction log using API !");
					e.printStackTrace();
				}
			}
		}.start();

	}

	public void addErrorLog(HttpServletRequest request, String projectName, Integer userId, String errorStacktrace,
			String errorMessage, String page, String operation, String clientName, String parameter2, String parameter3,
			String parameter4) {

		new Thread() {
			@Override
			public void run() {
				try {

					LogDTO errorLog = new LogDTO();
					errorLog.setUserId(userId == null ? "" : userId.toString());
					errorLog.setUserName("");
					errorLog.setType("ERROR");
					errorLog.setDomainName(environment.getProperty("app.log.domain-name"));
					errorLog.setModule(environment.getProperty("app.log.module-name"));
					errorLog.setPage(page == null ? "" : page);
					errorLog.setOperation(operation == null ? "" : operation);
					errorLog.setClientName(clientName == null ? "" : clientName);
					errorLog.setAttributes(parameter2 == null ? "" : parameter2);
					errorLog.setErrorStacktrace(errorStacktrace == null ? "" : errorStacktrace);
					errorLog.setErrorMessage(errorMessage == null ? "" : errorMessage);

					elkLogService.saveLog(errorLog);
				} catch (Exception e) {
					logger.log(Level.SEVERE, ":: Error while adding error log using API !");
					e.printStackTrace();
				}

			}
		}.start();
	}

	public void addLoginLogoutLog(String userId, String userName, String clientName, String page, String operation,
			String type, String attributes, String errorStacktrace, String errorMessage, HttpServletRequest request) {

		new Thread() {
			@Override
			public void run() {
				try {

					LogDTO log = new LogDTO();
					log.setUserId(userId == null ? "" : userId);
					log.setUserName(userName == null ? "" : userName);
					log.setDomainName(environment.getProperty("app.log.domain-name"));
					log.setClientName(clientName == null ? "" : clientName);
					log.setModule(environment.getProperty("app.log.module-name"));
					log.setPage(page == null ? "" : page);
					log.setOperation(operation == null ? "" : operation);
					log.setType(type == null ? "" : type);
					log.setAttributes(attributes == null ? "" : attributes);
					log.setErrorStacktrace(errorStacktrace == null ? "" : errorStacktrace);
					log.setErrorMessage(errorMessage == null ? "" : errorMessage);
					elkLogService.saveLoginLogoutLog(log, request);

				} catch (Exception e) {
					logger.log(Level.SEVERE, ":: Error while adding transaction log using log proxy !");
					e.printStackTrace();
				}
			}
		}.start();
	}
}
