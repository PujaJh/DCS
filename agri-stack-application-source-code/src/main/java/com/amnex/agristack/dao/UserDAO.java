/**
 * 
 */
package com.amnex.agristack.dao;

import lombok.Data;

/**
 * @author majid.belim
 *
 */
@Data
public class UserDAO {
	private Long userId;
	private String userFirstName;
	private String userLastName;
	private String userFullName;
	private String userName;
	private String userPrId;
	private Integer userStateLGDCode;

	private Integer userDistrictLGDCode;

	private Integer userTalukaLGDCode;

	private Integer userVillageLGDCode;
	private Long farmAssignCount;

}
