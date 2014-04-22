package com.github.leeonlee.crowdshop_app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.ObjectParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
* Created by ten-young on 4/22/14.
*/
class ObjectMapperParser implements ObjectParser {

	private final ObjectMapper objectMapper;

	public ObjectMapperParser() {
		this(new ObjectMapper());
	}

	public ObjectMapperParser(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public <T> T parseAndClose(InputStream in, Charset charset, Class<T> dataClass) throws IOException {
		try {
			return objectMapper.readValue(in, dataClass);
		} finally {
			in.close();
		}
	}

	@Override
	public Object parseAndClose(InputStream in, Charset charset, Type dataType) throws IOException {
		return parseAndClose(in, charset, dataType.getClass());
	}

	@Override
	public <T> T parseAndClose(Reader reader, Class<T> dataClass) throws IOException {
		try {
			return objectMapper.readValue(reader, dataClass);
		} finally {
			reader.close();
		}
	}

	@Override
	public Object parseAndClose(Reader reader, Type dataType) throws IOException {
		return parseAndClose(reader, dataType.getClass());
	}
}
