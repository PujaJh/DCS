package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OwnerTypeRepository extends JpaRepository<OwnerType, Long> {

	public List<OwnerType> findByIsDeletedFalse();

	@Query("select owt.ownerTypeDescEng from  OwnerType owt where owt.isDeleted is false")
	public List<String> getOwnerTypes();

	public OwnerType findByOwnerTypeDescEng(String ownerTypeDescEng);

}
