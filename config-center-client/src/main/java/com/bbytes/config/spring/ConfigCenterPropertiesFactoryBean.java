package com.bbytes.config.spring;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.util.CollectionUtils;

import com.bbytes.config.client.CloudConfigurationSource;
import com.netflix.config.PollResult;

public class ConfigCenterPropertiesFactoryBean extends PropertiesFactoryBean {

	private String host;

	private String port = "9000"; // default port

	private String project;

	private String environment;

	private String clientId;

	private String secretKey;

	private int pollDelay = 10; // 10 secs

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
	 * Template method that subclasses may override to construct the object
	 * returned by this factory. The default implementation returns the plain
	 * merged Properties instance.
	 * <p>
	 * Invoked on initialization of this FactoryBean in case of a shared
	 * singleton; else, on each {@link #getObject()} call.
	 * 
	 * @return the object returned by this factory
	 * @throws IOException
	 *             if an exception occured during properties loading
	 * @see #mergeProperties()
	 */
	protected Properties createProperties() throws IOException {
		Properties result = new Properties();
		Properties xmlProps = super.createProperties();
		CollectionUtils.mergePropertiesIntoMap(xmlProps, result);
		try {
			CloudConfigurationSource cloudConfigurationSource = new CloudConfigurationSource(
					getHost(), getPort(), getProject(), getEnvironment(),
					getClientId(), getSecretKey());

			PollResult pollResult = cloudConfigurationSource.poll(true, null);
			Properties ccProps = new Properties();
			ccProps.putAll(pollResult.getComplete());

			CollectionUtils.mergePropertiesIntoMap(ccProps, result);

		} catch (Exception e) {
			throw new IOException(e);
		}

		return result;

	}

}
