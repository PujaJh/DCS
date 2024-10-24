package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.DummyLandOwnerShipMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DummyLandOwnerShipMasterRepository extends JpaRepository<DummyLandOwnerShipMaster, Long> {

	public List<DummyLandOwnerShipMaster> findByVillageLgdCode(Long villageLgdCode);

	@Modifying
	@Transactional
	@Query(value = "DELETE from agri_stack.uploaded_land_ownership_master where village_code =:villageLgdCode", nativeQuery = true)
	public void deleteAllByVillageCode(Long villageLgdCode);
}
