package com.bbytes.ccenter.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.bbytes.ccenter.domain.custom.CustomLocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;

/**
 * A CCProject.
 */

@Document(collection = "T_CCPROJECT")
public class CCProject implements Serializable {

	private static final long serialVersionUID = -7647962788378221105L;

	@Id
	private String id;
	
	@Field("project_name")
	private String projectName;

	@NotNull
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@Field("creation_date")
	private LocalDate creationDate;

	@NotNull
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@Field("update_date")
	private LocalDate updateDate;
	
	@Field("owner")
    private String owner;
	
	@Field("environment")
	private Environment environment;

	@DBRef
	private List<CCProperty> properties;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDate getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(LocalDate updateDate) {
		this.updateDate = updateDate;
	}

	public List<CCProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<CCProperty> properties) {
		this.properties = properties;
	}

	public void addProperty(CCProperty property) {
		if (this.properties == null) {
			this.properties = new ArrayList<>();
		}
		this.properties.add(property);
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  @Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CCProject ccproject = (CCProject) o;

		if (!projectName.equals(ccproject.projectName)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return projectName != null ? projectName.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "CCProject{" + "id=" + projectName + "}'";
	}


}
