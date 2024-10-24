package com.amnex.agristack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.entity.LoginLogoutActivityLog;
import com.amnex.agristack.repository.LoginLogoutActivityLogReposiptory;

@Service
public class LoginLogutActivityLogService {

	@Autowired
	LoginLogoutActivityLogReposiptory logReposiptory;

	public LoginLogoutActivityLog addLog(LoginLogoutActivityLog input) {
		try {
			return logReposiptory.save(input);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
