package com.amnex.agristack.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.LoginLogoutActivityLog;
import com.amnex.agristack.entity.UserMaster;

public interface LoginLogoutActivityLogReposiptory extends JpaRepository<LoginLogoutActivityLog, Long> {

	Page<LoginLogoutActivityLog> findByUserId(Long userId, Pageable pageable);

	List<LoginLogoutActivityLog> findByUserId(Long userId);

	Page<LoginLogoutActivityLog> findAll(Pageable pageable);

	LoginLogoutActivityLog findByAuthToken(String userId);

	@Query(value = "select u from LoginLogoutActivityLog u where (:userId is null or u.userId =:userId)  and ((u.logInDate between :startDate and :endDate) OR (u.logoutDate between :startDate and :endDate))")
	Page<LoginLogoutActivityLog> findByUserIdAndLoginLogOutData(Long userId, Date startDate, Date endDate,
			Pageable pageable);
}
