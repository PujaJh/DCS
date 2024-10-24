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
public class UserReturnDAO {
    Long userId;
    String stateName;
    Long stateLgdCode;
    String districtName;
    String districtLgdCode;
    String subDistrictName;
    Long subDistrictLgdCode;
    String villageName;
    Long villageLgdCode;
    Long roleId;
    String roleName;
    String territoryLevel;
    String userName;
    String userFullName;
    String userEmailAddress;
    String userMobileNumber;
    Boolean isActive;
    String createdBy;
    String createdOn;
    String modifiedBy;
    String modifiedOn;
}
