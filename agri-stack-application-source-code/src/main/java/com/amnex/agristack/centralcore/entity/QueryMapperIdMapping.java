package com.amnex.agristack.centralcore.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
@Table(schema = "agristack_api_spec")
public class QueryMapperIdMapping {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "TEXT")
	private String queryName;
	
	@Column(columnDefinition = "TEXT")
	private String mapperId;

	@Column(columnDefinition = "TEXT")
	private String procedureName;

	@Column(columnDefinition = "TEXT")
	private String schemaName;

}
