package com.amnex.agristack.repository;

import java.util.List;

import com.amnex.agristack.entity.UserDefaultLanguageMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserDefaultLanguageMappingRepository extends JpaRepository<UserDefaultLanguageMapping, Long> {

	 List<UserDefaultLanguageMapping> findByUserMasterUserId(Long userId);

	@Query(value = "select distinct(language_id) from agri_stack.user_default_language_mapping where user_id in (select distinct(user_id) from agri_stack.user_village_mapping where village_lgd_code in (select village_lgd_code from agri_stack.user_village_mapping where user_id = :userId limit 1))", nativeQuery = true)
	Long findLangugaeIdByUserMaster(Long userId);
}
