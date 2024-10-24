/**
 *
 */
package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.StateUnitTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.StateLgdMaster;

/**
 * @author kinnari.soni
 *
 */
public interface StateUnitTypeRepository extends JpaRepository<StateUnitTypeMaster, Long> {

	StateUnitTypeMaster findByUnitTypeMasterId(Long unitTypeMasterId);

	List<StateUnitTypeMaster> findByStateLgdCode(StateLgdMaster state);

	StateUnitTypeMaster findByUnitTypeDescEngAndUnitValueAndStateLgdCode(String unitName, double unitValue,
			StateLgdMaster stateLgdCode);

	StateUnitTypeMaster findByStateLgdCodeAndIsDefault(StateLgdMaster stateLgdMaster, boolean isDefault);

	List<StateUnitTypeMaster> findByUnitTypeDescEngIgnoreCaseAndStateLgdCode(String unitName,StateLgdMaster stateLgdCode);
}
