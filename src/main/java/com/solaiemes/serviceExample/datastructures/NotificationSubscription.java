package com.solaiemes.serviceExample.datastructures;

public class NotificationSubscription {
	
	private String notificationType;
	private String callbackData;
	private String notifyURL;
	private int duration;
	
	private String text;
	
	public String getNotificationType() {
		return notificationType;
	}
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}
	public String getCallbackData() {
		return callbackData;
	}
	public void setCallbackData(String callbackData) {
		this.callbackData = callbackData;
	}
	public String getNotifyURL() {
		return notifyURL;
	}
	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	
	public NotificationSubscription(String notificationType, String callbackData, String notifyURL,
			int duration) {
		super();
		this.notificationType = notificationType;
		this.callbackData = callbackData;
		this.notifyURL = notifyURL;
		this.duration = duration;
		this.text = "{\"" + notificationType + "\": {" + 
									"\"callbackReference\": { " +
									"\"callbackData\": \"" + callbackData + "\"," +
									"\"notifyURL\": \"" + notifyURL + "\"" +
									"}," + 
							"\"duration\": " + duration +
							"}" + 
							"}";
	}

	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
