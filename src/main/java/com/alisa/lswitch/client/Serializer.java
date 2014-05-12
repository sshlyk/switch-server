package com.alisa.lswitch.client;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Serializer {

  private static final int SERIALIZER_VERSION = 1;
  private static final int MAX_PACKET_LENGTH = 1024;

  private final Auth auth;

  public Serializer(final Auth auth) {
    this.auth = auth;
  }

  public byte[] serialize(final Request request) {
    try {
      final ByteBuffer bb = serializeBase(request);
      if (request instanceof SwitchRequest) {
        final SwitchRequest switchRequest = (SwitchRequest) request;
        bb.putInt(switchRequest.getOperation().ordinal());
      } else if (request instanceof  StatusRequest) {
        // has no additional fields
      } else {
        throw new SerializerException("Unknown request type " + request.getClass().getSimpleName());
      }
      return bb.array();
    } catch (Exception e) { //TODO do not catch generic exception
      throw new SerializerException("Failed to serialize switch request", e);
    }
  }

  public<T extends Request> T deserialize(final byte[] requestBytes, Class<T> klass)
  throws SerializerException {
    try {
      if (SwitchRequest.class.equals(klass)) {
        return (T) deserializeSwitchRequest(requestBytes);
      } else if (StatusRequest.class.equals(klass)) {
        return (T) deserializeStatusRequest(requestBytes);
      } else {
        throw new SerializerException("Unknown request type: " + klass);
      }
    } catch (BufferUnderflowException e) {
      throw new SerializerException("Invalid request. Not all the fields are passed.");
    }
  }

  private SwitchRequest deserializeSwitchRequest(final byte[] requestBytes)
  throws SerializerException {
    final SwitchRequest request = new SwitchRequest();
    final ByteBuffer bb = deserializeBase(requestBytes, request);
    // extract operation
    final int operationOrdinal = bb.getInt();
    final SwitchRequest.Operation[] availableOperations = SwitchRequest.Operation.values();
    if (operationOrdinal < 0 || operationOrdinal >= availableOperations.length) {
      throw new SerializerException("Invalid switch request operation: " + operationOrdinal);
    }
    request.setOperation(availableOperations[operationOrdinal]);
    return request;
  }

  private StatusRequest deserializeStatusRequest(final byte[] requestBytes)
  throws SerializerException {
    final StatusRequest request = new StatusRequest();
    final ByteBuffer bb = deserializeBase(requestBytes, request);
    return request;
  }

  private ByteBuffer serializeBase(final Request request) {
    ByteBuffer out = ByteBuffer.wrap(new byte[MAX_PACKET_LENGTH]);
    out.putInt(SERIALIZER_VERSION);
    final UUID requestId = request.getRequestId();
    out.putLong(requestId.getMostSignificantBits());
    out.putLong(requestId.getLeastSignificantBits());
    out.putLong(request.getTimestampMsec());

    final byte[] signature = auth.sign(request);
    out.putInt(signature.length);
    out.put(signature);

    return out;
  }

  private<T extends  Request> ByteBuffer deserializeBase(
      final byte[] requestBytes, final T request) {
    final ByteBuffer bb = ByteBuffer.wrap(requestBytes);
    final int requestSerializerVersion = bb.getInt();
    if (SERIALIZER_VERSION != requestSerializerVersion) {
      throw new SerializerException("Unknown request version: " + requestSerializerVersion);
    }
    request.setRequestId(new UUID(bb.getLong(), bb.getLong()));
    request.setTimestampMsec(bb.getLong());

    final int signatureLength = bb.getInt();
    final byte[] signature = new byte[signatureLength];
    bb.get(signature);

    if (!auth.verify(request, signature)) {
      throw new SerializerException("Not authorized. Invalid signature");
    }

    return bb;
  }

  public static class SerializerException extends RuntimeException {
    public SerializerException(String message) {
      super(message);
    }
    public SerializerException(String message, Throwable t) {
      super(message, t);
    }
  }
}
