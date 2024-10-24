package com.amnex.agristack.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amnex.agristack.dao.common.ResponseModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.amnex.agristack.entity.MenuMaster;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.service.RedisService;
import com.amnex.agristack.utils.Constants;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

import feign.FeignException.Unauthorized;
import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	private static final Logger logger = Logger.getLogger(JwtRequestFilter.class.getName());
	private static List<MenuMaster> menuList;
	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${app.excludeURLs}")
	List<String> excludeURLs;
	@Value("${app.allow.multipleSessions}")
	private Boolean multipleSessions;
	@Autowired
	private RedisService redisService;
	@Value("${app.redis.name}")
	private String redisAppName;
	
	@Value("${app.redis.enable}")
	private Boolean isRedis;
	
//	@Value("${app.allowed.hosts}")
//	List<String> allowedHosts;
//
//	@Value("${app.allowed.origins}")
//	List<String> allowedOrigins;
//	
//	@Value("${api-key}")
//	String API_KEY;
//	
//	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
//		String requestedHost = request.getServerName();
//		String origin = request.getHeader("Origin");
		 
//		System.out.println("allowedHosts Proper "+allowedHosts);
		
//		System.out.println("requestedHost request "+requestedHost);
//		System.out.println("============================");
//		System.out.println("origin request"+origin);
//		   String hostHeader = request.getHeader("Host");
//		   System.out.println("hostHeader "+hostHeader);
//		System.out.println("origin Proper"+allowedOrigins);
//		System.out.println("============================");
//					if (isValidOrigin(origin)) {
//						System.out.println("allowedHosts "+allowedHosts);
//						
//						if (isValidHost(requestedHost)) {
//							final String requestAPIKey = request.getHeader("x-api-key");
//			
//							if (requestAPIKey != null) {
//								if (requestAPIKey.equals(API_KEY)) {
//									chain.doFilter(request, response);
//								}
//							} else {
							
								
								final String requestTokenHeader = request.getHeader("Authorization");

								String apiUrl = request.getRequestURI().toString();

								Boolean isURLExcluded = checkExcludeURLs(apiUrl);

//								System.out.println(apiUrl);

								if (!isURLExcluded) {

									String username = getUserName(requestTokenHeader);
									// Once we get the token validate it.
									if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
										String jwtToken = requestTokenHeader.substring(7);
										try {
									
											UserDetails userDetails = null;
											if(isRedis!=null && isRedis.equals(true)) {
												userDetails=	this.jwtUserDetailsService.loadUserByUsernameRedis(username);
											}else {
												userDetails=this.jwtUserDetailsService.loadUserByUsername(username);
											}
											

											if (multipleSessions.equals(true)||jwtTokenUtil.validateToken(jwtToken, userDetails) && userDetails.getPassword() != null && !userDetails.getPassword().equals("")
													&& userDetails.getPassword().equals(jwtToken)) {

												UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
														userDetails, null, userDetails.getAuthorities());
												usernamePasswordAuthenticationToken
														.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
												SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
												chain.doFilter(request, response);

											} else {
												
												 ObjectMapper mapper = new ObjectMapper();
												 ResponseModel res=CustomMessages.makeResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE,
															CustomMessages.UNAUTHORIZED_ERROR, CustomMessages.UNAUTHORIZED);
												 String responseMsg = mapper.writeValueAsString(res);
												 response.getWriter().write(responseMsg);
												((HttpServletResponse) response).setHeader("Content-Type", "application/json");
												((HttpServletResponse) response).setStatus(401);
												return;
											}
										} catch (UsernameNotFoundException u) {
											 ObjectMapper mapper = new ObjectMapper();
											 ResponseModel res=CustomMessages.makeResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE,
														CustomMessages.UNAUTHORIZED_ERROR, CustomMessages.UNAUTHORIZED);
											 String responseMsg = mapper.writeValueAsString(res);
											 response.getWriter().write(responseMsg);
											((HttpServletResponse) response).setHeader("Content-Type", "application/json");
											((HttpServletResponse) response).setStatus(401);
											return;
										} catch (Exception e) {

											e.printStackTrace();
										}
									} else {
										 ObjectMapper mapper = new ObjectMapper();
										 ResponseModel res=CustomMessages.makeResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE,
													CustomMessages.UNAUTHORIZED_ERROR, CustomMessages.UNAUTHORIZED);
										 String responseMsg = mapper.writeValueAsString(res);
										 response.getWriter().write(responseMsg);
										((HttpServletResponse) response).setHeader("Content-Type", "application/json");
										((HttpServletResponse) response).setStatus(401);
										return;
									}

								} else {
									chain.doFilter(request, response);
								}
//							}
//			
//						} else {
//							// Reject the request
//							// You can return an HTTP 403 Forbidden response or perform other actions
//							// Example:
//							((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid Host Header");
//							return;
//						}
//				} 
//				else 
//				{
//						((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid request origin.");
//						return;
//						
//				}

		

	}
	
	/**
	 * @author majid.belim
	 * @param requestedHost
	 * @return
	 */
//	private boolean isValidOrigin(String origin) {
//		if (origin == null) {
//			return true;
//		}
//		return allowedOrigins.contains(origin);
//	}
//
//	/**
//	 * @author majid.belim
//	 * @param requestedHost
//	 * @return
//	 */
//	private boolean isValidHost(String requestedHost) {
//		return allowedHosts.contains(requestedHost);
//	}

	Boolean checkExcludeURLs(String apiUrl) {

		Boolean isURLExcluded = false;
		for (String url : excludeURLs) {

			if (apiUrl.contains(url)) {
				isURLExcluded = true;
			}

		}

		return isURLExcluded;

	}

	String getUserName(String requestTokenHeader) {

		String userName = null;

		// JWT Token is in the form "Bearer token". Remove Bearer word and get
		// only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {

			String jwtToken = requestTokenHeader.substring(7);

			try {

				userName = jwtTokenUtil.getUsernameFromToken(jwtToken);

			} catch (IllegalArgumentException e) {
				logger.info("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				logger.info("JWT Token has expired");
			} catch (Exception e) {
				logger.info("JWT Token has expired" + e);
			}

		} else {
			logger.warning("JWT Token does not begin with Bearer String");
		}
		return userName;
	}

	public String findUserIdFromRequest(HttpServletRequest request) {
		String userId = request.getHeader("userId");
		return userId;
	}

}