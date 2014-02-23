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

	public static String identity = null;
	public static String basicAuthHeader = null;

	public static void main(String[] args) throws MalformedObjectNameException, NullPointerException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, InterruptedException {
		
		// Just your username is needed
		if(args.length != 3) {
			log.error("FAIL - Empty username, password or identity, you need to provide the username and password you were provided with on the GSMA website\n" + "NOTE: surround your password parameter with double quotes because it can contain characters that will be caught by the shell");
			return;
		} else {
			identity = args[2].replace("+", "%2b");
			String aux = args[0] + ":" + args[1];
			basicAuthHeader = new String("Basic " + new String(Base64.encodeBase64(aux.getBytes()))).trim();
			log.info("OK - Starting service as user " + identity);
		}
		
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName mbeanName = new ObjectName("com.solaiemes.serviceExample.utils:type=ServiceControl");
		ServiceControl mbean = new ServiceControl();
		mbean.setUsername(identity);
		mbean.setBasicAuthenticationHeader(basicAuthHeader);
		
		mbs.registerMBean(mbean, mbeanName);
		mbean.startService();
		// If this point is reached, the service is up and running, listening then forever to the stop signal 
        log.info("OK - Listening for the commands coming from the MBean...");

	}
	
}
