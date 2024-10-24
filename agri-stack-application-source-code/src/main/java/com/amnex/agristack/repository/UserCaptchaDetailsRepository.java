package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.UserCaptchaDetails;

public interface UserCaptchaDetailsRepository extends JpaRepository<UserCaptchaDetails, Long> {
	List<UserCaptchaDetails> findByUserIdAndIsActiveAndIsDeleted(Long userId, Boolean isActive, Boolean isDeleted);
	
	List<UserCaptchaDetails> findByCaptcha(String captcha);
	List<UserCaptchaDetails> findByCaptchaAndUserIdAndIsActiveAndIsDeleted(String captcha,Long userId, Boolean isActive, Boolean isDeleted);
	
	@Query(value="update agri_stack.User_Captcha_Details set is_active=false,is_deleted=true where user_id = :userId and is_active=true and is_deleted=false ", nativeQuery = true)
	@Modifying
	void updateUserCaptchaDetailsByUserId(Long userId);
}
