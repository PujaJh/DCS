/**
 * 
 */
package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.PRPaymentRelease;
/**
 * @author majid.belim
 *
 */
public interface PRPaymentReleaseRepository extends JpaRepository<PRPaymentRelease, Long> {
	
	List<PRPaymentRelease> findBySurveyorId_UserIdInAndIsActiveAndIsDeleted(List<Long> ids,Boolean isActive,Boolean isDeleted);
	
	Optional<PRPaymentRelease> findByPrIdAndIsActiveAndIsDeleted(Long id,Boolean isActive,Boolean isDeleted);
	
	List<PRPaymentRelease> findByPrIdInAndIsActiveAndIsDeleted(List<Long> ids,Boolean isActive,Boolean isDeleted);
	
	List<PRPaymentRelease> findByStartYearAndEndYearAndSeason_SeasonIdAndSurveyorId_UserIdInAndIsActiveAndIsDeleted(Integer startYear,Integer endYear,Long seasonId,List<Long> ids,Boolean isActive,Boolean isDeleted);

}
