package com.amnex.agristack.repository;

import com.amnex.agristack.entity.NicOwnerLog;
import com.amnex.agristack.entity.UPRoRDataUploadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NicOwnerLogRepository extends JpaRepository<NicOwnerLog, Long> {

	@Query(value = "select agri_stack.fn_get_nic_up_survey_detail_v3(:villageCode)", nativeQuery = true)
//    @Query(value = "select gces.fn_web_get_upload_plan_details(:stateCodeList, :districtCodeList,:year,:seasonId, :page, :limit)", nativeQuery = true)
	String uploadOwnerDetail(Long villageCode);

	List<NicOwnerLog> findByIsUploadedIsNullAndIsActiveTrue();

}
