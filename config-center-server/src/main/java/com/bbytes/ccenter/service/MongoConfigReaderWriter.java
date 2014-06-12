package com.bbytes.ccenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbytes.ccenter.domain.CCProject;
import com.bbytes.ccenter.domain.CCProperty;
import com.bbytes.ccenter.domain.DataType;
import com.bbytes.ccenter.domain.Environment;
import com.bbytes.ccenter.repository.CCProjectRepository;
import com.bbytes.ccenter.repository.CCPropertyRepository;

@Service
public class MongoConfigReaderWriter implements IConfigReaderWriter {

	@Autowired
	private CCPropertyRepository ccPropertyRepository;

	@Autowired
	private CCProjectRepository projectRepository;

	@Override
	public Object getPropertyValue(String propertyName, String project,
			Environment environment) throws CloudConfigException {

		CCProperty property = ccPropertyRepository.findPropertyForProject(
				propertyName, project, environment);
		if (property != null)
			return property.getPropertyValue();

		return null;
	}

	@Override
	public void saveProperty(String propertyName, Object propertyValue,
			DataType dataType, String project, Environment environment)
			throws CloudConfigException {
		CCProperty ccProperty = new CCProperty();
		ccProperty.setId(UUID.randomUUID().toString());
		ccProperty.setPropertyName(propertyName);
		ccProperty.setPropertyValue(propertyValue);
		ccProperty.setCreationDate(new LocalDate());
		ccProperty.setUpdateDate(new LocalDate());
		ccProperty.setDataType(dataType);
		ccProperty.setEnvironment(environment);
		ccProperty.setProjectName(project);
		ccPropertyRepository.save(ccProperty);

		CCProject ccProject = getProject(project, environment);
		ccProject.addProperty(ccProperty);
		projectRepository.save(ccProject);

	}

	@Override
	public void saveCCProperty(CCProperty property, String project,
			Environment environment) throws CloudConfigException {
		property.setId(UUID.randomUUID().toString());
		property.setCreationDate(new LocalDate());
		property.setUpdateDate(new LocalDate());
		
		ccPropertyRepository.save(property);

		CCProject ccProject = getProject(project, environment);
		ccProject.addProperty(property);
		projectRepository.save(ccProject);

	}

	@Override
	public void updateProperty(String propertyName, Object propertyValue,
			DataType dataType, String project, Environment environment)
			throws CloudConfigException {
		CCProperty ccProperty = getCCProperty(propertyName, project,
				environment);
		if (ccProperty != null) {
			ccProperty.setPropertyValue(propertyValue);
			ccProperty.setDataType(dataType);
			ccPropertyRepository.save(ccProperty);
		}
	}

	@Override
	public void updateCCProperty(CCProperty property, String project,
			Environment environment) throws CloudConfigException {
		CCProperty ccProperty = ccPropertyRepository.findOne(property.getId());
		if (ccProperty != null) {
			ccProperty.setPropertyName(property.getPropertyName());
			ccProperty.setPropertyValue(property.getPropertyValue());
			ccProperty.setDataType(property.getDataType());
			ccPropertyRepository.save(ccProperty);
		}
	}

	@Override
	public void deleteProperty(String propertyName, String project,
			Environment environment) throws CloudConfigException {
		CCProperty ccProperty = getCCProperty(propertyName, project,
				environment);
		if (ccProperty != null)
			ccPropertyRepository.delete(ccProperty.getId());

	}

	@Override
	public void deleteCCProperty(CCProperty property, String project,
			Environment environment) throws CloudConfigException {
		ccPropertyRepository.delete(property.getId());
	}

	@Override
	public CCProperty getCCProperty(String propertyName, String project,
			Environment environment) throws CloudConfigException {
		return ccPropertyRepository.findPropertyForProject(propertyName,
				project, environment);
	}

	@Override
	public List<CCProperty> getProjectCCProperties(String project,
			Environment environment) throws CloudConfigException {
		CCProject ccProject = getProject(project, environment);
		if (ccProject != null)
			return ccProject.getProperties();

		return new ArrayList<CCProperty>();
	}

	@Override
    public void createProject(CCProject project) {
	  
	  CCProject ccProject = null;

      ccProject = projectRepository.findOne(project.getProjectName() + "_" + project.getEnvironment());
      if (ccProject != null)
          return;

      ccProject = new CCProject();
      ccProject.setProjectName(project.getProjectName());
      ccProject.setEnvironment(project.getEnvironment());
      ccProject.setCreationDate(new LocalDate());
      ccProject.setUpdateDate(new LocalDate());
      ccProject.setId(project.getProjectName() + "_" + project.getEnvironment());
      ccProject.setProperties(project.getProperties());
      ccProject.setOwner(project.getOwner());
      projectRepository.save(ccProject);
      
	}
	
	
	@Override
	public void createProject(String project, Environment environment) {
		CCProject ccProject = null;

		ccProject = projectRepository.findOne(project + "_" + environment);
		if (ccProject != null)
			return;

		ccProject = new CCProject();
		ccProject.setProjectName(project);
		ccProject.setEnvironment(environment);
		ccProject.setCreationDate(new LocalDate());
		ccProject.setUpdateDate(new LocalDate());
		ccProject.setId(project + "_" + environment);
		projectRepository.save(ccProject);
	}
	
	@Override
	public void updateProject(String project,Environment environment) {
		CCProject ccproject = getProject(project, environment);
		
		if (ccproject != null) {
			ccproject.setProjectName(ccproject.getProjectName());
			ccproject.setEnvironment(ccproject.getEnvironment());
			projectRepository.save(ccproject);
		}
	}

	@Override
	public void deleteProject(String project, Environment environment) {
		projectRepository.delete(project + "_" + environment);
	}

	@Override
	public boolean isProjectExist(String project) {
		List<CCProject> ccProjects = projectRepository
				.findByProjectName(project);
		if (ccProjects != null && ccProjects.size() > 0)
			return true;

		return false;
	}

	@Override
	public boolean isEnvironmentExist(String project, Environment environment) {
		CCProject ccProject = getProject(project, environment);
		if (ccProject != null)
			return true;

		return false;
	}

	@Override
	public List<CCProject> listProjects() throws CloudConfigException {
		return projectRepository.findAll();
	}

	@Override
	public CCProject getProject(String project, Environment environment) {
		CCProject ccProject = projectRepository.findOne(project + "_"
				+ environment);
		return ccProject;
	}

	@Override
	public List<CCProject> getProjects(String project)
			throws CloudConfigException {
		return projectRepository.findByProjectName(project);
	}

}
