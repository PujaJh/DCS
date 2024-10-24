/**
 * 
 */
package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;

import com.amnex.agristack.entity.VillageWiseCropDataLog;
import com.amnex.agristack.entity.VillageWiseCropSurveyDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.VillageWiseGeomapLog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author majid.belim
 *
 */
public interface VillageWiseGeomapLogRepository extends JpaRepository<VillageWiseGeomapLog, Long> {
	List<VillageWiseGeomapLog> findByIsAcknowledgedOrderByIdDesc(Integer value);

	List<VillageWiseGeomapLog> findByIsActiveAndIsDeletedOrderByIdDesc(Boolean isActive, Boolean isDeleted);

	List<VillageWiseGeomapLog> findAllByOrderByIdDesc();

	@Query(value = "select * from agri_stack.village_wise_geomap_log where is_acknowledged = 108 order by id desc limit 1",nativeQuery = true)
	Optional<VillageWiseGeomapLog> findLastSharedSuccessLog();

	@Query (value = "select * from agri_stack.village_wise_geomap_log where is_acknowledged = 103 order by id desc limit 1",nativeQuery = true)
	Optional<VillageWiseGeomapLog> findLastSharedPendingLog();

	VillageWiseGeomapLog findByApiCallReferenceId(String referenceId);

	@Query (value = "select * from agri_stack.fn_get_geomap_shared_data_log()",nativeQuery = true)
	String findGeoMapSharedLog();
}
