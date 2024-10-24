package com.amnex.agristack.dao;

import com.amnex.agristack.entity.DepartmentMaster;
import com.amnex.agristack.entity.DesignationMaster;
import com.amnex.agristack.entity.UserBankDetail;
import com.amnex.agristack.entity.VillageLgdMaster;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class UserAvailabilityOutputDAO {

	private Long userId;
	private Boolean isAvailable;
	private List<Long> seasonIdList;
	private String startingYear;
	private String endingYear;
	private Long year;
}
