package com.amnex.agristack.centralcore.client;

import com.amnex.agristack.centralcore.dao.AIUTokenRequestDAO;
import com.amnex.agristack.centralcore.dao.TokenDAO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FeignClient(name = "centralService", url = "${feign.centralcore.url}")
public interface CentralServiceCall {

	@PostMapping(path = "/token", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public String getToken(@RequestParam TokenDAO responseData);
//    public String getToken(
//                                  @RequestParam   ("client_id") String client_id,
//                           @RequestParam("username") String username,
//                           @RequestParam("password") String password,
//                           @RequestParam("grant_type") String  grant_type);

	@PostMapping(path = "/validateTokenOfAIUFromSC")
	public String validateTokenOfAIUFromSC(@RequestHeader(name = "agristackUser") String agristackUser,@RequestHeader(name = "agristackPassword") String agristackPassword,
			@RequestBody AIUTokenRequestDAO aiuTokenRequestDAO);
}
