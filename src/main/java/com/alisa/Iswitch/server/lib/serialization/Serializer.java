package com.alisa.Iswitch.server.lib.serialization;

import java.io.Serializable;


public interface Serializer <E extends Serializable> {
	public byte[] serialize(E request) throws Exception;
	public E deserialize(byte[] bytes, Class<E> clazz) throws Exception;
	
}
