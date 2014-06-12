package com.bbytes.ccenter.web.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bbytes.ccenter.domain.CCProject;
import com.bbytes.ccenter.domain.CCProperty;
import com.bbytes.ccenter.domain.Environment;
import com.bbytes.ccenter.service.CloudConfigException;
import com.bbytes.ccenter.service.IConfigReaderWriter;
import com.codahale.metrics.annotation.Timed;

@RestController
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	IConfigReaderWriter configReaderWriter;

	/**
	 * List all projects
	 * 
	 * @return
	 * @throws CloudConfigException
	 */
	@RequestMapping("/list")
	@ResponseBody
	@Timed
	public List<CCProject> listAllProjects() throws CloudConfigException {
		return configReaderWriter.listProjects();
	}

	/**
	 * List all environments for given project
	 * 
	 * @param project
	 * @return
	 * @throws CloudConfigException
	 */
	@RequestMapping("/{project}/environment")
	@ResponseBody
	@Timed
	public List<Environment> listProjectEnvironment(@PathVariable String project)
			throws CloudConfigException {
		List<CCProject> projects = configReaderWriter.getProjects(project);
		List<Environment> environments = new ArrayList<>();
		for (Iterator<CCProject> iterator = projects.iterator(); iterator
				.hasNext();) {
			CCProject ccProject = iterator.next();
			environments.add(ccProject.getEnvironment());
		}

		return environments;
	}

	/**
	 * Returns the project property list based on given environment
	 * 
	 * @param project
	 * @param environment
	 * @return
	 * @throws CloudConfigException
	 */
	@RequestMapping("/{project}/{environment}/property")
	@ResponseBody
	@Timed
	public List<CCProperty> listProjectEnvironmentProperties(
			@PathVariable String project, @PathVariable String environment)
			throws CloudConfigException {
		return configReaderWriter.getProjectCCProperties(project, Environment.getForLabel(environment));
	}

	/**
	 * Returns the project property list based on default environment which is
	 * PROD
	 * 
	 * @param project
	 * @return
	 * @throws CloudConfigException
	 */
	@RequestMapping("/{project}/property")
	@ResponseBody
	@Timed
	public List<CCProperty> listProjectProperties(@PathVariable String project)
			throws CloudConfigException {
		return configReaderWriter.getProjectCCProperties(project,
				Environment.PROD);
	}

	@ExceptionHandler(CloudConfigException.class)
	@ResponseBody
	@Timed
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleException(Exception e) {
		return e.getMessage();
	}

}