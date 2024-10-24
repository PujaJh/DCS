/**
 *
 */
package com.amnex.agristack.repository;

import com.amnex.agristack.entity.DepartmentMaster;
import com.amnex.agristack.entity.VillageLgdMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author krupali.jogi
 *
 */
public interface DepartmentRepository extends JpaRepository<DepartmentMaster, Long> {

    List<DepartmentMaster> findByIsDeletedFalseOrderByDepartmentNameAsc();

    Optional<DepartmentMaster> findByDepartmentType(Integer private_resident);

	DepartmentMaster findByDepartmentId(Long departmentId);
}
