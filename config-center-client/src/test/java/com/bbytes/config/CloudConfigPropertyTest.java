package com.bbytes.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bbytes.ccenter.domain.Environment;
import com.bbytes.config.client.ConfigCenterException;
import com.bbytes.config.client.CloudConfigurationSource;
import com.netflix.config.ClasspathPropertiesConfiguration;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConcurrentMapConfiguration;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicDoubleProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.FixedDelayPollingScheduler;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class CloudConfigPropertyTest {

  @Before
  public void SetUp() throws ConfigCenterException {}

  @Test
  public void cloudConfigProperty() throws ConfigCenterException, InterruptedException,
      ConfigurationException {
    FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 5000, true);

    CloudConfigurationSource cloudConfigurationSource =
        new CloudConfigurationSource("localhost", "9090", "errzero", Environment.DEV.toString(),
            "sVHAMjrVmR", "RdktOfJIWmYatjiYEzcgakRvcuekdqtXXKPKKudtspTLgDJuqvXnHJfZemrBRBbh");
    DynamicConfiguration dynamicConfiguration = null;
    try {
      dynamicConfiguration = new DynamicConfiguration(cloudConfigurationSource, scheduler);
    } catch (Exception e1) {
      // do nothing
    }

    ClasspathPropertiesConfiguration.initialize();

    ConcurrentMapConfiguration configFromPropertiesFile =
        new ConcurrentMapConfiguration(new PropertiesConfiguration("prop1.properties"));

    PropertiesConfiguration propertiesConfiguration =
        new PropertiesConfiguration("META-INF/prop2.properties");

    // create a hierarchy of configuration that makes
    // 1) dynamic configuration source override system properties and,
    // 2) system properties override properties file
    ConcurrentCompositeConfiguration finalConfig = new ConcurrentCompositeConfiguration();
    if (dynamicConfiguration != null)
      finalConfig.addConfiguration(dynamicConfiguration, "dynamicConfig");
    finalConfig.addConfiguration(configFromPropertiesFile, "fileConfig");
    finalConfig.addConfiguration(propertiesConfiguration, "fileConfig2");

    // install with ConfigurationManager so that finalConfig
    // becomes the source of dynamic properties
    DynamicPropertyFactory.initWithConfigurationSource(finalConfig);

    // DynamicPropertyFactory.initWithConfigurationSource(configuration);

    int i = 0;
    while (i <= 2) {

      DynamicStringProperty defaultProp1 =
          DynamicPropertyFactory.getInstance().getStringProperty("prop1", "3");
      System.out.println("prop1 string value is " + defaultProp1.getValue());

      DynamicDoubleProperty defaultProp2 =
          DynamicPropertyFactory.getInstance().getDoubleProperty("prop2", 7);
      System.out.println("prop2 double value is " + defaultProp2.getValue());

      DynamicLongProperty defaultProp5 =
          DynamicPropertyFactory.getInstance().getLongProperty("prop5", 89999);
      System.out.println("prop5 long value is " + defaultProp5.getValue());

      DynamicStringProperty defaultProp6 =
          DynamicPropertyFactory.getInstance().getStringProperty("mongo.database", "mongo-default");
      System.out.println("prop6 string value is " + defaultProp6.getValue());

      Assert.assertEquals(defaultProp6.getValue(), "errzero-dev");


      System.out.println("---------------------------------------");

      try {
        Thread.currentThread().sleep(6000);
        i++;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  @After
  public void cleanUp() {

  }
}
