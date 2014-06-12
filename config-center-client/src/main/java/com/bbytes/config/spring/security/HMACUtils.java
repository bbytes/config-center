package com.bbytes.config.spring.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.ning.http.client.Request;
import com.ning.http.util.Base64;

public class HMACUtils {

	// Enable Multi-Read for PUT and POST requests
	private static final Set<String> METHOD_HAS_CONTENT = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER) {
		private static final long serialVersionUID = 1L;
		{
			add("PUT");
			add("POST");
		}
	};

	public static String calculateSignature(String secret, String timestamp, Request request) {
		try {
			return calculateHMAC(secret, calculateContentToSign(request, timestamp));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return secret;
	}

	public static String calculateHMAC(String secret, String data) {
		try {
			if (secret == null || data == null)
				return "";

			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(data.getBytes());
			String result = new String(Base64.encode(rawHmac));
			return result;
		} catch (GeneralSecurityException e) {
			throw new IllegalArgumentException();
		}
	}

	public static String calculateContentToSign(Request request, String timestamp) throws NoSuchAlgorithmException {

		MessageDigest md5 = MessageDigest.getInstance("MD5");

		// get md5 content and content-type if the request is POST or PUT method
		boolean hasContent = METHOD_HAS_CONTENT.contains(request.getMethod());
		String contentMd5 = hasContent ? md5.digest(request.getByteData()).toString() : "";

		// calculate content to sign
		StringBuilder toSign = new StringBuilder();
		toSign.append(request.getMethod()).append("\n").append(contentMd5).append("\n").append(timestamp).append("\n")
				.append(request.getUrl());

		return toSign.toString();
	}

	public static String getTimeStampUTC() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		return f.format(new Date());
	}

}
