package com.solaiemes.serviceExample.datastructures;

public class SimpleMessageNotification {
	
	private boolean invitingMessage;
	private String text;
	private String remoteParty;
	private String URLForAcceptingSession;
	
	public SimpleMessageNotification(String message, String remoteParty, boolean invitingMessage, String URLForAcceptingSession) {
		this.text = message;
		this.remoteParty =  remoteParty;
		this.invitingMessage = invitingMessage;
		this.URLForAcceptingSession = URLForAcceptingSession;
	}

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	public String getRemoteParty() {
		return remoteParty;
	}
	public void setRemoteParty(String remoteParty) {
		this.remoteParty = remoteParty;
	}

	public boolean isInvitingMessage() {
		return invitingMessage;
	}

	public void setInvitingMessage(boolean invitingMessage) {
		this.invitingMessage = invitingMessage;
	}

	public String getURLForAcceptingSession() {
		return URLForAcceptingSession;
	}

	public void setURLForAcceptingSession(String uRLForAcceptingSession) {
		URLForAcceptingSession = uRLForAcceptingSession;
	}
	
}
