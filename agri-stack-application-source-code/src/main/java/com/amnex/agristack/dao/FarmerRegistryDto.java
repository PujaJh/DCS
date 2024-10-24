package com.amnex.agristack.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.amnex.agristack.entity.*;

import lombok.Data;

@Data
public class FarmerRegistryDto {

	private Long farmerRegistryId;

	private String farmerRegistryNumber;

	private String farmerAadhaarHash;

	private String farmerAadhaarValutRefId;

	private String farmerAadhaarValutRefSource;

	private FarmerTypeMaster farmerType;

	private Date farmerDob;

	private Integer farmerAge;

	// private Long farmerLocationTypeId;
	private LocationTypeMaster farmerLocationType;

	private Long farmerVillageLgdCode;

	private Long farmerUrbanLgdCode;

	private Long farmerWardLgdCode;

	private Long farmerPinCode;

	private String farmerAddressEn;

	private String farmerAddressLocal;

	private String farmerNameEn;

	private String farmerNameLocal;

	private GenderMaster farmerGenederType;

	private String farmerIdentifierNameEn;

	private String farmerIdentifierNameLocal;

	private IdentifierTypeMaster farmerIdentifierType;

	private CasteCategoryMaster famerCasteCategory;

	private Double farmerTotalLandOwned;

	// private UnitTypeMaster farmerLandUnitType;
	private Double farmerTotalLandTenanted;

	private Integer farmerNumberOfLandOwned;

	private FarmerCategoryMaster farmerCategory;

	private FarmerStatusMaster farmerStatus;

	private ApprovalStatusMaster farmerApprovalStatus;

	private Date farmerLastUpdateDate;

	private Date farmerSynchronisationDate;

	private Long farmerSocialRegistryLinkageType;

	private String farmerPdsFamilyId;

	private String farmerMemberResidentId;

	private String farmerEmailId;

	private String farmerMobileNumber;

	private StateLgdMaster stateLgdMaster;

	private DistrictLgdMaster districtLgdMaster;

	private SubDistrictLgdMaster subDistrictLgdMaster;

	private VillageLgdMaster villageLgdMaster;

	private Boolean isDrafted;

	private FarmerRegistryExtendedFieldDTO farmerRegistryExtendedFieldDTO;

	// private Map<String,Object> customFieldMap=new HashedMap();
	// private CustomFieldRequestDto customFieldRequestDto;

	private List<FarmerRegistryCustomFieldDto> farmerRegistryCustomFieldDtos;

	private StringBuilder errorMessage= new StringBuilder();

	private Boolean isValidData = true;

	private List<FarmerOccuptationMaster> farmerOccuptationMasters;

	private List<FarmerDisablityMappingDto> farmerDisablityMappingDtos;

	private Integer aadhaarNameMatchScore;

	private Integer identifierNameMatchScore;

	private List<FarmerOccupationDto> farmerOccupationDto;

	private int extendedSequence;

	private Map<String, List<CustomTableValueDto>> customMapValueForBulkUplod;

	private DepartmentMaster departmentMaster;

	private Long deployedStateLgdCode;
}
