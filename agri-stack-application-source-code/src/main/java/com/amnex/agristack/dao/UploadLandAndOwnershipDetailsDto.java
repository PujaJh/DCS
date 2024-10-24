package com.amnex.agristack.dao;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UploadLandAndOwnershipDetailsDto {

	private Long statelgdCode;
	private  List<MultipartFile> excelFile;
	private List<MultipartFile>shpFile;
	private List<UploadLandDetailsExcelFileColumnDao> considerExcelColumns;
	private List<UploadLandDetailsExcelFileColumnDao>mappingExcelColumns;
	private List<String>considerShpFileColumns;
	private List<String>mappingShpFileColumn;
	private Boolean isSurveyNumberWise;
	
	
	private String geometryType;
	private String plotGenerationType;
	private Integer projectionCode;
	private Boolean isDeleteExistingData;
	
}
