package com.bbytes.ccenter.domain.custom;

import java.io.IOException;

import com.bbytes.ccenter.domain.DataType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DataTypeSerializer extends JsonSerializer<DataType> {

	@Override
	public void serialize(DataType value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeFieldName("name");
		jgen.writeString(value.name());
		jgen.writeFieldName("label");
		jgen.writeString(value.getLabel());
		jgen.writeFieldName("displayName");
		jgen.writeString(value.getDisplayName());
		jgen.writeEndObject();
		
	}

}
