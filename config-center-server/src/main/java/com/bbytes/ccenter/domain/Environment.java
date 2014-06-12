package com.bbytes.ccenter.domain;

import com.bbytes.ccenter.domain.custom.EnvironmentDeserializer;
import com.bbytes.ccenter.domain.custom.EnvironmentSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The possible property data types
 * 
 * @author Thanneer
 * 
 */

@JsonSerialize(using = EnvironmentSerializer.class)
@JsonDeserialize(using = EnvironmentDeserializer.class)
public enum Environment {

	DEV("dev"),

	PROD("prod"),

	TEST("test");

	private final String label;

	Environment(String label) {
		this.label = label;

	}

	public String getLabel() {
		return label;
	}

	/*
	 * If nothing matches it will return string type
	 */
	public static Environment getForLabel(String label) {
		for (Environment dt : Environment.values()) {
			if (dt.getLabel().equals(label)) {
				return dt;
			}
		}
		return Environment.PROD;
	}

	public String toString() {
		return label;
	}

}
