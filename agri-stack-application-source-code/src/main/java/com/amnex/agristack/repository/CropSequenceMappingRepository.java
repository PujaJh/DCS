/**
 * 
 */
package com.amnex.agristack.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amnex.agristack.entity.CropSequenceMapping;

/**
 * @author majid.belim
 *
 */
public interface CropSequenceMappingRepository extends JpaRepository<CropSequenceMapping,Long>{
	
//	List<CropSequenceMapping> findOneByStateLgdCodeInAndIsStateLevel(List<Long> codes,Boolean isStateLevel);
	List<CropSequenceMapping> findOneBySubDistrictLgdCodeAndIsStateLevelAndSeasonId(Long codes,Boolean isStateLevel,Long seasonId);
//	List<CropSequenceMapping> findOneBySubDistrictLgdCodeAndIsStateLevelOrderBySequenceNumberAsc(Long code,Boolean isStateLevel);
	List<CropSequenceMapping> findOneBySubDistrictLgdCodeAndSeasonIdOrderBySequenceNumberAsc(Long code,Long seasonId);
	@Transactional
	public void deleteBySubDistrictLgdCodeIn(List<Long> subDistrictCodes);
	
	@Transactional
	public void deleteByStateLgdCodeIn(List<Long> stateLgdCodes);
}
