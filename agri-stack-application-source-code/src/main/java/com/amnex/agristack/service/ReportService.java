package com.amnex.agristack.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.ReportInputDAO;
import com.amnex.agristack.entity.LoginLogoutActivityLog;
import com.amnex.agristack.repository.LoginLogoutActivityLogReposiptory;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.ResponseMessages;

import reactor.core.publisher.Mono;

@Service
public class ReportService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	LoginLogoutActivityLogReposiptory loginActivityReposiptory;

	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * Retrieves user logs based on the provided ReportInputDAO.
	 *
	 * @param reportInputDAO The ReportInputDAO object containing the report input data.
	 * @return A ResponseEntity wrapping a Mono that represents the user logs.
	 */
	public ResponseEntity<Mono<?>> getUserLogs(ReportInputDAO reportInputDAO) {
		try {
			StringBuilder sqlQuery = new StringBuilder();
			List<Map<String, Object>> reportDAO = null;
			sqlQuery.append(
					"select original_data, new_data, action_tstamp, module_name, user_id, user_name, ip_address, updated_values, level_type from tbl_audit_logged_actions");
			if (reportInputDAO.getStartDate() != null && reportInputDAO.getEndDate() != null
					&& reportInputDAO.getUserId() != null) {
				sqlQuery.append(" where action_tstamp between '" + reportInputDAO.getStartDate() + "' and '"
						+ reportInputDAO.getEndDate() + "' and user_id='" + reportInputDAO.getUserId() + "'");
			} else if (reportInputDAO.getUserId() == null && reportInputDAO.getStartDate() != null
					&& reportInputDAO.getEndDate() != null && reportInputDAO.getUserId() != null) {
				sqlQuery.append(" where action_tstamp between '" + reportInputDAO.getStartDate() + "' and '"
						+ reportInputDAO.getEndDate() + "'");
			} else if (reportInputDAO.getUserId() != null && reportInputDAO.getStartDate() == null
					&& reportInputDAO.getEndDate() == null) {
				sqlQuery.append(" where user_id='" + reportInputDAO.getUserId() + "'");
			}
			sqlQuery.append(" order by action_tstamp desc offset " + (reportInputDAO.getOffset()) + " limit "
					+ reportInputDAO.getLimit());
			reportDAO = jdbcTemplate.queryForList(sqlQuery.toString());

			Map<String, Object> responseData = new HashMap();
			responseData.put("data", reportDAO);
			responseData.put("total", reportDAO.size());
			responseData.put("page", reportInputDAO.getPage());
			responseData.put("limit", reportInputDAO.getLimit());
			responseData.put("sortField", reportInputDAO.getSortField());
			responseData.put("sortOrder", reportInputDAO.getSortOrder());

			return new ResponseEntity<Mono<?>>(Mono.just(ResponseMessages.Toast(ResponseMessages.SUCCESS,
					"User log list fetched successfully.", responseData)), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Retrieves login activity based on the provided ReportInputDAO and HttpServletRequest.
	 *
	 * @param reportInputDAO The ReportInputDAO object containing the report input data.
	 * @param httpServletRequest The HttpServletRequest object containing the HTTP request data.
	 * @return A ResponseModel representing the login activity.
	 */
	public ResponseModel getLoginActivity(ReportInputDAO reportInputDAO, HttpServletRequest httpServletRequest) {
		ResponseModel responseModel = null;
		try {
			Page<LoginLogoutActivityLog> result = null;

			Pageable pageable = PageRequest.of(reportInputDAO.getPage(), reportInputDAO.getLimit(),
					Sort.by(reportInputDAO.getSortField()).descending());

			if (reportInputDAO.getStartDate() != null && reportInputDAO.getEndDate() != null) {
				result = loginActivityReposiptory.findByUserIdAndLoginLogOutData(reportInputDAO.getUserId(),
						reportInputDAO.getStartDate(), reportInputDAO.getEndDate(), pageable);
			} else if (reportInputDAO.getUserId() != null) {
				result = loginActivityReposiptory.findByUserId(reportInputDAO.getUserId(), pageable);
			} else {
				result = loginActivityReposiptory.findAll(pageable);
			}

			Map<String, Object> responseData = new HashMap();
			responseData.put("data", result.getContent());
			responseData.put("total", result.getTotalElements());
			responseData.put("page", reportInputDAO.getPage());
			responseData.put("limit", reportInputDAO.getLimit());
			responseData.put("sortField", reportInputDAO.getSortField());
			responseData.put("sortOrder", reportInputDAO.getSortOrder());

			return CustomMessages.makeResponseModel(result, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public ResponseEntity<Mono<?>> getTotalUserLogsCount(ReportInputDAO reportInputDAO) {
		try {
			StringBuilder sqlQuery = new StringBuilder();
			Integer count = 0;
			sqlQuery.append(
					"select count(*) from tbl_audit_logged_actions");
			if (reportInputDAO.getStartDate() != null && reportInputDAO.getEndDate() != null
					&& reportInputDAO.getUserId() != null) {
				sqlQuery.append(" where action_tstamp between '" + reportInputDAO.getStartDate() + "' and '"
						+ reportInputDAO.getEndDate() + "' and user_id='" + reportInputDAO.getUserId() + "'");
			} else if (reportInputDAO.getUserId() == null && reportInputDAO.getStartDate() != null
					&& reportInputDAO.getEndDate() != null && reportInputDAO.getUserId() != null) {
				sqlQuery.append(" where action_tstamp between '" + reportInputDAO.getStartDate() + "' and '"
						+ reportInputDAO.getEndDate() + "'");
			} else if (reportInputDAO.getUserId() != null && reportInputDAO.getStartDate() == null
					&& reportInputDAO.getEndDate() == null) {
				sqlQuery.append(" where user_id='" + reportInputDAO.getUserId() + "'");
			}
			count = jdbcTemplate.queryForObject(sqlQuery.toString(), Integer.class);

			return new ResponseEntity<Mono<?>>(Mono.just(
					ResponseMessages.Toast(ResponseMessages.SUCCESS, "User log list fetched successfully.", count)),
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
