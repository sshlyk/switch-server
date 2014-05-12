package com.alisa.lswitch.client.model;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Base class for all com.alisa.lswitch.client.
 */
public abstract class BaseModel {

  private UUID requestId = UUID.randomUUID();
  private long timestampMsec = System.currentTimeMillis();

  public static final int SERIALIZER_VERSION = 1;
  public static final int MAX_PACKET_LENGTH = 1024;

  public BaseModel() { }

  public BaseModel(ByteBuffer serializedRequest) {
    try {
      final int requestSerializerVersion = serializedRequest.getInt();
      if (SERIALIZER_VERSION != requestSerializerVersion) {
        throw new RuntimeException("Unknown request version: " + requestSerializerVersion);
      }
      requestId = new UUID(serializedRequest.getLong(), serializedRequest.getLong());
      timestampMsec = serializedRequest.getLong();
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

  @Override
  public String toString() {
    return "BaseModel{" +
        "requestId=" + requestId +
        ", timestampMsec=" + timestampMsec +
        '}';
  }

  public byte[] serialize() {
    ByteBuffer out = ByteBuffer.wrap(new byte[4 + 3 * 8]);
    out.putInt(SERIALIZER_VERSION);
    out.putLong(requestId.getMostSignificantBits());
    out.putLong(requestId.getLeastSignificantBits());
    out.putLong(timestampMsec);
    return out.array();
  }

  /* Auto-generated */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BaseModel request = (BaseModel) o;

    if (timestampMsec != request.timestampMsec) return false;
    if (requestId != null ? !requestId.equals(request.requestId) : request.requestId != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = requestId != null ? requestId.hashCode() : 0;
    result = 31 * result + (int) (timestampMsec ^ (timestampMsec >>> 32));
    return result;
  }

  public static class SerializationException extends RuntimeException {
    public SerializationException(String message) {
      super(message);
    }
  }
}
