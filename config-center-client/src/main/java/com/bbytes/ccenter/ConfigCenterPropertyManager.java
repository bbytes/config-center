package com.bbytes.ccenter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbytes.config.client.CloudConfigurationSource;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.FixedDelayPollingScheduler;


public class ConfigCenterPropertyManager {

  final static Logger logger = LoggerFactory.getLogger(ConfigCenterPropertyManager.class);


  private static class ServerConnection {
    private static int port = 0;
    private static String host = null;
    private static String clientId = null;
    private static String secretKey = null;
    private static String project = null;
    private static String environment = null;
    private static int delayInSec = 60;
  }

  private static FixedDelayPollingScheduler scheduler = null;

  /**
   * init the properties from the config center server before calling the get property methods
   */
  public static void init() {
    CloudConfigurationSource cloudConfigurationSource =
        new CloudConfigurationSource(ServerConnection.host,
            Integer.toString(ServerConnection.port), ServerConnection.project,
            ServerConnection.environment, ServerConnection.clientId, ServerConnection.secretKey);

    scheduler = new FixedDelayPollingScheduler(0, ServerConnection.delayInSec * 1000, true);

    DynamicConfiguration dynamicConfiguration = null;
    try {
      dynamicConfiguration = new DynamicConfiguration(cloudConfigurationSource, scheduler);
    } catch (Exception exp) {
      logger.error("Not able to connect to cloud config server " + exp);
    }

    ConcurrentCompositeConfiguration finalConfig = new ConcurrentCompositeConfiguration();

    if (dynamicConfiguration != null)
      finalConfig.addConfigurationAtFront(dynamicConfiguration, "dynamicConfig");

    // install so that finalConfig becomes the source of dynamic properties
    DynamicPropertyFactory.initWithConfigurationSource(finalConfig);

  }


  /**
   * Set the server connection details and init the properties from the config center server before calling the get property methods
   * 
   * @param host
   * @param port
   * @param clientId
   * @param secretKey
   * @param project
   * @param environment
   * @param delayInSec
   */
  public static void init(String host, int port, String clientId, String secretKey, String project,
      String environment, int delayInSec) {
    ServerConnection.host = host;
    ServerConnection.port = port;
    ServerConnection.clientId = clientId;
    ServerConnection.secretKey = secretKey;
    ServerConnection.project = project;
    ServerConnection.environment = environment;
    ServerConnection.delayInSec = delayInSec;
    init();

  }

  public static void setHost(String host) {
    ServerConnection.host = host;
  }

  public static void setPort(int port) {
    ServerConnection.port = port;
  }

  public static void setClientId(String clientId) {
    ServerConnection.clientId = clientId;
  }

  public static void setSecretKey(String secretKey) {
    ServerConnection.secretKey = secretKey;
  }


  public static void setProject(String project) {
    ServerConnection.project = project;
  }

  public static void setEnvironment(String environment) {
    ServerConnection.environment = environment;
  }

  public static void setDelayInSec(int delayInSec) {
    ServerConnection.delayInSec = delayInSec;
  }


  /**
   * Get string property given the property name or key 
   * @param propertyKey
   * @return
   */
  public static String getProperty(String propertyKey) {
    return DynamicPropertyFactory.getInstance().getStringProperty(propertyKey, null).get();
  }

  /**
   * Get long property given the property name or key 
   * @param propertyKey
   * @return
   */
  public static long getLongProperty(String propertyKey) {
    return DynamicPropertyFactory.getInstance().getLongProperty(propertyKey, 0L).get();
  }

  /**
   * Get double property given the property name or key 
   * @param propertyKey
   * @return
   */
  public static double getDoubleProperty(String propertyKey) {
    return DynamicPropertyFactory.getInstance().getDoubleProperty(propertyKey, 0).get();
  }

  /**
   * Get boolean property given the property name or key 
   * @param propertyKey
   * @return
   */
  public static boolean getBooleanProperty(String propertyKey) {
    return DynamicPropertyFactory.getInstance().getBooleanProperty(propertyKey, false).get();
  }

  /**
   * Get float property given the property name or key 
   * @param propertyKey
   * @return
   */
  public static float getFloatProperty(String propertyKey) {
    return DynamicPropertyFactory.getInstance().getFloatProperty(propertyKey, 0).get();
  }

  /**
   * Get integer property given the property name or key 
   * @param propertyKey
   * @return
   */
  public static int getIntegerProperty(String propertyKey) {
    return DynamicPropertyFactory.getInstance().getIntProperty(propertyKey, 0).get();
  }



}
