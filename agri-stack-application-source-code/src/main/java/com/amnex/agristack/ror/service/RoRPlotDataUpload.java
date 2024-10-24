/**
 * 
 */
package com.amnex.agristack.ror.service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.repository.*;
import com.amnex.agristack.service.FarmerLandOwnershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.entity.UPRoRDataUploadLog;
import com.amnex.agristack.ror.dao.FRPlotRegistryDAO;
import com.amnex.agristack.service.GeneralService;
import com.amnex.agristack.service.MediaMasterService;
import com.amnex.agristack.service.UploadRoRDataService;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;

/**
 * @author darshankumar.gajjar
 *
 */

@Service
public class RoRPlotDataUpload {

	@Autowired
	private StatePrefixMasterRepository statePrefixMasterRepository;

	@Autowired
	GeneralService generalService;

	@Autowired
	private FarmerLandOwnershipService farmerLandOwnershipService;

	@Autowired
	private FarmerRegistryRepository farmerRegistryRepository;

	@Autowired
	private DummyLandOwnerShipMasterRepository dummyLandOwnerShipMasterRepository;

	@Autowired
	private DummyLandRecordREpo dummyLandRecordREpo;

	@Autowired
	private OwnerTypeRepository ownerTypeRepository;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	private CommonUtil commonUtil;
	@Autowired
	private StateUnitTypeRepository stateUnitTypeRepository;
	@Autowired
	private IdentifierTypeRepository identifierTypeRepository;

	@Autowired
	private FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	private FarmerLandOwnershipRegistryRepository farmerLandOwnershipRegistryRepository;

	@Autowired
	private MediaMasterService mediaMasterService;
	@Autowired
	private RoleMasterRepository roleMasterRepository;
	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	private UploadLandAndOwnershipFileHistoryRepo uploadLandAndOwnershipFileHistoryRepo;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UPRoRDataUploadLogRepository roRDataUploadLogRepository;

	@Autowired
	private UploadRoRDataService uploadRoRDataService;

	@Value("${media.folder.document}")
	private String folderDocument;

	@Value("${file.upload-dir}")
	private String path;

	/***
	 * UPLOAD DATA FOR UP STATE FROM FARMER REGISTRY
	 **/

	public ResponseModel uploadUPPlotOwnerDetailsFromFR() {
		try {

			new Thread() {
				public void run() {

					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository.findByVillageLgdCode(171841L);

//					List<UPRoRDataUploadLog> allVillageLgd = roRDataUploadLogRepository
//							.findByIsUploadedIsNullAndIsActiveTrue();

					System.out.println("RoR data Upload Started for count :: " + allVillageLgd.size());

					allVillageLgd.stream().forEach(x -> {
						System.out.println("Initiated for :: " + x.getVillageLgdCode());
						fetchUPPlotOwnerDetailsFromFRByVillageCode(Integer.valueOf(x.getVillageLgdCode().toString()),
								true);
					});

					System.out.println("RoR data Upload Completed");

				}
			}.start();

			return new ResponseModel(null, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	protected void fetchUPPlotOwnerDetailsFromFRByVillageCode(Integer villageLgdCode, boolean fullUpload) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = "https://agristack.gov.in/fetchUPRoRDataV1/" + villageLgdCode;

			ResponseEntity<FRPlotRegistryDAO[]> responseEntity = restTemplate.getForEntity(url,
					FRPlotRegistryDAO[].class);

			if (responseEntity != null) {

				FRPlotRegistryDAO[] stateRoRDAOs = responseEntity.getBody();

				if (stateRoRDAOs != null && stateRoRDAOs.length > 0) {

					if (fullUpload == true) {
						farmerLandOwnershipRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(villageLgdCode));
						farmlandPlotRegistryRepository.deleteAllByVillageLgdCode(Long.valueOf(villageLgdCode));
					}

					List<FRPlotRegistryDAO> plotDAOs = Arrays.asList(stateRoRDAOs);

					importAllDataToRegistryTable(plotDAOs, fullUpload);

				} else {
					List<UPRoRDataUploadLog> rorLogs = roRDataUploadLogRepository
							.findByVillageLgdCode(Long.valueOf(villageLgdCode));
					for (UPRoRDataUploadLog upRoRDataUploadLog : rorLogs) {
						upRoRDataUploadLog.setIsUploaded(false);
						upRoRDataUploadLog.setCount(0);
						upRoRDataUploadLog.setUploadedOn(new Timestamp(new Date().getTime()));
						roRDataUploadLogRepository.save(upRoRDataUploadLog);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void importAllDataToRegistryTable(List<FRPlotRegistryDAO> plotDAOs, boolean fullUpload) {
		try {
			Integer villageCode = plotDAOs.get(0).getVillageLgdCode();
			List<String> existingFarmlandIds = farmlandPlotRegistryRepository
					.findAllByFprFarmlandIdByFprVillageLgdCode(Long.valueOf(villageCode));

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
