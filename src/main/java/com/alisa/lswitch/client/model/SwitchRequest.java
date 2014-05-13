package com.alisa.lswitch.client.model;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * BaseModel to operate switch.
 */
public class SwitchRequest extends BaseModel {

  public UUID deviceId;
  public enum Operation { SET_ON, SET_OFF }

  private Operation operation = Operation.SET_OFF;

  public SwitchRequest() {
    super();
  }

  public SwitchRequest(ByteBuffer serializedRequest) {
    super(serializedRequest);
    try {
      deviceId = new UUID(serializedRequest.getLong(), serializedRequest.getLong());
      // extract operation
      final int operationOrdinal = serializedRequest.getInt();
      final SwitchRequest.Operation[] availableOperations = SwitchRequest.Operation.values();
      if (operationOrdinal < 0 || operationOrdinal >= availableOperations.length) {
        throw new SerializationException("Invalid switch request operation: " + operationOrdinal);
      }
      operation = availableOperations[operationOrdinal];
    } catch (BufferUnderflowException e) {
      throw new SerializationException("Invalid request. Not all the fields are passed");
    }
  }


  public Operation getOperation() {
    return operation;
  }

  public void setOperation(Operation operation) {
    this.operation = operation;
  }

  public UUID getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(UUID deviceId) {
    this.deviceId = deviceId;
  }

  @Override
  public byte[] serialize() {
    final byte[] base = super.serialize();
    final ByteBuffer bb = ByteBuffer.wrap(new byte[base.length + 16 + 4]);
    bb.put(base);
    if (deviceId == null) {
      throw new SerializationException("Device id is missing");
    }
    bb.putLong(deviceId.getMostSignificantBits());
    bb.putLong(deviceId.getLeastSignificantBits());
    bb.putInt(operation.ordinal());
    return bb.array();
  }

  @Override
  public String toString() {
    return "SwitchRequest{" +
        "operation=" + operation +
        ' ' + super.toString() + '}';
  }

  /* Auto-generated */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    SwitchRequest request = (SwitchRequest) o;

    if (operation != request.operation) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (operation != null ? operation.hashCode() : 0);
    return result;
  }
}
