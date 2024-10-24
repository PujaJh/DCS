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

import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.repository.DistrictLgdMasterRepository;
import com.amnex.agristack.utils.ResponseMessages;

import reactor.core.publisher.Mono;

/**
 * @author kinnari.soni
 *
 */

@Service
public class DistrictLgdMasterService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private DistrictLgdMasterRepository districtRepository;

	/**
	 * Retrieves the list of districts based on the state LGD code.
	 * @param stateLgdCode The state LGD code
	 * @return ResponseEntity containing the list of districts
	 */
	public ResponseEntity<Mono<?>> getDistrictList(Long stateLgdCode) {
		try {
			StringBuilder queryBuilder = new StringBuilder();
			Map<String, Object> queryParameter = new HashMap<String, Object>();

			queryBuilder.append("select *  from agri_stack.district_lgd_master ");
			if (stateLgdCode != null) {
				queryBuilder.append(" where state_lgd_code = :stateLgdCode ");
				queryParameter.put("stateLgdCode", stateLgdCode);
			}

			queryBuilder.append(" order by district_name asc ");

			Query query = entityManager.createNativeQuery(queryBuilder.toString(), DistrictLgdMaster.class);
			for (Entry<String, Object> queryPar : queryParameter.entrySet()) {
				query.setParameter(queryPar.getKey(), queryPar.getValue());
			}
			List<DistrictLgdMaster> districtLgdList = query.getResultList();
			return new ResponseEntity<Mono<?>>(Mono.just(ResponseMessages.Toast(ResponseMessages.SUCCESS,
					"District list fetched successfully.", districtLgdList)), HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves the DistrictLgdMaster object based on the LGD code.
	 * @param lgdCode The LGD code
	 * @return DistrictLgdMaster object
	 */
	public DistrictLgdMaster getDistrictByLGDCode(Long lgdCode) {
		return districtRepository.findByDistrictLgdCode(lgdCode);
	}

	/**
	 * Retrieves the list of DistrictLgdMaster objects based on the LGD codes.
	 * @param districtLgdCodes The LGD codes of districts
	 * @return List of DistrictLgdMaster objects
	 */
	public List<DistrictLgdMaster>getDistrictByLgdCodes(List<Long> districtLgdCodes){
		return districtRepository.findAllByDistrictLgdCode(districtLgdCodes);
	}
}
