package com.amnex.agristack.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.amnex.agristack.entity.LoginLogoutActivityLog;
import com.amnex.agristack.repository.LoginLogoutActivityLogReposiptory;

public class UserLogoutScheduler {

	@Value("${jwt.token.validity}")
	public long JWT_TOKEN_VALIDITY;

	@Autowired
	private LoginLogoutActivityLogReposiptory loginLogoutActivityLogReposiptory;

	@Scheduled(fixedRate = 60 * 60 * 1000)
	public void checkAndLogoutUser() {
		try {
			List<LoginLogoutActivityLog> logList = loginLogoutActivityLogReposiptory.findAll();
			Calendar calendar = Calendar.getInstance();
			
			logList.forEach((log) -> {
				Date logoutDate = log.getLogoutDate();

				if (logoutDate == null) {
					// need to check login time
					Date currentDate = new Date();
					Date loginDate = log.getLogInDate();

					long difference = currentDate.getTime() - loginDate.getTime();
					long diffMinutes = difference / (60 * 1000);         
					long diffHours = difference / (60 * 60 * 1000);

					if (diffHours >= (JWT_TOKEN_VALIDITY * 1000)) {
						calendar.setTime(loginDate);
						calendar.add(Calendar.MILLISECOND, (int) (JWT_TOKEN_VALIDITY * 1000));
						Date updatedLogoutDate = calendar.getTime();
						log.setLogoutDate(updatedLogoutDate);
						log.setSessionDuration(diffMinutes);
						loginLogoutActivityLogReposiptory.save(log);
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
