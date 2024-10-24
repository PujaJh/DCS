/**
 * 
 */
package com.amnex.agristack.gateway.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author majid.belim
 *
 */
@Component
public class JwtTokenUtil {

	private static final long serialVersionUID = -2550185165626007488L;

	@Value("${jwt.token.validity}")
	public long JWT_TOKEN_VALIDITY;

	@Value("${jwt.mobile.token.validity}")
	public long JWT_MOBILE_TOKEN_VALIDITY;

	@Value("${jwt.secret}")
	private String secret;

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// generate token for user

//	public String generateToken(UserDetails userDetails, Boolean forMobile) {
//
//		Map<String, Object> claims = new HashMap<>();
//		claims.put("role", userDetails.getAuthorities());
//
////		Optional<UserMaster> userOptional = userMasterRepository.findByUserIdAndIsDeletedAndIsActive(
////				Long.valueOf(userDetails.getUsername()), Boolean.FALSE, Boolean.TRUE);
//		List<Integer> userAuthority = new ArrayList<>();
//
////		if (userOptional.isPresent()) {
////			UserMaster userMaster = userOptional.get();
////			if (userMaster.getRoleId() != null && userMaster.getRoleId().getMenu() != null
////					&& userMaster.getRoleId().getMenu().size() > 0) {
////				userMaster.getRoleId().getMenu().forEach(action -> {
////					userAuthority.add(action.getMenuCode());
////
////				});
////			}
////		}
//
//		claims.put("userAuthority", userAuthority);
//		claims.put("userId", userDetails.getUsername());
//		if (forMobile) {
//			return doGenerateMobileToken(claims, userDetails.getUsername());
//		} else {
//			return doGenerateToken(claims, userDetails.getUsername());
//		}
//	}

//	public String generateToken(UserDetails userDetails) {
//		Map<String, Object> claims = new HashMap<>();
//		String authorities="WEB_USER";
//		claims.put("role", authorities);
//		List<Integer> userAuthority = new ArrayList<>();
//		userAuthority.add(1);
//		claims.put("userAuthority", userAuthority);
//		claims.put("userId", userDetails.getUsername());
//		return doGenerateToken(claims, userDetails.getUsername());
//	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	private String doGenerateToken(Map<String, Object> claims, String subject) {

//		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
//				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
//				.signWith(SignatureAlgorithm.HS512, secret).compact();
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	private String doGenerateMobileToken(Map<String, Object> claims, String subject) {

//		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
//				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
//				.signWith(SignatureAlgorithm.HS512, secret).compact();
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_MOBILE_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	// validate token
//	public Boolean validateToken(String token, UserDetails userDetails) {
//		final String username = getUsernameFromToken(token);
//		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//	}

	public Claims getData(String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			e.printStackTrace();
			claims = null;
		}
		return claims;
	}

	public Map<String, Object> getMapFromIoJsonwebtokenClaims(Claims claims) {
		Map<String, Object> expectedMap = new HashMap<String, Object>();
		for (Entry<String, Object> entry : claims.entrySet()) {
			expectedMap.put(entry.getKey(), entry.getValue());
		}
		return expectedMap;
	}

}
