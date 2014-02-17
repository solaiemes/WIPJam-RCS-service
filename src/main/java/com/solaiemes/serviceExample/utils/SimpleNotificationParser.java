package com.solaiemes.serviceExample.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;

import com.solaiemes.serviceExample.ServiceExample;
import com.solaiemes.serviceExample.datastructures.SimpleMessageNotification;

public class SimpleNotificationParser {
	
	private static final Log log = LogFactory.getLog(SimpleNotificationParser.class);

	public static SimpleMessageNotification[] parse(String json) {
		SimpleMessageNotification notifications[];
		Vector<SimpleMessageNotification> notifs = new Vector<SimpleMessageNotification>();

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory f = mapper.getJsonFactory();
			JsonParser jp = f.createJsonParser(json);
			JsonToken current = jp.nextToken(); 

			if (current != JsonToken.START_OBJECT) {
				throw new IllegalStateException("FAIL - Error: root should be an object: quiting.");
			}

			while (jp.nextToken() != JsonToken.END_OBJECT) {

				String fieldname = jp.getCurrentName();
				current = jp.nextToken(); 
										
				if ("notificationList".equalsIgnoreCase(fieldname)) {
					if (current == JsonToken.START_ARRAY) {
						
						JsonNode node = null;
						// Read all the notifications and return those that are chat messages 
						while (jp.nextToken() != JsonToken.END_ARRAY) {
							try {
								
								node = mapper.readTree(jp);
								// There are two types of chat messages, need to distinguish
								if (node.has("messageNotification")) {
									// Established chat (MSRP) message
									node = node.get("messageNotification");
									String message = node.get("chatMessage").getFields().next().getValue().getTextValue();
									String remoteParty = node.get("senderAddress").getTextValue();
									log.info("OK - Received message: "	+ message + " from " + remoteParty);
									SimpleMessageNotification newMsgNot = new SimpleMessageNotification(message, remoteParty, false, null);
									
									notifs.add(newMsgNot);
									
								} else if (node.has("chatSessionInvitationNotification")) {
									// Coming on the (SIP) message
									JsonNode theMessageNotification = node.get("chatSessionInvitationNotification");
									String remoteParty = theMessageNotification.get("originatorAddress").getTextValue();
									String subject = theMessageNotification.get("subject").getTextValue();
									log.info("OK - Received message: "	+ subject + " from " + remoteParty);
									SimpleMessageNotification newMsgNot = new SimpleMessageNotification(subject, remoteParty, true, getURLForAccepting(theMessageNotification));
									notifs.add(newMsgNot);
								} else if (node.has("sessionEventNotification")) {
									JsonNode sessionEventNotification = node.get("sessionEventNotification");
									String theSessionEvent = sessionEventNotification.get("event").getTextValue();
									if ("unregisterSuccess".equals(theSessionEvent)) {
										ServiceExample.setUnregisterNotificationArrived(true);
									} else { // It should be just register on this point now
										ServiceExample.setUnregisterNotificationArrived(false);
									}
								}
							} catch (Exception e) {
								if (node != null)
									log.error("FAIL - Error processing the notification: " + node.toString());
								e.printStackTrace();
							}
						}
					} else {
						log.info("OK - No notifications for your service right now...");
						jp.skipChildren();
					}
				} 
			}
			jp.close(); 
		} catch (IOException e) {
			throw new IllegalStateException("FAIL - Error parsing the body, is a JSON?");
		} 
		notifications = new SimpleMessageNotification[notifs.size()];
		return notifs.toArray(notifications);

	}
	
	
	private static String getURLForAccepting(JsonNode theNotification) {
		JsonNode currentNode;
		JsonNode theLink = theNotification.get("link");
		Iterator<JsonNode> theElements = theLink.getElements();
		while (theElements.hasNext()) {
			currentNode = theElements.next();
			if ("ParticipantSessionStatus".equals(currentNode.get("rel").getTextValue())) {
				return currentNode.get("href").getTextValue();
			}
		}
		log.info("WARN - Could not accept explicitly the session, going on anyway, but this might cause the response not to reach the other party");
		return null;
	}
	
	
	public static boolean extractRegistrationResult(String theNotificationText) {
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonFactory f = mapper.getJsonFactory();
			JsonParser jp = f.createJsonParser(theNotificationText);
			JsonToken current = jp.nextToken(); 
			String registerResult;

			if (current != JsonToken.START_OBJECT)
				return false;

			while (jp.nextToken() != JsonToken.END_OBJECT) {

				String fieldname = jp.getCurrentName();
				current = jp.nextToken(); 
										
				if ("notificationList".equalsIgnoreCase(fieldname)) {
					if (current == JsonToken.START_ARRAY) {
						
						JsonNode node = null;
						// Read all the notifications until you get the registration one, then check success or failure
						while (jp.nextToken() != JsonToken.END_ARRAY) {
							try {
								node = mapper.readTree(jp);
								if (node.has("sessionEventNotification")) {
									// A register notification, success or failure is returned
									node = node.get("sessionEventNotification");
									registerResult = node.get("event").getTextValue();
									return "registerSuccess".equals(registerResult) || "unregisterSuccess".equals(registerResult);
								} 
							} catch (Exception e) {
								if (node != null)
									log.error("FAIL - Error processing the notification: " + node.toString());
								e.printStackTrace();
							}
						}
					} else {
						log.info("OK - No notifications for your service right now...");
						jp.skipChildren();
					}
				} 
			}
			jp.close();
		} catch (Exception e) {
			log.error("FAIL - Could not parse the register notification...");
			e.printStackTrace();
		} 
		return false;
	}
	
}
