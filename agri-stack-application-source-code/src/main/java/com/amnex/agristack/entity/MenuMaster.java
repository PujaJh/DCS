package com.amnex.agristack.entity;

import javax.persistence.*;

import com.amnex.agristack.Enum.MenuTypeEnum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import java.util.List;

/**
 * @author majid.belim
 *
 * Entity class representing menu details.
 * Each instance corresponds to a specific menu with its unique identifier, name, type, description,
 * code, parent ID, URL, display serial number, icon, level, parent menu name, project type,
 * project type list, and main menu ID.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class MenuMaster extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long menuId;

	@Column(columnDefinition = "VARCHAR(100)")
	private String menuName;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "VARCHAR(10)")
	private MenuTypeEnum menuType;

	@Column(columnDefinition = "VARCHAR(100)")
	private String menuDescription;

	@Column(columnDefinition = "Integer")
	private Integer menuCode;
//	@Column(columnDefinition = "Integer", unique = true)
//	private MenuCode menuCode;

	@Column(columnDefinition = "Integer")
	private Long menuParentId;

	@Column(columnDefinition = "VARCHAR(200)")
	private String menuUrl;

	@Column(name = "display_sr_no", columnDefinition = "Integer")
	private Integer displaySrNo;

	@Column(columnDefinition = "VARCHAR(100)")
	private String menuIcon;

	@Column(columnDefinition = "Integer")
	private Integer menuLevel;

	@Transient
	private String parentMenuName;

	@Column(name = "project_type")
	@Type(type = "text")
	private String projectType;

	@Transient
	private List<String> projectTypeList;
	
	@Transient
	private Long menuMainId;
}
