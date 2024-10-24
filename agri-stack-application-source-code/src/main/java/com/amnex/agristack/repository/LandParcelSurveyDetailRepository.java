package com.amnex.agristack.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.amnex.agristack.entity.LandParcelSurveyDetail;
import com.amnex.agristack.entity.LandParcelSurveyMaster;

import javax.transaction.Transactional;

@Repository
public interface LandParcelSurveyDetailRepository extends JpaRepository<LandParcelSurveyDetail, Long> {
    public Optional<LandParcelSurveyDetail> findByLpsmIdAndReviewNo(Long lpsm, Integer reviewNo);

//    @Query(value = "SELECT lpsd_id,survey_date,survey_by " +
//            "    FROM agri_stack.land_parcel_survey_detail " +
//            "    WHERE survey_by = :userID  and status = 103 order by survey_date",nativeQuery = true)
//    List<Object[]> getAllSurveyDetails(Long userID);

    @Query(value = "SELECT lpsd_id, survey_date, survey_by " +
            "FROM agri_stack.land_parcel_survey_detail " +
            "WHERE survey_by = :userID AND status = 103 AND survey_date < '2024-09-17' " +
            "ORDER BY survey_date",
            nativeQuery = true)
    List<Object[]> getAllSurveyDetails(Long userID);



    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_servey_image_mapping (user_id, survey_id, survey_date, image_date, stage_id, image_path) " +
            "VALUES (:userId, :surveyId, :surveyDate, :imageDate, :stageId, :imagePath)", nativeQuery = true)
    void insertTOSurveyImageMapping(@Param("userId") Integer userId,
                                    @Param("surveyId") Integer surveyId,
                                    @Param("surveyDate") Timestamp surveyDate,
                                    @Param("imageDate") Timestamp imageDate,
                                    @Param("stageId") Integer stageId,
                                    @Param("imagePath") String imagePath);


    @Query(value = "select * from user_servey_image_mapping where survey_id = :surveyID", nativeQuery = true)
    List<Object[]> getSurveyImageMapping(Long surveyID);


    @Query(value = "select * from user_servey_image_mapping where user_id = :userID", nativeQuery = true)
    List<Object[]> getAllimageForUser(Long userID);


    @Modifying
    @Transactional
    @Query(value = "UPDATE user_servey_image_mapping " +
            "SET stage_id = :stageId " +  // Set the stage_id directly
            "WHERE survey_id = :surveyID AND image_date = :imageDate", nativeQuery = true)
    void updateSurveyImageMapping(@Param("surveyID") Long surveyID,
                                  @Param("imageDate") Timestamp imageDate,
                                  @Param("stageId") Integer stageId);  // Add stageId as a parameter

}
