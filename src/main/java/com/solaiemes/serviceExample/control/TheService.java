package com.solaiemes.serviceExample.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ning.http.client.AsyncHttpClient;
import com.solaiemes.serviceExample.ServiceExample;
import com.solaiemes.serviceExample.ServiceExampleActions;
import com.solaiemes.serviceExample.utils.HttpClientProvider;

public class TheService {
	
	private static final Log log = LogFactory.getLog(TheService.class);
	
	// The service itself will run in an independent thread
	private Thread notificationRequester;
	private AsyncHttpClient theNotificationsHttpClient;
	
	
	private boolean setUpTheService(String username, String basicAuthenticationHeader) {
		
		// Create the notification channel and subscriptions (for register and chat related notifications, eg: file-transfer notifications are not relevant at the moment)
		if (ServiceExampleActions.createChannelAndSubscriptions(username, basicAuthenticationHeader)) {
			log.info("OK - Notification channel and subscriptions created correctly for user: " + username);
		} else {
			log.error("FAIL - Notification channel and subscriptions failed to be created for user: " + username);
			if(ServiceExampleActions.unregister(username, basicAuthenticationHeader)) {
				log.info("OK - Unregister ok as user " + username);
			} else {
				log.error("FAIL - Unregister service fail.");
				return false;
			}
		}
		
		// Register your service on the RCS core
		if(ServiceExampleActions.register(username, basicAuthenticationHeader)) {
			log.info("OK - Register ok as user " + username);
		} else {
			log.error("FAIL - Register fail...");
			return false;
		}
		
		theNotificationsHttpClient = HttpClientProvider.createAsyncClient();

		// This class serves both as launcher of the service and as service itself, that will run on a separate thread
		ServiceExample receiver = new ServiceExample(username, basicAuthenticationHeader, theNotificationsHttpClient);
		notificationRequester = new Thread(receiver);
		notificationRequester.start();
		
		return true;

	}
	
	public String shutdown(final String username, final String basicAuthenticationHeader) {
		
		log.info("OK - Shuting down the service.");
		
		// Now unregister the service		
		// Unregister your service on the RCS core
		if (ServiceExampleActions.unregister(username, basicAuthenticationHeader)) {
			log.info("OK - Unregister ok as user " + username);
			theNotificationsHttpClient.close();
			return "Gracefully closed...";			
		} else {
			log.error("FAIL - Unregister service fail.");
			theNotificationsHttpClient.close();
			return "Non gracefully closed...";
		}
		
	}

	
	public String start(final String username, final String basicAuthenticationHeader) {
		try {
			if (setUpTheService(username, basicAuthenticationHeader)) {
				log.info("OK - The service was re-launched...");
				return "Service already started";
			} else {
				log.error("FAIL - The service could not be re-launched...");
				return "Service could not start, you can retry...";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
	}
	

}
