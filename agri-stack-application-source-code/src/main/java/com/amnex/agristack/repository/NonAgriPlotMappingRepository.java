package com.amnex.agristack.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.amnex.agristack.entity.NonAgriPlotMapping;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NonAgriPlotMappingRepository extends JpaRepository<NonAgriPlotMapping, Long> {

	Object findAllBySeasonIdStartYearEndYearAndSubDistrictLGDCodeWithPagination = null;

	@Modifying
	@Query("DELETE FROM NonAgriPlotMapping n WHERE  n.season.seasonId = :seasonId AND n.startYear = :startYear AND n.endYear = :endYear "
			+ " AND n.landParcelId IN (SELECT f.farmlandPlotRegistryId "
			+ " from FarmlandPlotRegistry f where f.villageLgdMaster.villageLgdCode = :villageLgdCode)")
	void deleteBySeasonIdStartYearEndYearAndVillageLGDCode(Long seasonId, Integer startYear, Integer endYear,
			Long villageLgdCode);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM NonAgriPlotMapping n WHERE  n.season.seasonId = :seasonId AND n.startYear = :startYear AND n.endYear = :endYear "
			+ " AND n.landParcelId IN (SELECT f.farmlandPlotRegistryId "
			+ " from FarmlandPlotRegistry f where f.villageLgdMaster.villageLgdCode = :villageLgdCode and f.farmlandId=:farmlandId )")
	void deleteBySeasonIdStartYearEndYearAndVillageLGDCodeFarmLandId(Long seasonId, Integer startYear, Integer endYear,
			Long villageLgdCode,String farmlandId);
	
	
	@Query("select n FROM NonAgriPlotMapping n WHERE  n.season.seasonId = :seasonId AND n.startYear = :startYear AND n.endYear = :endYear "
			+ " AND n.landParcelId.villageLgdMaster.villageLgdCode in (:villageLgdCodes) ")
	List<NonAgriPlotMapping> findAllBySeasonIdStartYearEndYearAndVillageLGDCode(Long seasonId, Integer startYear, Integer endYear,
			List<Long> villageLgdCodes);
	

	@Query("select n FROM NonAgriPlotMapping n WHERE  n.season.seasonId = :seasonId AND n.startYear = :startYear AND n.endYear = :endYear "
			+ " AND n.landParcelId.villageLgdMaster.subDistrictLgdCode.subDistrictLgdCode in (:subDistrictLgdCodes) ")
	List<NonAgriPlotMapping> findAllBySeasonIdStartYearEndYearAndSubDistictLGDCode(Long seasonId, Integer startYear, Integer endYear,
			List<Long> subDistrictLgdCodes);
	
	@Query("select n FROM NonAgriPlotMapping n "
			+ "inner join UserVillageMapping uvm on uvm.userMaster.userId=:userId  "
			+ " WHERE  n.season.seasonId = :seasonId AND n.startYear = :startYear AND n.endYear = :endYear "
			+ " AND n.landParcelId.villageLgdMaster.villageLgdCode in (uvm.villageLgdMaster.villageLgdCode) ")
	List<NonAgriPlotMapping> findAllBySeasonIdStartYearEndYearAndVillageLGDCodeByUserId(Long seasonId, Integer startYear, Integer endYear,
			Long userId);

	@Query("select n FROM NonAgriPlotMapping n WHERE  n.season.seasonId = :seasonId AND n.startYear = :startYear AND n.endYear = :endYear "
			+ " AND n.landParcelId.villageLgdMaster.subDistrictLgdCode.subDistrictLgdCode in (:subDistrictLgdCodes) " +
			" AND ((:villageLgdCodes) is null OR n.landParcelId.villageLgdMaster.villageLgdCode in (:villageLgdCodes)) ")
	List<NonAgriPlotMapping> findAllBySeasonIdStartYearEndYearAndSubDistrictLGDCode(Long seasonId, Integer startYear,
			Integer endYear, List<Long> subDistrictLgdCodes,@Param("villageLgdCodes") List<Long> villageLgdCodes );
	
	@Query("select n FROM NonAgriPlotMapping n WHERE  n.season.seasonId = :seasonId AND n.startYear = :startYear AND n.endYear = :endYear "
			+ " AND n.landParcelId.villageLgdMaster.subDistrictLgdCode.subDistrictLgdCode in (:subDistrictLgdCodes) " +
			" AND ((:villageLgdCodes) is null OR n.landParcelId.villageLgdMaster.villageLgdCode in (:villageLgdCodes)) and" +
			"(" +
			" :search is null OR ( LOWER(n.landParcelId.farmlandId) like %:search% OR LOWER(n.landParcelId.surveyNumber) like %:search% " +
			" OR LOWER(n.landParcelId.subSurveyNumber) like %:search% ))" )
	Page<NonAgriPlotMapping> findAllBySeasonIdStartYearEndYearAndSubDistrictLGDCodeWithPagination(@Param("search") String search,Long seasonId, Integer startYear,

			Integer endYear, List<Long> subDistrictLgdCodes,@Param("villageLgdCodes") List<Long> villageLgdCodes,Pageable pageable );

	@Query("select n FROM NonAgriPlotMapping n WHERE  n.season.seasonId = :seasonId AND n.startYear = :startYear AND n.endYear = :endYear "
			+ " AND n.landParcelId.villageLgdMaster.villageLgdCode = :villageLgdCode order by n.landParcelId.landParcelId asc ")
	List<NonAgriPlotMapping> findAllBySeasonIdStartYearEndYearAndVillageLgdCode(Long seasonId, Integer startYear,
			Integer endYear, Long villageLgdCode);
	
	
	@Query("select n FROM NonAgriPlotMapping n WHERE n.landParcelId.villageLgdMaster.villageLgdCode in (:villageLgdCodes) and n.isActive=true and isDeleted=false ")
	List<NonAgriPlotMapping> findAllByVillageLGDCodeIn(List<Long> villageLgdCodes);
	
	@Query("select n FROM NonAgriPlotMapping n WHERE n.landParcelId.villageLgdMaster.subDistrictLgdCode.subDistrictLgdCode in (:SubDistirctCodes) and n.isActive=true and isDeleted=false ")
	List<NonAgriPlotMapping> findAllBySubDistirctIn(List<Long> SubDistirctCodes);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM NonAgriPlotMapping n WHERE  n.naPlotId IN (:naPlotIds) ")
	void deleteByNAPlotIds(List<Long> naPlotIds);
	
	
	
}
