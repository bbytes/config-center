package com.bbytes.config.spring;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.BaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.ObjectUtils;

import com.bbytes.config.client.CloudConfigurationSource;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.FixedDelayPollingScheduler;

/**
 * Bean that should be used instead of the {@link PropertyPlaceholderConfigurer}
 * if you want to have access to the resolved properties from Spring context.
 * 
 */
public class ConfigCenterPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

	final static Logger logger = LoggerFactory.getLogger(ConfigCenterPropertyPlaceholderConfigurer.class);

	private String host;

	private String port = "9000"; // default port

	private String project;

	private String environment;

	private String clientId;

	private String secretKey;

	private int pollDelay = 10; // 10 secs

	private Properties properties;

	private Map<String, String> resolvedProps;


	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(String project) {
		this.project = project;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * @return the pollDelay
	 */
	public int getPollDelay() {
		return pollDelay;
	}

	/**
	 * @param pollDelay
	 *            the pollDelay to set
	 */
	public void setPollDelay(int pollDelay) {
		this.pollDelay = pollDelay;
	}

	

	/**
	 * {@linkplain #mergeProperties Merge}, {@linkplain #convertProperties convert} and
	 * {@linkplain #processProperties process} properties against the given bean factory.
	 * @throws BeanInitializationException if any properties cannot be loaded
	 */
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		try {
			Properties mergedProps = mergeProperties();

			// Convert the merged properties, if necessary.
			convertProperties(mergedProps);

			afterPropertiesSet(mergedProps);
			
			properties = mergedProps;
			
			// Let the subclass process the properties.
			processProperties(beanFactory, mergedProps);
			
			
		}
		catch (Exception ex) {
			throw new BeanInitializationException("Could not load properties", ex);
		}
	}
	
	public Map<String, String> getResolvedProps() {
		return Collections.unmodifiableMap(resolvedProps);
	}

	public void afterPropertiesSet(Properties props) throws Exception {
		logger.info("Came in to Cloud Config Property Initializer");

		FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, getPollDelay() * 1000, true);

		CloudConfigurationSource cloudConfigurationSource = new CloudConfigurationSource(getHost(), getPort(),
				getProject(), getEnvironment(), getClientId(), getSecretKey());

		DynamicConfiguration dynamicConfiguration = null;
		try {
			dynamicConfiguration = new DynamicConfiguration(cloudConfigurationSource, scheduler);
		} catch (Exception exp) {
			logger.error("Not able to connect to cloud config server" + exp);
		}

		ConcurrentCompositeConfiguration finalConfig = new ConcurrentCompositeConfiguration();

		// add all config files
		BaseConfiguration baseConfiguration = new BaseConfiguration();
		Enumeration<?> propertyNames = props.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String propertyName = (String) propertyNames.nextElement();
			String propertyValue = props.getProperty(propertyName);
			String convertedValue = convertProperty(propertyName, propertyValue);
			if (ObjectUtils.nullSafeEquals(propertyValue, convertedValue)) {
				baseConfiguration.addProperty(propertyName, convertedValue);
			}
		}
			
		
		finalConfig.addConfiguration(baseConfiguration, "spring-xml-config");

		if (dynamicConfiguration != null)
			finalConfig.addConfigurationAtFront(dynamicConfiguration, "dynamicConfig");

		// install so that finalConfig becomes the source of dynamic properties
		DynamicPropertyFactory.initWithConfigurationSource(finalConfig);

	}

	@Override
	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
		return DynamicPropertyFactory.getInstance().getStringProperty(placeholder, properties.getProperty(placeholder)).get();
	}
}
