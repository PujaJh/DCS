package com.amnex.agristack.repository;

import com.amnex.agristack.entity.CropCategoryMaster;
import com.amnex.agristack.entity.CropRegistry;
import com.amnex.agristack.entity.CropRegistryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CropMasterRepository extends JpaRepository<CropRegistry, Long> {
	CropRegistry findByCropId(Long cropId);

	@Query(value = "select c from CropRegistry c where c.isActive = true and c.isDeleted= false and c.cropId in (:cropIds) order by cropId Asc")
	List<CropRegistry> findByCropIds(@Param("cropIds") List<Long> cropIds);


	List<CropRegistry> findByIsDeletedFalseOrderByModifiedOnDesc();

	List<CropRegistry> findByIsDeletedFalseAndIsActiveTrueOrderByCropNameAsc();

	List<CropRegistry> findByIsDeletedFalseOrderByCropIdAsc();

	@Query(value = "select c from CropRegistry c where c.isActive = :isActive and c.isDeleted= :isDeleted and c.isDefault= :isDefault order by cropId Asc")
	List<CropRegistry> findByIsDeletedFalseAndCropIdAsc(@Param("isActive") Boolean isActive, @Param("isDeleted") Boolean isDeleted,  @Param("isDefault") Boolean isDefault);


	@Query(value = "select c from CropRegistry c where c.isDeleted = false and (c.isDefault = true OR c.stateLgdCode in (:stateLgdCode)) order by c.cropName ASC")
	List<CropRegistry> findStateCrops(@Param("stateLgdCode") List<Long> stateLgdCode);
	
	@Query(value = "select c from CropRegistry c where c.isDeleted = false and c.isActive = true and (c.isDefault = true OR c.stateLgdCode in (:stateLgdCode)) order by c.cropName ASC")
	List<CropRegistry> findStateCrops(Long stateLgdCode);
	List<CropRegistry> findByIsDefaultTrueAndIsActiveTrueAndIsDeletedFalse();
	
	List<CropRegistry> findByIsDefaultTrueAndIsDeletedFalse();

    List<CropRegistry> findByCropUniqueIdIsNullOrderByCropIdAsc();
	@Query(value = "select cropUniqueId from CropRegistry c where c.cropUniqueId is not null order by c.cropUniqueId DESC")
	List<Integer> findByCropUniqueIdIsNotNullCropUniqueIdDesc();
	
	@Query(value = "select c from CropRegistry c where c.isDeleted = false and c.isActive = true and  c.stateLgdCode in (:stateLgdCode) order by c.cropName ASC")
	List<CropRegistry> findStateCropList(List<Long> stateLgdCode);
	
	@Query(value = "select c from CropRegistry c where c.isDeleted = false and c.isActive = true and  c.stateLgdCode in (:stateLgdCode) and cropId not in (:cropIds) order by c.cropName ASC")
	List<CropRegistry> findStateCropListNotIn(List<Long> stateLgdCode,List<Long> cropIds);
	
	@Query(value = "select c from CropRegistry c where c.isDeleted = false and c.isActive = true and cropId not in (:cropIds) and (c.isDefault = true OR c.stateLgdCode in (:stateLgdCode)) order by c.cropName ASC")
	List<CropRegistry> findStateCropsAndCropNotIn(List<Long> stateLgdCode,List<Long> cropIds);

	@Query(value = "select agri_stack.fn_get_geo_referenced_maps(:villageLgdCode, :surveyNumber, :uniqueLandCode)", nativeQuery = true)
	String getGeorReferencedMaps(Long villageLgdCode, String surveyNumber, String uniqueLandCode);

	@Query(value = "select agri_stack.fn_get_crop_survey_data(:villageLgdCode, :surveyNumber, :uniqueLandCode, :year, :season)", nativeQuery = true)
	String getCropSurveyData(Long villageLgdCode, String surveyNumber, String uniqueLandCode, String year, String season);

}
