package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author krupali.jogi
 *
 * Table containing department details
 */
@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(indexes = @Index(name = "department_id_index", columnList = "department_id"))
public class DepartmentMaster extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "department_id")
	private Long departmentId;

	@Column(name = "department_name", columnDefinition = "VARCHAR(100)")
	private String departmentName;

	@Column(name = "department_type")
	private Integer departmentType;

	@Column(name = "department_code", columnDefinition = "VARCHAR(10)")
	private String departmentCode;


	public DepartmentMaster(Long id, String departmentName, Integer departmentType, String departmentCode) {
		this.departmentId = id;
		this.departmentName = departmentName;
		this.departmentType = departmentType;
		this.departmentCode = departmentCode;
	}

	public DepartmentMaster(Long id, String departmentName, Integer departmentType) {
		this.departmentId = id;
		this.departmentName = departmentName;
		this.departmentType = departmentType;
	}

}
