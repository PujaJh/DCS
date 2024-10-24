package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amnex.agristack.Enum.NotificationSourceType;
import com.amnex.agristack.dao.MobileNotificationDAO;
import com.amnex.agristack.entity.MobileNotificationMaster;

public interface MobileNotificationRepository extends JpaRepository<MobileNotificationMaster, Long>{
	
	
	List<MobileNotificationMaster> findByIsActiveTrueAndIsDeletedFalseOrderByCreatedOnDesc();
	
	@Query(value = "select new com.amnex.agristack.dao.MobileNotificationDAO(mnm.title, mnm.message, mnm.serialNo) from MobileNotificationMaster mnm where mnm.isActive = true and mnm.isDeleted = false AND mnm.sourceType = :sourceType order by mnm.createdOn desc")
	List<MobileNotificationDAO> findfindByIsActiveTrueAndIsDeletedFalseOrderByCreatedOnDesc(@Param("sourceType") NotificationSourceType sourceType);


}
