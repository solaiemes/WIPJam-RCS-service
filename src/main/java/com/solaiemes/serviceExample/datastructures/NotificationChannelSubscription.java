package com.solaiemes.serviceExample.datastructures;

public class NotificationChannelSubscription {
	
	private String clientCorrelator;
	private String applicationTag;
	private int maxNotifications;
	private String type;
	private int channelLifetime;
	private String text;
	
	public String getClientCorrelator() {
		return clientCorrelator;
	}
	
	
	public void setClientCorrelator(String clientCorrelator) {
		this.clientCorrelator = clientCorrelator;
	}
	
	public String getApplicationTag() {
		return applicationTag;
	}
	
	public void setApplicationTag(String applicationTag) {
		this.applicationTag = applicationTag;
	}
	
	public int getMaxNotifications() {
		return maxNotifications;
	}
	
	public void setMaxNotifications(int maxNotifications) {
		this.maxNotifications = maxNotifications;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public int getChannelLifetime() {
		return channelLifetime;
	}
	
	public void setChannelLifetime(int channelLifetime) {
		this.channelLifetime = channelLifetime;
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


	public NotificationChannelSubscription(String clientCorrelator,
			String applicationTag, int maxNotifications,
			String type, int channelLifetime) {
		super();
		this.clientCorrelator = clientCorrelator;
		this.applicationTag = applicationTag;
		this.maxNotifications = maxNotifications;
		this.type = type;
		this.channelLifetime = channelLifetime;
		this.text = formatText();
	}

	public String formatText() {
		return "{\"notificationChannel\": {" + 
					"\"channelData\": { " +
										"\"maxNotifications\": " + this.maxNotifications +
										"}," + 
					"\"channelLifetime\": " + this.channelLifetime + "," +
					"\"channelType\": \"" + this.type + "\"" +
					"}" + 
			"}";
										
	}
	
}
