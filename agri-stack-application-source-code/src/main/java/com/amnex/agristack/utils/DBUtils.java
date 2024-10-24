package com.amnex.agristack.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.SqlData;

@Service
public class DBUtils {

	static Connection conn = null;
	static PreparedStatement pstmt = null;
	static ResultSet rset = null;

	@Autowired
	DataSource dataSource;

	@Autowired
	EntityManager entityManager;

	public String getResult(String sql, String defaultReturn, SqlData[] data) {
		String result = defaultReturn;

		try {

			conn = dataSource.getConnection();
			String cursorName = null;

			if (conn == null) {
				System.out.println("Connection is null");

				// LOGGER.fatal(" method: getResult ; connection is null");
			} else {

				pstmt = conn.prepareStatement(sql);

				if (data != null) {
					for (SqlData sqlData : data) {
						switch (sqlData.getType().toLowerCase()) {

							case "ref":
								// pstmt.setRef(1, new Ref);
								cursorName = sqlData.getValue();
								// pstmt.setRef(1, Ref.);
								pstmt.setCursorName(sqlData.getValue());
								// pstmt.setInt(sqlData.getPosition(), Integer.parseInt(sqlData.getValue()));
								break;
							case "int":
								pstmt.setInt(sqlData.getPosition(), Integer.parseInt(sqlData.getValue()));
								break;
							case "boolean":
								pstmt.setBoolean(sqlData.getPosition(), Boolean.parseBoolean(sqlData.getValue()));
								break;
							case "float":
								pstmt.setFloat(sqlData.getPosition(), Float.parseFloat(sqlData.getValue()));
								break;
							case "double":
								pstmt.setDouble(sqlData.getPosition(), Double.parseDouble(sqlData.getValue()));
								break;
							case "string":
								pstmt.setString(sqlData.getPosition(), sqlData.getValue());
								break;
							default:
								break;
						}

					}
				}

				rset = pstmt.executeQuery();
				// pstmt.getResultSet();

				if (rset.next()) {
					Ref a = rset.getRef(cursorName);
					result = rset.getString(1);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "error_occured";

			// LOGGER.error(" method: getResult ; " + e.getMessage());
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rset != null) {
				try {
					rset.close();
				} catch (SQLException e) {

				}
				rset = null;
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {

				}
				pstmt = null;
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {

				}
				conn = null;
			}
		}

		return result;
	}

	public Object executeStoredProcedure(String sql, String defaultReturn, SqlData[] data) {
		String result = defaultReturn;
		Object resultObj = defaultReturn;

		try {
			String cursorName = null;

			StoredProcedureQuery storedProcedure = entityManager.createStoredProcedureQuery(sql);

			if (data != null) {
				for (SqlData sqlData : data) {
					switch (sqlData.getType().toLowerCase()) {

						case "int":

							storedProcedure
									.registerStoredProcedureParameter(sqlData.getPosition(), Integer.class,
											ParameterMode.IN)
									.setParameter(sqlData.getPosition(), Integer.parseInt(sqlData.getValue()));
							break;
						case "boolean":
							storedProcedure
									.registerStoredProcedureParameter(sqlData.getPosition(), Boolean.class,
											ParameterMode.IN)
									.setParameter(sqlData.getPosition(), Boolean.getBoolean(sqlData.getValue()));
							break;
						case "float":
							storedProcedure
									.registerStoredProcedureParameter(sqlData.getPosition(), Float.class,
											ParameterMode.IN)
									.setParameter(sqlData.getPosition(), Float.parseFloat(sqlData.getValue()));
							break;
						case "double":
							storedProcedure
									.registerStoredProcedureParameter(sqlData.getPosition(), Double.class,
											ParameterMode.IN)
									.setParameter(sqlData.getPosition(), Double.parseDouble(sqlData.getValue()));
							break;
						case "string":
							storedProcedure
									.registerStoredProcedureParameter(sqlData.getPosition(), String.class,
											ParameterMode.IN)
									.setParameter(sqlData.getPosition(), String.valueOf(sqlData.getValue()));
							break;
						default:
							break;
					}

				}
			}

			resultObj = storedProcedure.getSingleResult();

			// if (rset.next()) {
			// Ref a = rset.getRef(cursorName);
			// result = rset.getString(1);
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "error_occured";

		}

		return resultObj;
	}

}
