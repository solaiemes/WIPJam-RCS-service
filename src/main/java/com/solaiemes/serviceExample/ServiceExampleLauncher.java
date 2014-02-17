package com.solaiemes.serviceExample;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.solaiemes.serviceExample.control.ServiceControl;

public class ServiceExampleLauncher {
	
	private static final Log log = LogFactory.getLog(ServiceExampleLauncher.class);
	
	public static String username = null;
	public static String password = null;

	public static void main(String[] args) throws MalformedObjectNameException, NullPointerException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, InterruptedException {
		
		username = null;
		password = null;
		// Just the your username is needed
		if(args.length != 2) {
			log.error("FAIL - Empty username or password, you need to provide the username and password you were provided with on the GSMA website\n" + "NOTE: surround your password parameter with double quotes because it can contain characters that will be caught by the shell");
			return;
		} else {
			username = args[0].replace("+", "%2b");
			String aux = args[0] + ":" + args[1];
			password = new String("Basic " + new String(Base64.encodeBase64(aux.getBytes()))).trim();
			log.info("OK - Starting service as user " + username + " " + password);
		}
		
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName mbeanName = new ObjectName("com.solaiemes.serviceExample.utils:type=ServiceControl");
		ServiceControl mbean = new ServiceControl();
		mbean.setUsername(username);
		mbean.setBasicAuthenticationHeader(password);
		
		mbs.registerMBean(mbean, mbeanName);
		mbean.startService();
		// If this point is reached, the service is up and running, listening then forever to the stop signal 
        log.info("OK - Listening for the commands coming from the MBean...");

	}
	
}
