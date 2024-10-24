package com.amnex.agristack.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.Enum.MenuTypeEnum;
import com.amnex.agristack.entity.MenuMaster;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuMasterRepository extends JpaRepository<MenuMaster, String> {
	@Override
	List<MenuMaster> findAll();

	List<MenuMaster> findByIsActiveTrueAndIsDeletedFalse();
	Set<MenuMaster> findByIsDeletedFalseAndIsActiveTrue();

	List<MenuMaster> findByIsDeletedFalse();

	Set<MenuMaster> findByIsActiveTrueAndIsDeletedFalseAndMenuIdIn(List<Long> ids);

	Optional<MenuMaster> findByIsActiveTrueAndIsDeletedFalseAndMenuId(Long id);

	List<MenuMaster> findByIsActiveTrueAndIsDeletedFalseAndMenuParentIdIn(List<Long> ids);

	List<MenuMaster> findByIsActiveTrueAndIsDeletedFalseAndMenuType(MenuTypeEnum menuTypeEnum);

	@Query(value = "select * from agri_stack.menu_master where is_active = true and " +
			"is_deleted = false and " +
			"menu_type = :menuTypeEnum and " +
			"((:projectType is null ) OR :projectType in (SELECT regexp_split_to_table\\:\\:text " +
			"                                                            from regexp_split_to_table(''||project_type||'',','))) order By menu_name ASC", nativeQuery = true)
	List<MenuMaster> findMenuTypeWihProject(@Param("menuTypeEnum") String menuTypeEnum,@Param("projectType") String projectType);

	Set<MenuMaster> findByIsActiveAndIsDeleted(Boolean isActive,Boolean isDeleted);

    MenuMaster findByMenuId(Long menuId);

	Optional<MenuMaster> findByIsDeletedAndMenuId(Boolean aFalse, Long menuId);
}
