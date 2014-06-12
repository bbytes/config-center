package com.bbytes.ccenter.web.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.TreeSet;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Base64;

public class HMACUtils {

  // Enable Multi-Read for PUT and POST requests
  private static final Set<String> METHOD_HAS_CONTENT = new TreeSet<String>(
      String.CASE_INSENSITIVE_ORDER) {
    private static final long serialVersionUID = 1L;
    {
      add("PUT");
      add("POST");
    }
  };

  public static String calculateSignature(String secret, String timestamp,
      AuthenticationRequestWrapper request) {
    return calculateHMAC(secret, calculateContentToSign(request, timestamp));
  }


  public static String calculateHMAC(String secret, String data) {
    try {
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

  public static String calculateContentToSign(AuthenticationRequestWrapper request, String timestamp) {

    MessageDigest md5 = null;
    String contentMd5 = "";

    try {
      md5 = MessageDigest.getInstance("MD5");

      // get md5 content and content-type if the request is POST or PUT method
      boolean hasContent = METHOD_HAS_CONTENT.contains(request.getMethod());
      contentMd5 = hasContent ? md5.digest(request.getPayload().getBytes()).toString() : "";
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    // calculate content to sign
    StringBuilder toSign = new StringBuilder();
    toSign.append(request.getMethod()).append("\n").append(contentMd5).append("\n")
        .append(timestamp).append("\n").append(request.getRequestURL());

    return toSign.toString();
  }


}
