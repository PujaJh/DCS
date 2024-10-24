package com.amnex.agristack.service;

import java.nio.file.attribute.UserPrincipal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.opengis.filter.expression.Add;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.amnex.agristack.Enum.ChildMenuEnum;
import com.amnex.agristack.Enum.MenuTypeEnum;
import com.amnex.agristack.Enum.ProjectType;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.MenuInputDAO;
import com.amnex.agristack.dao.MenuOutputDAO;
import com.amnex.agristack.entity.MenuMaster;
import com.amnex.agristack.entity.RoleMaster;
import com.amnex.agristack.entity.RoleMenuMasterMapping;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.repository.MenuMasterRepository;
import com.amnex.agristack.repository.RoleMenuMasterMappingRepository;
import com.amnex.agristack.repository.UserMasterRepository;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.ResponseMessages;

import reactor.core.publisher.Mono;

/**
 * @author krupali.jogi Service Menu Service to handle Menu operations
 */
@Service
public class MenuService {

	@Autowired
	MenuMasterRepository menuMasterRepository;
	@Autowired
	private JwtTokenUtil jwtTokenUtils;
	@Autowired 
	private UserMasterRepository userMasterRepository;
	@Autowired
	private RoleMenuMasterMappingRepository roleMenuMasterMappingRepository;
	
    /**
     * Get menu list.
     *
     * @param request HttpServletRequest object
     * @return ResponseEntity containing the menu list {@code MenuMaster}
     */
	public ResponseEntity<?> getMenuList(HttpServletRequest request) {
		List<MenuMaster> menuList = menuMasterRepository.findByIsDeletedFalse();
		menuList.stream().forEach(ele -> {
			if (ele.getMenuParentId() != null) {
				Optional<MenuMaster> parentMenu = menuMasterRepository
						.findByIsActiveTrueAndIsDeletedFalseAndMenuId(ele.getMenuParentId());
				if (parentMenu.isPresent()) {
					MenuMaster parentMenuMaster = parentMenu.get();
					ele.setParentMenuName(parentMenuMaster.getMenuName());
				}
			}
			if (Objects.nonNull(ele.getProjectType())) {
				List<String> list = Arrays.asList(ele.getProjectType().split(","));
				List<String> projectTypes = list.stream().map(String::valueOf).collect(Collectors.toList());
				ele.setProjectTypeList(projectTypes);
			}
		});

		return new ResponseEntity<String>(CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, menuList),
				HttpStatus.OK);
	}

	
    /**
     * Add menu.
     *
     * @param menuInputDAO {@code MenuInputDAO} object containing menu details
     * @param request      HttpServletRequest object
     * @return ResponseEntity indicating the status of the operation
     */
	public ResponseEntity<?> addMenu(MenuInputDAO menuInputDAO, HttpServletRequest request) {
		try {
			MenuMaster menuMaster = new MenuMaster();
			menuMaster.setMenuName(menuInputDAO.getMenuName());
			menuMaster.setMenuDescription(menuInputDAO.getMenuDescription());
			menuMaster.setMenuCode(menuInputDAO.getMenuCode());
			menuMaster.setMenuParentId(menuInputDAO.getMenuParentId());
			menuMaster.setMenuUrl(menuInputDAO.getMenuUrl());
			menuMaster.setDisplaySrNo(menuInputDAO.getDisplaySrNo());
			menuMaster.setMenuIcon(menuInputDAO.getMenuIcon());
			menuMaster.setMenuLevel(menuInputDAO.getMenuLevel());
			menuMaster.setMenuType(menuInputDAO.getMenuType());
			menuMaster.setIsActive(true);
			menuMaster.setIsDeleted(false);
			menuMaster.setCreatedOn(new Timestamp(new Date().getTime()));
			menuMaster.setModifiedOn(new Timestamp(new Date().getTime()));
			menuMaster.setCreatedBy("1");
			menuMaster.setModifiedBy("1");
			menuMaster.setCreatedIp(CommonUtil.getRequestIp(request));
			menuMaster.setModifiedIp(CommonUtil.getRequestIp(request));
			if(Objects.nonNull(menuInputDAO.getProjectType())) {
				String str = menuInputDAO.getProjectType().stream()
						.map(n -> String.valueOf(n))
						.collect(Collectors.joining(","));
				menuMaster.setProjectType(str);
			}
			menuMaster = menuMasterRepository.save(menuMaster);
			Long userId =Long.valueOf(CustomMessages.getUserId(request, jwtTokenUtils));
			Optional<UserMaster> user = this.userMasterRepository.findByUserId(userId);
			if(user.isPresent()) {
				RoleMenuMasterMapping roleMenuMasterMapping=new RoleMenuMasterMapping();
				roleMenuMasterMapping.setMenu(menuMaster);
				roleMenuMasterMapping.setRole(user.get().getRoleId());
				roleMenuMasterMapping.setIsActive(true);
				roleMenuMasterMapping.setIsDeleted(false);
				roleMenuMasterMapping.setCreatedOn(new Timestamp(new Date().getTime()));
				roleMenuMasterMapping.setModifiedOn(new Timestamp(new Date().getTime()));
				roleMenuMasterMapping.setIsAdd(true);
				roleMenuMasterMapping.setIsEdit(true);
				roleMenuMasterMapping.setIsView(true);
				roleMenuMasterMapping.setIsDelete(true);
				roleMenuMasterMappingRepository.save(roleMenuMasterMapping);
			}
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.MENU_ADD_SUCCESSFULLY, menuMaster), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	
    /**
     * Get menu by parent IDs.
     *
     * @param request HttpServletRequest object
     * @param ids     List of parent IDs
     * @return ResponseEntity containing the menu {@code MenuOutputDAO}
     */
	public ResponseEntity<?> getMenuByParentId(HttpServletRequest request, List<Long> ids) {
		List<MenuMaster> menuMasterList = menuMasterRepository
				.findByIsActiveTrueAndIsDeletedFalseAndMenuParentIdIn(ids);
		List<MenuOutputDAO> menuOutputDAOList = new ArrayList<>();
		if (menuMasterList != null && menuMasterList.size() > 0) {
			menuMasterList.forEach(action -> {
				menuOutputDAOList.add(CustomMessages.returnUserOutputDAO(action));
			});

		}
		return new ResponseEntity<String>(
				CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, menuMasterList), HttpStatus.OK);
	}

    /**
     * Get menu by ID.
     *
     * @param request HttpServletRequest object
     * @param id      ID of the menu
     * @return ResponseEntity containing the menu {@link MenuMaster}
     */
	public ResponseEntity<?> getMenuById(HttpServletRequest request, Long id) {
		Optional<MenuMaster> menuMaster = menuMasterRepository.findByIsActiveTrueAndIsDeletedFalseAndMenuId(id);

		return new ResponseEntity<String>(
				CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, menuMaster), HttpStatus.OK);
	}
    /**
     * Get menu list by menu type and project type.
     *
     * @param request     HttpServletRequest object
     * @param type        Menu type
     * @param projectType Project type
     * @return ResponseEntity containing the menu list
     */
	public ResponseEntity<?> getMenuListByMenuType(HttpServletRequest request, String type, String projectType) {
		String projectTypeLabel = null;
		if (!projectType.equals("null")) {
			projectTypeLabel = Objects.isNull(ProjectType.valueOfLabel(projectType).toString()) ? "": ProjectType.valueOfLabel(projectType).toString();
		}
		List<MenuMaster> menuList = menuMasterRepository.findMenuTypeWihProject(MenuTypeEnum.valueOf(type).toString(), projectTypeLabel);


		return new ResponseEntity<String>(CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, menuList),
				HttpStatus.OK);
	}
	
    /**
     * Update menu status.
     *
     * @param request       HttpServletRequest object
     * @param menuInputDAO  MenuInputDAO object containing menu details
     * @return ResponseModel indicating the status of the operation
     */
	public ResponseModel updateStatus(HttpServletRequest request, MenuInputDAO menuInputDAO) {

		ResponseModel responseModel = null;

		try {
			Optional<MenuMaster> opMaster = menuMasterRepository.findByIsDeletedAndMenuId(Boolean.FALSE,
					menuInputDAO.getMenuId());
			if (opMaster.isPresent()) {
				MenuMaster menuMaster = opMaster.get();
				menuMaster.setIsActive(menuInputDAO.getIsActive());
				menuMasterRepository.save(menuMaster);
			}
			responseModel = CustomMessages.makeResponseModel(null, CustomMessages.STATUS_UPDATED_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

    /**
     * Delete menu by ID.
     *
     * @param request HttpServletRequest object
     * @param menuId  ID of the menu to be deleted
     * @return ResponseModel indicating the status of the operation
     */
	public ResponseModel deleteMenu(HttpServletRequest request, Long menuId) {

		ResponseModel responseModel = null;

		try {
			MenuMaster opMaster = menuMasterRepository.findByMenuId(menuId);
			if (opMaster != null) {

				opMaster.setIsActive(false);
				opMaster.setIsDeleted(true);
				menuMasterRepository.save(opMaster);
			}
			responseModel = CustomMessages.makeResponseModel(null, CustomMessages.STATUS_UPDATED_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

    /**
     * Update menu.
     *
     * @param request      HttpServletRequest object
     * @param inputDAO MenuInputDAO object containing menu details
     * @return ResponseModel indicating the status of the operation
     */
	public ResponseModel updateMenu(HttpServletRequest request, MenuInputDAO inputDAO) {

		ResponseModel responseModel = null;

		try {
			Optional<MenuMaster> opMenu = menuMasterRepository
					.findByIsActiveTrueAndIsDeletedFalseAndMenuId(inputDAO.getMenuId());
			if (opMenu.isPresent()) {
				MenuMaster menuMaster = opMenu.get();
				menuMaster.setMenuName(inputDAO.getMenuName());
				menuMaster.setMenuUrl(inputDAO.getMenuUrl());
				menuMaster.setMenuParentId(inputDAO.getMenuParentId());
				menuMaster.setMenuUrl(inputDAO.getMenuUrl());
				menuMaster.setMenuCode(inputDAO.getMenuCode());
				menuMaster.setMenuDescription(inputDAO.getMenuDescription());
				menuMaster.setDisplaySrNo(inputDAO.getDisplaySrNo());
				menuMaster.setMenuType(inputDAO.getMenuType());
				menuMaster.setMenuIcon(inputDAO.getMenuIcon());
				menuMaster.setMenuLevel(inputDAO.getMenuLevel());
				if(Objects.nonNull(inputDAO.getProjectType())) {
					String str = inputDAO.getProjectType().stream()
							.map(n -> String.valueOf(n))
							.collect(Collectors.joining(","));
					menuMaster.setProjectType(str);
				}
				menuMasterRepository.save(menuMaster);

			}

			responseModel = CustomMessages.makeResponseModel(responseModel, CustomMessages.STATUS_UPDATED_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}

	}

	/**
	 * Retrieves menus by menu type and project type (updated version).
	 *
	 * @param request the HTTP servlet request
	 * @param type the menu type
	 * @param projectType the project type
	 * @return the ResponseEntity containing the menus
	 */
	public ResponseEntity<?> getMenuListByMenuTypeNew(HttpServletRequest request, String type, String projectType) {
		String projectTypeLabel = null;
		
		if (!projectType.equals("null")) {
			projectTypeLabel = Objects.isNull(ProjectType.valueOfLabel(projectType).toString()) ? ""
					: ProjectType.valueOfLabel(projectType).toString();
		}
		
		Long userId =Long.valueOf(CustomMessages.getUserId(request, jwtTokenUtils));
		Optional<UserMaster> user = this.userMasterRepository.findByUserId(userId);
		List<MenuMaster> menuList =new ArrayList<>();
		if(user.isPresent()) {
			
		List<RoleMenuMasterMapping> roleList = this.roleMenuMasterMappingRepository.findByRoleRoleIdAndIsActiveTrueAndIsDeletedFalse(user.get().getRoleId().getRoleId());
		menuList=roleList.stream().map(x->x.getMenu()).collect(Collectors.toList());
		
		
		
//		List<MenuMaster> menuList = menuMasterRepository.findMenuTypeWihProject(MenuTypeEnum.valueOf(type).toString(),
//				projectTypeLabel);
		List<MenuMaster> addList = new ArrayList<>();

		if (menuList != null) {
			Set<Long> menuIds = menuList.stream().map(x -> x.getMenuId()).collect(Collectors.toSet());
			Set<Long> parnetMenuIds = menuList.stream().map(x -> x.getMenuParentId()).collect(Collectors.toSet());

			if (menuIds != null && menuIds.size() > 0) {
				menuIds.removeIf(Objects::isNull);
				parnetMenuIds.removeIf(Objects::isNull);
//				System.out.println(menuIds);
//				System.out.println(parnetMenuIds);
				menuIds.removeAll(parnetMenuIds);
//				System.out.println(menuIds);
				
				menuList.forEach(action -> {
					menuIds.forEach(action2 -> {
						if (action.getMenuId().equals(action2)) {
							addList.add(CommonUtil.returnMenuMaster(Long.sum(action.getMenuId() , ChildMenuEnum.Add.getValue()), ChildMenuEnum.Add.toString(),
									action.getMenuId(), action.getMenuType(), action.getProjectType(),action.getMenuId()));
							addList.add(CommonUtil.returnMenuMaster(Long.sum(action.getMenuId() , ChildMenuEnum.Edit.getValue()), ChildMenuEnum.Edit.toString(),
									action.getMenuId(), action.getMenuType(), action.getProjectType(),action.getMenuId()));
							addList.add(CommonUtil.returnMenuMaster(Long.sum(action.getMenuId() , ChildMenuEnum.View.getValue()),ChildMenuEnum.View.toString(),
									action.getMenuId(), action.getMenuType(), action.getProjectType(),action.getMenuId()));
							addList.add(CommonUtil.returnMenuMaster(Long.sum(action.getMenuId() , ChildMenuEnum.Delete.getValue()), ChildMenuEnum.Delete.toString(),
									action.getMenuId(), action.getMenuType(), action.getProjectType(),action.getMenuId()));
						}
					});
				});
				if(addList!=null && addList.size()>0) {
					menuList.addAll(addList);	
				}
				
			}

		}
	}

		return new ResponseEntity<String>(CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, menuList),
				HttpStatus.OK);
	}


	public ResponseEntity<?> roleAssignedMenuList(HttpServletRequest request, String type, String projectType) {

		String projectTypeLabel = null;
		if (!projectType.equals("null")) {
			projectTypeLabel = Objects.isNull(ProjectType.valueOfLabel(projectType).toString()) ? ""
					: ProjectType.valueOfLabel(projectType).toString();
		}
		Long userId = Long.valueOf(CustomMessages.getUserId(request, jwtTokenUtils));
		Optional<UserMaster> user = this.userMasterRepository.findByUserId(userId);
		List<MenuMaster> menuList = new ArrayList<>();

		if (user.isPresent()) {

			List<RoleMenuMasterMapping> roleMenuMappingList = roleMenuMasterMappingRepository
					.findByRoleRoleIdAndIsActiveTrueAndIsDeletedFalse(user.get().getRoleId().getRoleId());

			if (user.get().getRoleId().getCode().equals("SUPERADMIN")) {
				menuList = menuMasterRepository.findByIsActiveTrueAndIsDeletedFalse();
//				List<MenuMaster> tmpMenuList= roleMenuMappingList.stream().map(x -> x.getMenu()).collect(Collectors.toList());
//				List<Long> tmpMenuIdsList=tmpMenuList.stream().map(x->x.getMenuId()).collect(Collectors.toList());
//				if(tmpMenuList!=null && tmpMenuList.size()>0) {
//					menuList.remove(tmpMenuList);	
//				}

			} else {

				menuList = roleMenuMappingList.stream().map(x -> x.getMenu()).collect(Collectors.toList());
			}

			List<MenuMaster> addList = new ArrayList<>();
			if (menuList != null) {
				List<Long> menuIds = menuList.stream().map(x -> x.getMenuId()).collect(Collectors.toList());
				List<Long> parentMenuIds = menuList.stream().map(x -> x.getMenuParentId()).collect(Collectors.toList());

				if (menuIds != null && menuIds.size() > 0) {
					menuIds.removeIf(Objects::isNull);
					parentMenuIds.removeIf(Objects::isNull);
					menuIds.removeAll(parentMenuIds);

					roleMenuMappingList.forEach(action -> {
						menuIds.forEach(action2 -> {
							if (action.getMenu().getMenuId().equals(action2)) {
								if (action.getIsAdd()) {
									addList.add(
											CommonUtil.returnMenuMaster(
													Long.sum(action.getMenu().getMenuId(),
															ChildMenuEnum.Add.getValue()),
													ChildMenuEnum.Add.toString(), action.getMenu().getMenuId(),
													action.getMenu().getMenuType(), action.getMenu().getProjectType(),
													action.getMenu().getMenuId()));
								}
								if (action.getIsEdit()) {
									addList.add(CommonUtil.returnMenuMaster(
											Long.sum(action.getMenu().getMenuId(), ChildMenuEnum.Edit.getValue()),
											ChildMenuEnum.Edit.toString(), action.getMenu().getMenuId(),
											action.getMenu().getMenuType(), action.getMenu().getProjectType(),
											action.getMenu().getMenuId()));
								}
								if (action.getIsView()) {
									addList.add(CommonUtil.returnMenuMaster(
											Long.sum(action.getMenu().getMenuId(), ChildMenuEnum.View.getValue()),
											ChildMenuEnum.View.toString(), action.getMenu().getMenuId(),
											action.getMenu().getMenuType(), action.getMenu().getProjectType(),
											action.getMenu().getMenuId()));
								}
								if (action.getIsDelete()) {
									addList.add(CommonUtil.returnMenuMaster(
											Long.sum(action.getMenu().getMenuId(), ChildMenuEnum.Delete.getValue()),
											ChildMenuEnum.Delete.toString(), action.getMenu().getMenuId(),
											action.getMenu().getMenuType(), action.getMenu().getProjectType(),
											action.getMenu().getMenuId()));
								}
							}
						});
					});
					if (addList != null && addList.size() > 0) {
						menuList.addAll(addList);
					}
				}
			}
		}
		// Set createdIp and modifiedIp to null in each MenuMaster object
	    for (MenuMaster menu : menuList) {
	        menu.setCreatedIp(null);
	        menu.setModifiedIp(null);
	    }
		return new ResponseEntity<String>(CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, menuList),
				HttpStatus.OK);
	}
	
}
