package com.nhnent.asoglocation.service;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;

@Component
public class LocationCellImpl implements LocationManager{

	private Jedis jedis;

	@Inject
	LocationCellImpl(@Value("${redis.url}") String url, @Value("${redis.password}")String password) {
		jedis = new Jedis(url, 6379);
		if(password != null && !password.isEmpty()) {
			jedis.auth(password);
		}
	}

	@Override
	public String addTemplateLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findTemplateLocation(int templateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addUrlLocation(String vote) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findUrlLocation(String vote) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addMemberLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findMemberLocation(int memberId) {
		// TODO Auto-generated method stub
		return null;
	}

}
