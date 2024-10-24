package com.amnex.agristack.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.amnex.agristack.dao.SeasonMasterOutputDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.SowingSeason;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeasonMasterRepository extends JpaRepository<SowingSeason, Long> {
	SowingSeason findBySeasonId(Long seasonId);
    List<SowingSeason> findAllByIsDeletedFalse();
    
    List<SowingSeason> findBySeasonIdIn(Set<Long> seasonIds);
    List<SowingSeason> findBySeasonIdIn(List<Long> seasonIds);

    List<SowingSeason> findByIsDeletedFalseAndIsActiveTrueOrderBySeasonIdAsc();

    List<SowingSeason> findByIsDeletedFalseOrderBySeasonIdAsc();
    @Query("select s from SowingSeason s where " +
            "s.isDeleted = :isDeleted " +
            "and ((((:startDate BETWEEN TO_DATE(s.startingMonth, 'dd-MM-yyyy') and TO_DATE(s.endingMonth, 'dd-MM-yyyy')) ) " +
            " OR ((:endDate BETWEEN TO_DATE(s.startingMonth, 'dd-MM-yyyy') and TO_DATE(s.endingMonth, 'dd-MM-yyyy'))) )" +
            " OR  ( TO_DATE(starting_Month, 'dd-MM-yyyy')  >  :startDate and TO_DATE(ending_Month, 'dd-MM-yyyy') <  :endDate ) )")
    List<SowingSeason> checkDurationIsValid(@Param("isDeleted") Boolean isDeleted, @Param("startDate") Date startDate ,@Param("endDate") Date endDate);

    @Query("select s from SowingSeason s where " +
            "s.isDeleted = false " +
            "and s.seasonName = :seasonName")
    List<SowingSeason> findByIsDeletedFalseAndSeasonName(@Param("seasonName") String seasonName);

    List<SowingSeason> findByIsDeletedFalseAndIsActiveTrueOrderBySeasonNameAsc();
    
    @Query(value="select season_id  from agri_stack.Sowing_Season where is_active=true and is_deleted=false  "
			+ "and extract(month from ending_month\\:\\:date) >= extract(month from CURRENT_DATE) order by  TO_DATE(ending_Month, 'DD-MM')  asc ",nativeQuery = true)
    List<Long> getSeasonIds();
    
    @Query(value="select season_id  from agri_stack.Sowing_Season where is_active=true and is_deleted=false  "
			+ "and extract(month from ending_month\\:\\:date) >= extract(month from CURRENT_DATE) and season_id !=:id order by  TO_DATE(ending_Month, 'DD-MM')  asc ",nativeQuery = true)
    List<Long> getSeasonIdsNotIn(Long id);
    
    
//    @Query(value="select season_id  from agri_stack.Sowing_Season where is_active=true and is_deleted=false and TO_DATE(ending_month, 'DD-MM-YYYY')> CURRENT_DATE ",nativeQuery = true)
//    @Query(value="select s from SowingSeason s where s.isActive=true and s.isDeleted=false and TO_DATE(s.endingMonth, 'DD-MM-YYYY') > CURRENT_DATE ")
//    List<SowingSeason> getUpComingSeasonIds();
//    @Query(value="select s from SowingSeason s where s.isActive=true and s.isDeleted=false and extract(month from s.ending_month::date) >= extract(month from now()) ")
//    List<SowingSeason> getUpComingSeasonIds();

    @Query("select s from SowingSeason s where " +
            "s.isDeleted = false " +
            "and lower(s.seasonName) = lower(:seasonName)")
    List<SowingSeason> findByIsDeletedFalseAndSeason(@Param("seasonName") String seasonName);

    @Query("select new com.amnex.agristack.dao.SeasonMasterOutputDAO(s.seasonId,s.seasonName) from SowingSeason s where " +
            "s.seasonId in (:seasonId)")
    List<SeasonMasterOutputDAO> findSeasonOutputList(@Param("seasonId") List<Long> seasonId);

    SowingSeason findByIsCurrentSeasonTrue();
}
