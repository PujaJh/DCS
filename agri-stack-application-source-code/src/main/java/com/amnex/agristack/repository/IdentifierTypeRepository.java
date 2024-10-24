/**
 *
 */
package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.IdentifierTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author kinnari.soni
 *
 */
public interface IdentifierTypeRepository extends JpaRepository<IdentifierTypeMaster, Long>{

	@Query("select itm.identifierTypeDescEng from IdentifierTypeMaster itm where itm.isDeleted is false ")
	public List<String> findIdentifierType();

	IdentifierTypeMaster findByIdentifierTypeDescEngIgnoreCase(String IdentifierTypeName);
	@Query("select gm from IdentifierTypeMaster gm where gm.isDeleted is false order by gm.identifierTypeDescEng ")
	public List<IdentifierTypeMaster> findAllByOrderByIdentifierTypeDescEng();

	public List<IdentifierTypeMaster> findAllByIsDeletedFalse();
}


