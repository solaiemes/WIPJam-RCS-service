package com.solaiemes.serviceExample.utils;

public class HttpErrorCodeEvaluator {
	
	public static boolean isCorrectRegistrationOperation(int statusCode) {
		return "204".equals(String.valueOf(statusCode));
	}
	
	public static boolean isCorrectUnregistrationOperation(int statusCode) {
		return "204".equals(String.valueOf(statusCode));
	}
	
	public static boolean isChatAccepted(int statusCode) {
		return "204".equals(String.valueOf(statusCode));
	}
	
	public static boolean isChatMessageSent(int statusCode) {
		return "201".equals(String.valueOf(statusCode));
	}

	public static boolean isChannelCorrectlyCreated(int statusCode) {
		return "201".equals(String.valueOf(statusCode));
	}
	
	public static boolean isSessionSubscribeCorrect(int statusCode) {
		return "201".equals(String.valueOf(statusCode));
	}
	
	public static boolean isChatSubscribeCorrect(int statusCode) {
		return "201".equals(String.valueOf(statusCode));
	}
	
	public static boolean serviceFoundSomething(int statusCode) {
		return String.valueOf(statusCode).contains("200");
	}
	
	

}
