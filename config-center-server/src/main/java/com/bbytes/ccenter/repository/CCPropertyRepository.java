package com.bbytes.ccenter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.bbytes.ccenter.domain.CCProperty;
import com.bbytes.ccenter.domain.Environment;

/**
 * Spring Data MongoDB repository for the CCProperty entity.
 */
public interface CCPropertyRepository extends MongoRepository<CCProperty, String> {

	@Query("{ 'property_name' : ?0 , 'project_name' : ?1 , 'environment' : ?2 } ")
	public CCProperty findPropertyForProject(String propertyName, String project , Environment environment);
}
