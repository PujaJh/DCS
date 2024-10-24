package com.amnex.agristack.centralcore.dao;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SearchResponseDataDAO {
	
	private String reg_record_type;
	private Object reg_records;
//	private List<RegRecordsResponseDAO> reg_records;

}
