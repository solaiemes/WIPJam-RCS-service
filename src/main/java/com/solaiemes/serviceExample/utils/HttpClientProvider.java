package com.solaiemes.serviceExample.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

public class HttpClientProvider {

	// Create async client to issue requests to the API
	public static AsyncHttpClient createAsyncClient() {
		AsyncHttpClientConfig.Builder builderSend = new AsyncHttpClientConfig.Builder();
		builderSend.setExecutorService(Executors.newCachedThreadPool());
		final ScheduledExecutorService reaperExecSendFT = Executors.newScheduledThreadPool(5); // Executors.newSingleThreadScheduledExecutor();
		builderSend.setScheduledExecutorService(reaperExecSendFT);
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient(builderSend.build());
		return asyncHttpClient;
	} 
	
}
