package com.solaiemes.serviceExample.internetconnectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.solaiemes.serviceExample.ServiceExampleActions;
import com.solaiemes.serviceExample.utils.HttpClientProvider;
import com.solaiemes.serviceExample.utils.HttpErrorCodeEvaluator;

public class QuoteGetter {
	
	private static final Log log = LogFactory.getLog(QuoteGetter.class);

	private static final String QUOTE_URL = "http://www.thebookmarkshop.com/latin/latinrandom.htm";
	protected static final AsyncHttpClient quoteHTTPRequester = HttpClientProvider.createAsyncClient();

	public static void askToTheService(String username, String basicAuthenticationHeader, String destUri, String query) {

		final String theUsername = username;
		final String theBasicAuthenticationHeader = basicAuthenticationHeader;
		final String theDestUri = destUri;
		final String theQuery = query;
		
		try{				
			
			BoundRequestBuilder reqBsender = quoteHTTPRequester.prepareGet(QUOTE_URL);
								
			log.info("OK - Asking quote service: " + query);
			// -- Async response mamangement --
			reqBsender.execute(new AsyncCompletionHandler<Response>() {
				@Override
				public Response onCompleted(Response response) throws Exception {
					
					if(!HttpErrorCodeEvaluator.serviceFoundSomething(response.getStatusCode())) {
						log.error("OK - Quote service found nothing: " + response.getStatusCode());
						ServiceExampleActions.sendIMAutomaticSession(theUsername, theBasicAuthenticationHeader, theDestUri, "Quote service found nothing related to your query: " + theQuery);
					} else {
						log.info("OK - HTTP response from quote service: " + response.getStatusCode());
						String parsedResponse = new String(response.getResponseBody().getBytes(),"UTF-8");
						parsedResponse = parseQuote(parsedResponse, theQuery);
						ServiceExampleActions.sendIMAutomaticSession(theUsername, theBasicAuthenticationHeader, theDestUri, parsedResponse);
					} 
					return null;
				}
				@Override
				public void onThrowable(Throwable t) {
					log.error("FAIL - The web endpoint is not reachable at the moment...");
					ServiceExampleActions.sendIMAutomaticSession(theUsername, theBasicAuthenticationHeader, theDestUri, "Your request did not succeed, try again later...");
				}
				
				// Simply parse the obtained HTTP response
				private String parseQuote(String parsedResponse, String theQuery) throws IOException {
					int position = parsedResponse.indexOf("<SCRIPT>\r\n<!-- \r\n");
					String treatedString = parsedResponse.substring(position + "<SCRIPT>\r\n<!-- \r\n".length());
					BufferedReader reader = new BufferedReader(new StringReader(treatedString));
					String line;
					ArrayList<String> availableSayings = new ArrayList<String>();
					while (true) {
						line = reader.readLine();
						if (!line.startsWith("quote")) break;
						line = line.substring(line.indexOf("\"") + 1);
						line = line.replace("\"", "");
						line = line.replace("<br><br>", " ");
						availableSayings.add(line);
					}
					return availableSayings.get(new Random().nextInt(availableSayings.size())) + " by " + QUOTE_URL;
				}
				
			});				
		
		} catch(Exception e) {
			log.error("FAIL - Critical error while querying Quote service: username=" + username + ", destination uri=" + destUri + ", message=" + query + ", cause: " + e.getCause());
		}
	}
	
}
