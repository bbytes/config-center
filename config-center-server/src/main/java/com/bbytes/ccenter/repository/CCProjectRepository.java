package com.bbytes.ccenter.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bbytes.ccenter.domain.CCProject;
import com.bbytes.ccenter.domain.Environment;

/**
 * Spring Data MongoDB repository for the CCProject entity.
 */
public interface CCProjectRepository extends MongoRepository<CCProject, String> {

  public CCProject findByProjectNameAndEnvironment(String projectName, Environment environment);

  public List<CCProject> findByProjectName(String projectName);
  
  public List<CCProject> findByOwner(String userId);
}
