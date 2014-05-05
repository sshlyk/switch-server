package com.alisa.Iswitch.server.lib.serialization;

import java.io.Serializable;

import org.codehaus.jackson.map.ObjectMapper;

public class JsonSerializer<E extends Serializable> implements Serializer<E> {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public byte[] serialize(E request) throws Exception {
		return mapper.writeValueAsBytes(request);
	}

	@Override
	public E deserialize(byte[] bytes, Class<E> clazz) throws Exception {
		return mapper.readValue(bytes, clazz);
	}
}
