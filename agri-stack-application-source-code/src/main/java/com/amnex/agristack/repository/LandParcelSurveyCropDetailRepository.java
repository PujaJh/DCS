package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.LandParcelSurveyCropMediaMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.amnex.agristack.entity.LandParcelSurveyCropDetail;

import javax.persistence.Lob;

@Repository
public interface LandParcelSurveyCropDetailRepository extends JpaRepository<LandParcelSurveyCropDetail, Long> {
    @Query("Select cd from LandParcelSurveyCropDetail cd where cd.lpsdId = :lpsdId")
    public List<LandParcelSurveyCropDetail> findByLpsdId(Long lpsdId);

    @Modifying
    @Query(value = "DELETE from agri_stack.media_master where media_id in (Select media_id from agri_stack.land_parcel_survey_crop_media_mapping where survey_crop_id in (select crop_sd_id from agri_stack.land_parcel_survey_crop_detail where lpsd_id = :lpsdId));", nativeQuery = true)
    public void deleteCropMediaByLpsdId(Long lpsdId);

    @Modifying
    @Query(value = "DELETE from agri_stack.land_parcel_survey_crop_media_mapping where survey_crop_id in (select crop_sd_id from agri_stack.land_parcel_survey_crop_detail where lpsd_id = :lpsdId);", nativeQuery = true)
    public void deleteCropMediaMappingByLpsdId(Long lpsdId);

    @Modifying
    @Query(value = "select * from agri_stack.land_parcel_survey_crop_media_mapping where survey_crop_id = :surveyCropId", nativeQuery = true)
    public List<Object[]>  getCropMediaMappingBySurveyCropId(Long surveyCropId);

//    @Modifying
//    @Query(value = "select * from agri_stack.media_master_v2 where survey_crop_id = :surveyCropId", nativeQuery = true)
//    public List<Object[]>  getCropMediaUsingSurveyID(Long surveyCropId);

       @Modifying
       @Query(value = "select distinct(user_id) from agri_stack.media_master_v2", nativeQuery = true)
       public List<Object[]>  getDistinctUserId();

    @Modifying
    @Query(value = "select * from agri_stack.media_master_v2 where user_id = :userID", nativeQuery = true)
    public List<Object[]>  getAllUserImage(Long userID);


    @Modifying
    @Query(value = "DELETE from agri_stack.land_parcel_survey_crop_detail cd where cd.lpsd_id = :lpsdId ;", nativeQuery = true)
    public void deleteByLpsdId(Long lpsdId);
}
