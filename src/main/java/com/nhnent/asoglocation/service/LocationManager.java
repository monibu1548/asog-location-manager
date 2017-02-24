package com.nhnent.asoglocation.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class LocationManager{

	private static final int HASHED_KEY_LEN = 50;

	private JedisPool jedisPool;
	private Jedis jedis;
	
	private final static Logger logger = LoggerFactory.getLogger(LocationManager.class);

	@Inject
	LocationManager(@Value("${redis.url}") String url, @Value("${redis.password}")String password) {
		jedisPool = new JedisPool(new JedisPoolConfig(), url, 6379);
		jedis = jedisPool.getResource();
		if(password != null && !password.isEmpty()) {
			jedis.auth(password);
		}
	}

	public String addUrlLocation(String vote) {

		JsonObject jsonObject = new JsonObject();
		if (vote ==  null) {
			jsonObject.addProperty("result", "vote hash value is empty");
			return jsonObject.toString();
		}

		int dbIndex = getDBIndexByKey(vote);
		if (dbIndex != 0 && dbIndex != 1) {
			jsonObject.addProperty("result", "url hash error");
			return jsonObject.toString();
		}
		jedis.hset("url", vote, String.valueOf(dbIndex));
		jsonObject.addProperty("db", dbIndex);

		jsonObject.addProperty("result", "success");
		return jsonObject.toString();
	}

	public String findUrlLocation(String vote) {
		
		String getResult = jedis.hget("url", vote);

		JsonObject jsonObject = new JsonObject();
		if (getResult ==  null) {
			jsonObject.addProperty("result", "could not find url : " + vote);
			return jsonObject.toString();
		}
		
		int dbIndex = getDBIndexByKey(vote);

		if (dbIndex != 0 && dbIndex != 1) {
			jsonObject.addProperty("result", "url hash error");
			logger.info("hash result : " + dbIndex);
			return jsonObject.toString();
		}

		jsonObject.addProperty("result", "success");
		jsonObject.addProperty("db", dbIndex);
		return jsonObject.toString();
	}

	private int getDBIndexByKey(String key) {
		String hashedKey = calcHashSHA256(key);
		char keyChar = hashedKey.charAt(HASHED_KEY_LEN - 1);

		return keyChar & 0b1;
	}
	
	private String calcHashSHA256(String str){
		String SHA256;
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-256"); 
			md.update(str.getBytes()); 
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}

			/* cut hashresult for length limit */
			SHA256 = sb.toString().substring(0, HASHED_KEY_LEN);
		} catch(NoSuchAlgorithmException e){
			logger.debug("No Such Algorithm Exception Error in calcHshSHA256(), detail here : " + e.getMessage());
			SHA256 = null; 
		}

		return SHA256;
	}
}
