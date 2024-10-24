package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class AIPTokenResponseDAO {

	/***
	 * AIU : State Core/ Network Adapter
	 */
	public String access_token;
	public int expires_in;
	public int refresh_expires_in;
	public String refresh_token;
	public String token_type;
	public String session_state;
	public String scope;
}
