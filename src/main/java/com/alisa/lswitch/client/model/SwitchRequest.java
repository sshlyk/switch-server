package com.alisa.lswitch.client.model;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * BaseRequest to operate switch.
 */
public class SwitchRequest extends BaseRequest {

  public enum Operation { SET_ON, SET_OFF, BLINK }

  private Operation operation = Operation.SET_OFF;

  public SwitchRequest() {
    super();
  }

  public SwitchRequest(ByteBuffer serializedRequest) {
    super(serializedRequest);
    try {
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

  @Override
  public byte[] serialize() {
    final byte[] base = super.serialize();
    final ByteBuffer bb = ByteBuffer.wrap(new byte[base.length + 4]);
    bb.put(base);
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
