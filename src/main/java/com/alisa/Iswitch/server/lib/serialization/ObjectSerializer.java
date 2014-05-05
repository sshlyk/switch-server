package com.alisa.Iswitch.server.lib.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectSerializer<E extends Serializable> implements Serializer<E> {
	
	@Override
	public byte[] serialize(E request) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
		oos.writeObject(request);
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public E deserialize(byte[] bytes, Class<E> clazz) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream is = new ObjectInputStream(in);
		return (E) is.readObject();
	}

}
