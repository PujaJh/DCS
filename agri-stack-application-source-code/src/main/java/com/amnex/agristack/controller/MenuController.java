package com.amnex.agristack.controller;

import com.amnex.agristack.dao.MenuInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    MenuService menuService;

    /**
     * Get menu list.
     *
     * @param request HttpServletRequest object
     * @return ResponseEntity containing the menu list
     */
    @GetMapping(value = "/getMenuList")
    public ResponseEntity<?> getMenuList(HttpServletRequest request) {
        try {
            return menuService.getMenuList(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add menu.
     *
     * @param menuInputDAO MenuInputDAO object containing menu details
     * @param request      HttpServletRequest object
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping(value = "/addMenu")
    public ResponseEntity<?> addMenu(@RequestBody MenuInputDAO menuInputDAO, HttpServletRequest request) {
        try {
            return menuService.addMenu(menuInputDAO, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get menu by parent IDs.
     *
     * @param request HttpServletRequest object
     * @param ids     List of parent IDs
     * @return ResponseEntity containing the menu
     */
    @PostMapping(value = "/getMenuByParentIds")
    public ResponseEntity<?> getMenuByParentId(HttpServletRequest request, @RequestBody List<Long> ids) {
        try {
            return menuService.getMenuByParentId(request, ids);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get menu by ID.
     *
     * @param request HttpServletRequest object
     * @param id      ID of the menu
     * @return ResponseEntity containing the menu
     */
    @PostMapping(value = "/getMenuById/{id}")
    public ResponseEntity<?> getMenuById(HttpServletRequest request, @PathVariable("id") Long id) {
        try {
            return menuService.getMenuById(request, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get menu list by menu type and project type.
     *
     * @param request     HttpServletRequest object
     * @param type        Menu type
     * @param projectType Project type
     * @return ResponseEntity containing the menu list
     */
    @GetMapping(value = "/getMenuListByMenuType/{type}/{projectType}")
    public ResponseEntity<?> getMenuListByMenuType(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("projectType") String projectType) {
        try {
            return menuService.getMenuListByMenuType(request, type, projectType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get menu list by menu type and project type (new implementation).
     *
     * @param request     HttpServletRequest object
     * @param type        Menu type
     * @param projectType Project type
     * @return ResponseEntity containing the menu list
     */
    @GetMapping(value = "/getMenuListByMenuTypeNew/{type}/{projectType}")
    public ResponseEntity<?> getMenuListByMenuTypeNew(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("projectType") String projectType) {
        try {
            return menuService.getMenuListByMenuTypeNew(request, type, projectType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update menu status.
     *
     * @param request       HttpServletRequest object
     * @param menuInputDAO  MenuInputDAO object containing menu details
     * @return ResponseModel indicating the status of the operation
     */
    @PutMapping(value = "/updateStatus")
    public ResponseModel updateStatus(HttpServletRequest request, @RequestBody MenuInputDAO menuInputDAO) {
        try {
            return menuService.updateStatus(request, menuInputDAO);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete menu by ID.
     *
     * @param request HttpServletRequest object
     * @param menuId  ID of the menu to be deleted
     * @return ResponseModel indicating the status of the operation
     */
    @DeleteMapping(value = "/deleteMenu/{menuId}")
    public ResponseModel deleteMenu(HttpServletRequest request, @PathVariable("menuId") Long menuId) {
        try {
            return menuService.deleteMenu(request, menuId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update menu.
     *
     * @param request      HttpServletRequest object
     * @param menuInputDAO MenuInputDAO object containing menu details
     * @return ResponseModel indicating the status of the operation
     */
    @PutMapping(value = "/updateMenu")
    public ResponseModel updateMenu(HttpServletRequest request, @RequestBody MenuInputDAO menuInputDAO) {
        try {
            return menuService.updateMenu(request, menuInputDAO);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * fetch the role assignment menu list.
     *
     * @param type  type of role assignment
     * @param projectType project type of role assignment
     * @return ResponseModel indicating the status of the operation
     */
    @GetMapping(value = "/roleAssignedMenuList/{type}/{projectType}")
    public ResponseEntity<?> roleAssignedMenuList(HttpServletRequest request, @PathVariable("type") String type, @PathVariable("projectType") String projectType){
    	try {
			return menuService.roleAssignedMenuList(request, type, projectType);

    	} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
}
