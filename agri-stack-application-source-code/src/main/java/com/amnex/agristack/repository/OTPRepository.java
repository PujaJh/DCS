package com.amnex.agristack.repository;

import com.amnex.agristack.entity.OTPRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;

public interface OTPRepository extends JpaRepository<OTPRegistration, Long>{
	public OTPRegistration findByOtpAndVerificationSource(String otp, String verificationSource);

	@Transactional
	public void deleteByOtpAndVerificationSource(String otp, String verificationSource);

	public OTPRegistration findByVerificationSource(String verificationSource);
	@Transactional
	public void deleteByVerificationSource(String verificationSource);
}
