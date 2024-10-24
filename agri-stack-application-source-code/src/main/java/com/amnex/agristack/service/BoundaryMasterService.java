/**
 *
 */
package com.amnex.agristack.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.amnex.agristack.dao.common.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.entity.BoundaryMaster;
import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.SubDistrictLgdMaster;
import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.repository.BoundaryLevelMasterRepository;
import com.amnex.agristack.repository.BoundaryMasterRepository;
import com.amnex.agristack.repository.DistrictLgdMasterRepository;
import com.amnex.agristack.repository.StateLgdMasterRepository;
import com.amnex.agristack.repository.SubDistrictLgdMasterRepository;
import com.amnex.agristack.repository.VillageLgdMasterRepository;

/**
 * @author kinnari.soni
 *
 */

@Service
public class BoundaryMasterService {

	@Autowired
	private BoundaryMasterRepository boundaryMasterRepository;

	@Autowired
	private BoundaryLevelMasterRepository boundaryLevelMasterRepository;

	@Autowired
	private StateLgdMasterRepository stateLgdMasterRepository;

	@Autowired
	private DistrictLgdMasterRepository districtLgdMasterRepository;

	@Autowired
	private SubDistrictLgdMasterRepository subDistrictLgdMasterRepository;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	/**
	 * @param boundaryDao
	 */
	public Boolean syncStateBoundaryData(List<StateMasterDAO> boundaryDao) {
		try {

			List<StateMasterDAO> pendingStateDataList = new ArrayList<StateMasterDAO>();

			List<StateLgdMaster> stateLgdMaster = stateLgdMasterRepository.findAll();
			for(StateMasterDAO dao : boundaryDao) {
				List<StateLgdMaster> masterStateData = stateLgdMaster.stream()
						.filter(b-> b.getStateLgdCode().equals(dao.getStateLgdCode()))
						.collect(Collectors.toList());

				if(masterStateData.size() > 0) {
					continue;
				}else {
					pendingStateDataList.add(dao);
				}
			}

			System.out.println("Final State list of missing record from List : " + pendingStateDataList.size());
			// Convert to entity
			if(pendingStateDataList.size() > 0) {
				List<StateLgdMaster> entity = convertStateDtoToEntity(pendingStateDataList);
				stateLgdMasterRepository.saveAll(entity);
				System.out.println("Boundary record saved successfully....");
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param boundaryDistrictDao
	 * @return
	 */
	public Boolean syncDistrictBoundaryData(List<DistrictMasterDAO> boundaryDistrictDao) {
		try {
			List<DistrictMasterDAO> pendingDistrictDataList = new ArrayList<DistrictMasterDAO>();

			List<DistrictLgdMaster> districtLgdMaster = districtLgdMasterRepository.findAll();
			for(DistrictMasterDAO dao : boundaryDistrictDao) {
				List<DistrictLgdMaster> masterDistrictData = districtLgdMaster.stream()
						.filter(b-> b.getDistrictLgdCode().equals(dao.getDistrictLgdCode()))
						.collect(Collectors.toList());

				if(masterDistrictData.size() > 0) {
					continue;
				}else {
					pendingDistrictDataList.add(dao);
				}
			}

			System.out.println("Final District list of missing record from List : " + pendingDistrictDataList.size());
			// Convert to entity
			if(pendingDistrictDataList.size() > 0) {
				List<DistrictLgdMaster> entity = convertDistrictDtoToEntity(pendingDistrictDataList);
				districtLgdMasterRepository.saveAll(entity);
				System.out.println("Boundary record saved successfully....");
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * @param boundarySubDistrictDao
	 * @return
	 */
	public Boolean syncSubDistrictBoundaryData(List<SubDistrictMasterDAO> boundarySubDistrictDao) {
		try {
			List<SubDistrictMasterDAO> pendingSubDistrictDataList = new ArrayList<SubDistrictMasterDAO>();

			List<SubDistrictLgdMaster> subDistrictLgdMaster = subDistrictLgdMasterRepository.findAll();
			for(SubDistrictMasterDAO dao : boundarySubDistrictDao) {
				List<SubDistrictLgdMaster> masterSubDistrictData = subDistrictLgdMaster.stream()
						.filter(b-> b.getSubDistrictLgdCode().equals(dao.getSubDistrictLgdCode()))
						.collect(Collectors.toList());

				if(masterSubDistrictData.size() > 0) {
					continue;
				}else {
					pendingSubDistrictDataList.add(dao);
				}
			}

			System.out.println("Final Sub-District list of missing record from List : " + pendingSubDistrictDataList.size());
			// Convert to entity
			if(pendingSubDistrictDataList.size() > 0) {
				List<SubDistrictLgdMaster> entity = convertSubDistrictDtoToEntity(pendingSubDistrictDataList);
				subDistrictLgdMasterRepository.saveAll(entity);
				System.out.println("Boundary record saved successfully....");
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * @param boundaryVillageDao
	 * @return
	 */
	public Boolean syncVillageBoundaryData(List<VillageMasterDAO> boundaryVillageDao) {
		try {
			List<VillageMasterDAO> pendingVillageDataList = new ArrayList<VillageMasterDAO>();

			List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository.findAll();
			for(VillageMasterDAO dao : boundaryVillageDao) {
				List<VillageLgdMaster> masterVillageData = villageLgdMaster.stream()
						.filter(b-> b.getVillageLgdCode().equals(dao.getVillageLgdCode()))
						.collect(Collectors.toList());

				if(masterVillageData.size() > 0) {
					continue;
				}else {
					pendingVillageDataList.add(dao);
				}
			}

			System.out.println("Final Village list of missing record from List : " + pendingVillageDataList.size());
			// Convert to entity
			if(pendingVillageDataList.size() > 0) {
				List<VillageLgdMaster> entity = convertVillageDtoToEntity(pendingVillageDataList);

				//				villageLgdMasterRepository.saveAll(entity);
				System.out.println("Boundary record saved successfully....");
			}

			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * @param daoList
	 * @return
	 */
	private List<StateLgdMaster> convertStateDtoToEntity(List<StateMasterDAO> daoList) {
		return daoList.stream()
				.map(dto -> new StateLgdMaster
						(dto.getStateLgdCode(),
								dto.getStateName()))
				.collect(Collectors.toList());
	}

	/**
	 * @param pendingDistrictDataList
	 * @return
	 */
	private List<DistrictLgdMaster> convertDistrictDtoToEntity(List<DistrictMasterDAO> pendingDistrictDataList) {
		return pendingDistrictDataList.stream()
				.map(dto -> new DistrictLgdMaster
						(dto.getDistrictLgdCode(),
								dto.getDistrictName(),
								stateLgdMasterRepository.findByStateLgdCode(dto.getStateLgdCode())))
				.collect(Collectors.toList());
	}


	/**
	 * @param pendingSubDistrictDataList
	 * @return
	 */
	private List<SubDistrictLgdMaster> convertSubDistrictDtoToEntity(List<SubDistrictMasterDAO> pendingSubDistrictDataList) {

		return pendingSubDistrictDataList.stream()
				.map(dto -> new SubDistrictLgdMaster
						(dto.getSubDistrictLgdCode(),
								dto.getSubDistrictName(),
								stateLgdMasterRepository.findByStateLgdCode(dto.getStateLgdCode()),
								districtLgdMasterRepository.findByDistrictLgdCode(dto.getDistrictLgdCode())))
				.collect(Collectors.toList());
	}


	/**
	 * @param pendingVillageDataList
	 * @return
	 */
	private List<VillageLgdMaster> convertVillageDtoToEntity(List<VillageMasterDAO> pendingVillageDataList) {
		List<VillageLgdMaster> finalList = new ArrayList<>();
		int size = pendingVillageDataList.size();
		int counter = 0;
		List<VillageLgdMaster> temp = new ArrayList<>();

		for (VillageMasterDAO dto : pendingVillageDataList) {
			temp.add(new VillageLgdMaster
					(dto.getVillageLgdCode(),
							dto.getVillageName(),
							stateLgdMasterRepository.findByStateLgdCode(dto.getStateLgdCode()),
							districtLgdMasterRepository.findByDistrictLgdCode(dto.getDistrictLgdCode()),
							subDistrictLgdMasterRepository.findBySubDistrictLgdCode(dto.getSubDistrictLgdCode())));
			if ((counter + 1) % 500 == 0 || (counter + 1) == size) {
				System.out.println("Entity converted ...."+ counter);
				//				finalList.addAll(temp);
				villageLgdMasterRepository.saveAll(temp);
				temp.clear();
			}
			counter++;
		}
		System.out.println("...............Entity converted successfully...."+ finalList.size());
		return finalList;

		//		return pendingVillageDataList.stream()
		//				.map(dto -> new VillageLgdMaster
		//						(dto.getVillageLgdCode(),
		//								dto.getVillageName(),
		//								stateLgdMasterRepository.findByStateLgdCode(dto.getStateLgdCode()),
		//								districtLgdMasterRepository.findByDistrictLgdCode(dto.getDistrictLgdCode()),
		//								subDistrictLgdMasterRepository.findBySubDistrictLgdCode(dto.getSubDistrictLgdCode())))
		//				.collect(Collectors.toList());
	}


	public Boolean syncBoundaryData(List<BoundaryMaster> boundaryDao) {
		try {
			System.out.println("Inside syncBoundaryData..");
			List<BoundaryMaster> pendingDataList = new ArrayList<BoundaryMaster>();
			List<BoundaryMaster> boundaryMaster = boundaryMasterRepository.findAll();

			for(BoundaryMaster dao : boundaryDao) {
				List<BoundaryMaster> masterStateData = boundaryMaster.stream()
						.filter(b-> b.getBoundaryLgdCode().equals(dao.getBoundaryLgdCode())
								&& b.getParentBoundaryLgdCode().equals(dao.getParentBoundaryLgdCode()))
						.collect(Collectors.toList());

				if(masterStateData.size() > 0) {
					continue;
				}else {
					pendingDataList.add(dao);
				}
			}

			System.out.println("Final State list of missing record from List : " + pendingDataList.size());
			//			 Convert to entity
			if(pendingDataList.size() > 0) {
				//				List<BoundaryMaster> entity = convertDtoToEntity(pendingDataList);
				//				boundaryMasterRepository.saveAll(pendingDataList);
				int size = pendingDataList.size();
				int counter = 0;
				List<BoundaryMaster> temp = new ArrayList<>();

				for (BoundaryMaster bm : pendingDataList) {
					temp.add(bm);
					if ((counter + 1) % 500 == 0 || (counter + 1) == size) {
						boundaryMasterRepository.saveAll(temp);
						temp.clear();
					}
					counter++;
				}
				System.out.println("Boundary record saved successfully....");
			}
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param pendingDataList
	 * @return
	 */
	private List<BoundaryMaster> convertDtoToEntity(List<BoundaryMasterDAO> pendingDataList) {

		return pendingDataList.stream()
				.map(dto -> new BoundaryMaster
						(dto.getBoundaryLgdCode(), //boundary lgd code
								dto.getBoundaryName(), //boundary name
								boundaryLevelMasterRepository.findByBoundaryLevelCode(dto.getBoundaryLevelCode()),
								dto.getParentBoundaryLgdCode(), //  parent ldg code
								boundaryLevelMasterRepository.findByBoundaryLevelCode(dto.getParentBoundaryLevelCode())
								))
				.collect(Collectors.toList());
	}

}
