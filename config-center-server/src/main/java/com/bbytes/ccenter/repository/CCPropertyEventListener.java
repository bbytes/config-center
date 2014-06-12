package com.bbytes.ccenter.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

import com.bbytes.ccenter.domain.CCProject;
import com.bbytes.ccenter.domain.CCProperty;
import com.bbytes.ccenter.domain.Environment;
import com.mongodb.DBObject;

/**
 * Class called when we query or save the user object into mongo db
 * 
 * @author Thanneer
 * 
 */
public class CCPropertyEventListener extends AbstractMongoEventListener<CCProperty> {

  @Autowired
  private CCProjectRepository projectRepository;

  @Autowired
  private CCPropertyRepository propertyRepository;

  public void onBeforeDelete(DBObject dbo) {
    String id = dbo.get("id").toString();
    CCProperty property = propertyRepository.findOne(id);
    if (property == null)
      return;

    CCProject project =
        projectRepository.findByProjectNameAndEnvironment(property.getProjectName(),
            property.getEnvironment());
    if (project != null) {

      List<CCProperty> properties = project.getProperties();
      for (CCProperty ccProperty : properties) {
        if (ccProperty.getId().equals(id)) {
          properties.remove(ccProperty);
          break;
        }
      }
      project.setProperties(properties);
      projectRepository.save(project);

    }
  }


}
