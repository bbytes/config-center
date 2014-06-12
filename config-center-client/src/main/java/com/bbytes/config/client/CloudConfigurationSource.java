package com.bbytes.config.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbytes.ccenter.domain.CCProperty;
import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;

public class CloudConfigurationSource implements PolledConfigurationSource {

  protected ConfigCenterClient cloudConfigClient;

  protected String host;

  protected String port;

  protected String project;

  protected String environment;

  protected String clientId;

  protected String secretKey;

  public CloudConfigurationSource() {

  }

  public CloudConfigurationSource(String host, String port) {
    this.host = host;
    this.port = port;
  }

  public CloudConfigurationSource(String host, String port, String project, String environment,
      String clientId, String secretKey) {
    this.host = host;
    this.port = port;
    this.project = project;
    this.environment = environment;
    this.clientId = clientId;
    this.secretKey = secretKey;
  }

  public PollResult poll(boolean initial, Object checkPoint) throws Exception {
    if (host == null || port == null)
      throw new IllegalStateException(
          "Cloud Configuration Server host and port is not set on source");

    if (project == null || project.isEmpty())
      throw new IllegalStateException("Cloud Configuration project name is not available");

    Map<String, Object> map = new HashMap<String, Object>();

    if (cloudConfigClient == null) {
      cloudConfigClient = new ConfigCenterClient(host, port, clientId, secretKey);
    }

    if (!cloudConfigClient.isClosed() && cloudConfigClient.pingSuccess()) {
      List<CCProperty> properties = cloudConfigClient.getPropertiesForProject(project, environment);
      if (properties != null && !properties.isEmpty()) {
        for (CCProperty ccProperty : properties) {
          map.put(ccProperty.getPropertyName(), ccProperty.getPropertyValue());
        }
      }
    }

    if (cloudConfigClient != null) {
      cloudConfigClient.close();
      cloudConfigClient = null;
    }

    return PollResult.createFull(map);
  }

  /**
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * @param host the host to set
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * @return the port
   */
  public String getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(String port) {
    this.port = port;
  }

  /**
   * @return the project
   */
  public String getProject() {
    return project;
  }

  /**
   * @param project the project to set
   */
  public void setProject(String project) {
    this.project = project;
  }
}
