package com.amnex.agristack.centralcore.entity;

import com.amnex.agristack.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.JsonArray;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.json.JSONArray;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
@Table(schema = "agristack_api_spec")
//@TypeDef(name = "MyJsonType", typeClass = com.amnex.agristack.centralcore.entity.TransactionRequestMapping.class)
public class TransactionRequestMapping extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionRequestMappingId;

	@Column(columnDefinition = "TEXT")
	private String transactionId;

	@Column(columnDefinition = "TEXT", unique = true)
	private String referenceId;

	private Timestamp timeStamp;

	@Column(columnDefinition = "TEXT")
	private String locale;

	@Column(columnDefinition = "TEXT")
	private String queryType;

	@Column(columnDefinition = "TEXT")
	private String regType;

	@Column(columnDefinition = "TEXT")
	private String queryName;

	@Column(columnDefinition = "TEXT")
	private String mapperId;

	Long pageSize;
	Long pageNumber;

	@Column(columnDefinition = "TEXT")
	private String consentArtifect;

	@Column(columnDefinition = "TEXT")
	private String queryParams;

	@Column(columnDefinition = "TEXT")
	private String sort;
	
	@Column(columnDefinition = "TEXT")
	private String attributes;
	
	@Column(columnDefinition = "TEXT")
	private String assetArtifactDecoded;
}
