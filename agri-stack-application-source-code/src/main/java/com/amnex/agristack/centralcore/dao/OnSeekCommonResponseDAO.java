package com.amnex.agristack.centralcore.dao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class OnSeekCommonResponseDAO {

	 private Long total_count;
	 
	 private String data;
}
