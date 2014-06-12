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

  DATE("date", "Date"),

  DATETIME("datetime", "Date Time"),

  LONG("long", "Long"),

  FLOAT("float", "Float"),

  INTEGER("integer", "Integer"),

  STRING("string", "String"),

  BOOLEAN("boolean", "Boolean");

  private final String label;

  private final String displayName;

  DataType(String label, String displayName) {
    this.label = label;
    this.displayName = displayName;

  }

  public String getLabel() {
    return label;
  }

  public String getDisplayName() {
    return displayName;
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
  
  /*
   * If nothing matches it will return string type
   */
  public static DataType getForDisplayName(String displayName) {
    for (DataType dt : DataType.values()) {
      if (dt.getDisplayName().equals(displayName)) {
        return dt;
      }
    }
    return DataType.STRING;
  }

  public String toString() {
    return label;
  }

  

}
