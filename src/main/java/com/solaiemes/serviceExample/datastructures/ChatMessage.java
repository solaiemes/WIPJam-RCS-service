package com.solaiemes.serviceExample.datastructures;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import com.solaiemes.serviceExample.datastructures.Enums.MessageStatus;


public class ChatMessage extends GenericType {
	
	private String text;
	private ArrayList<MessageStatus> reportRequest;
	private String resourceURL;
	
	public ChatMessage() {
		super();
		reportRequest = new ArrayList<MessageStatus>();
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ArrayList<MessageStatus> getReportRequest() {
		return reportRequest;
	}

	public void addReportRequest(MessageStatus reportRequest) {
		this.reportRequest.add(reportRequest);
	}

	public String getResourceURL() {
		return resourceURL;
	}

	public void setResourceURL(String resourceURL) {
		this.resourceURL = resourceURL;
	}

	@Override
	public String toString() {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonFactory f = new JsonFactory();
		
		try {
			
			JsonGenerator jg = f.createJsonGenerator(baos);
			jg.writeStartObject();
			jg.writeObjectFieldStart("chatMessage");
			jg.writeStringField("reportRequest", "Displayed");
			jg.writeStringField("text", text);
			jg.writeEndObject();
			jg.flush();
			jg.close();
			return baos.toString("utf-8");
			
		} catch (IOException e) {
			
			// Default behavior on error 
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("{\"chatMessage\": { \"reportRequest\":\"Displayed\", \"text\":\"Could not build your response\"}}");
			return stringBuilder.toString();
			
		}
	}
	
}

