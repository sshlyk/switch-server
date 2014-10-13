package com.alisa.lswitch.client.model;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Base class for all com.alisa.lswitch.client.
 */
public abstract class BaseRequest {

  private UUID requestId = UUID.randomUUID();
  private long timestampMsec = System.currentTimeMillis();
  private UUID deviceId = new UUID(0, 0);
  private byte[] sha1 = new byte[20];

  public static final int SERIALIZER_VERSION = 1;

  public BaseRequest() { }

  public BaseRequest(ByteBuffer serializedRequest) {
    try {
      final int requestSerializerVersion = serializedRequest.getInt();
      if (SERIALIZER_VERSION != requestSerializerVersion) {
        throw new SerializationException(
            "Unknown request format version: " + requestSerializerVersion);
      }
      requestId = new UUID(serializedRequest.getLong(), serializedRequest.getLong());
      timestampMsec = serializedRequest.getLong();
      deviceId = new UUID(serializedRequest.getLong(), serializedRequest.getLong());
      serializedRequest.get(sha1);
    } catch (BufferUnderflowException e) {
      throw new SerializationException("Invalid request. Not all the fields are passed");
    }
  }

  public UUID getRequestId() {
    return requestId;
  }

  public void setRequestId(UUID requestId) {
    if (requestId == null) { return; }
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
    if (deviceId == null) { return; }
    this.deviceId = deviceId;
  }

  public void sign(final byte[] secret) {
    if (secret == null) { return; }
    sha1 = calculateSha1(secret);
  }

  public boolean verifySignature(final byte[] secret) {
    if (secret == null) { return false; }
    return Arrays.equals(sha1, calculateSha1(secret));
  }

  private byte[] calculateSha1(final byte[] secret) {
    final MessageDigest mDigest;
    try {
      mDigest = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Could not initialize SHA1 digest", e);
    }
    final ByteBuffer bb = ByteBuffer.wrap(new byte[16 + 16 + 8 + secret.length]);
    bb.putLong(requestId.getMostSignificantBits());
    bb.putLong(requestId.getLeastSignificantBits());
    bb.putLong(deviceId.getMostSignificantBits());
    bb.putLong(deviceId.getLeastSignificantBits());
    bb.putLong(timestampMsec);
    bb.put(secret);
    return mDigest.digest(bb.array());
  }

  public byte[] serialize() {
    ByteBuffer out = ByteBuffer.wrap(new byte[4 + 16 + 8 + 16 + sha1.length]);
    out.putInt(SERIALIZER_VERSION);
    out.putLong(requestId.getMostSignificantBits());
    out.putLong(requestId.getLeastSignificantBits());
    out.putLong(timestampMsec);
    out.putLong(deviceId.getMostSignificantBits());
    out.putLong(deviceId.getLeastSignificantBits());
    out.put(sha1);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BaseRequest request = (BaseRequest) o;

    if (timestampMsec != request.timestampMsec) return false;
    if (deviceId != null ? !deviceId.equals(request.deviceId) : request.deviceId != null)
      return false;
    if (requestId != null ? !requestId.equals(request.requestId) : request.requestId != null)
      return false;
    if (!Arrays.equals(sha1, request.sha1)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = requestId != null ? requestId.hashCode() : 0;
    result = 31 * result + (int) (timestampMsec ^ (timestampMsec >>> 32));
    result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
    result = 31 * result + (sha1 != null ? Arrays.hashCode(sha1) : 0);
    return result;
  }

  @Override
  public String toString() {
    return "requestId=" + requestId +
        ", timestampMsec=" + timestampMsec +
        ", deviceId=" + deviceId +
        ", sha1=" + Arrays.toString(sha1);
  }

}
