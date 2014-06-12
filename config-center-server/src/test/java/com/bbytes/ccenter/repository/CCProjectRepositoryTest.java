package com.bbytes.ccenter.repository;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import com.bbytes.ccenter.Application;
import com.bbytes.ccenter.domain.CCProject;
import com.bbytes.ccenter.domain.CCProperty;
import com.bbytes.ccenter.domain.Environment;
import com.bbytes.ccenter.web.rest.CCProjectResource;

/**
 * Test class for the CCProjectResource REST controller.
 * 
 * @see CCProjectResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
@ActiveProfiles("dev")
public class CCProjectRepositoryTest {

	private static final String DEFAULT_ID = "1";

	private static final LocalDate DEFAULT_SAMPLE_DATE = new LocalDate();

	private static final String DEFAULT_PROJECT_NAME = "test_project";

	@Inject
	private CCProjectRepository ccprojectRepository;

	private CCProject ccproject;

	private CCProperty ccProperty;

	@Before
	public void setup() {

		ccproject = new CCProject();
		ccproject.setId(DEFAULT_ID);
		ccproject.setCreationDate(DEFAULT_SAMPLE_DATE);
		ccproject.setUpdateDate(DEFAULT_SAMPLE_DATE);
		ccproject.setProjectName(DEFAULT_PROJECT_NAME);
		ccproject.setEnvironment(Environment.DEV);

		ccProperty = new CCProperty();
		ccProperty.setId(DEFAULT_ID);
		ccProperty.setCreationDate(DEFAULT_SAMPLE_DATE);
		ccProperty.setUpdateDate(DEFAULT_SAMPLE_DATE);
		ccProperty.setPropertyName(DEFAULT_PROJECT_NAME);
		ccProperty.setPropertyValue(DEFAULT_PROJECT_NAME);

		ccproject.addProperty(ccProperty);
	}

	@Test
	public void testCRUDCCProject() throws Exception {
		ccprojectRepository.save(ccproject);
		ccproject = ccprojectRepository.findByProjectNameAndEnvironment(DEFAULT_PROJECT_NAME, Environment.DEV);
		Assert.notNull(ccproject);
	}
}
