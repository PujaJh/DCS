package com.amnex.agristack.dao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UPStateRoRDataResponseDAO {

	public Integer villagecode;
	public String surveynumber;
	public String districtname;
	public String tehsilname;
	public String plotgeometry;
	public Double plotarea;
	public String plotareaunit;
	public Integer projectioncode;
	public List<UPStateRoRDataOwnerDAO> owner;
}
