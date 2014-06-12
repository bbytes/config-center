package com.bbytes.ccenter.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import com.bbytes.ccenter.security.AjaxAuthenticationFailureHandler;
import com.bbytes.ccenter.security.AjaxAuthenticationSuccessHandler;
import com.bbytes.ccenter.security.AjaxLogoutSuccessHandler;
import com.bbytes.ccenter.security.AuthoritiesConstants;
import com.bbytes.ccenter.security.Http401UnauthorizedEntryPoint;
import com.bbytes.ccenter.web.security.RestHMACAuthProvider;
import com.bbytes.ccenter.web.security.RestHMACAuthURLMatchingFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Inject
	private Environment env;

	@Inject
	private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;

	@Inject
	private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;

	@Inject
	private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

	@Inject
	private Http401UnauthorizedEntryPoint authenticationEntryPoint;

	@Inject
	private UserDetailsService userDetailsService;

	@Inject
	private RememberMeServices rememberMeServices;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new StandardPasswordEncoder();
	}

	@Bean
	protected AuthenticationProvider getRestAuthProvider() throws Exception {
		return new RestHMACAuthProvider();
	}

	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
	protected AbstractAuthenticationProcessingFilter getRestHMACAuthFilter()
			throws Exception {
		RestHMACAuthURLMatchingFilter restFilter = new RestHMACAuthURLMatchingFilter(
                new RegexRequestMatcher("^/project.*", null));
		restFilter.setAuthenticationManager(authenticationManagerBean());
		return restFilter;
	}

	@Inject
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(
				passwordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/bower_components/**")
				.antMatchers("/fonts/**").antMatchers("/images/**")
				.antMatchers("/scripts/**").antMatchers("/styles/**")
				.antMatchers("/views/**").antMatchers("/swagger-ui/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authenticationProvider(getRestAuthProvider())
				.addFilterAfter(getRestHMACAuthFilter(),
						UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint).and()
				.rememberMe().rememberMeServices(rememberMeServices)
				.key(env.getProperty("jhipster.security.rememberme.key")).and()
				.formLogin().loginProcessingUrl("/app/authentication")
				.successHandler(ajaxAuthenticationSuccessHandler)
				.failureHandler(ajaxAuthenticationFailureHandler)
				.usernameParameter("j_username")
				.passwordParameter("j_password").permitAll().and().logout()
				.logoutUrl("/app/logout")
				.logoutSuccessHandler(ajaxLogoutSuccessHandler)
				.deleteCookies("JSESSIONID").permitAll().and().csrf().disable()
				.headers().frameOptions().disable().authorizeRequests()
				.antMatchers("/app/rest/authenticate").permitAll()
				.antMatchers("/app/rest/logs/**")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/app/**").authenticated()
				.antMatchers("/websocket/tracker")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/websocket/**").permitAll()
				.antMatchers("/metrics*")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/metrics/**")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/health*")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/health/**")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/trace*")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/trace/**")
				.hasAuthority(AuthoritiesConstants.ADMIN).antMatchers("/dump*")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/dump/**")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/shutdown*")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/shutdown/**")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/beans*")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/beans/**")
				.hasAuthority(AuthoritiesConstants.ADMIN).antMatchers("/info*")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/info/**")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/autoconfig*")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/autoconfig/**")
				.hasAuthority(AuthoritiesConstants.ADMIN).antMatchers("/env*")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/env/**")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/trace*")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/trace/**")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/api-docs/**")
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.antMatchers("/protected/**").authenticated();

	}

	@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
	private static class GlobalSecurityConfiguration extends
			GlobalMethodSecurityConfiguration {
	}
}