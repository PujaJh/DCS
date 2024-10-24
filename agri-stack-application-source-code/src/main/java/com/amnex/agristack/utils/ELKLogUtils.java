package com.amnex.agristack.utils;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.LogDTO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;


@Service
public class ELKLogUtils {

	private static final Logger LOG = LogManager.getLogger(ELKLogUtils.class.getClass());

	public void saveLog(@RequestBody LogDTO logDto) {
		try {
			LOG.log(Level.INFO, logDto.toString());
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	public void saveLoginLogoutLog(@RequestBody LogDTO logDto, HttpServletRequest request) {
		try {
			LOG.log(Level.INFO, logDto.toString());
		} catch (Exception e) {
			LOG.error(e);
		}
	}

}
