package com.amnex.agristack.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class ResponseMessages {

	public final static String ADD = "add";
	public final static String SUCCESS = "success";
	public final static String UPDATE = "update";
	public final static String DELETE = "delete";
	public final static String INTERNAL_SERVER_ERROR = "internalServerError";
	public final static String NOT_FOUND = "notFound";
	public final static String TYPE_NOT_SUPPORTED = "typeNotSupported";
	public final static String CONFLICT = "conflict";
	public final static String NOT_ACCEPTED = "notAccepted";
	public final static String CIRCUIT_BREAKER = "circuit-breaker";
	public final static String FAILED = "failed";

	public static String Toast(String type, String fieldMsg, Object data) {
		Map<String, Object> response = null;
		switch (type) {
		case ADD:
			response = new HashMap<String, Object>();
			response.put("status", "success");
			response.put("message", fieldMsg + " Added successfully.");
			response.put("data", data);
			response.put("method", "ADD");
			return new Gson().toJson(response);
		case SUCCESS:
			response = new HashMap<String, Object>();
			response.put("status", "success");
			response.put("message", fieldMsg);
			response.put("data", data);
			response.put("method", "GET");
			return new Gson().toJson(response);
		case UPDATE:
			response = new HashMap<String, Object>();
			response.put("status", "success");
			response.put("message", fieldMsg + " Updated successfully.");
			response.put("data", data);
			response.put("method", "UPDATE");
			return new Gson().toJson(response);
		case DELETE:
			response = new HashMap<String, Object>();
			response.put("status", "success");
			response.put("message", fieldMsg + " Deleted successfully.");
			response.put("method", "DELETE");
			return new Gson().toJson(response);

		case INTERNAL_SERVER_ERROR:
			response = new HashMap<String, Object>();
			response.put("status", "failed");
			response.put("message",  fieldMsg);
			return new Gson().toJson(response);

		case NOT_FOUND:
			response = new HashMap<String, Object>();
			response.put("status", "failed");
			response.put("message", "Resource Not Found." + fieldMsg);
			return new Gson().toJson(response);

		case TYPE_NOT_SUPPORTED:
			response = new HashMap<String, Object>();
			response.put("status", "failed");
			response.put("message", "Resource Type Not Supported." + fieldMsg);
			return new Gson().toJson(response);

		case CONFLICT:
			response = new HashMap<String, Object>();
			response.put("status", "conflict");
			response.put("message", fieldMsg + " name already exists.");
			return new Gson().toJson(response);

		case NOT_ACCEPTED:
			response = new HashMap<String, Object>();
			response.put("status", "not acceptable");
			response.put("message", fieldMsg + " is required.");
			return new Gson().toJson(response);

		case CIRCUIT_BREAKER:
			response = new HashMap<String, Object>();
			response.put("status", "service unavailable");
			response.put("message", fieldMsg);
			return new Gson().toJson(response);

		case FAILED:
			response = new HashMap<String, Object>();
			response.put("status", "failed");
			response.put("message", fieldMsg);
			return new Gson().toJson(response);

		default:
			break;
		}
		return null;
	}
}
