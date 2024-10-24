/**
 * 
 */
package com.amnex.agristack.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.ActivityStatus;

/**
 * @author majid.belim
 *
 */
public interface ActivityStatusRepository extends JpaRepository<ActivityStatus, Long>{
	Optional<ActivityStatus> findByActivityStatusName(String activityStatusName);
}
