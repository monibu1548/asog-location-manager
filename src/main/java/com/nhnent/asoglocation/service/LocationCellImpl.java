package com.nhnent.asoglocation.service;

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
	public String addTemplateLocation() {
		int templateIndex = getSafeIndex("template");
		if (templateIndex == 0) {
			return "error";
		}

		int dbIndex = integerHash(templateIndex);
		jedis.hset("template", String.valueOf(templateIndex), String.valueOf(dbIndex));
		logger.info("hset template " + templateIndex + " " + dbIndex);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", templateIndex);
		jsonObject.addProperty("db", dbIndex);
		return jsonObject.toString();
	}

	@Override
	public String findTemplateLocation(int templateId) {
		String getResult = jedis.hget("template", String.valueOf(templateId));
		logger.info("hget template " + templateId + " : " + getResult);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("db", Integer.valueOf(getResult));
		return jsonObject.toString();
	}

	@Override
	public String addUrlLocation(String vote) {

		int dbIndex = voteHash(vote);
		jedis.hset("url", vote, String.valueOf(dbIndex));

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("db", dbIndex);
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
	public String addMemberLocation() {
		int memberIndex = getSafeIndex("member");
		if (memberIndex == 0) {
			return "error";
		}

		int dbIndex = integerHash(memberIndex);
		jedis.hset("member", String.valueOf(memberIndex), String.valueOf(dbIndex));

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", memberIndex);
		jsonObject.addProperty("db", dbIndex);
		return jsonObject.toString();
	}

	@Override
	public String findMemberLocation(int memberId) {
		String getResult = jedis.hget("member", String.valueOf(memberId));

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("db", Integer.valueOf(getResult));
		return jsonObject.toString();
	}

	private synchronized int getSafeIndex(String type){
		String getResult = jedis.hget(type, "counter");
		if (getResult == null) {
			return 0;
		}
		int currentIndex = Integer.valueOf(getResult);
		currentIndex += 1;
		jedis.hset(type, "counter", String.valueOf(currentIndex));
		return currentIndex;
	}

	private int integerHash(int value) {
		return value%2;
	}

	private int voteHash(String vote) {
		char ch = vote.charAt(vote.length()-1);
		return ch%2;
	}
}
