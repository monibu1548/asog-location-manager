package com.nhnent.asoglocation;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nhnent.asoglocation.service.LocationManager;

import redis.clients.jedis.Jedis;

@RestController
public class LocationRestController {
	
	@Autowired
	private LocationManager locationManager;

	private static final Logger logger = LoggerFactory.getLogger(LocationRestController.class);
	
	private Jedis jedis;

	@Inject
	LocationRestController(@Value("${redis.url}") String url, @Value("${redis.password}")String password) {
		jedis = new Jedis(url, 6379);
		if(password != null && !password.isEmpty()) {
			jedis.auth(password);
		}
	}

	@RequestMapping(value = "/location/url/{hashedUrl}", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public String addUrlLocation(@PathVariable(value = "hashedUrl") String hashedUrl) {
		return locationManager.addUrlLocation(hashedUrl);
	}

	@RequestMapping(value = "/location/url/{hashedUrl}", method = RequestMethod.GET, produces="application/json;charset=UTF-8")
	public String findUrlLocation(@PathVariable(value = "hashedUrl") String hashedUrl) {
		return locationManager.findUrlLocation(hashedUrl);
	}

}
