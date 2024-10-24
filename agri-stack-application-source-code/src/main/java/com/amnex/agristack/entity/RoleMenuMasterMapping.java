/**
 * 
 */
package com.amnex.agristack.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author majid.belim
 * 
 * Entity class representing the mapping between roles and menus with access permissions.
 * Each mapping has a unique identifier, a reference to the associated role,
 * a reference to the associated menu, and flags indicating the access permissions
 * for adding, editing, viewing, and deleting.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class RoleMenuMasterMapping extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@JoinColumn(name = "role_id")
	@OneToOne(optional = true)
	private RoleMaster role;

	@JoinColumn(name = "menu_id")
	@OneToOne(optional = true)
	private MenuMaster menu;

	private Boolean isAdd;
	private Boolean isEdit;
	private Boolean isView;
	private Boolean isDelete;

}
