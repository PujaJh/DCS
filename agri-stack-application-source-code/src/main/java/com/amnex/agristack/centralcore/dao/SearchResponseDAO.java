package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SearchResponseDAO {
	public String reference_id;
	public Timestamp timestamp;
	public String locale;

	public SearchCriteriaDAO search_criteria;

	public SearchResponseDataDAO data;
}
