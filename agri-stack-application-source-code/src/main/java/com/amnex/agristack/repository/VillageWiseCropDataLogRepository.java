/**
 * 
 */
package com.amnex.agristack.repository;

import com.amnex.agristack.entity.VillageWiseCropDataLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * @author krupali.jogi
 *
 */
public interface VillageWiseCropDataLogRepository extends JpaRepository<VillageWiseCropDataLog, Long>{
	@Query (value = "select * from agri_stack.village_wise_crop_data_log where is_acknowledged = 108 order by reference_id desc limit 1",nativeQuery = true)
	Optional<VillageWiseCropDataLog> findLastSharedSuccessLog();

	@Query (value = "select * from agri_stack.village_wise_crop_data_log where is_acknowledged = 103 order by reference_id desc limit 1",nativeQuery = true)
	Optional<VillageWiseCropDataLog> findLastSharedPendingLog();
	List<VillageWiseCropDataLog> findByIsAcknowledgedOrderByReferenceIdDesc(Boolean isAcknowledged);
	VillageWiseCropDataLog findByApiCallReferenceId(String referenceId);

	List<VillageWiseCropDataLog> findAllByOrderByReferenceIdDesc();

	@Query (value = "select * from agri_stack.fn_get_village_wise_shared_data_log()",nativeQuery = true)
	String findVillageWiseSharedSurveyLog();
}
