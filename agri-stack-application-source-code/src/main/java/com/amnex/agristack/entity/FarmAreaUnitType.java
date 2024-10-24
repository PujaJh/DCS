package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

import javax.persistence.*;


@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class FarmAreaUnitType extends  BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long unitTypeId;
	@Column
	private String unitType;
	@Column
	private String unitTypeLocal;

	public FarmAreaUnitType(Long id, String unitType) {
		this.unitTypeId = id;
		this.unitType = unitType;
	}
}
