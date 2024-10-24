package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.dao.SurveyVerificationConfigurationMasterDAO;
import com.amnex.agristack.entity.SurveyVerificationConfigurationMaster;

public interface SurveyVerificationConfigurationMasterRepository
		extends JpaRepository<SurveyVerificationConfigurationMaster, Integer> {

	@Query(value = "SELECT new com.amnex.agristack.dao.SurveyVerificationConfigurationMasterDAO(s.surveyVerificationConfigurationMasterId,"
			+ " s.randomPickPercentageOfVillages, s.randomPickPercentageOfPlots, s.randomPickMinimumNumberOfPlots,"
			+ " s.surveyRejectedBySupervisorForSecondTime, s.objectionRaisedByFarmerAndMarkedBySupervisor,"
			+ " s.isActive, s.isDeleted, s.stateLgdMaster.stateLgdCode, s.appliedYear.id, s.appliedSeason.seasonId,"
			+ " s.appliedYear.yearValue, s.appliedSeason.seasonName, s.createdOn)"
			+ " FROM SurveyVerificationConfigurationMaster s "
			+ " WHERE s.stateLgdMaster.stateLgdCode = :stateLgdCode AND s.isActive IS TRUE AND s.isDeleted IS FALSE")
	public SurveyVerificationConfigurationMasterDAO findByStateLgdCode(Long stateLgdCode);

	@Query(value = "SELECT s FROM SurveyVerificationConfigurationMaster s "
			+ " WHERE s.surveyVerificationConfigurationMasterId = :surveyVerificationConfigurationMasterId")
	public SurveyVerificationConfigurationMaster findBySurveyVerificationConfigurationMasterId(
			Integer surveyVerificationConfigurationMasterId);
	
	@Query(value = "SELECT s FROM SurveyVerificationConfigurationMaster s WHERE s.isDeleted IS FALSE "
			+ " AND s.stateLgdMaster.stateLgdCode IN (:stateLgdCodes) order by surveyVerificationConfigurationMasterId desc ")
	public List<SurveyVerificationConfigurationMaster> findAll(List<Long> stateLgdCodes);
	
	@Query(value = "SELECT s FROM SurveyVerificationConfigurationMaster s WHERE s.stateLgdMaster.stateLgdCode = :stateLgdCode "
			+ " AND s.isActive IS TRUE AND s.isDeleted IS FALSE")
	public SurveyVerificationConfigurationMaster findMasterDataByStateLgdCode(Long stateLgdCode);
	
	@Query(value = "SELECT MAX(s.surveyVerificationConfigurationMasterId) " + 
			" FROM SurveyVerificationConfigurationMaster s " + 
			" WHERE s.isActive IS FALSE AND isDeleted IS FALSE AND s.stateLgdMaster.stateLgdCode = :stateLgdCode ")
	public Integer getPreviousInactiveEntryForGivenState(Long stateLgdCode);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE SurveyVerificationConfigurationMaster s SET isDeleted = FALSE, isActive = TRUE "
			+ " WHERE s.surveyVerificationConfigurationMasterId = :surveyVerificationConfigurationMasterId")
	public void activateSurveyVerificationConfiguration(Integer surveyVerificationConfigurationMasterId);
	
	public Optional<SurveyVerificationConfigurationMaster> findByStateLgdMaster_StateLgdCodeAndIsDeletedAndIsActive(Long stateLgdCode,Boolean isDeleted,Boolean isActive);
	
	
	public Optional<SurveyVerificationConfigurationMaster> findByStateLgdMaster_StateLgdCodeAndIsDeletedAndIsApppliedNextSeason(Long stateLgdCode,Boolean isDeleted,Boolean isApppliedNextSeason);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE SurveyVerificationConfigurationMaster s SET s.isActive = false " + 
			"where isActive=true and isDeleted=false and stateLgdMaster.stateLgdCode in (:codes) " + 
			"and appliedSeason.seasonId !=:seasonId ")
	public void  deActivateSurveyVerificationConfiguration(List<Long> codes,Long seasonId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE SurveyVerificationConfigurationMaster s  SET  s.isActive = true ,  s.isApppliedNextSeason=false  " + 
			"where isDeleted=false and stateLgdMaster.stateLgdCode in (:codes) and isApppliedNextSeason=true " + 
			"and appliedSeason.seasonId =:seasonId ")
	public void  activateSurveyVerificationConfiguration(List<Long> codes,Long seasonId);
}
