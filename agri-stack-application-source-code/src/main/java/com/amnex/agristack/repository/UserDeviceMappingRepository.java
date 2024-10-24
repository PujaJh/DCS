package com.amnex.agristack.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.UserDeviceMapping;

import feign.Param;

public interface UserDeviceMappingRepository extends JpaRepository<UserDeviceMapping, Long> {

//	@Query("select com.amnex.agristack.entity.UserDeviceMapping(A.statusName) from UserDeviceMapping A INNER JOIN StatusMaster B ON B.statusCode = A.statusCode")
	List<UserDeviceMapping> findByisActiveTrueOrderByUserDeviceMappingIdDesc();

	Optional<UserDeviceMapping> findByUserDeviceMappingId(Long userDeviceMappingId);

//	Page<UserDeviceMapping> findByUserId_UserVillageLGDCodeInAndCreatedOnGreaterThanEqualAndCreatedOnBetween(List<Integer> villageList,Date startDate, Date endDate, Pageable pageable);
	
	@Query("SELECT u FROM UserDeviceMapping u WHERE u.userId.userVillageLGDCode IN :villageList AND DATE(u.createdOn) >= :startDate AND DATE(u.createdOn) <= :endDate")
    Page<UserDeviceMapping> findByUserId_UserVillageLGDCodeInAndCreatedOnBetween(
            @Param("villageList") List<Integer> villageList,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable);

//	Page<UserDeviceMapping> findByUserId_UserNameOrUserId_UserMobileNumberOrUserId_UserEmailAddressOrImeiNumberAndCreatedOnBetween(
//			String userName, String userMobile, String userEmail, String imeiNumber,Date startDate, Date endDate, Pageable pageable);
	
	@Query("SELECT u FROM UserDeviceMapping u WHERE " +
	        "(u.userId.userName = :userName OR " +
	        "u.userId.userMobileNumber = :userMobile OR " +
	        "u.userId.userEmailAddress = :userEmail OR " +
	        "u.imeiNumber = :imeiNumber) AND " +
	        "DATE(u.createdOn) >= :startDate AND DATE(u.createdOn) <= :endDate")
	Page<UserDeviceMapping> findByUserId_UserNameOrUserId_UserMobileNumberOrUserId_UserEmailAddressOrImeiNumberAndCreatedOnBetween(
	        @Param("userName") String userName,
	        @Param("userMobile") String userMobile,
	        @Param("userEmail") String userEmail,
	        @Param("imeiNumber") String imeiNumber,
	        @Param("startDate") Date startDate,
	        @Param("endDate") Date endDate,
	        Pageable pageable);

	@Query(" select u from UserDeviceMapping u where u.userId.userId = :userId and u.imeiNumber = :imeiNumber and u.statusCode = :statusCode ")
	List<UserDeviceMapping> findByImeiNumberAndUserIdAndStatusCode(String imeiNumber, Long userId, Integer statusCode);

}
