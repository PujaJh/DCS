package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

/**
 * Role Input DAO
 * @author krupali.jogi
 */
@Data
public class RoleInputDAO {
	private Long roleId;
	private String roleName;
	private Long userId;
	private List<Long> menuIds;
	private Boolean isDeleted;
	private Boolean isActive;
	private String prefix;
	private Long assignStateLgdcode;

	private List<PatternDAO> patternDAO;

	private List<PatternDAO> removedPatternDAO;
}
