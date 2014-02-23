package com.solaiemes.serviceExample;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.Response;
import com.solaiemes.serviceExample.datastructures.SimpleMessageNotification;
import com.solaiemes.serviceExample.internetconnectors.QuoteGetter;
import com.solaiemes.serviceExample.utils.HttpResources;
import com.solaiemes.serviceExample.utils.SimpleNotificationParser;

public class ServiceExample implements Runnable {
	
	private static final Log log = LogFactory.getLog(ServiceExample.class);
	
	private BoundRequestBuilder notificationRequester;
	private String username;
	private String basicAuthenticationHeader;
	protected static final long TIME_BETWEEN_REQUESTS = 1000;
	private final AsyncHttpClient asynchttpclient;
	private static AtomicBoolean unregisterNotificationArrived = new AtomicBoolean(false);


	public ServiceExample(String username, String basicAuthenticationHeader, AsyncHttpClient asynchttpclient) {
		this.username = username;
		this.asynchttpclient = asynchttpclient;
		this.basicAuthenticationHeader = basicAuthenticationHeader;
		unregisterNotificationArrived.set(false);
	}

	public void run() {
		
		notificationRequester = this.asynchttpclient.preparePost(HttpResources.getLongPollingURL())
													.addHeader("Authorization", basicAuthenticationHeader)
													.addHeader("Content-Type", "application/json")
													.addHeader("Accept", "application/json")
													.addHeader("Accept-Language", "en-US,en;q=0.5")
													.addHeader("DNT", "1")
													.addHeader("Connection", "keep-alive")
													.addHeader("Pragma", "no-cache")
													.addHeader("Cache-Control", "no-cache");
		
		try {
			
			notificationRequester.execute(new AsyncCompletionHandler<Response>() {
				
				@Override
				public Response onCompleted(Response response) throws Exception {
					
					final String jsonResponse = new String(response.getResponseBodyAsBytes(), "UTF-8");
					//Is not an empty response?
					if (jsonResponse.length() == 0) {
						notificationRequester.execute(this); //Continue receiving
						return null;
					}
					
					// Is a json response?
					if (response.getContentType() == null || !response.getContentType().contains("application/json")) {
						log.error("FAIL - ERROR: non-json response received: "  +  response.getResponseBody());
						Thread.sleep(TIME_BETWEEN_REQUESTS);
						notificationRequester.execute(this);
						return null;
					}

					// Getting the notification
					try {
						SimpleMessageNotification[] messageMotifications = SimpleNotificationParser.parse(jsonResponse);
						for(int i = 0; i < messageMotifications.length; i++) {
							// If it is a brand new chat, first accepting
							if (messageMotifications[i].isInvitingMessage()) {
								log.info("OK - ============= STARTING CHAT SESSION WITH " + username + " ===================");
								ServiceExampleActions.acceptInvite(messageMotifications[i].getURLForAcceptingSession(), username, basicAuthenticationHeader, messageMotifications[i].getRemoteParty(), messageMotifications[i].getText());
							} else {
								// If it is an established chat session then inmediate responding
								QuoteGetter.askToTheService(username, basicAuthenticationHeader, messageMotifications[i].getRemoteParty(), messageMotifications[i].getText());
							}
							log.info("OK - Notification received: "  +  messageMotifications[i].getText());
						}
					} catch(Exception e) {
						log.info("FAIL - Exception parsing received notifications.");
						e.printStackTrace();
					}
					notificationRequester.execute(this);
					return null;
				}

				@Override
				public void onThrowable(Throwable t) {
					
					if (!(t instanceof TimeoutException)) {
						t.printStackTrace();
					} else {
						log.info("TIMEOUT - No notification arrived so far, re-requesting...");
					}
					// On errors continue receiving notifications
					try {
						notificationRequester.execute(this);
					} catch (Exception e) {
						log.error("FAIL - Could not issue the HTTP request...");
						log.error("FAIL - Unregistering service...");
						ServiceExampleActions.unregister(username, basicAuthenticationHeader);
						e.printStackTrace();
					} 
				}		
			});
			
		} catch (IOException e) {
			log.error("FAIL - ERROR: ServiceExample: " + e.getCause());
			e.printStackTrace();
		}
		return;
	}

	public static boolean isUnregisterNotificationArrived() {
		return unregisterNotificationArrived.get();
	}

	public static void setUnregisterNotificationArrived(boolean unregisterNotificationArrived) {
		ServiceExample.unregisterNotificationArrived.set(unregisterNotificationArrived);
	}
	

}
