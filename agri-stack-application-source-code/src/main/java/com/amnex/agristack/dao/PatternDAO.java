/**
 *
 */
package com.amnex.agristack.dao;

import java.util.Date;

import lombok.Data;

/**
 * @author kinnari.soni
 *
 * 2 Mar 2023 5:47:23 pm
 */
@Data
public class PatternDAO {
	private Long rolePatternMappingId;
	private String territoryLevel;
	private Long departmentId;
	private String departmentName;
	private Integer oldNoOfUser;
	private Integer NoOfUser;
	private Integer totalNoOfUser;;
	private Boolean isDeleted;
	private Boolean isActive;
	private Date CreatedOn;
}
