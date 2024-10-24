package com.amnex.agristack.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.entity.CultivatorMaster;
import com.amnex.agristack.entity.CultivatorTypeMaster;
import com.amnex.agristack.repository.CultivatorMasterRepository;
import com.amnex.agristack.repository.CultivatorTypeRepository;
import com.amnex.agristack.utils.CustomMessages;

@Service
public class CultivatorService {

	@Autowired
	private CultivatorTypeRepository cultivatorTypeRepository;

	@Autowired
	private CultivatorMasterRepository cultivatorMasterRepository;

	public Object getCultivatorListByVillageCodes(HttpServletRequest request, CommonRequestDAO dao) {
		try {
			List<CultivatorMaster> cultivatorMaster = cultivatorMasterRepository
					.findByIsActiveTrueAndIsDeletedFalseAndVillageLgdCodeIn(dao.getVillageLgdCodeList());

			return new ResponseModel(cultivatorMaster, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);

		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	public ResponseModel getCultivatorTypes() {
		try {
			List<CultivatorTypeMaster> cultivatorTypeMaster = cultivatorTypeRepository
					.findByIsActiveTrueAndIsDeletedFalse();

			return new ResponseModel(cultivatorTypeMaster, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);

		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

}
