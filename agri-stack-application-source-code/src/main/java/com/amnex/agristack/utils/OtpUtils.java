package com.amnex.agristack.utils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class OtpUtils {

	private LoadingCache<String, Integer> otpCache;

	public void OtpService() {

	}

	public int generateOTP(String key, int expireMins) {
		this.otpCache = CacheBuilder.newBuilder().expireAfterWrite(expireMins, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					@Override
					public Integer load(String key) {
						return 0;
					}
				});
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		otpCache.put(key, otp);
		return otp;
	}

	public int generateOTPWithoutExpiriy(String key) {
		this.otpCache = CacheBuilder.newBuilder()
				.build(new CacheLoader<String, Integer>() {
					@Override
					public Integer load(String key) {
						return 0;
					}
				});
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		otpCache.put(key, otp);
		return otp;
	}

	public int addOtp(String key, int expireMins) {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		otpCache.put(key, otp);
		return otp;
	}

	public int getOtp(String key) {
		try {
			return otpCache.get(key);
		} catch (Exception e) {
			return 0;
		}
	}

	public void clearOTP(String key) {
		otpCache.invalidate(key);
	}
}
