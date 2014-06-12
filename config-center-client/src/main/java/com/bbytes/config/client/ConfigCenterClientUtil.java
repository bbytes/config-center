
package com.bbytes.config.client;

import com.bbytes.config.spring.security.HMACUtils;
import com.bbytes.config.spring.security.RestHMACAuthConstants;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.Request;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class ConfigCenterClientUtil {

	

	public static BoundRequestBuilder buildRequest(AsyncHttpClient asyncHttpClient, String httpMethodType, String url) {
		switch (httpMethodType.toLowerCase()) {
		case "post":
			return asyncHttpClient.preparePost(url);
		case "get":
			return asyncHttpClient.prepareGet(url);
		case "put":
			return asyncHttpClient.preparePut(url);
		case "delete":
			return asyncHttpClient.prepareDelete(url);
		default:
			return asyncHttpClient.prepareGet(url);
		}
	}
	
	
	public static BoundRequestBuilder buildRequest(AsyncHttpClient asyncHttpClient, String httpMethodType, String url,
			String userName , String password) {
		BoundRequestBuilder requestBuilder = buildRequest(asyncHttpClient,httpMethodType,url);
		requestBuilder.addHeader(RestHMACAuthConstants.API_KEY, userName);
		Request request = requestBuilder.build();
		String timestamp = HMACUtils.getTimeStampUTC();
		requestBuilder.addHeader(RestHMACAuthConstants.TIME_STAMP, timestamp);
		requestBuilder.addHeader(RestHMACAuthConstants.AUTH_HEADER, HMACUtils.calculateSignature(password,timestamp,request));
		return requestBuilder;
	}

	
}