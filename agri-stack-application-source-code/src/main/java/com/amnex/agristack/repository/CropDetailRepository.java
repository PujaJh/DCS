package com.amnex.agristack.repository;

import com.amnex.agristack.entity.CropRegistry;
import com.amnex.agristack.entity.CropRegistryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CropDetailRepository extends JpaRepository<CropRegistryDetail, Long> {


	@Query(value = "select cd from CropRegistryDetail cd where cd.isActive = true and cd.isDeleted = false and cd.cropId.cropId = :cropId and cd.stateLgdCode = :stateLgdCode order by cd.createdOn DESC")
	List<CropRegistryDetail> findByCropIdAndState(@Param("cropId") Long cropId, @Param("stateLgdCode") Long stateLgdCode);

	@Query(value = "select cd from CropRegistryDetail cd where cd.cropId.cropId = :cropId and cd.stateLgdCode in (:stateLgdCode)")
	List<CropRegistryDetail> findByCropIdAndStateList(Long cropId, List<Long> stateLgdCode);

	@Query(value = "select cd from CropRegistryDetail cd where cd.stateLgdCode in (:stateLgdCode) order by cd.cropId.cropId ASC")
	List<CropRegistryDetail> findCropDetailUsingState(List<Long> stateLgdCode);
}
