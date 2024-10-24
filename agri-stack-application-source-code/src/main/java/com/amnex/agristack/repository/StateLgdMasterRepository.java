/**
 *
 */
package com.amnex.agristack.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.StateLgdMaster;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author kinnari.soni
 *
 */
public interface StateLgdMasterRepository extends JpaRepository<StateLgdMaster, Long> {

	/**
	 * @param stateLgdCode
	 * @return
	 */
	StateLgdMaster findByStateLgdCode(Long stateLgdCode);

	/**
	 * @return
	 */
	List<StateLgdMaster> findAllByOrderByStateNameAsc();

	@Query("select s from StateLgdMaster s where s.stateLgdCode in (:stateLgdCodes) order by s.stateName ASC" )
	List<StateLgdMaster> findAllByStateLgdCodeIn(@Param("stateLgdCodes") List<Long> stateLgdCodes);
	
	@Query("select s from StateLgdMaster s where s.stateLgdCode in (:stateLgdCodes) order by s.stateName ASC" )
	List<StateLgdMaster> findAllByStateLgdCodeIn(@Param("stateLgdCodes") Set<Long> stateLgdCodes);



	@Query(value = "select  state_lgd_code from agri_stack.state_lgd_master where state_name = :stateName", nativeQuery = true)
	public String  getStateLgdCode(String stateName);

}
