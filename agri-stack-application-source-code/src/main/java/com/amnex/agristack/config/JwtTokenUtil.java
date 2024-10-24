package com.amnex.agristack.config;

import java.security.Key;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.repository.LandParcelSurveyDetailRepository;
import com.amnex.agristack.repository.ValidationLogRepository;
import com.amnex.agristack.utils.CustomMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.repository.UserMasterRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;


@Component
public class JwtTokenUtil {

	private static final long serialVersionUID = -2550185165626007488L;
	
	@Value("${jwt.token.validity}")
	public long JWT_TOKEN_VALIDITY;

	@Value("${jwt.mobile.token.validity}")
	public long JWT_MOBILE_TOKEN_VALIDITY;

	@Value("${jwt.secret}")
	private String secret;

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private static ValidationLogRepository validationLogRepository;


	public static String token ="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIyQxMjMiLCJyb2xlIjoiYWRtaW4iLCJzY29wZSI6InJlYWQiLCJleHAiOjE3NDE4MTIxMTIsImlhdCI6MTcyNjI2MDExMn0.pF2WETPvKJgjm82IinauCEj35WLqxrfRkc4fUM9XKoc";
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
	private  static String mykey ="Thisismykey12345";
	private  static SecretKey secretKey = new SecretKeySpec(mykey.getBytes(), "AES"); //keyGenerator.generateKey();


	private static final long SIX_MONTHS_IN_MILLIS = 6L * 30L * 24L * 60L * 60L * 1000L; // 6 months



	// Method to generate the secure token
//	public static String generateSecureToken(Map<String, Object> claims, String subject, HttpServletRequest request) throws Exception {
//		String requestTokenHeader = request.getHeader("Authorization");
//		validateToken(requestTokenHeader);
//		Date expirationTime = getExpirationTime(requestTokenHeader);
//		long currentTimeMillis = System.currentTimeMillis();
//
//
//		return Jwts.builder()
//				.setClaims(claims)  // Set custom claims
//				.setSubject(subject) // Subject: e.g., a user identifier or app context
//				.setIssuedAt(new Date(currentTimeMillis))  // Current time as issue time
//				.setExpiration(new Date(currentTimeMillis + SIX_MONTHS_IN_MILLIS)) // Set token expiration (2 hours from now)
//				.signWith(SignatureAlgorithm.HS256, secretKey)  // Sign with secure key using HS256 algorithm
//				.compact();  // Generate the token
//	}
//
//	public static Date getExpirationTime (String token){
//		Claims claims = Jwts.parser()
//				.setSigningKey(secretKey)
//				.parseClaimsJws(token)
//				.getBody();
//
//		// Extract expiration time
//		Date expiration = claims.getExpiration();
//		return expiration;
//	}
//	public static void validateToken(String token) throws Exception {
//		Date expirationTime = getExpirationTime(token);  // Get expiration time
//		Date currentTime = new Date();  // Current time
//
//		// Compare expiration time with current time
//		if (expirationTime.before(currentTime)) {
//			throw new Exception("Token has expired!");
//		} else {
//			System.out.println("Token is valid.");
//		}
//	}

	public static ResponseModel generateSecureToken(Map<String, Object> claims, String subject, HttpServletRequest request) throws Exception {
		String requestTokenHeader = request.getHeader("Authorization");
		if ( requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {

			String jwtToken = requestTokenHeader.substring(7);
			if(!jwtToken.equals(getActiveTokenByServiceName("central_api_access")))
				return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
						CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

		}
		if ( requestTokenHeader != null && !requestTokenHeader.startsWith("Bearer ")) {

			String jwtToken = requestTokenHeader.substring(7);
			if(!jwtToken.equals(getActiveTokenByServiceName("central_api_access")))
				return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
						CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

		}

		if ( requestTokenHeader == null) {
			return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
					CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

		}
		String jwtToken = requestTokenHeader.substring(7);


		System.out.println("requestTokenHeader "+requestTokenHeader);

		// Check if the token is expired and generate a new one if needed
		if (isTokenExpiredNew(jwtToken)) {
			String newToken = createNewToken(claims, subject);
			insertTokenInDatabase(newToken, "api_access", "example_service");  // Store new token in DB
			return new ResponseModel(newToken,CustomMessages.SUCCESS,CustomMessages.GET_DATA_SUCCESS,CustomMessages.SUCCESS,CustomMessages.METHOD_GET);
		} else {
			// If token is still valid, return the existing token
			return new ResponseModel("token has not expired",CustomMessages.SUCCESS,CustomMessages.GET_DATA_SUCCESS,CustomMessages.SUCCESS,CustomMessages.METHOD_GET);
		}
	}

	// Method to create a new token
	public static String createNewToken(Map<String, Object> claims, String subject) {
		long currentTimeMillis = System.currentTimeMillis();
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(currentTimeMillis))  // Set issue time to now
				.setExpiration(new Date(currentTimeMillis + SIX_MONTHS_IN_MILLIS))  // 6 months expiration
				.signWith(SignatureAlgorithm.HS256, secretKey)  // Use HS256 and secret key for signing
				.compact();
	}

	// Method to extract expiration time from a token
	public static Date getExpirationTime(String token) {
		System.out.println("token "+token);
		Claims claims = Jwts.parser()
				.setSigningKey(secretKey)
				.parseClaimsJws(token)
				.getBody();
		return claims.getExpiration();
	}

	// Method to check if a token is expired
	public static boolean isTokenExpiredNew(String token) throws Exception {
		Date expirationTime = getExpirationTime(token);
		Date currentTime = new Date();  // Get current time
		System.out.println("expirationTime "+expirationTime);

		// Return true if the token is expired, false otherwise
		return expirationTime.before(currentTime);
	}

	// Method to insert the new token into the database
	public static void insertTokenInDatabase(String token, String tokenType, String serviceName) throws Exception {
		// Database connection details
		Timestamp issuedAt = new Timestamp(new Date().getTime());
		Timestamp expiresAt = new Timestamp(issuedAt.getTime() + (6L * 30 * 24 * 60 * 60 * 1000));  // 6 months expiration

		// Call repository method to insert the new token
		validationLogRepository.insertToken(token, tokenType, serviceName, issuedAt, expiresAt, true);
	}

	// generate token for user
	
	public String generateToken(UserDetails userDetails,Boolean forMobile) {
		
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", userDetails.getAuthorities());
		
		
		Optional<UserMaster> 	userOptional=userMasterRepository.findByUserIdAndIsDeletedAndIsActive(Long.valueOf(userDetails.getUsername()), Boolean.FALSE, Boolean.TRUE);
		List<Integer> userAuthority = new ArrayList<>();
		
		if(userOptional.isPresent()){
			UserMaster userMaster=userOptional.get();
			if(userMaster.getRoleId()!=null &&userMaster.getRoleId().getMenu()!=null && userMaster.getRoleId().getMenu().size()>0){
				userMaster.getRoleId().getMenu().forEach(action->{
					userAuthority.add(action.getMenuCode());
					
				});
			}
		}
		
		
		
		claims.put("userAuthority", userAuthority);
		claims.put("userId", userDetails.getUsername());
		if(forMobile) {
			return doGenerateMobileToken(claims, userDetails.getUsername());
		} else {
			return doGenerateToken(claims, userDetails.getUsername());
		}
	}



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
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	public Claims getData(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
        	 e.printStackTrace();
            claims = null;
        }
        return claims;
    }
	public Map<String, Object>  getMapFromIoJsonwebtokenClaims(Claims claims){
	    Map<String, Object> expectedMap = new HashMap<String, Object>();
	    for(Entry<String, Object> entry : claims.entrySet()) {
	        expectedMap.put(entry.getKey() , entry.getValue());
	    }
	    return expectedMap;
	}
	public static String getActiveTokenByServiceName(String serviceName) {
		System.out.println("serviceName "+serviceName);
		//String token = validationLogRepository.getActiveTokenByServiceName(serviceName);
		System.out.println("token");
		return token;
		//return validationLogRepository.getActiveTokenByServiceName(serviceName);
	}
}
