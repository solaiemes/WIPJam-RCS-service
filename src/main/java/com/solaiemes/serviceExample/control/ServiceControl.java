package com.solaiemes.serviceExample.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ServiceControl implements ServiceControlMBean {
	
	private static final Log log = LogFactory.getLog(ServiceControl.class);
	
	private static Object theServiceLock = new Object();
	private static TheService theService = null;
	private static String identity = null;
	private static String basicAuthenticationHeader = null;
	
	public static String getUsername() {
		return identity;
	}

	public void setUsername(String username) {
		ServiceControl.identity = username;
	}
	
	public String getBasicAuthenticationHeader() {
		return basicAuthenticationHeader;
	}

	public void setBasicAuthenticationHeader(String basicAuthenticationHeader) {
		ServiceControl.basicAuthenticationHeader = basicAuthenticationHeader;
	}
	
	public String stopService() {
		log.info("OK - Service will stop");
		return theService.shutdown(identity, basicAuthenticationHeader);
	}
	
	public String startService() {
		synchronized (theServiceLock) {
			if (theService == null) {
				theService = new TheService();
			}
			log.info("OK - Service will start");
			return theService.start(identity, basicAuthenticationHeader);
		}
	}

}
