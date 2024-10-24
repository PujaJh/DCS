/**
 * 
 */
package com.amnex.agristack.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.amnex.agristack.gateway.DAO.TokenDAO;

/**
 * @author majid.belim
 *
 */
@FeignClient(name = "agri-stack", url = "${feign.url}")
@Repository
public interface AgristackServiceProxy {
	@PostMapping(path = "/cs/agristack/v1/api/authenticate/user/getToken",consumes = "application/json")
	public Object findByToken(@RequestBody Object token);
}
