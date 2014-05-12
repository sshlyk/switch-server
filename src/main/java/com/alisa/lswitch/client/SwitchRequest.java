package com.alisa.lswitch.client;

/**
 * Request to operate switch.
 */
public class SwitchRequest extends Request {

  public enum Operation { SET_ON, SET_OFF }

  private Operation operation;

  public Operation getOperation() {
    return operation;
  }

  public void setOperation(Operation operation) {
    this.operation = operation;
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
