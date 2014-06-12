package com.bbytes.ccenter.service;

import java.util.List;

import com.bbytes.ccenter.domain.CCProject;
import com.bbytes.ccenter.domain.CCProperty;
import com.bbytes.ccenter.domain.DataType;
import com.bbytes.ccenter.domain.Environment;

public interface IConfigReaderWriter {

	public static final String PROJECT_LIST = "CC_PROJECT_LIST";

	public Object getPropertyValue(String propertyName, String project,
			Environment environment) throws CloudConfigException;

	public void saveProperty(String propertyName, Object propertyValue,DataType dataType,
			String project, Environment environment) throws CloudConfigException;
	
	public void saveCCProperty(CCProperty property, String project,
			Environment environment) throws CloudConfigException;
	
	public void updateProperty(String propertyName, Object propertyValue,DataType dataType,
			String project, Environment environment) throws CloudConfigException;
	
	public void updateCCProperty(CCProperty property, String project,
			Environment environment) throws CloudConfigException;
	
	public void deleteProperty(String propertyName,
			String project, Environment environment) throws CloudConfigException;
	
	public void deleteCCProperty(CCProperty property, String project,
			Environment environment) throws CloudConfigException;

	public CCProperty getCCProperty(String propertyName, String project,
			Environment environment) throws CloudConfigException;


	public List<CCProperty> getProjectCCProperties(String project,Environment environment)
			throws CloudConfigException;

	public CCProject getProject(String project,Environment environment);
	
	public void createProject(String project,Environment environment);
	
    public void createProject(CCProject project);
	
	public void updateProject(String project,Environment environment);
	
	public void deleteProject(String project,Environment environment);

	public boolean isProjectExist(String project);

	public boolean isEnvironmentExist(String project, Environment environment);

	public List<CCProject> listProjects() throws CloudConfigException;
	
	public List<CCProject> getProjects(String project) throws CloudConfigException;

}
