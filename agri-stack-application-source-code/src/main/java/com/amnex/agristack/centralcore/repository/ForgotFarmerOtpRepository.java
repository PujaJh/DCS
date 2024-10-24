package com.amnex.agristack.centralcore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.centralcore.entity.ForgotFarmerOtp;

public interface ForgotFarmerOtpRepository extends JpaRepository<ForgotFarmerOtp, Long> {
    Optional<ForgotFarmerOtp> findFirstByFarmerIdAndOtpOrderByCreatedOnDesc(String farmerId, String otp);
}
