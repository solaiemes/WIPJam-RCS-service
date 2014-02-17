package com.solaiemes.serviceExample.datastructures;

public class Enums {
	
	public enum FileDisposition{
		RENDER("Render"),
		ATTACHMENT("Attachment");
		
		private final String value;
		private FileDisposition(String v) {
	        value = v;
		}
		public String value() {
			return value;
		}
	    public static FileDisposition fromValue(String v) {
	        for (FileDisposition c: FileDisposition.values()) {
	            if (c.value.equalsIgnoreCase(v)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(v);
	    }
	}
	
	public enum ReceiverStatus{
		INVITED("Invited"),
		CONNECTED("Connected"),
		DISCONNECTED("Disconnected");
		
		
		private final String value;
		private ReceiverStatus(String v) {
	        value = v;
		}
		public String value() {
			return value;
		}
	    public static ReceiverStatus fromValue(String v) {
	        for (ReceiverStatus c: ReceiverStatus.values()) {
	            if (c.value.equalsIgnoreCase(v)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(v);
	    }
	}
	
	public enum ParticipantStatus{
		INVITED("Invited"),
		CONNECTED("Connected"),
		DISCONNECTED("Disconnected");
		
		
		private final String value;
		private ParticipantStatus(String v) {
	        value = v;
		}
		public String value() {
			return value;
		}
	    public static ParticipantStatus fromValue(String v) {
	        for (ParticipantStatus c: ParticipantStatus.values()) {
	            if (c.value.equalsIgnoreCase(v)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(v);
	    }
	}
	
	public enum MessageStatus{
		SENT("Sent"),
		DELIVERED("Delivered"),
		DISPLAYED("Displayed"),
		FAILED("Failed");
		
		
		private final String value;
		private MessageStatus(String v) {
	        value = v;
		}
		public String value() {
			return value;
		}
	    public static MessageStatus fromValue(String v) {
	        for (MessageStatus c: MessageStatus.values()) {
	            if (c.value.equalsIgnoreCase(v)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(v);
	    }
	}
	
	public enum SessionStatus{
		INITIAL("Initial"),
		CONNECTED("Connected"),
		TERMINATED("Terminated");
		
		private final String value;
		private SessionStatus(String v) {
	        value = v;
		}
		public String value() {
			return value;
		}
	    public static SessionStatus fromValue(String v) {
	        for (SessionStatus c: SessionStatus.values()) {
	            if (c.value.equalsIgnoreCase(v)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(v);
	    }
	}
	
	public enum BWType{
		CONFERENCE_TOTAL("CT"),
		APPLICATION_SPECIFIC("AS"),
		TERMINATED("Terminated");
		
		private final String value;
		private BWType(String v) {
	        value = v;
		}
		public String value() {
			return value;
		}
	    public static BWType fromValue(String v) {
	        for (BWType c: BWType.values()) {
	            if (c.value.equalsIgnoreCase(v)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(v);
	    }
	}
	
	public enum MediaType{
		VIDEO("Video"),
		AUDIO("Audio");
		
		private final String value;
		private MediaType(String v) {
	        value = v;
		}
		public String value() {
			return value;
		}
	    public static MediaType fromValue(String v) {
	        for (MediaType c: MediaType.values()) {
	            if (c.value.equalsIgnoreCase(v)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException(v);
	    }
	}
}
