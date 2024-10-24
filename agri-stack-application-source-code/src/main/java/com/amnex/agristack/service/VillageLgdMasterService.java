/**
 *
 */
package com.amnex.agristack.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.repository.VillageLgdMasterRepository;
import com.amnex.agristack.utils.ResponseMessages;

import reactor.core.publisher.Mono;

/**
 * @author kinnari.soni
 *
 */

@Service
public class VillageLgdMasterService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private VillageLgdMasterRepository villageRepository;

	/**
	 * Retrieves the list of villages based on the state LGD code, district LGD code, and sub-district LGD code.
	 *
	 * @param stateLgdCode       The LGD code of the state.
	 * @param districtLgdCode    The LGD code of the district.
	 * @param subDistrictLgdCode The LGD code of the sub-district.
	 * @return A ResponseEntity containing Mono wrapping the response with the list of villages.
	 */
	public ResponseEntity<Mono<?>> getVillageList(Long stateLgdCode, Long districtLgdCode,Long subDistrictLgdCode) {
		try {
			StringBuilder queryBuilder = new StringBuilder();
			Map<String, Object> queryParameter = new HashMap<String, Object>();

			queryBuilder.append("select *  from agri_stack.village_lgd_master ");
			if (stateLgdCode != null) {
				queryBuilder.append(" where state_lgd_code = :stateLgdCode ");
				queryParameter.put("stateLgdCode", stateLgdCode);
			}

			if (districtLgdCode != null) {
				queryBuilder.append(" and district_lgd_code = :districtLgdCode ");
				queryParameter.put("districtLgdCode", districtLgdCode);
			}

			if (subDistrictLgdCode != null) {
				queryBuilder.append(" and sub_district_lgd_code = :subDistrictLgdCode ");
				queryParameter.put("subDistrictLgdCode", subDistrictLgdCode);
			}

			queryBuilder.append(" order by village_name asc ");

			Query query = entityManager.createNativeQuery(queryBuilder.toString(), VillageLgdMaster.class);
			for (Entry<String, Object> queryPar : queryParameter.entrySet()) {
				query.setParameter(queryPar.getKey(), queryPar.getValue());
			}

			List<VillageLgdMaster> villageLgdList = query.getResultList();

			return new ResponseEntity<Mono<?>>(Mono.just(ResponseMessages.Toast(ResponseMessages.SUCCESS,
					"Village list fetched successfully.", villageLgdList)), HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves a village by its LGD code.
	 *
	 * @param lgdCode The LGD code of the village.
	 * @return The VillageLgdMaster object representing the village.
	 */
	public VillageLgdMaster getVillageByLGDCode(Long lgdCode) {
		return villageRepository.findByVillageLgdCode(lgdCode);
	}

	/**
	 * Retrieves villages by their LGD codes and sub-district LGD code.
	 *
	 * @param villageCodes    The list of LGD codes of the villages.
	 * @param SubDistrictCode The LGD code of the sub-district.
	 * @return The list of VillageLgdMaster objects representing the villages.
	 */
	public List<VillageLgdMaster> getVillageByLGDCodesAndSubDistrictCode(List<Long> villageCodes,Long SubDistrictCode ) {
		return villageRepository.findByVillageLgdCodeInAndSubDistrictLgdCodeSubDistrictLgdCode(villageCodes, SubDistrictCode);
	}

	/**
	 * Retrieves villages by their LGD codes and multiple sub-district LGD codes.
	 *
	 * @param villageCodes     The list of LGD codes of the villages.
	 * @param SubDistrictCodes The list of LGD codes of the sub-districts.
	 * @return The list of VillageLgdMaster objects representing the villages.
	 */
	public List<VillageLgdMaster> getVillageByLGDCodesAndSubDistrictCodes(List<Long> villageCodes,List<Long> SubDistrictCodes ) {
		return villageRepository.findByVillageLgdCodeInAndSubDistrictLgdCodeSubDistrictLgdCodeIn(villageCodes, SubDistrictCodes);
	}

}
