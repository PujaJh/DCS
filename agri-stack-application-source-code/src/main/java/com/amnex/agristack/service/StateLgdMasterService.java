/**
 *
 */
package com.amnex.agristack.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.repository.StateLgdMasterRepository;
import com.amnex.agristack.utils.ResponseMessages;

import reactor.core.publisher.Mono;

/**
 * @author kinnari.soni
 *
 */

@Service
public class StateLgdMasterService {


	@Autowired
	private StateLgdMasterRepository stateLgdMasterRepository;

	/**
	 * Retrieves the list of states.
	 *
	 * @return The ResponseEntity containing the response.
	 */
	public ResponseEntity<Mono<?>> getStateList() {
		try {
			List<StateLgdMaster> stateLgdList = stateLgdMasterRepository.findAllByOrderByStateNameAsc();
			return new ResponseEntity<Mono<?>>(Mono.just(ResponseMessages.Toast(ResponseMessages.SUCCESS,
					"State list fetched successfully.", stateLgdList)), HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves a state by its LGD code.
	 *
	 * @param lgdCode The LGD code of the state.
	 * @return The StateLgdMaster object representing the state.
	 */
	public StateLgdMaster getStateByLGDCode(Long lgdCode) {
		return stateLgdMasterRepository.findByStateLgdCode(lgdCode);
	}
}
