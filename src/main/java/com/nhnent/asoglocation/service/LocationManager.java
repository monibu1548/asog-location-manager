package com.nhnent.asoglocation.service;

public interface LocationManager {

	String addTemplateLocation(String templateId);

	String findTemplateLocation(String templateId);

	String addUrlLocation(String hashedUrl);

	String findUrlLocation(String hashedUrl);

	String addMemberLocation(String memberId);

	String findMemberLocation(String memberId);
}
