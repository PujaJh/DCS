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

import com.amnex.agristack.entity.SubDistrictLgdMaster;
import com.amnex.agristack.repository.SubDistrictLgdMasterRepository;
import com.amnex.agristack.utils.ResponseMessages;

import reactor.core.publisher.Mono;

/**
 * @author kinnari.soni
 *
 */

@Service
public class SubDistrictLgdMasterService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private SubDistrictLgdMasterRepository subDistrictLgdMasterRepository;

	/**
	 * Retrieves the list of sub-districts based on the state and district LGD codes.
	 *
	 * @param stateLgdCode    The LGD code of the state.
	 * @param districtLgdCode The LGD code of the district.
	 * @return The ResponseEntity containing the response.
	 */
	public ResponseEntity<Mono<?>> getSubDistrictList(Long stateLgdCode, Long districtLgdCode) {
		try {

			StringBuilder queryBuilder = new StringBuilder();
			Map<String, Object> queryParameter = new HashMap<String, Object>();

			queryBuilder.append("select *  from agri_stack.sub_district_lgd_master ");
			if (stateLgdCode != null) {
				queryBuilder.append(" where state_lgd_code = :stateLgdCode ");
				queryParameter.put("stateLgdCode", stateLgdCode);
			}

			if (districtLgdCode != null) {
				queryBuilder.append(" and district_lgd_code = :districtLgdCode ");
				queryParameter.put("districtLgdCode", districtLgdCode);
			}

			queryBuilder.append(" order by sub_district_name asc ");

			Query query = entityManager.createNativeQuery(queryBuilder.toString(), SubDistrictLgdMaster.class);
			for (Entry<String, Object> queryPar : queryParameter.entrySet()) {
				query.setParameter(queryPar.getKey(), queryPar.getValue());
			}

			List<SubDistrictLgdMaster> subDistrictLgdList = query.getResultList();

			return new ResponseEntity<Mono<?>>(Mono.just(ResponseMessages.Toast(ResponseMessages.SUCCESS,
					"Sub District list fetched successfully.", subDistrictLgdList)), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves a sub-district by its LGD code.
	 *
	 * @param lgdCode The LGD code of the sub-district.
	 * @return The SubDistrictLgdMaster object representing the sub-district.
	 */
	public SubDistrictLgdMaster getSubDistrictByLGDCode(Long lgdCode) {
		return subDistrictLgdMasterRepository.findBySubDistrictLgdCode(lgdCode);
	}

	/**
	 * Retrieves a list of sub-districts based on the LGD codes.
	 *
	 * @param lgdCodes The list of LGD codes of the sub-districts.
	 * @return The list of SubDistrictLgdMaster objects representing the sub-districts.
	 */
	public List<SubDistrictLgdMaster> getSubDistrictsByLGDCodes(List<Long> lgdCodes) {
		return subDistrictLgdMasterRepository.findByLgdCodes(lgdCodes);
	}

	/**
	 * Retrieves a list of sub-districts based on the sub-district LGD codes and district LGD code.
	 *
	 * @param subDistrictLgdCodes The list of LGD codes of the sub-districts.
	 * @param districtLgdCode     The LGD code of the district.
	 * @return The list of SubDistrictLgdMaster objects representing the sub-districts.
	 */
	public List<SubDistrictLgdMaster> getSubLgdMastersByLgdCodeInAndDistrictCode(List<Long> subDistrictLgdCodes,
			Long districtLgdCode) {
		return subDistrictLgdMasterRepository.findBySubDistrictLgdCodeInAndDistrictLgdCodeDistrictLgdCode(subDistrictLgdCodes,
				districtLgdCode);
	}

	/**
	 * Retrieves a list of sub-districts based on the sub-district LGD codes and district LGD codes.
	 *
	 * @param subDistrictLgdCodes The list of LGD codes of the sub-districts.
	 * @param districtLgdCodes    The list of LGD codes of the districts.
	 * @return The list of SubDistrictLgdMaster objects representing the sub-districts.
	 */
	public List<SubDistrictLgdMaster> getSubLgdMastersByLgdCodeInAndDistrictCodeIn
	(List<Long> subDistrictLgdCodes,List<Long> districtLgdCodes) {
		return subDistrictLgdMasterRepository.findBySubDistrictLgdCodeInAndDistrictLgdCodeDistrictLgdCodeIn(subDistrictLgdCodes,
				districtLgdCodes);
	}

}
