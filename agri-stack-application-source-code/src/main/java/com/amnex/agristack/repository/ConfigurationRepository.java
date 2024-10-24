package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.entity.ConfigurationMaster;

public interface ConfigurationRepository extends JpaRepository<ConfigurationMaster, Long> {

	List<ConfigurationMaster> findByIsActiveTrueAndIsDeletedFalse();

	// Make Query For Set To ConfigCode Asc
	List<ConfigurationMaster> findByIsActiveTrueAndIsDeletedFalseOrderByConfigCodeAsc();

	Optional<ConfigurationMaster> findByIsActiveTrueAndIsDeletedFalseAndConfigCode(ConfigCode configCode);

	Optional<ConfigurationMaster> findByConfigKey(String configKey);

	Optional<ConfigurationMaster> findByConfigCode(Integer configCode);
	
	Optional<ConfigurationMaster> findByConfigCode(ConfigCode configCode);

	List<ConfigurationMaster> findByIsActiveTrueAndIsDeletedFalseAndConfigCodeIn(List<ConfigCode> configCode);
	
	@Query(nativeQuery = true,value="	" + 
			"	select ST_Distance(ST_GeogFromText(st_astext(fpr_plot_geometry,4326)),ST_GeogFromText(st_astext(ST_SetSRID(ST_MakePoint(:surveyorLong,  :surveyorLat), 4326),4326))) >CAST(cm.config_value AS INTEGER)  as is_accuracy " + 
			"			from agri_stack.farmland_plot_registry flpr " + 
			"			 inner join agri_stack.configuration_master cm on cm.config_code=3 " + 
			"			where flpr.fpr_farmland_plot_registry_id=:parcelId ")
	Boolean checkAccuracy(    Double surveyorLat, Double surveyorLong,Long parcelId);
	
	
}