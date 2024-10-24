package com.amnex.agristack.ror.service;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.NicOwnerLog;
import com.amnex.agristack.repository.NicOwnerLogRepository;
import com.amnex.agristack.utils.CustomMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class NicDataUploadService {
	@Autowired
	private NicOwnerLogRepository nicOwnerLogRepository;

	public ResponseModel OwnerDataUpload() {
		try {
			String key = "UtaDataAgriData";

			new Thread() {
				@Override
				public void run() {
					List<NicOwnerLog> allVillageLgd = nicOwnerLogRepository.findByIsUploadedIsNullAndIsActiveTrue();

//

					System.out.println("RoR data Upload Started for villages count :: " + allVillageLgd.size());

					allVillageLgd.stream().forEach(x -> {
						System.out.println("Initiated for :: " + x.getVillageLgdCode());
						System.out.println("Data Upload for start for :: " + x.getVillageLgdCode());
						nicOwnerLogRepository.uploadOwnerDetail(x.getVillageLgdCode());
						x.setIsUploaded(true);
						nicOwnerLogRepository.save(x);
						System.out.println("Data Upload for complete for :: " + x.getVillageLgdCode());
					});

					System.out.println("RoR data Upload Completed");

				}
			}.start();

//			return fetchUPVillageDataByVillageCode(villageLgdCode, key);
			return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

}
