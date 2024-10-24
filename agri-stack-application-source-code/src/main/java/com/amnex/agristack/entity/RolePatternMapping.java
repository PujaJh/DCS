package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author kinnari.soni
 * Entity class representing the mapping between roles and patterns, 
 * such as territory level and department, along with the number of users.
 * Each mapping has a unique identifier, a territory level, a reference to the associated role,
 * a reference to the associated department, the number of users, the total number of users, 
 * and an assigned state LGD code.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(indexes = @Index(name = "role_pattern_mapping_index", columnList = "role_pattern_mapping_id"))
public class RolePatternMapping extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="role_pattern_mapping_id")
	private Long rolePatternMappingId;

	@Column(name = "territory_level")
	private String territoryLevel;

	@OneToOne
	@JoinColumn(name = "role_id", referencedColumnName = "role_id")
	private RoleMaster role;

	@OneToOne
	@JoinColumn(name = "department_id", referencedColumnName = "department_id")
	private DepartmentMaster department;

	@Column(name = "no_of_user")
	private Integer noOfUser;

	@Column(name = "total_no_of_user")
	private Integer totalNoOfUser;

	@Column(name="assign_state_lgd_code")
	private Long assignStateLgdCode;
}
