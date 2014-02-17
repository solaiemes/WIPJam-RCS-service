package com.solaiemes.serviceExample;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.Response;
import com.solaiemes.serviceExample.datastructures.ChatMessage;
import com.solaiemes.serviceExample.datastructures.NotificationChannelSubscription;
import com.solaiemes.serviceExample.datastructures.NotificationSubscription;
import com.solaiemes.serviceExample.utils.HttpClientProvider;
import com.solaiemes.serviceExample.utils.HttpErrorCodeEvaluator;
import com.solaiemes.serviceExample.utils.HttpResources;
import com.solaiemes.serviceExample.utils.SimpleNotificationParser;

public class ServiceExampleActions {
	
	private static final Log log = LogFactory.getLog(ServiceExampleActions.class);
	
//	protected static String NOTIFICATION_LOCATION = "http://api.oneapi-gw.gsma.com/cometoma/rcsbox-notifieroma/NotificationsCometHandler?username=";
	protected static final AsyncHttpClient asynSenderclient = HttpClientProvider.createAsyncClient();	
	private static String notificationChannelURL;
	

	public static void sendIMAutomaticSession(String username, String basicAuthenticationHeader, String destUri, String message) {
		
		try {				
			
			BoundRequestBuilder reqBsender = asynSenderclient.preparePost(HttpResources.getBaseHost() + HttpResources.getChatResource(username, destUri))
											.addHeader("Authorization", basicAuthenticationHeader)
											.addHeader("Content-Type", "application/json; charset=utf-8");

			ChatMessage cm = new ChatMessage();
			cm.setText(message);
			log.info("OK - Send message request:\n" + cm.toString());
			reqBsender.setBody(cm.toString().getBytes("utf-8")).setBodyEncoding("utf-8");
			
			// -- Async response management, once the message is sent you can keep working, no need to wait for the response from the API
			reqBsender.execute(new AsyncCompletionHandler<Response>() {
				@Override
				public Response onCompleted(Response response) throws Exception {
					if(!HttpErrorCodeEvaluator.isChatMessageSent(response.getStatusCode())) {
						log.error("FAIL -	Could not send message: " + response.getStatusCode());
					}else{
						log.info("OK -	Message sent: " + response.getStatusCode());
					}
					return null;
				}
				@Override
				public void onThrowable(Throwable t) {
					log.error("FAIL - ERROR: run - onThrowable: " +  t.getCause());
				}
			});				
		
		}catch(Exception e) {
			log.error("FAIL - SENDIMMESSAGE ERROR: username=" + username + ", destination uri=" + destUri + ", message=" + message + ", cause: " + e.getCause());
		}
	}
	

	public static boolean register(String username, String basicAuthenticationHeader) {
		try{				
			BoundRequestBuilder reqBregister = asynSenderclient.preparePost(HttpResources.getBaseHost() + HttpResources.getRegisterResource(username))
															   .addHeader("Authorization", basicAuthenticationHeader);
			String registerNotification;
			// -- Sync response management, no other thing can be done before this step is completed
			Response response = reqBregister.execute().get();
			if (!HttpErrorCodeEvaluator.isCorrectRegistrationOperation(response.getStatusCode())) {
				log.error("FAIL - Could not register " + response.getStatusCode());
				return false;
			} else {
				registerNotification = requestRegisterNotification(username, basicAuthenticationHeader);
				if (registerNotification != null) {
					if(SimpleNotificationParser.extractRegistrationResult(registerNotification)) {
						log.info("OK - Registration requested successfully: " + response.getStatusCode());
						return true;
					}
				}
			}
		}catch(Exception e) {
			log.error("FAIL - REGISTER ERROR: username=" + username + ", cause: " + e.getCause());
		}
		return false;
	}
	
	
	private static String requestRegisterNotification(String username, String basicAuthenticationHeader) {
		
		BoundRequestBuilder reqBregister = asynSenderclient.preparePost(HttpResources.getNotifierBaseHost() + HttpResources.getNotificationResource(username))
														   .addHeader("Authorization", basicAuthenticationHeader);
		// -- Sync response management, no other thing can be done before this step is completed
		try {
			
			Response response = reqBregister.execute().get();
			if (response.getStatusCode() != 200) {
				log.error("FAIL - Bad response to the notification request: " + response.getStatusCode() + " checking if it already came...");
			} else {
				return response.getResponseBody();
			}
			
		} catch (Exception e) {
			log.error("FAIL - Could not request the registration notification...");
			e.printStackTrace();
		}
		return null;
	}


	public static boolean unregister(String username, String basicAuthenticationHeader) {
		
		try{				
			BoundRequestBuilder reqBunregister = asynSenderclient.prepareDelete(HttpResources.getBaseHost() + HttpResources.getRegisterResource(username))
																 .addHeader("Authorization", basicAuthenticationHeader);
			// -- Sync response management, no other thing can be done before this step is completed
			Response response = reqBunregister.execute().get();
			if (!HttpErrorCodeEvaluator.isCorrectRegistrationOperation(response.getStatusCode())) {
				log.error("FAIL - Could not register " + response.getStatusCode());
				return false;
			} else {
//				unregisterNotification = requestRegisterNotification(username, basicAuthenticationHeader);
				if (ServiceExample.isUnregisterNotificationArrived()) { // You got the registration notification
					log.info("OK - Unregistered successfully: " + response.getStatusCode());
					return true;
				} else { // The only possibility is that it was caught on the very last notification request
					log.info("OK - Waiting for the unregister notification to come...");
					Thread.sleep(21000);
					return ServiceExample.isUnregisterNotificationArrived();
				}
			}
		} catch(Exception e) {
			log.error("FAIL - UNREGISTER ERROR: username=" + username + ", cause: " + e.getCause());
		}
		return false;
	}
	
	
	public static boolean createChannelAndSubscriptions(String username, String basicAuthenticationHeader) {
		
		try{			
			
			// Channel creation
			BoundRequestBuilder reqSubscribe = asynSenderclient.preparePost(HttpResources.getBaseHost() + HttpResources.getNotificationChannelResource(username))
															   .addHeader("Authorization", basicAuthenticationHeader);
			
			NotificationChannelSubscription ncs = new NotificationChannelSubscription("12345","myService", 50, "LongPolling", 3600);
			
			reqSubscribe.setBody(ncs.getText());
			
			// -- Sync response management, no other thing can be done before this step is completed
			Response response = reqSubscribe.execute().get();
			notificationChannelURL = getChannelURL(response.getResponseBody());
			if ((!HttpErrorCodeEvaluator.isChannelCorrectlyCreated(response.getStatusCode())) || (notificationChannelURL == null)) {
				log.error("FAIL - Channel not created " + response.getStatusCode());
				return false;
			} else {
				log.info("OK - Channel correctly created, HTTP " + response.getStatusCode());
			}
			
			
			NotificationSubscription ns = new NotificationSubscription("sessionSubscription", "sessionSubscription", notificationChannelURL, 3600);
			
			
			// Subscription creation
			// -- Sync response management, no other thing can be done before this step is completed
			
			// Session subscription
			reqSubscribe = asynSenderclient.preparePost(HttpResources.getBaseHost() + HttpResources.getSubscriptionsResource(username, "register"))
										   .addHeader("Authorization", basicAuthenticationHeader);
			reqSubscribe.setBody(ns.getText());
			response = reqSubscribe.execute().get();
			if(!HttpErrorCodeEvaluator.isSessionSubscribeCorrect(response.getStatusCode())) {
				log.error("FAIL - Session notification subscription could not be created: " + response.getStatusCode());
				return false;
			}else{
				log.info("OK - Session notification subscription created: " + response.getStatusCode());
			}
			
			// Chat subscription
			ns = new NotificationSubscription("chatNotificationSubscription", "chatSubscription1", notificationChannelURL, 3600);
			reqSubscribe = asynSenderclient.preparePost(HttpResources.getBaseHost() + HttpResources.getSubscriptionsResource(username, "chat"))
										   .addHeader("Authorization", basicAuthenticationHeader);
			reqSubscribe.setBody(ns.getText());
			response = reqSubscribe.execute().get();
			if(!HttpErrorCodeEvaluator.isChatSubscribeCorrect(response.getStatusCode())) {
				log.error("FAIL - Chat notification subscription could not be created: " + response.getStatusCode());
				return false;
			} else {
				log.info("OK - Chat notification subscription created: " + response.getStatusCode());
			}
			
		} catch(Exception e) {
			log.error("FAIL - NOTIFICATION CHANNEL ERROR: username=" + username + ", cause: " + e.getCause());
			return false;
		}
		return true;
	}
	
	
	public static void acceptInvite(String theURL, String username, String basicAuthenticationHeader, String remoteParty, String text) {
		
		BoundRequestBuilder chatAcceptance = asynSenderclient.preparePut(theURL)
															 .addHeader("Authorization", basicAuthenticationHeader);
		chatAcceptance.setBody(getAcceptanceBody());
		
		// -- Async response management, once the message is sent you can keep working, no need to wait for the response from the API
		try {
			chatAcceptance.execute(new AsyncCompletionHandler<Response>() {
				@Override
				public Response onCompleted(Response response) throws Exception {
					
					if(!HttpErrorCodeEvaluator.isChatAccepted(response.getStatusCode())) {
						log.error("FAIL - Chat invitation could not be accepted: " + response.getStatusCode());
					} else {
						log.info("OK - Chat invitation accepted: " + response.getStatusCode());
//						WikipediaGetter.askWikipedia(theUsername, theRemoteParty, theText);
					}
					return null;
				}
				@Override
				public void onThrowable(Throwable t) {
					log.error("FAIL - ERROR: run - onThrowable: " +  t.getCause());
				}
			});
		} catch (IOException e) {
			log.error("FAIL - Could not accept the session corresponding to: " + theURL);
		}				
		
	}
	
	
	private static byte[] getAcceptanceBody() {
		return "{\"participantSessionStatus\": {\"status\": \"Connected\"}}".getBytes();
	}


	private static String getChannelURL(String responseBody) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory f = mapper.getJsonFactory();
			JsonParser jp = f.createJsonParser(responseBody);
			JsonToken current = jp.nextToken(); 

			if (current != JsonToken.START_OBJECT) {
				throw new IllegalStateException("FAIL - Error: root should be an object: quiting.");
			}
			
			JsonNode node = mapper.readTree(jp);
			if (node.has("notificationChannel")) {
				node = node.get("notificationChannel");
				if (node.has("channelData")) {
					node = node.get("channelData");
					if (node.has("channelURL")) {
						return node.get("channelURL").getTextValue();
					}
					
				}
			}

			jp.close(); 
		} catch (IOException e) {
			throw new IllegalStateException("FAIL - Error parsing the body, is a JSON?");
		} 
		
		return null;
	}
	
}
