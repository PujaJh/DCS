package com.amnex.agristack.repository;

import com.amnex.agristack.entity.StateDefaultLanguageMapping;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author Hardik.Siroya
 *
 */
public interface StateDefaultLanguageMappingRepository extends JpaRepository<StateDefaultLanguageMapping, Long> {

	StateDefaultLanguageMapping findByIsActiveTrueAndIsDeletedFalseAndStateLgdMasterStateLgdCode(Long stateLgdCode);
}
