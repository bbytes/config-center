package com.bbytes.ccenter.web.rest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bbytes.ccenter.domain.CCProject;
import com.bbytes.ccenter.domain.CCProperty;
import com.bbytes.ccenter.domain.DataType;
import com.bbytes.ccenter.domain.Environment;
import com.bbytes.ccenter.repository.CCProjectRepository;
import com.bbytes.ccenter.security.AuthoritiesConstants;
import com.bbytes.ccenter.security.SecurityUtils;
import com.bbytes.ccenter.service.CloudConfigException;
import com.bbytes.ccenter.service.IConfigReaderWriter;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing CCProject.
 */
@RestController
@RequestMapping("/app")
@RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER})
public class CCProjectResource {

  private final Logger log = LoggerFactory.getLogger(CCProjectResource.class);

  @Inject
  private CCProjectRepository ccprojectRepository;

  @Inject
  private IConfigReaderWriter configReaderWriter;

  /**
   * POST /rest/ccprojects -> Create a new ccproject.
   */
  @RequestMapping(value = "/rest/ccprojects",
            method = RequestMethod.POST,
            produces = "application/json")
    @Timed
    public void create(@RequestBody CCProject ccproject) {
        log.debug("REST request to save CCProject : {}", ccproject);
        ccproject.setOwner(SecurityUtils.getCurrentLogin());
        String status = "success";
    	if (!ResourceUtils.projectExists(ccprojectRepository.findByOwner(SecurityUtils.getCurrentLogin()), 
    			ccproject.getProjectName())) {
    		configReaderWriter.createProject(ccproject);
    	} 
    }

  /**
   * POST /rest/ccproject -> Update ccproject.
   * 
   * @throws CloudConfigException
   */
  @RequestMapping(value = "/rest/ccprojects", method = RequestMethod.PUT,
      produces = "application/json")
  @Timed
  public void update(@RequestBody CCProject ccproject) throws CloudConfigException {
    log.debug("REST request to save CCProject : {}", ccproject);
    configReaderWriter.updateProject(ccproject.getProjectName(), ccproject.getEnvironment());

  }

  /**
   * GET /rest/ccprojects -> get all the ccprojects for current user.
   * 
   * @throws CloudConfigException
   */
  @RequestMapping(value = "/rest/ccprojects", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public List<CCProject> getAll() throws CloudConfigException {
    log.debug("REST request to get all CCProjects");
    return ccprojectRepository.findByOwner(SecurityUtils.getCurrentLogin());
  }

  /**
   * GET /rest/ccprojects/:id -> get the "id" ccproject.
   */
  @RequestMapping(value = "/rest/ccprojects/{id}", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public ResponseEntity<CCProject> get(@PathVariable String id, HttpServletResponse response) {
    log.debug("REST request to get CCProject : {}", id);
    CCProject ccproject = ccprojectRepository.findOne(id);
    if (ccproject == null) {
      return new ResponseEntity<>(ccproject, HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(ccproject, HttpStatus.OK);
  }

  /**
   * DELETE /rest/ccprojects/:id -> delete the "id" ccproject.
   */
  @RequestMapping(value = "/rest/ccprojects/{id}", method = RequestMethod.DELETE,
      produces = "application/json")
  @Timed
  public void delete(@PathVariable String id) {
    log.debug("REST request to delete CCProject : {}", id);
    ccprojectRepository.delete(id);
  }

  /**
   * GET /rest/ccprojects/getenvs -> get Environment[]
   */
  @RequestMapping(value = "/rest/ccprojects/getenvs", method = RequestMethod.GET,
      produces = "application/json")
  @Timed
  public ResponseEntity<Environment[]> getenvs() {
    log.debug("Request to get all Environment values");
    Environment[] environmentValues = Environment.values();
    if (environmentValues == null) {
      return new ResponseEntity<>(environmentValues, HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(environmentValues, HttpStatus.OK);
  }

  /**
   * Upload properties file and store new properties to project
   */
  @RequestMapping(value = "/ccprojects/uploadFile", method = RequestMethod.POST)
  public String uploadFileHandler(HttpServletRequest req, @RequestParam("projId") String projId,
      @RequestParam("file") MultipartFile file) {

    if (!file.isEmpty()) {
      try {
        byte[] bytes = file.getBytes();
        // Creating the directory to store file
        String rootPath = System.getProperty("catalina.home");
        File dir = new File(rootPath + File.separator + "tmpFiles");
        if (!dir.exists())
          dir.mkdirs();
        // Create the file on server
        File serverFile =
            new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(bytes);
        stream.close();
        log.info("Server File Location=" + serverFile.getAbsolutePath());
        TreeMap<String, String> map = ResourceUtils.getProperties(serverFile.getPath());
        if (map.size() < 1) {
        	return "no properties";
        }
        
        CCProject ccproject = ccprojectRepository.findOne(projId);
        String projName = ccproject.getProjectName();
        List<CCProperty> ccproperties = configReaderWriter.getProjectCCProperties(ccproject.getProjectName(), ccproject.getEnvironment());
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (!ResourceUtils.propertyExists(ccproperties, key)) {
				CCProperty ccproperty = new CCProperty();
				ccproperty.setProjectName(projName);
				ccproperty.setEnvironment(ccproject.getEnvironment());
				ccproperty.setDataType(DataType.STRING);
				ccproperty.setPropertyName(key);
				ccproperty.setPropertyValue(value);
				configReaderWriter.saveCCProperty(ccproperty,
						ccproperty.getProjectName(),
						ccproperty.getEnvironment());
			}
		}

        log.info("You successfully uploaded file=" + file.getOriginalFilename());
      } catch (IOException e) {
        log.error("");
      } catch (Exception e) {
        return "You failed to upload " + "filename" + " => " + e.getMessage();
      }
    } else {
      return "You failed to upload " + "filename" + " because the file was empty.";
    }
    return "success";
  }

  /**
   * Download properties file for project
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/ccprojects/propertiesDownload/{id}", method = RequestMethod.GET)
  @ResponseBody
  public ModelAndView download(HttpServletRequest request, HttpServletResponse response,
      @PathVariable("id") String projId) throws Exception {

    CCProject ccproject = ccprojectRepository.findOne(projId);
    String filename =
        ccproject.getProjectName() + "_" + ccproject.getEnvironment().getLabel() + ".properties";

    List<CCProperty> properties = ccproject.getProperties();
    if (properties != null  && properties.size() > 0) {
	    response.setContentType("text/plain");
	    // response.setContentLength(file.getFile().length);
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
	    ResourceUtils.setProperties(properties, response.getOutputStream());
    }
	    
    return null;

  }
  
  /**
   * Download config-center-client zip file containing all dependent jars required for configuration
   * 
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/ccprojects/jarDownload", method = RequestMethod.GET)
  @ResponseBody
  public ModelAndView jarDownload(HttpServletRequest request, HttpServletResponse response, 
		  HttpSession session) throws Exception {

	/*String workingDir = System.getProperty("user.dir");String tomcatDir = System.getProperty("catalina.base");
	System.out.println("Current working directory : " + workingDir + " tomcat dir" + tomcatDir);
	  
	File directory = new File(workingDir);  
	File[] files = directory.listFiles();  
	File file = null;
	String fileName = "";
	for (int index = 0; index < files.length; index++)  
	{  
		if (files[index].isFile()) {
			if (files[index].getName().endsWith("zip")) {
				file = files[index];
				fileName = files[index].getName();
			}
		}
	} */
	
    response.setContentType("application/zip");
//    response.setContentLength(file.getFile().length);
//    FileUtils.copyFile(file, response.getOutputStream());
    
    ServletContext application = session.getServletContext();
    InputStream in = null;
    try {
    	String fileName = "", path = "";
    	Set<String> resources = application.getResourcePaths("/cc-client/");
    	if (resources != null) {
	    	for (String res : resources) {
				if (res.endsWith(".zip")) {
					path = res;
					break;
				}
			} 
    	}
    	fileName = path.split("client/")[1];
    	response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    	in = application.getResourceAsStream(path);
    	   	
    	IOUtils.copy(in, response.getOutputStream());
    } catch (Exception e) {
		// TODO: handle exception
	} finally {
		if(in != null)
			in.close();
	}
    return null;

  }

}
