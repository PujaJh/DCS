/**
 * 
 */
package com.amnex.agristack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.ActivityType;

/**
 * @author majid.belim
 *
 */

public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long> {
	Optional<ActivityType> findByActivityTypeName(String activityType);
}
