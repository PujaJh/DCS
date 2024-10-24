package com.amnex.agristack.dao;

import com.amnex.agristack.Enum.MenuTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * Menu Input DAO
 * @author krupali.jogi
 */

@Data
public class MenuInputDAO {
    private Long menuId;
    private String menuName;
    private MenuTypeEnum menuType;
    private String menuDescription;
    private Long menuParentId;
    private String menuUrl;
    private Integer displaySrNo;
    private Integer menuCode;
    private Integer menuLevel;
	private String menuIcon;
    private List<String> projectType;
    private Boolean isActive;
}
