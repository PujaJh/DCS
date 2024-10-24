package com.amnex.agristack.centralcore.repository;

import com.amnex.agristack.centralcore.entity.TransactionRequestMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRequestMappingRepository extends JpaRepository<TransactionRequestMapping, Long> {

	TransactionRequestMapping findByReferenceId(String referenceId);

	
	@Query(value = "select agristack_api_spec.get_data_for_on_seek_by_reference_id( :referenceId) ", nativeQuery = true)
	String fetchDataOnSeek(String referenceId);

	@Query(value = "select agristack_api_spec.get_data_for_on_seek_by_reference_id_for_count( :referenceId, true) ", nativeQuery = true)
	String fetchDataOnSeekForCount(String referenceId);

	@Query(value = "select agristack_api_spec.fn_get_farmer_data(:stateLgdCode, :farmerId) ", nativeQuery = true)
	String fetchFarmerData(Long stateLgdCode, String farmerId);

	@Query(value = "select agristack_api_spec.fn_get_crop_sown_data_for_fr(:villageLgdCode, :surveyNumber, :subSurveyNumber)", nativeQuery = true)
	String getCropSownData(Long villageLgdCode, String surveyNumber, String subSurveyNumber);

	@Query(value = "select agristack_api_spec.fn_get_crop_details_for_fr_records(:villageLgdCode, :surveyNumber, :subSurveyNumber)", nativeQuery = true)
	String getCropDetails(Long villageLgdCode, String surveyNumber, String subSurveyNumber);

	@Query(value = "select agristack_api_spec.fn_get_crop_and_area_data_for_fr(:villageLgdCode, :surveyNumber, :subSurveyNumber, :seasonId, :year)", nativeQuery = true)
	String getOwnerAndCropAndAreaData(Long villageLgdCode, String surveyNumber, String subSurveyNumber, Long seasonId, String year);

	@Query(value = "select agristack_api_spec.fn_get_farmer_crop_data_by_surveynumber_for_fr(:surveyNumber,:stateLgdCode,:villageLgdCode, :year, :season)", nativeQuery = true)
	String getO3CropSownData(String surveyNumber,Long stateLgdCode,Long villageLgdCode, String year, String season);

	@Query(value = "select agristack_api_spec.fn_get_common_crop_sown_data_for_fr(:surveyNumber,:stateLgdCode,:villageLgdCode, :year, :season)", nativeQuery = true)
	String getCommonCropSownData(String surveyNumber,Long stateLgdCode,Long villageLgdCode, String year, String season);

}
