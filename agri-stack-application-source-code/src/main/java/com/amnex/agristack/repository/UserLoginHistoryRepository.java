package com.amnex.agristack.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.UserLoginHistory;



public interface UserLoginHistoryRepository extends JpaRepository<UserLoginHistory, Long> {

}
