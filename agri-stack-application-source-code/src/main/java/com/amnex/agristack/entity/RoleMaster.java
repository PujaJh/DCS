package com.amnex.agristack.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 *
 * Entity class representing user roles with associated menu allocations.
 * Each role has a unique identifier, a name, a set of associated menus,
 * a code, a flag indicating if it's a default role, a prefix, and a state LGD code.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
@Table(indexes = @Index(name = "role_id_index", columnList = "role_id"))
public class RoleMaster extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Long roleId;

	@Column(columnDefinition = "VARCHAR(25)")
	private String roleName;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "role_menu_mapping", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "menu_id"))
	private Set<MenuMaster> menu;

	@Column(name = "code", columnDefinition = "VARCHAR(25)")
	private String code;

	@Column(name = "is_default")
	private Boolean isDefault;

	@Column(name = "prefix", columnDefinition = "VARCHAR(10)")
	private String prefix;

	@Column(name = "state_lgd_code")
	private Long stateLgdCode;
}
