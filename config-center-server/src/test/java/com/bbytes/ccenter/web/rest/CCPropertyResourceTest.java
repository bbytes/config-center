package com.bbytes.ccenter.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bbytes.ccenter.Application;
import com.bbytes.ccenter.domain.CCProperty;
import com.bbytes.ccenter.domain.DataType;
import com.bbytes.ccenter.domain.Environment;
import com.bbytes.ccenter.repository.CCPropertyRepository;


/**
 * Test class for the CCPropertyResource REST controller.
 *
 * @see CCPropertyResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@ActiveProfiles("dev")
public class CCPropertyResourceTest {
    
    private static final String DEFAULT_ID = "1";

    private static final LocalDate DEFAULT_SAMPLE_DATE_ATTR = new LocalDate(0L);

    private static final String DEFAULT_SAMPLE_TEXT_ATTR = "sampleTextAttribute";

 
    @Inject
    private CCPropertyRepository ccpropertyRepository;

    private MockMvc restCCPropertyMockMvc;
    
    private CCProperty ccproperty;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CCPropertyResource ccpropertyResource = new CCPropertyResource();
        ReflectionTestUtils.setField(ccpropertyResource, "ccpropertyRepository", ccpropertyRepository);

        this.restCCPropertyMockMvc = MockMvcBuilders.standaloneSetup(ccpropertyResource).build();

        ccproperty = new CCProperty();
        ccproperty.setId(DEFAULT_ID);
    	ccproperty.setCreationDate(DEFAULT_SAMPLE_DATE_ATTR);
    	ccproperty.setPropertyName(DEFAULT_SAMPLE_TEXT_ATTR);
    	ccproperty.setPropertyValue(DEFAULT_SAMPLE_TEXT_ATTR);
    	ccproperty.setEnvironment(Environment.DEV);
    	ccproperty.setDataType(DataType.STRING);
    	ccproperty.setUpdateDate(DEFAULT_SAMPLE_DATE_ATTR);
    }

    @Test
    public void testCRUDCCProperty() throws Exception {

    	// Create CCProperty
    	restCCPropertyMockMvc.perform(post("/app/rest/ccpropertys")
    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ccproperty)))
                .andExpect(status().isOk());

    	// Read CCProperty
    	restCCPropertyMockMvc.perform(get("/app/rest/ccpropertys/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID))
    			.andExpect(jsonPath("$.creationDate").value(DEFAULT_SAMPLE_DATE_ATTR.toString()))
    			.andExpect(jsonPath("$.propertyValue").value(DEFAULT_SAMPLE_TEXT_ATTR));

    	// Update CCProperty
    	ccproperty.setCreationDate(DEFAULT_SAMPLE_DATE_ATTR);
    	ccproperty.setPropertyValue(DEFAULT_SAMPLE_TEXT_ATTR);
  
    	restCCPropertyMockMvc.perform(post("/app/rest/ccpropertys")
    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ccproperty)))
                .andExpect(status().isOk());

    	// Read updated CCProperty
    	restCCPropertyMockMvc.perform(get("/app/rest/ccpropertys/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID))
    			.andExpect(jsonPath("$.creationDate").value(DEFAULT_SAMPLE_DATE_ATTR.toString()))
    			.andExpect(jsonPath("$.propertyValue").value(DEFAULT_SAMPLE_TEXT_ATTR));

    	// Delete CCProperty
    	restCCPropertyMockMvc.perform(delete("/app/rest/ccpropertys/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

    	// Read nonexisting CCProperty
    	restCCPropertyMockMvc.perform(get("/app/rest/ccpropertys/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }
}
