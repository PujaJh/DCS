package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.amnex.agristack.entity.LandParcelSurveyMaster;

@Repository
public interface LandParcelSurveyMasterRespository extends JpaRepository<LandParcelSurveyMaster, Long> {
    @Query("Select lpsm from LandParcelSurveyMaster lpsm where lpsm.parcelId.farmlandPlotRegistryId = :parcelId  and lpsm.seasonStartYear = :seasonStartYear  and lpsm.seasonEndYear = :seasonEndYear  and lpsm.seasonId = :seasonId")
    public Optional<LandParcelSurveyMaster> findByParcelSeasonAndYear(Long parcelId, Integer seasonStartYear,
            Integer seasonEndYear, Long seasonId);

    String findLpsmForDuplicateSurveyNumbers = " Select " +
            " 	lpsm.lpsm_id," +
            " 	flpr.fpr_survey_number," +
            " 	flpr.fpr_sub_survey_number" +
            " from agri_stack.land_parcel_survey_master lpsm " +
            " inner join  agri_stack.farmland_plot_registry flpr" +
            " 	on lpsm.parcel_id = flpr.fpr_farmland_plot_registry_id" +
            " where " +
            " 	COALESCE(flpr.fpr_survey_number,'') = :surveyNumber" +
            " 	and COALESCE(flpr.fpr_sub_survey_number,'') = :subSurveyNumber" +
            " 	and lpsm.lpsm_id != :lpsmId" +
            " 	and lpsm.season_start_year = :seasonStartYear  and lpsm.season_end_year = :seasonEndYear  and lpsm.season_id = :seasonId "
            +
            " group by lpsm_id,flpr.fpr_survey_number,flpr.fpr_sub_survey_number";

    @Query(value = findLpsmForDuplicateSurveyNumbers, nativeQuery = true)
    public List<Object[]> getLpsmForDuplicateSurveyNumbers(String surveyNumber, String subSurveyNumber, Long lpsmId,
            Integer seasonStartYear,
            Integer seasonEndYear, Long seasonId);
    
    public List<LandParcelSurveyMaster> findByLpsmIdIn(List<Long> ids);


    @Query(value = "select lpsm.parcel_id from agri_stack.land_parcel_survey_master lpsm "
            + "	where lpsm.parcel_id in (:parcelIds) ", nativeQuery = true)
    public List<Long> getParcelIdsFromLandParcelSurveyMaster(List<Long> parcelIds);

    @Query(value = "select agri_stack.fn_get_owner_and_cultivator_details_by_parcel(:lpsmId,:parcelId,:seasonId,:startYear,:endYear)", nativeQuery = true)
	public String getOwnerAndCultivatorDetailsByParcel(Long lpsmId, Long parcelId,
			Long seasonId, Integer startYear, Integer endYear);

    @Query(value = "select distinct lpsm.season_id from agri_stack.land_parcel_survey_master lpsm where lpsm.parcel_id = :farmlandPlotRegistryId", nativeQuery = true)
    List<Long> getSeasonNameForParcelId(Long farmlandPlotRegistryId);

    @Query(value = "select distinct lpsm.season_start_year || '-' || lpsm.season_end_year  " +
            "from agri_stack.land_parcel_survey_master lpsm where lpsm.parcel_id = :farmlandPlotRegistryId", nativeQuery = true)
    List<String> getYearForParcelId(Long farmlandPlotRegistryId);

    @Query(value = "select agri_stack.fn_get_approved_crop_survey_for_state_by_date_range(:startDate,:endDate)", nativeQuery = true)
	public String getApprovedCropSurveyForStateByDateRange(String startDate,String endDate);

    @Query(value = "SELECT lpsm.* FROM agri_stack.land_parcel_survey_master lpsm" +
            " inner join agri_stack.land_parcel_survey_detail lpsd on lpsd.lpsm_id = lpsm.lpsm_id" +
            " inner join agri_stack.farmland_plot_registry fpr on fpr.fpr_farmland_plot_registry_id = lpsm.parcel_id" +
            " inner join agri_stack.village_lgd_master vlm on vlm.village_lgd_code = fpr.fpr_village_lgd_code" +
            " where lpsd.review_no = 2 and lpsm.survey_one_status = 102 and lpsm.survey_two_status = 102" +
            " and lpsm.season_id = :seasonId and lpsm.season_start_year = :startYear and lpsm.season_end_year = :endYear and vlm.village_lgd_code = :villageLgdCode", nativeQuery = true)
    List<LandParcelSurveyMaster> findLandParcelOfSecondRejectSurvey(Long seasonId, Integer startYear, Integer endYear, Long villageLgdCode);




}
