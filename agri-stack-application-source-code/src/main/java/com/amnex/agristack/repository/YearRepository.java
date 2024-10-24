package com.amnex.agristack.repository;

import com.amnex.agristack.dao.YearCountDAO;
import com.amnex.agristack.entity.CropStatusMaster;
import com.amnex.agristack.entity.YearMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface YearRepository extends JpaRepository<YearMaster, Long> {

	List<YearMaster> findAll();
	List<YearMaster> findByOrderByStartYearAsc();
	@Query(value = "select y.* from agri_stack.year_master y  order by y.id desc limit 1", nativeQuery = true)
	YearMaster findCurrentYear();

    Optional<YearMaster> findByStartYearAndEndYear(String startingYear, String endingYear);
    
    YearMaster findByEndYear(String endingYear);
    
    @Query(value = "select count(end_year) total,id  id,end_year endYear,start_Year startYear from agri_stack.year_master " + 
    		"GROUP BY  id,end_year " + 
    		"HAVING year_master.end_year >= :year " + 
    		"order by end_year asc", nativeQuery = true)
	List<YearCountDAO> getAllYears(String year);

	YearMaster findByYearValue(String yearValue);

    YearMaster findByIsCurrentYearIsTrue();
}
