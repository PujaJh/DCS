package com.amnex.agristack.repository;

import com.amnex.agristack.entity.SurveyActivityConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SurveyActivityConfigurationRepository extends JpaRepository<SurveyActivityConfiguration, Long> {

	List<SurveyActivityConfiguration> findAll();

    @Query("select c from SurveyActivityConfiguration c where" +
            " c.isActive = true and" +
            " c.isDeleted = false and" +
            " ((:seasonId is null) or c.seasonId = :seasonId) and" +
            " ((:year is null) or c.year = :year) and" +
            " ((:activityId is null) or c.surveyActivity.id = :activityId)")
    Optional<SurveyActivityConfiguration> checkActivityAlreadyConfigured(@Param("seasonId") Long seasonId,
                                                                         @Param("activityId") Long activityId,
                                                                         @Param("year") Long year);

    @Query("select c from SurveyActivityConfiguration c where" +
            " c.isActive = true and" +
            " c.isDeleted = false and" +
            " ((:seasonId is null) or c.seasonId = :seasonId) and" +
            " ((:year is null) or c.year = :year) and" +
            " ((:activityIds) is null or c.surveyActivity.id in (:activityIds)) order by id ASC")
    List<SurveyActivityConfiguration> getSurveyActivityConfigList(@Param("seasonId") Long seasonId,
                                                                  @Param("activityIds") List<Long> activityIds,
                                                                  @Param("year") Long year);
}
