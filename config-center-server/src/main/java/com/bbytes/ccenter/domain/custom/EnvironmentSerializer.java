package com.bbytes.ccenter.domain.custom;

import java.io.IOException;

import com.bbytes.ccenter.domain.Environment;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EnvironmentSerializer extends JsonSerializer<Environment> {

	@Override
	public void serialize(Environment value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeFieldName("name");
		jgen.writeString(value.name());
		jgen.writeFieldName("label");
		jgen.writeString(value.toString());
		jgen.writeEndObject();
		
	}

}
