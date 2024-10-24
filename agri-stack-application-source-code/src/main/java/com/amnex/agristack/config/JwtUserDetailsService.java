package com.amnex.agristack.config;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.repository.UserMasterRepository;
import com.amnex.agristack.service.RedisService;
import com.amnex.agristack.utils.Constants;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private UserMasterRepository userMasterRepository;
	@Value("${app.redis.name}")
	private String redisAppName;
	@Autowired
	private RedisService redisService;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		Optional<UserMaster> user = userMasterRepository.findByUserIdAndIsDeleted(Long.valueOf(userId), Boolean.FALSE);
//				userMasterRepository.findByUserIdAndIsDeletedAndIsActive(Long.valueOf(userId), Boolean.FALSE,
//				Boolean.TRUE);

		if (!user.isPresent()) {                              
			throw new UsernameNotFoundException("User not found with username: " + userId);
		} else if (user.get().getUserToken() == null) {
			user.get().setUserToken("");
		}
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		UserMaster userMaster = user.get();
		if (userMaster.getRoleId() != null) {
			grantedAuthorities.add(new SimpleGrantedAuthority(userMaster.getRoleId().getRoleName()));

		}
		return new User(user.get().getUserId() + "", user.get().getUserToken(), grantedAuthorities);
	}

	public UserDetails loadUserByUsernameRedis(String userId) throws UsernameNotFoundException {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		String userKey = redisAppName + "_" + Constants.REDIS_USER_KEY + userId;
		Object userDataObj = null;
		String userToken = "";
		String getUserId;
		try {
			userDataObj = redisService.getValue(userKey);
			if (userDataObj != null) {
				JSONObject responseJson = new JSONObject(userDataObj.toString());
				if (responseJson.has("userToken")) {
					userToken = (String) responseJson.get("userToken");
				}
				if (responseJson.has("userId")) {
					userId = String.valueOf(responseJson.get("userId"));
				}
				if (responseJson.has("roleId")) {

					JSONObject rowdata = (JSONObject) responseJson.get("roleId");
					if (rowdata.has("roleName")) {
						grantedAuthorities.add(new SimpleGrantedAuthority((String) rowdata.get("roleName")));
					}

				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
			System.out.println(e.getMessage());
		}
		return new User(userId, userToken, grantedAuthorities);

	}

}
