package com.nhnent.asoglocation.service;

public interface LocationManager {

	String addTemplateLocation();

	String findTemplateLocation();

	String addUrlLocation(String vote);

	String findUrlLocation(String vote);

	String addMemberLocation();

	String findMemberLocation();
}
