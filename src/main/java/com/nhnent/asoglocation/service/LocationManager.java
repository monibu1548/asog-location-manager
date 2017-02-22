package com.nhnent.asoglocation.service;

public interface LocationManager {

	public String addTemplateLocation();

	public String findTemplateLocation();

	public String addUrlLocation(String vote);

	public String findUrlLocation(String vote);

	public String addMemberLocation();

	public String findMemberLocation();
}
