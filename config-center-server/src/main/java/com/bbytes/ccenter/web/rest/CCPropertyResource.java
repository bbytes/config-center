package com.bbytes.ccenter.web.rest;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.ccenter.domain.CCProject;
import com.bbytes.ccenter.domain.CCProperty;
import com.bbytes.ccenter.domain.DataType;
import com.bbytes.ccenter.repository.CCProjectRepository;
import com.bbytes.ccenter.repository.CCPropertyRepository;
import com.bbytes.ccenter.service.CloudConfigException;
import com.bbytes.ccenter.service.IConfigReaderWriter;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing CCProperty.
 */
@RestController
@RequestMapping("/app")
public class CCPropertyResource {

	private final Logger log = LoggerFactory
			.getLogger(CCPropertyResource.class);
	
	@Inject
	private CCProjectRepository ccprojectRepository;
	
	@Inject
	private CCPropertyRepository ccpropertyRepository;

	@Inject
	private IConfigReaderWriter configReaderWriter;

	/**
	 * POST /rest/ccpropertys -> Create a new ccproperty.
	 * 
	 * @throws CloudConfigException
	 */
	@RequestMapping(value = "/rest/ccpropertys", method = RequestMethod.POST, produces = "application/json")
	@Timed
	public void create(@RequestBody CCProperty ccproperty)
			throws CloudConfigException {
		log.debug("REST request to save CCProperty : {}", ccproperty);
		
		/*if (ccproperty.getDataType().getLabel().equals("date")) {
			String date = ccproperty.getPropertyValue().toString().split("T")[0];
			ccproperty.setPropertyValue(date);
		}*/
		
		CCProject ccProject = configReaderWriter.getProject(ccproperty.getProjectName(), ccproperty.getEnvironment());
		List<CCProperty> ccproperties = configReaderWriter.getProjectCCProperties(ccProject.getProjectName(), ccProject.getEnvironment());
			
		if (!ResourceUtils.propertyExists(ccproperties, ccproperty.getPropertyName())) {
			configReaderWriter.saveCCProperty(ccproperty,
				ccproperty.getProjectName(), ccproperty.getEnvironment());
		}
		
	}

	/**
	 * POST /rest/ccpropertys -> Updates ccproperty.
	 * @throws CloudConfigException 
	 */
	@RequestMapping(value = "/rest/ccpropertys", method = RequestMethod.PUT, produces = "application/json")
	@Timed
	public void update(@RequestBody CCProperty ccproperty) throws CloudConfigException {
		log.debug("REST request to save CCProperty : {}", ccproperty);
		configReaderWriter.updateCCProperty(ccproperty,
				ccproperty.getProjectName(), ccproperty.getEnvironment());
	}

	/**
	 * GET /rest/ccpropertys/:id -> get the "id" ccproperty.
	 */
	@RequestMapping(value = "/rest/ccpropertys/{id}", method = RequestMethod.GET, produces = "application/json")
	@Timed
	public ResponseEntity<CCProperty> get(@PathVariable String id,
			HttpServletResponse response) {
		log.debug("REST request to get CCProperty : {}", id);
		CCProperty ccproperty = ccpropertyRepository.findOne(id);
		if (ccproperty == null) {
			return new ResponseEntity<>(ccproperty, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(ccproperty, HttpStatus.OK);
	}

	/**
	 * GET /rest/ccpropertys/:id -> get the ccpropertys for given projectId.
	 * @throws CloudConfigException 
	 */
	@RequestMapping(value = "/rest/ccprojpropertys/{projectId}", method = RequestMethod.GET, produces = "application/json")
	@Timed
	public ResponseEntity<List<CCProperty>> getAllProjectProperties(
			@PathVariable String projectId,	HttpServletResponse response) throws CloudConfigException {
		log.debug("REST request to get CCProperties");
		
		CCProject ccproject = ccprojectRepository.findOne(projectId);
		List<CCProperty> ccproperties = configReaderWriter.getProjectCCProperties(ccproject.getProjectName(), ccproject.getEnvironment());
		if (ccproperties == null) {
			return new ResponseEntity<>(ccproperties, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(ccproperties, HttpStatus.OK);
	}
	
	/**
	 * DELETE /rest/ccpropertys/:id -> delete the "id" ccproperty.
	 */
	@RequestMapping(value = "/rest/ccpropertys/{id}", method = RequestMethod.DELETE, produces = "application/json")
	@Timed
	public void delete(@PathVariable String id) {
		log.debug("REST request to delete CCProperty : {}", id);
		ccpropertyRepository.delete(id);
	}
	
	  /**
     * GET  /rest/ccprojects/getenvs -> get DataType[]
     */
    @RequestMapping(value = "/rest/ccpropertys/getdatatypes",
            method = RequestMethod.GET,
            produces = "application/json")
    @Timed
    public ResponseEntity<DataType[]> getDataTypes() {
        log.debug("Request to get all Environment values");
        DataType[] dataTypes = DataType.values();
        if (dataTypes == null) {
        	return new ResponseEntity<>(dataTypes, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dataTypes, HttpStatus.OK);
    }
}
