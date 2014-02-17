package com.solaiemes.serviceExample.utils;

public class HttpResources {
	
	public static String getBaseHost() {
		return "http://api.oneapi-gw.gsma.com/";
	}
	
	public static String getNotifierBaseHost() {
		return "http://api.oneapi-gw.gsma.com/";
	}

	public static String getSubscriptionsResource(String username, String type) {
		return type + "/0.1/" + username + "/subscriptions";
	}


	public static String getNotificationChannelResource(String username) {
		return "notificationchannel/0.1/" + username + "/channels";
	}


	public static String getRegisterResource(String username) {
		return "register/0.1/" + username + "/sessions";
	}
	
	
	public static String getChatResource(String username, String destUri) {
		return "chat/0.1/" + username + "/oneToOne/" + destUri + "/adhoc/messages";
	}
	
	public static String getNotificationResource(String username) {
		return "cometoma/rcsbox-notifieroma/NotificationsCometHandler?username=" + username;
//		return "rcsbox-notifieroma/NotificationsCometHandler?username=" + username;
//		return "rcsbox-notifieroma/NotificationsCometHandler?username=" + username;
	}
	
}
