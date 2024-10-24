package com.amnex.agristack.repository;

import com.amnex.agristack.dao.MediaDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.amnex.agristack.entity.MediaMaster;

import java.util.List;

public interface MediaMasterRepository extends JpaRepository<MediaMaster, String> {

    @Query("SELECT M FROM MediaMaster M WHERE M.mediaId = :mediaId")
    public MediaMaster getMediaByID(@Param("mediaId") String mediaId);

    @Query(value = "with cte as (\n" +
            "\t\t\t  select \n" +
            "\t\t\t\trow_number() OVER (\n" +
            "\t\t\t\tpartition by lpsm.parcel_id \n" +
            "\t\t\t\torder by \n" +
            "\t\t\t\tlpsd.review_no desc\n" +
            "\t\t\t\t) as rnum,  \n" +
            "\t \t\t\tlpsm.lpsm_id,\n" +
            "\t\t\t\tlpsd.review_no,\n" +
            "\t\t\t\tfpr.fpr_village_lgd_code,\n" +
            "\t\t\t\t lpsd.survey_by,\n" +
            "\t\t\t\t lpsd.surveyor_lat,lpsd.surveyor_long,\n" +
            "\tmm.media_id,mm.media_url\n" +
            "\t\t\tfrom agri_stack.land_parcel_survey_detail lpsd \n" +
            "\t\t\tinner join agri_stack.land_parcel_survey_master lpsm on lpsm.lpsm_id = lpsd.lpsm_id \n" +
            "\t\t\tinner join agri_stack.farmland_plot_registry fpr on fpr.fpr_farmland_plot_registry_id = lpsm.parcel_id \n" +
            "\tinner join agri_stack.land_parcel_survey_crop_detail cd on cd.lpsd_id = lpsd.lpsd_id\n" +
            "\tinner join agri_stack.land_parcel_survey_crop_media_mapping cmm  on cmm.survey_crop_id = cd.crop_sd_id\n" +
            "\tinner join agri_stack.media_master mm on mm.media_id = cmm.media_id \n" +
            "\t\t\twhere \n" +
            "\t\t\t\tlpsm.is_active = true \n" +
            "\t\t\t\tand lpsm.is_deleted = false \n" +
            "\t\t\t\tand lpsm.season_id = 2\n" +
            "\t\t\t\tand lpsm.season_start_year = 2023\n" +
            "\t\t\t\tand lpsm.season_end_year = 2024\n" +
            "\t\t\t\tand fpr.fpr_village_lgd_code in (166150)\n" +
            "-- \t\t\t\tAND LPSM.survey_status = 101 \n" +
            "\t\t\t\tAND (\n" +
            "\t\t\t\t  lpsd.review_no = ANY (ARRAY[1, 2])\n" +
            "\t\t\t\t)\n" +
            "\t\t) \n" +
            "\t\tselect  \n" +
            "c.media_id,\n" +
            "\t\t\tc.media_url\n" +
            "\t\t\tfrom cte c\n" +
            "\t\tinner join agri_stack.village_lgd_master vm on vm.village_lgd_code = c.fpr_village_lgd_code\n" +
            "\t\tINNER JOIN agri_stack.sub_district_lgd_master subd on subd.sub_district_lgd_code = vm.sub_district_lgd_code \n" +
            "\t\tINNER JOIN agri_stack.district_lgd_master dm on dm.district_lgd_code = subd.district_lgd_code\n" +
            "\t\tinner join  agri_stack.user_master um on um.user_id= c.survey_by \n" +
            "\t\twhere rnum = 1  and (c.surveyor_long = 0 and c.surveyor_lat = 0) \n" +
            "-- \t\tand um.user_mobile_number = '9839073225'\n" +
            "\t\tgroup by \n" +
            "\t\tc.media_id,\n" +
            "\t\tc.media_url", nativeQuery = true)
    List<Object[]> fetchImageUrls();
}
