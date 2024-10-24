package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.NonAgriPlotTypeMaster;


public interface NonAgriPlotTypeMasterRepository extends JpaRepository<NonAgriPlotTypeMaster, Long> {
	

	@Override
	Optional<NonAgriPlotTypeMaster> findById(Long id);
	
	@Query(value = "select * from agri_stack.non_agri_plot_type_master where is_active = true order by case when id = 9 then 2 else 1 end, type_name asc",nativeQuery = true)
	List<NonAgriPlotTypeMaster> findActiveNonAgriPlotTypeIds();
}
