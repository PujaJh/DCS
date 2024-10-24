/**
 *
 */
package com.amnex.agristack.repository;

import com.amnex.agristack.entity.BankMaster;
import com.amnex.agristack.entity.DepartmentMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author krupali.jogi
 *
 */
public interface BankRepository extends JpaRepository<BankMaster, Long> {

    List<BankMaster> findByIsDeletedFalseOrderByBankNameAsc();

    Optional<BankMaster> findByBankIdAndIsDeletedFalse(Long userBankId);
}
