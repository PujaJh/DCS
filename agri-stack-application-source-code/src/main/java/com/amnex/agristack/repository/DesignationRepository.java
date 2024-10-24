/**
 *
 */
package com.amnex.agristack.repository;

import com.amnex.agristack.entity.DepartmentMaster;
import com.amnex.agristack.entity.DesignationMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author krupali.jogi
 *
 */
public interface DesignationRepository extends JpaRepository<DesignationMaster, Long> {

    List<DesignationMaster> findByIsDeletedFalseOrderByDesignationNameAsc();
}
