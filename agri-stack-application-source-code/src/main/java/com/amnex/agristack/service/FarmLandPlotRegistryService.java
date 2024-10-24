package com.amnex.agristack.service;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.FarmLandPlotRegistryDto;
import com.amnex.agristack.entity.DummyLandRecords;
import com.amnex.agristack.entity.FarmlandPlotRegistry;
import com.amnex.agristack.repository.DummyLandRecordREpo;
import com.amnex.agristack.repository.FarmlandPlotRegistryRepository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.repository.VillageLgdMasterRepository;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.Verhoeff;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.vividsolutions.jts.io.ParseException;

/**
 *
 * @author janmaijaysingh.bisen
 *
 */

@Service
public class FarmLandPlotRegistryService {

	@Autowired
	CommonUtil commonUtil;

	private FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	private DummyLandRecordREpo dummyLandRecordREpo;
	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@PostConstruct
	public void init() throws InvalidFormatException, IOException, ParseException {
		// readExcel();
		/*
		 * for(int i=0;i<50;i++) {
		 * System.out.println(Verhoeff.getFarmerUniqueIdWithChecksum()); }
		 */
	}

	public void readExcel() throws InvalidFormatException, IOException, ParseException {
		List<DummyLandRecords> dummyLandRecords = dummyLandRecordREpo.findAll();
		for (DummyLandRecords d : dummyLandRecords) {
			VillageLgdMaster villageLgdMaster = villageLgdMasterRepository.findByVillageLgdCode(d.getVillageLgdCode());
			if (villageLgdMaster != null) {
				FarmlandPlotRegistry farmlandPlotRegistry = new FarmlandPlotRegistry();
				String parcelId = d.getSurveyNumber() + "/" + d.getSubSurveyNumber();
				if (parcelId.length() >= 15) {
					farmlandPlotRegistry.setLandParcelId(parcelId.substring(0, 14));
				} else {
					farmlandPlotRegistry.setLandParcelId(parcelId);
				}
				farmlandPlotRegistry.setVillageLgdMaster(villageLgdMaster);
				String uniqueNumber = commonUtil
						.genrateLandParcelId(villageLgdMaster.getStateLgdCode().getStateLgdCode());
				farmlandPlotRegistry.setFarmlandId(uniqueNumber);
				// WKTReader wk = new WKTReader();
				// Geometry geometry = wk.read(d.getGeom());
				// geometry.getArea();
				farmlandPlotRegistry.setPlotGeometry(d.getGeom());
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.CEILING);
				farmlandPlotRegistry.setPlotArea(
						Double.valueOf(df.format(Double.valueOf(df.format(d.getGeom().getArea())) / 10000)));
				farmlandPlotRegistry.setSurveyNumber(d.getSurveyNumber());
				farmlandPlotRegistry.setSubSurveyNumber(d.getSubSurveyNumber());
				farmlandPlotRegistryRepository.save(farmlandPlotRegistry);

			}
		}

	}

	/*
	 * public void addTOLandData() { List<DummyLandRecords> dummyLandRecords =
	 * dummyLandRecordREpo.findAll(); for (DummyLandRecords d : dummyLandRecords) {
	 * int i=1; i++; System.out.println("final indec value"+i); //VillageLgdMaster
	 * villageLgdMaster =
	 * villageLgdMasterRepository.findByVillageLgdCode(d.getVillageLgdCode());
	 * List<VillageLgdMaster>
	 * villageLgdMasters=villageLgdMasterRepository.getVillageByName(d.
	 * getVillageName().toLowerCase()); if(villageLgdMasters.size()==1) {
	 * 
	 * 
	 * VillageLgdMaster v=villageLgdMasters.get(0); FarmlandPlotRegistry
	 * farmlandPlotRegistry=new FarmlandPlotRegistry();
	 * farmlandPlotRegistry.setVillageLgdMaster(villageLgdMasters.get(0));
	 * farmlandPlotRegistry.setPlotGeometry(d.getGeom()); //
	 * farmlandPlotRegistry.setPlotArea(); Formatter fm=new Formatter();
	 * fm.format("%.2f",d.getGeom().getArea()); System.out.println(fm); Double d1=
	 * Double.parseDouble(fm.toString()); // farmlandPlotRegistry.setPlotArea(d1);
	 * fm.close(); String parcelId = d.getSurveyNumber() + "/" +
	 * d.getSubSurveyNumber(); if(parcelId.length()>=15) {
	 * farmlandPlotRegistry.setLandParcelId(parcelId.substring(0,13));
	 * 
	 * }else { farmlandPlotRegistry.setLandParcelId(parcelId); }
	 * //farmlandPlotRegistry.setLandParcelId(parcelId.substring(0,13));
	 * farmlandPlotRegistry.setSubSurveyNumber(d.getSubSurveyNumber());
	 * farmlandPlotRegistry.setSurveyNumber(d.getSurveyNumber()); String
	 * uniqueNumber = commonUtil
	 * .genrateLandParcelId(v.getStateLgdCode().getStateLgdCode());
	 * farmlandPlotRegistry.setFarmlandId(uniqueNumber);
	 * farmlandPlotRegistryRepository.save(farmlandPlotRegistry);
	 * 
	 * }else { List<VillageLgdMaster> vds=
	 * villageLgdMasters.stream().filter(v->v.getDistrictLgdCode().getDistrictName()
	 * .toLowerCase().equals(d.getDistrictName().toLowerCase())).collect(Collectors.
	 * toList()); FarmlandPlotRegistry farmlandPlotRegistry=new
	 * FarmlandPlotRegistry(); if(vds.size()>0) { VillageLgdMaster vd=vds.get(0);
	 * farmlandPlotRegistry.setVillageLgdMaster(villageLgdMasters.get(0));
	 * farmlandPlotRegistry.setPlotGeometry(d.getGeom());
	 * //farmlandPlotRegistry.setPlotArea(d.getGeom().getArea()); Formatter fm=new
	 * Formatter(); fm.format("%.2f",d.getGeom().getArea()); System.out.println(fm);
	 * Double d1= Double.parseDouble(fm.toString()); //
	 * farmlandPlotRegistry.setPlotArea(d1); fm.close();
	 * farmlandPlotRegistry.setSubSurveyNumber(d.getSubSurveyNumber());
	 * farmlandPlotRegistry.setSurveyNumber(d.getSurveyNumber()); String parcelId =
	 * d.getSurveyNumber() + "/" + d.getSubSurveyNumber(); if(parcelId.length()>=15)
	 * { farmlandPlotRegistry.setLandParcelId(parcelId.substring(0,13));
	 * 
	 * }else { farmlandPlotRegistry.setLandParcelId(parcelId); } String uniqueNumber
	 * = commonUtil .genrateLandParcelId(vd.getStateLgdCode().getStateLgdCode());
	 * farmlandPlotRegistry.setFarmlandId(uniqueNumber);
	 * farmlandPlotRegistryRepository.save(farmlandPlotRegistry); } } } }
	 */
	public FarmLandPlotRegistryService(FarmlandPlotRegistryRepository farmlandPlotRegistryRepository) {
		this.farmlandPlotRegistryRepository = farmlandPlotRegistryRepository;
	}

	public FarmlandPlotRegistry addFarmLandPlotDetails(FarmLandPlotRegistryDto farmLandPlotRegistryDto) {
		FarmlandPlotRegistry farmlandPlotRegistry = null;
		if (farmLandPlotRegistryDto.getFarmLandPlotRegisterId() != null) {
			Optional<FarmlandPlotRegistry> farmlandPlotRegistryOptional = farmlandPlotRegistryRepository
					.findById(farmLandPlotRegistryDto.getFarmLandPlotRegisterId());
			farmlandPlotRegistry = farmlandPlotRegistryOptional.get();
			// farmlandPlotRegistry;
		} else {
			// if(farmlandPlotRegistryOptional!=null &&
			// !farmlandPlotRegistryOptional.isPresent()) {
			farmlandPlotRegistry = new FarmlandPlotRegistry(farmLandPlotRegistryDto);
			farmlandPlotRegistry.setLandParcelId(
					farmLandPlotRegistryDto.getSurveyNumber() + "/" + farmLandPlotRegistryDto.getSubSurveyNumber());
			farmlandPlotRegistry.setSurveyNumber(farmLandPlotRegistryDto.getSurveyNumber());
			farmlandPlotRegistry.setSubSurveyNumber(farmLandPlotRegistryDto.getSubSurveyNumber());
			String uniqueNumber = commonUtil
					.genrateLandParcelId(farmLandPlotRegistryDto.getVillage().getStateLgdCode().getStateLgdCode());
			farmlandPlotRegistry.setFarmlandId(uniqueNumber);
		}
		farmlandPlotRegistry = farmlandPlotRegistryRepository.save(farmlandPlotRegistry);
		return farmlandPlotRegistry;
	}

	public String getFarmlandPlotSurvey(FarmLandPlotRegistryDto requestDTO, HttpServletRequest request) {
		JSONObject responseObj = new JSONObject();
		try {
			String result = farmlandPlotRegistryRepository.getFarmlandPlotSurvey(
				requestDTO.getIsOwnerShipData(),requestDTO.getVillageLgdCode(),requestDTO.getLandUsageType(),requestDTO.getOwnerShipType(),requestDTO.getPlotId()	
			);

			ObjectMapper mapper = new ObjectMapper();
			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(result,
					typeFactory.constructCollectionType(List.class, Object.class));
			responseObj.put("data", resultObject);
			responseObj.put("responseCode", HttpStatus.OK);
			responseObj.put("responseMessage", CustomMessages.SUCCESS);
			return responseObj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.put("responseCode", HttpStatus.INTERNAL_SERVER_ERROR);
			responseObj.put("responseMessage", CustomMessages.FAILURE);
			return responseObj.toString();
		}
	}
}
