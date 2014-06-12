package com.bbytes.ccenter.domain;

import com.bbytes.ccenter.domain.custom.DataTypeDeserializer;
import com.bbytes.ccenter.domain.custom.DataTypeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The possible property data types
 * 
 * @author Thanneer
 * 
 */
@JsonSerialize(using = DataTypeSerializer.class)
@JsonDeserialize(using = DataTypeDeserializer.class)
public enum DataType {

	DATE("date"),

	DATETIME("datetime"),

	LONG("long"),

	FLOAT("float"),

	INTEGER("integer"),

	STRING("string"),

	BOOLEAN("boolean");

	private final String label;

	DataType(String label) {
		this.label = label;

	}

	public String getLabel() {
		return label;
	}

	/*
	 * If nothing matches it will return string type
	 */
	public static DataType getForLabel(String label) {
		for (DataType dt : DataType.values()) {
			if (dt.getLabel().equals(label)) {
				return dt;
			}
		}
		return DataType.STRING;
	}

	public String toString() {
		return label;
	}

}
