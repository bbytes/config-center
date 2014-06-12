//package com.bbytes.ccenter.web.rest;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import javax.inject.Inject;
//
//import org.joda.time.LocalDate;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestExecutionListeners;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
//import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
//import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.bbytes.ccenter.Application;
//import com.bbytes.ccenter.domain.CCProject;
//import com.bbytes.ccenter.domain.CCProperty;
//import com.bbytes.ccenter.repository.CCProjectRepository;
//
///**
// * Test class for the CCProjectResource REST controller.
// * 
// * @see CCProjectResource
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = Application.class)
//@WebAppConfiguration
//@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
//		DirtiesContextTestExecutionListener.class,
//		TransactionalTestExecutionListener.class })
//@ActiveProfiles("dev")
//public class CCProjectResourceTest {
//
//	private static final String DEFAULT_ID = "1";
//	
//	private static final String DEFAULT_NON_ID = "123";
//
//	private static final LocalDate DEFAULT_SAMPLE_DATE_ATTR = new LocalDate();
//
//	private static final String DEFAULT_SAMPLE_TEXT_ATTR = "sampleTextAttribute";
//
//
//	@Inject
//	private CCProjectRepository ccprojectRepository;
//
//	private MockMvc restCCProjectMockMvc;
//
//	private CCProject ccproject;
//
//	private CCProperty ccProperty;
//
//	@Before
//	public void setup() {
//		MockitoAnnotations.initMocks(this);
//		CCProjectResource ccprojectResource = new CCProjectResource();
//		ReflectionTestUtils.setField(ccprojectResource, "ccprojectRepository",
//				ccprojectRepository);
//
//		this.restCCProjectMockMvc = MockMvcBuilders.standaloneSetup(
//				ccprojectResource).build();
//
//		ccproject = new CCProject();
//		ccproject.setId(DEFAULT_ID);
//		ccproject.setCreationDate(DEFAULT_SAMPLE_DATE_ATTR);
//		ccproject.setUpdateDate(DEFAULT_SAMPLE_DATE_ATTR);
//		ccproject.setProjectName(DEFAULT_SAMPLE_TEXT_ATTR);
//
//		ccProperty = new CCProperty();
//		ccProperty.setId(DEFAULT_ID);
//		ccProperty.setCreationDate(DEFAULT_SAMPLE_DATE_ATTR);
//		ccProperty.setUpdateDate(DEFAULT_SAMPLE_DATE_ATTR);
//		ccProperty.setPropertyName(DEFAULT_SAMPLE_TEXT_ATTR);
//		ccProperty.setPropertyValue(DEFAULT_SAMPLE_TEXT_ATTR);
//		
//		ccproject.addProperty(ccProperty);
//	}
//
//	@Test
//	public void testCRUDCCProject() throws Exception {
//
//		// Create CCProject
//		restCCProjectMockMvc.perform(
//				post("/app/rest/ccprojects").contentType(
//						TestUtil.APPLICATION_JSON_UTF8).content(
//						TestUtil.convertObjectToJsonBytes(ccproject)))
//				.andExpect(status().isOk());
//
//		// Read CCProject
//		restCCProjectMockMvc
//				.perform(get("/app/rest/ccprojects/{id}", DEFAULT_ID))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(jsonPath("$.id").value(DEFAULT_ID))
//				.andExpect(
//						jsonPath("$.creationDate").value(
//								DEFAULT_SAMPLE_DATE_ATTR.toString()))
//				.andExpect(
//						jsonPath("$.projectName").value(
//								DEFAULT_SAMPLE_TEXT_ATTR));
//
//		// Update CCProject
//		ccproject.setCreationDate(DEFAULT_SAMPLE_DATE_ATTR);
//		ccproject.setProjectName(DEFAULT_SAMPLE_TEXT_ATTR);
//
//		restCCProjectMockMvc.perform(
//				post("/app/rest/ccprojects").contentType(
//						TestUtil.APPLICATION_JSON_UTF8).content(
//						TestUtil.convertObjectToJsonBytes(ccproject)))
//				.andExpect(status().isOk());
//
//		// Read updated CCProject
//		restCCProjectMockMvc
//				.perform(get("/app/rest/ccprojects/{id}", DEFAULT_ID))
//				.andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
//				.andExpect(jsonPath("$.id").value(DEFAULT_ID))
//				.andExpect(
//						jsonPath("$.creationDate").value(
//								DEFAULT_SAMPLE_DATE_ATTR.toString()))
//				.andExpect(
//						jsonPath("$.projectName").value(
//								DEFAULT_SAMPLE_TEXT_ATTR));
//
//		// Delete CCProject
//		restCCProjectMockMvc.perform(
//				delete("/app/rest/ccprojects/{id}", DEFAULT_ID).accept(
//						TestUtil.APPLICATION_JSON_UTF8)).andExpect(
//				status().isOk());
//
//		// Read nonexisting CCProject
//		restCCProjectMockMvc.perform(
//				get("/app/rest/ccprojects/{id}", DEFAULT_NON_ID).accept(
//						TestUtil.APPLICATION_JSON_UTF8)).andExpect(
//				status().isNotFound());
//
//	}
//}
