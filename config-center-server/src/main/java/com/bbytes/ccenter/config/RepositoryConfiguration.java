package com.bbytes.ccenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bbytes.ccenter.repository.CCPropertyEventListener;
import com.bbytes.ccenter.repository.UserEventListener;

@Configuration
public class RepositoryConfiguration {

	@Bean
	public UserEventListener getUserRepoEventListener() {
		return new UserEventListener();
	}

	
	@Bean
    public CCPropertyEventListener getCCPropertyEventListener() {
        return new CCPropertyEventListener();
    }
	

}
