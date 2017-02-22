package com.nhnent.asoglocation;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nhnent.asoglocation.service.LocationManager;

@RestController
public class LocationRestController {
	
	@Autowired
	private LocationManager locationManager;

	private static final Logger logger = LoggerFactory.getLogger(LocationRestController.class);
	
	@RequestMapping(value = "/location/template", method = RequestMethod.POST)
	public String addTemplateLocation() {
		locationManager.addTemplateLocation();
		logger.info("call add template location");
		return "home";
	}

	@RequestMapping(value = "/location/template/{templateId}", method = RequestMethod.GET)
	public String findTemplateLocation(@PathVariable(value = "templateId") int templateId) {
		return locationManager.findTemplateLocation(templateId);
	}

	@RequestMapping(value = "/location/url", method = RequestMethod.POST)
	public String addUrlLocation(HttpServletRequest request) {
		String vote = request.getParameter("vote");
		logger.info("call add url location");
		return locationManager.addUrlLocation(vote);
	}

	@RequestMapping(value = "/location/url", method = RequestMethod.GET)
	public String findUrlLocation(HttpServletRequest request) {
		String vote = request.getParameter("vote");
		logger.info("call find url location");
		return locationManager.findUrlLocation(vote);
	}
	
	@RequestMapping(value = "/location/member", method = RequestMethod.POST)
	public String addMemberLocation() {
		logger.info("call add member location");
		return locationManager.addMemberLocation();
	}

	@RequestMapping(value = "/location/member/{memberId}", method = RequestMethod.GET)
	public String findMemberLocation(@PathVariable(value = "memberId") int memberId) {
		logger.info("call find member location");
		return locationManager.findTemplateLocation(memberId);
	}
}
