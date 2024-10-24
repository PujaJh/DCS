package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class CensusDataDAO {
	private Long villagecodelgd;
	private String villagecodecensus;
	private String khasrano;
	private String uniquecode;
	private Double area;
}
