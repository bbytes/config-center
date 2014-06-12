
package com.bbytes.config;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bbytes.ccenter.domain.CCProperty;
import com.bbytes.ccenter.domain.Environment;
import com.bbytes.config.client.ConfigCenterClient;
import com.bbytes.config.client.ConfigCenterException;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class CCClientBasicTest extends ClientBaseTest {
	

	
	@Before
	public void SetUp() throws ConfigCenterException {
	}

	@Test
	public void CCClientTest() throws ConfigCenterException  {
		ConfigCenterClient ccClient = getCCClient();
		boolean success = ccClient.pingSuccess();
		Assert.assertTrue(success);
	}
	
	
	@Test
	public void getPropetyForProjectClientTest() throws ConfigCenterException  {
		ConfigCenterClient ccClient = getCCClient();
		List<String> projectList = ccClient.getProjectList();
		List<CCProperty> properties = ccClient.getPropertiesForProject(projectList.get(1),Environment.DEV.toString());
		for (CCProperty ccProperty : properties) {
			System.out.println(ccProperty);
		}
		Assert.assertTrue(properties.size() > 0);
	}
	
	

//	@After
//	public void cleanUp() {
//		asyncHttpClient.close();
//	}
}
