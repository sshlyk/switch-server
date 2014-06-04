package com.alisa.lswitch.client.model;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Base class for all com.alisa.lswitch.client.
 */
public abstract class BaseRequest {

  private UUID requestId = UUID.randomUUID();
  private long timestampMsec = System.currentTimeMillis();
  private UUID deviceId;

  public static final int SERIALIZER_VERSION = 1;

  public BaseRequest() { }

  public BaseRequest(ByteBuffer serializedRequest) {
    try {
      final int requestSerializerVersion = serializedRequest.getInt();
      if (SERIALIZER_VERSION != requestSerializerVersion) {
        throw new SerializationException(
            "Unknown serialization version: " + requestSerializerVersion);
      }
      requestId = new UUID(serializedRequest.getLong(), serializedRequest.getLong());
      timestampMsec = serializedRequest.getLong();
      deviceId = new UUID(serializedRequest.getLong(), serializedRequest.getLong());
    } catch (BufferUnderflowException e) {
      throw new SerializationException("Invalid request. Not all the fields are passed");
    }
  }

  public UUID getRequestId() {
    return requestId;
  }

  public void setRequestId(UUID requestId) {
    this.requestId = requestId;
  }

  public long getTimestampMsec() {
    return timestampMsec;
  }

  public void setTimestampMsec(long timestampMsec) {
    this.timestampMsec = timestampMsec;
  }

  public UUID getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(UUID deviceId) {
    this.deviceId = deviceId;
  }

  public byte[] serialize() {
    ByteBuffer out = ByteBuffer.wrap(new byte[4 + 3 * 8 + 16]);
    out.putInt(SERIALIZER_VERSION);
    out.putLong(requestId.getMostSignificantBits());
    out.putLong(requestId.getLeastSignificantBits());
    out.putLong(timestampMsec);
    if (deviceId != null) {
      out.putLong(deviceId.getMostSignificantBits());
      out.putLong(deviceId.getLeastSignificantBits());
    } else {
      out.putLong(0);
      out.putLong(0);
    }
    return out.array();
  }

  public static class SerializationException extends RuntimeException {
    public SerializationException(String message) {
      super(message);
    }
  }

  protected static byte[] serializeString(final String str) {
    final byte[] stringBytes;
    try {
      stringBytes = str != null ? str.getBytes("UTF-8") : new byte[0];
    } catch (UnsupportedEncodingException e) {
      throw new SerializationException(
          "Failed to serialize encoded string. UTF-8 encoding is not supported");
    }
    if (stringBytes.length > 256) {
      throw new SerializationException(
          "Can not serialize string since it exceeds max length 256. String: " + str);
    }
    final byte[] result = new byte[1 + stringBytes.length];
    result[0] = (byte) stringBytes.length;
    System.arraycopy(stringBytes, 0, result, 1, stringBytes.length);
    return result;
  }

  protected String deserializeString(final ByteBuffer bb) {
    final int length = bb.get();
    if (length == 0) return null;
    final byte[] stringBytes = new byte[length];
    bb.get(stringBytes);
    try {
      return new String(stringBytes, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new SerializationException(
          "Failed to deserialize encoded string. UTF-8 encoding is not supported");
    }
  }


  /* Auto-generated */
  @Override
  public String toString() {
    return "{" +
        "requestId=" + requestId +
        ", timestampMsec=" + timestampMsec +
        ", deviceId=" + deviceId +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BaseRequest baseModel = (BaseRequest) o;

    if (timestampMsec != baseModel.timestampMsec) return false;
    if (deviceId != null ? !deviceId.equals(baseModel.deviceId) : baseModel.deviceId != null)
      return false;
    if (requestId != null ? !requestId.equals(baseModel.requestId) : baseModel.requestId != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = requestId != null ? requestId.hashCode() : 0;
    result = 31 * result + (int) (timestampMsec ^ (timestampMsec >>> 32));
    result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
    return result;
  }
}
