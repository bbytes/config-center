package com.bbytes.ccenter.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

public class RestHMACAuthURLMatchingFilter extends
		AbstractAuthenticationProcessingFilter {

	
	
	protected RegexRequestMatcher urlRegex;

	private SimpleUrlAuthenticationSuccessHandler successHandler;

	public RestHMACAuthURLMatchingFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}

	public RestHMACAuthURLMatchingFilter(RegexRequestMatcher urlRegex) {
		super(urlRegex);
		this.urlRegex = urlRegex;
		successHandler = new SimpleUrlAuthenticationSuccessHandler();
		successHandler.setRedirectStrategy(new NoRedirectStrategy());
		setAuthenticationSuccessHandler(successHandler);
		setAllowSessionCreation(false);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest req,
			HttpServletResponse resp) throws AuthenticationException,
			IOException, ServletException {

		AuthenticationRequestWrapper request = new AuthenticationRequestWrapper(
				(HttpServletRequest) req);

		// Get authorization header
		String signature = request.getHeader(RestHMACAuthConstants.AUTH_HEADER);

		String principal = request.getHeader(RestHMACAuthConstants.API_KEY);
		
		String timestamp = request.getHeader(RestHMACAuthConstants.TIME_STAMP);

		// a rest credential is composed by request data to sign and the
		// signature
		RestHMACCredentials restCredential = new RestHMACCredentials(
				HMACUtils.calculateContentToSign(request,timestamp), signature);

		// Create an authentication token
		RestHMACAuthToken authentication = new RestHMACAuthToken(principal,
				restCredential);

		// Allow subclasses to set the "details" property
		setDetails(request, authentication);

//		return this.getAuthenticationManager().authenticate(authentication);
		
		//testing purpose return  without check 
		return authentication;

	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
		chain.doFilter(request, response);
	}

	/**
	 * Provided so that subclasses may configure what is put into the
	 * authentication request's details property.
	 * 
	 * @param request
	 *            that an authentication request is being created for
	 * @param authRequest
	 *            the authentication request object that should have its details
	 *            set
	 */
	protected void setDetails(HttpServletRequest request,
			AbstractAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource
				.buildDetails(request));
	}

	protected boolean requiresAuthentication(HttpServletRequest request,
			HttpServletResponse response) {
		 Authentication authentication
		  = SecurityContextHolder.getContext().getAuthentication();
		if (authentication!=null && authentication.isAuthenticated()) {
			return false;
		}

		return this.urlRegex.matches(request);
	}

}
