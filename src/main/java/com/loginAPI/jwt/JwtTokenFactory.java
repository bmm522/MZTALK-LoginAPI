package com.loginAPI.jwt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.loginAPI.model.User;
import com.loginAPI.properties.JwtProperties;

public class JwtTokenFactory {

	public Map<String, String> getJwtToken(User user) {
		HashMap<String, String> map = new HashMap<String, String>();
		String refreshToken = getRefreshToken();
		map.put("jwtToken",
		JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
				.withClaim("id", user.getId())
				.withClaim("username",user.getUsername())
				.withClaim("nickname",user.getNickname())
				.withClaim("email",user.getEmail())
				.withClaim("birth",user.getBirth())
				.withClaim("phone",user.getPhone())
				.withClaim("address",user.getAddress())
				.withClaim("role",user.getRole())
				.withClaim("provider",user.getProvider())
				.withClaim("providerId",user.getProviderId())
				.withClaim("createDate",getCreateDate(user.getCreateDate()))
				.sign(Algorithm.HMAC512(JwtProperties.SECRET+refreshToken)));
		map.put("refreshToken", refreshToken);
		
		return map;
	}
	

	
	private String getCreateDate(Timestamp createDate) {
		  java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(createDate);
	}
	
	
	private String getRefreshToken() {
		MessageDigest md = null;
		String randomByte = getRandomByte();
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(randomByte.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return String.format("%064x", new BigInteger(1, md.digest()));
	}
	
	
	private String getRandomByte() {
		byte[] randomByte = null;
		try {
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			randomByte = new byte[16];
			random.nextBytes(randomByte);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return new String(Base64.getEncoder().encode(randomByte));
	}
}
