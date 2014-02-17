package com.solaiemes.serviceExample.control;

public interface ServiceControlMBean {
	
	public String stopService();
	public String startService();
	public void setUsername(String username);
	public void setBasicAuthenticationHeader(String basicAuthenticationHeader);
	
}
