/**
 * 
 */
package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.amnex.agristack.entity.UnableToSurveyDetails;

/**
 * @author darshankumar.gajjar
 *
 */
public interface UnableToSurveyRepository extends JpaRepository<UnableToSurveyDetails, Long> {

    @Query(value = "select usd.parcel_id from agri_stack.unable_to_survey_details usd "
            + "	where usd.parcel_id in (:parcelIds) ", nativeQuery = true)
    public List<Long> getParcelIdsFromUnableTOSurvey(List<Long> parcelIds);

    
    
    @Transactional
    @Modifying
    @Query(value = "update agri_stack.unable_to_survey_details "
    		+ " set is_active = false "
    		+ " where parcel_id = :parcelId and season_id = :seasonId "
    		+ " and season_start_year = :startYear and season_end_year = :endYear ", nativeQuery = true)
	public void updateStatusByParcelIdSeasonAndYear(Long parcelId, Long seasonId, Integer startYear,
			Integer endYear); //	+ " ,is_deleted = true "
	
}
