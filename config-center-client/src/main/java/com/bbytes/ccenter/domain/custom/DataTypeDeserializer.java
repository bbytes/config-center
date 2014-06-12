package com.bbytes.ccenter.domain.custom;

import java.io.IOException;

import com.bbytes.ccenter.domain.DataType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class DataTypeDeserializer  extends JsonDeserializer<DataType> {

	@Override
	public DataType deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		ObjectCodec oc = jp.getCodec();
		JsonNode node = oc.readTree(jp);
		String name = node.get("name").textValue();
		return DataType.valueOf(name);
	}

}
