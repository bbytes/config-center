/**
 * 
 */
package com.bbytes.ccenter.web.security;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.RedirectStrategy;

import java.io.IOException;

public class NoRedirectStrategy implements RedirectStrategy{

    
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        // Forget about redirecting, there is no need!
    }
}