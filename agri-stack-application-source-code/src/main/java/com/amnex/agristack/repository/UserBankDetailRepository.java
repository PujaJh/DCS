package com.amnex.agristack.repository;

import com.amnex.agristack.entity.UserBankDetail;
import com.amnex.agristack.entity.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserBankDetailRepository extends JpaRepository<UserBankDetail, Long> {

    @Query(value = "select u from UserBankDetail u where u.isActive = true and u.isDeleted = false and userId.userId = :userId ")
    UserBankDetail findByUserId_UserId(@Param("userId") Long userId);


    @Query(value = "select u from UserBankDetail u where u.isActive = true and u.isDeleted = false and u.userBankAccountNumber = :userBankAccountNumber ")
    UserBankDetail findByUserBankAccounNumber(@Param("userBankAccountNumber") String userBankAccountNumber);


}
