Config Center client
==================================



Build the project and add maven artifact to local repo as the lib is not published to public repo yet . Once the maven artifcat is there in the local repo add the lib to your pom file .The ${cc-client-version} is the current stable version of config center client. JDK or JRE 1.7 is required for config center client.

		 <dependency>
			<groupId>com.bbytes</groupId>
			<artifactId>config-center-client</artifactId>
			<version>${cc-client-version}</version>
		  </dependency>

          
    		  
    		  
 Using cloud config manager in java spring project 
 
 The spring context file content : 
 
 
 	<!-- Config loading via config center -->
	<bean id="placeholderConfig"
		class="com.bbytes.config.spring.ConfigCenterPropertyPaceholderConfigurer">
		<!-- cloud config server host & port -->
		<property name="host" value="localhost" />
		<property name="port" value="9090" />
		<!-- project name the cloud config has to serve -->
		<property name="project" value="test" />
		<!-- project environment the cloud config has to serve -->
		<property name="environment" value="dev" />
		<property name="clientId" value="sVHAMjrVmR" />
		<property name="secretKey" value="RdktOfJIWmYatjiYEzcgakRvcuekdqtXXKPKKudtspTLgDJuqvXnHJfZemrBRBbh" />
		<!-- how freq the properties should be checked for update -->
		<property name="pollDelay" value="10" />
		<property name="locations">
			<list>
				<value>classpath:/META-INF/app.properties
				</value>
				<value>classpath*:META-INF/rabbitmq-config.properties
				</value>
				<value>classpath*:META-INF/mongo-config.properties
				</value>
				<value>classpath*:META-INF/oauth.properties
				</value>
				<value>classpath*:META-INF/notification.properties
				</value>
			</list>
		</property>
	</bean>


	<!-- Using the properties loaded by cloud config in a bean -->
	<!-- prop1 and prop2 are from sample.properties file -->
	<bean id="propTest" class="com.bbytes.config.PropLoadTestBean">
		<property name="prop1" value="${prop1}" />
		<property name="prop2" value="${prop2}" />
	</bean>
	
	


Using config center inside java code :

    String prop1Val = DynamicPropertyFactory.getInstance().getStringProperty("prop1", "default").get(); // if string
    double pollTime = DynamicPropertyFactory.getInstance().getDoubleProperty("pollTime", 1000d).get();  // if double
    
    
The config center is an extension of Netflix archaius project
    
    
REST APIs
==========

Get Project List
----------------

	METHOD : GET
	URL : /project/list/
	
Get Properties in Project for an env
-----------------------------------

	METHOD : GET
	URL : /project/${projectName}/${environment}/property
	



    
