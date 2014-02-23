package com.solaiemes.serviceExample.utils;

public class HttpResources {
	
	private static String longPollingURL;
	private static String channelURL;
	private static String notifyURL;
	
	public static String getBaseHost() {
		return "https://rcs.oneapi-gw.gsma.com/";
	}
	
	public static String getNotifierBaseHost() {
		return "https://rcs.oneapi-gw.gsma.com/";
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
		return "notifications/0.1/" + username + "/channels/";
	}

	public static String getLongPollingURL() {
		return longPollingURL;
	}

	public static void setLongPollingURL(String notificationChannelURL) {
		HttpResources.longPollingURL = notificationChannelURL;
	}

	public static String getChannelURL() {
		return channelURL;
	}

	public static void setChannelURL(String channelURL) {
		HttpResources.channelURL = channelURL;
	}

	public static String getNotifyURL() {
		return notifyURL;
	}

	public static void setNotifyURL(String notifyURL) {
		HttpResources.notifyURL = notifyURL;
	}
	
	
	
}
