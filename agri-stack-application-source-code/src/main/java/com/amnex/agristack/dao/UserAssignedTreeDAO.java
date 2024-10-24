package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class UserAssignedTreeDAO {

	public Integer stateLgdCode;
	public String stateName;
	public Integer totalDistrictCount;
	public Integer totalSubDistrictCount;
	public Integer totalVillageCount;
	public List<Integer> assignedStateCodes;
	public List<Integer> assignedDistrictCodes;
	public List<Integer> assignedSubDistrictCodes;
	public List<Integer> assignedVillageCodes;

}
