package com.solaiemes.serviceExample.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class LogLayout extends PatternLayout {

	 private static final String SYMBOL_TO_REPLACE_WITH = "+";
	 private static final Pattern SYMBOL_TO_LOOK_FOR = Pattern.compile("%2b");        
	    
	 @Override
	 public String format(LoggingEvent event) {
	   if (event.getMessage() instanceof String) {
	      String message = event.getRenderedMessage();

	      Matcher matcher = SYMBOL_TO_LOOK_FOR.matcher(message);
	           
	      if (matcher.find()) {
	         String maskedMessage = matcher.replaceAll(SYMBOL_TO_REPLACE_WITH);

	         Throwable throwable = 
	             event.getThrowableInformation() != null ?
	             event.getThrowableInformation().getThrowable() : null;
	                
	         LoggingEvent maskedEvent = new LoggingEvent(
	                 event.fqnOfCategoryClass,
	                 Logger.getLogger(event.getLoggerName()), 
	                 event.timeStamp, 
	                 event.getLevel(), 
	                 maskedMessage, 
	                 throwable);
	                
	         return super.format(maskedEvent);
	      } 
	   }

	   return super.format(event);

	 }
	
}
