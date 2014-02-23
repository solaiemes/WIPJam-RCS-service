package com.solaiemes.serviceExample.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

public class HttpClientProvider {

	// Create async client to issue requests to the API
	public static AsyncHttpClient createAsyncClient() {
		AsyncHttpClientConfig.Builder builderSend = new AsyncHttpClientConfig.Builder();
		builderSend.setExecutorService(Executors.newCachedThreadPool());
		final ExecutorService reaperExecSendFT = Executors.newCachedThreadPool(); // Executors.newSingleThreadScheduledExecutor();
		builderSend.setExecutorService(reaperExecSendFT);
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient(builderSend.build());
		return asyncHttpClient;
	} 
	
}
