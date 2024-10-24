/**
 * 
 */
package com.amnex.agristack.gateway.DAO;

/**
 * @author majid.belim
 *
 */

public class ResponseModel {
	private String status;
	private int code;
	private String message;
	private String method;
	private Object data;

	public String getStatus() {
		return status;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getMethod() {
		return method;
	}

	public Object getData() {
		return data;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
