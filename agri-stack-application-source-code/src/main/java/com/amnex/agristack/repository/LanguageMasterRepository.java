package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.LanguageMaster;

/**
 * @author janmaijaysingh.bisen
 *
 */


public interface LanguageMasterRepository extends JpaRepository<LanguageMaster, Long > {

	public List<LanguageMaster>findByIsActiveAndIsDeleted(boolean isActive,boolean isDeleted);
}
