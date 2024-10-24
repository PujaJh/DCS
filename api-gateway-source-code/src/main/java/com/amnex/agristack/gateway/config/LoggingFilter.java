/**
 * 
 */
package com.amnex.agristack.gateway.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.amnex.agristack.gateway.DAO.TokenDAO;
import com.amnex.agristack.gateway.client.AgristackServiceProxy;
import com.amnex.agristack.gateway.util.Constants;

import feign.FeignException.Unauthorized;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author majid.belim
 *
 */
@Component
public class LoggingFilter implements GlobalFilter,GatewayFilter {
//	
	private Log log = LogFactory.getLog(getClass());
	@Value("${app.excludeURLs}")
	private List<String> excludeURLs;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private AgristackServiceProxy agristackServiceProxy;
	
	@Value("${app.redis.name}")
	private String redisAppName;
	@Autowired
	private RedisService redisService;
	
	@Value("${app.redis.enable}")
	private Boolean isRedis;
	
	@Value("${app.rate.limit}")
	private Integer maxRequestPerSecond;
	
	
	@Value("${app.rate.limit.enable}")
	private Boolean isRateLimit;
	@Value("${app.allowed.hosts}")
	List<String> allowedHosts;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		try {
		ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();

		String apiUrl = exchange.getRequest().getPath().toString();
		String requestedHost=request.getHeaders().getHost().getHostName();
		

		System.out.println("============================");
		System.out.println("allowedHosts Proper "+allowedHosts);
		
		System.out.println("requestedHost request "+requestedHost);
		System.out.println("============================");
		
						if (isValidHost(requestedHost)) {
							Boolean isURLExcluded = checkExcludeURLs(apiUrl);
							String requestIP=getIp(request);
							if (isURLExcluded.equals(Boolean.FALSE)) {
								try {
									if(isRateLimit.equals(Boolean.TRUE)) {
										String clientIpAddress = exchange.getRequest().getRemoteAddress().getAddress().toString() + apiUrl;
										try {
											if(requestIP!=null) {
												clientIpAddress=requestIP + apiUrl;
											}else {
												clientIpAddress = exchange.getRequest().getRemoteAddress().getAddress().toString() + apiUrl;
											}
										}catch(Exception e) {
											
										}
										
										if(clientIpAddress==null || requestIP==null) {
											clientIpAddress = exchange.getRequest().getRemoteAddress().getAddress().toString() + apiUrl;
										}
										if (isMaximumRequestsPerSecondExceeded(clientIpAddress)) {
											exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
											return exchange.getResponse().setComplete();
										}					
									}

								} catch (Exception e1) {
									e1.printStackTrace();
								}
								if (!request.getHeaders().containsKey("Authorization")) {
									ServerHttpResponse response = exchange.getResponse();
									response.setStatusCode(HttpStatus.UNAUTHORIZED);

									return response.setComplete();
								}

								String accessToken = getAccessToken(exchange.getRequest().getHeaders().get("Authorization").toString());

								
								if (accessToken != null ) {
									try {
										

										String userName = jwtTokenUtil.getUsernameFromToken(accessToken);
										
										if(isRedis!=null && isRedis.equals(true)) {
											Boolean isValid=tokenValidateRedis(userName,apiUrl);
//											log.info(isValid);	
											if(isValid==null || isValid.equals(Boolean.FALSE)) {
												ServerHttpResponse response = exchange.getResponse();
												response.setStatusCode(HttpStatus.UNAUTHORIZED);
												return response.setComplete();							
											}

										}
										
									} catch (Exception e) {
//										log.info("==========isRedis Exception start ================== ");
										e.printStackTrace();
//										log.info(e);
//										log.info("============isRedis Exception end================ ");
//										ServerHttpResponse response = exchange.getResponse();
//										response.setStatusCode(HttpStatus.UNAUTHORIZED);
					//
//										return response.setComplete();
									
									}

								}
							}

							Set<URI> uris = exchange.getAttributeOrDefault(GATEWAY_ORIGINAL_REQUEST_URL_ATTR, Collections.emptySet());
							String originalUri = (uris.isEmpty()) ? "Unknown" : uris.iterator().next().toString();
							Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
							URI routeUri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
//							log.info("============================ Incoming request " + originalUri + " is routed to id: " + route.getId()
//									+ ", uri:" + routeUri);
//							log.info("++++++++++++++++++++START++++++++++++++++++++++++++++ ");
//							log.info("getStatusCode "+exchange.getResponse().getStatusCode());
//							log.info("getRawStatusCode "+exchange.getResponse().getRawStatusCode());
//							log.info("++++++++++++++++++++END++++++++++++++++++++++++++++ ");
							return chain.filter(exchange);	
						} else {
								
							   exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
						        return exchange.getResponse().setComplete();
						}
			


		} catch (Exception e) {
//			System.out.println("============================= (API GATE WAY) Start catch ==================================");
//			System.out.println("Exception catch  Exception e==> " + e);
//			System.out.println("Exception catch  Exception e==> " + e.getMessage());
//			System.out.println("============================= (API GATE WAY) End catch ==================================");
			return chain.filter(exchange);
		}
	}
	

	/**
	 * @author majid.belim
	 * @param requestedHost
	 * @return
	 */
	private boolean isValidHost(String requestedHost) {
		return allowedHosts.contains(requestedHost);
	}

	private String getAccessToken(String authorization) {
		String accessToken="";
		accessToken = convertToString(authorization);
		if (accessToken != null && accessToken.startsWith("Bearer ")) {
			accessToken = accessToken.substring(7);
		}
		return  accessToken;
	}
	private String convertToString(String clientId) {
		if (clientId != "") {
			StringBuilder sbclientId = new StringBuilder(clientId);
			sbclientId.deleteCharAt(clientId.length() - 1);
			sbclientId.deleteCharAt(0);
			clientId = sbclientId.toString();
			return clientId;
		} else {
			return "";
		}

	}

	Boolean checkExcludeURLs(String apiUrl) {

		Boolean isURLExcluded = false;
		for (String url : excludeURLs) {

			if (apiUrl.contains(url)) {
				isURLExcluded = true;
			}

		}

		return isURLExcluded;

	}
	
	public Boolean tokenValidateRedis(String userId,String apiUrl)  {
		
		Boolean isValid=false;
		String userKey ="";
		
			 userKey = redisAppName + "_" + Constants.REDIS_USER_KEY + userId;
		
		
		Object userDataObj;
		try {
			userDataObj = redisService.getValue(userKey);
			
			if (userDataObj != null) {
				
				JSONObject responseJson = new JSONObject(userDataObj.toString());
				if (responseJson.has("userToken")) {
					String userToken = (String) responseJson.get("userToken");
//					log.info("==========TOKEN============");
//					log.info(userToken);
//					log.info("===========================");
					isValid=true;
				}
	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		return isValid;

	}
	
	private boolean isMaximumRequestsPerSecondExceeded(String clientIpAddress) {
		Integer requests = 0;
		Object redisRequestCount;
		try {
			redisRequestCount = redisService.getValue(clientIpAddress);
			if (redisRequestCount != null) {
				requests = Integer.parseInt(redisRequestCount.toString());
				if (requests > maxRequestPerSecond) {
					setIpRateLimitCounts(clientIpAddress, requests);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		requests++;
		setIpRateLimitCounts(clientIpAddress, requests);
		return false;
	}

	public void setIpRateLimitCounts(String clientIpAddress, Integer requests) {
		try {
			redisService.setValue(clientIpAddress, requests);
			redisService.expireKey(clientIpAddress, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

		String getIp(ServerHttpRequest request){

			return  request.getHeaders().getFirst("X-Forwarded-For");
		}
}