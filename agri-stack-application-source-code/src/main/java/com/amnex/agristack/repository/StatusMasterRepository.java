/**
 *
 */
package com.amnex.agristack.repository;

import com.amnex.agristack.entity.StatusMaster;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author krupali.jogi
 *
 */
public interface StatusMasterRepository extends JpaRepository<StatusMaster, Long> {

   StatusMaster findByIsDeletedFalseAndStatusCode(Integer statusCode);
}
