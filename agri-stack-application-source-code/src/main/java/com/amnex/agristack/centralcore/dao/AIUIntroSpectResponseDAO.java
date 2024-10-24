package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class AIUIntroSpectResponseDAO {

	
	/***
	 * AIU : data seeker
	 * */
	public Integer exp;
	public Integer iat;
	public String jti;
	public String iss;
	public String aud;
	public String sub;
	public String typ;
	public String azp;
	public String session_state;
	public String preferred_username;
	public String email;
	public Boolean email_verified;
	public String acr;
	public String scope;
	public String client_id;
	public String username;
	public Boolean active;
}
