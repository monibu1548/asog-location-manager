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

@Component
public class LocationCellImpl implements LocationManager{

	private Jedis jedis;
	
	private final static Logger logger = LoggerFactory.getLogger(LocationCellImpl.class);

	@Inject
	LocationCellImpl(@Value("${redis.url}") String url, @Value("${redis.password}")String password) {
		jedis = new Jedis(url, 6379);
		if(password != null && !password.isEmpty()) {
			jedis.auth(password);
		}
	}

	@Override
	public String addTemplateLocation(String templateName) {

		JsonObject jsonObject = new JsonObject();
		if (templateName ==  null) {
			jsonObject.addProperty("result", "template name is empty");
			return jsonObject.toString();
		}

		String templatePrimaryKey = hashTemplateName(templateName);

		// divide
		jsonObject.addProperty("result", "success");
		jsonObject.addProperty("pk", templatePrimaryKey);
		if (templateName.length() < 10) {
			jedis.hset("template", templatePrimaryKey, "0");
			jsonObject.addProperty("db", 0);
		} else {
			jedis.hset("template", templatePrimaryKey, "1");
			jsonObject.addProperty("db", 1);
		}

		return jsonObject.toString();
	}

	@Override
	public String findTemplateLocation(String templatePrimaryKey) {
		
		JsonObject jsonObject = new JsonObject();
		if (templatePrimaryKey ==  null) {
			jsonObject.addProperty("result", "template name is empty");
			return jsonObject.toString();
		}

		String getResult = jedis.hget("template", templatePrimaryKey);
		if (getResult == null) {
			jsonObject.addProperty("result", "fail to find db index by template pk : " + templatePrimaryKey);
			return jsonObject.toString();
		}

		jsonObject.addProperty("result", "success");
		jsonObject.addProperty("db", Integer.valueOf(getResult));
		return jsonObject.toString();
	}

	@Override
	public String addUrlLocation(String vote) {

		JsonObject jsonObject = new JsonObject();
		if (vote ==  null) {
			jsonObject.addProperty("result", "vote hash value is empty");
			return jsonObject.toString();
		}

		char firstCharacter = vote.charAt(0);
		if (firstCharacter > 'm'){
			jedis.hset("vote", vote, "0");
			jsonObject.addProperty("db", 0);
		} else {
			jedis.hset("vote", vote, "1");
			jsonObject.addProperty("db", 1);
		}

		jsonObject.addProperty("result", "success");
		return jsonObject.toString();
	}

	@Override
	public String findUrlLocation(String vote) {
		String getResult = jedis.hget("url", vote);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("db", Integer.valueOf(getResult));
		return jsonObject.toString();
	}

	@Override
	public String addMemberLocation(String memberUuid) {

		JsonObject jsonObject = new JsonObject();
		if (memberUuid == null) {
			jsonObject.addProperty("result", "memberUUID is null");
			return jsonObject.toString();
		}

		if (memberUuid.length() > 20) {
			jedis.hset("member", memberUuid, "0");
			jsonObject.addProperty("db", 0);
		} else {
			jedis.hset("member", memberUuid, "1");
			jsonObject.addProperty("db", 1);
		}
		jsonObject.addProperty("result", "success");
		return jsonObject.toString();
	}

	@Override
	public String findMemberLocation(String memberUuid) {
		
		JsonObject jsonObject = new JsonObject();
		if (memberUuid == null) {
			jsonObject.addProperty("result", "memberUUID is null");
			return jsonObject.toString();
		}

		String getResult = jedis.hget("member", memberUuid);
		if (getResult == null) {
			logger.info("findMemberLocation : " + getResult);
			jsonObject.addProperty("result", "fail to find db index by memberUuid : " + memberUuid);
			return jsonObject.toString();
		}

		jsonObject.addProperty("db", Integer.valueOf(getResult));
		return jsonObject.toString();
	}

	private String hashTemplateName(String templateName) {

		String uniqueTemplateName = templateName + + System.currentTimeMillis();
		String SHA256;
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-256"); 
			md.update(uniqueTemplateName.getBytes()); 
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}

			/* cut hashresult for length limit */
			SHA256 = sb.toString().substring(0, 50);
		} catch(NoSuchAlgorithmException e){
			logger.debug("No Such Algorithm Exception Error in calcHshSHA256(), detail here : " + e.getMessage());
			SHA256 = null; 
		}

		return SHA256;
	}
}
