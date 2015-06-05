package com.alisa.lswitch.client.model;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class StatusReply extends BaseRequest {

  private int state;
  private String deviceType;
  private String deviceName;

  public StatusReply() { }

  public StatusReply(ByteBuffer serializedRequest) {
    super(serializedRequest);
    try {
      state = serializedRequest.getInt();
      deviceType = deserializeString(serializedRequest);
      deviceName = deserializeString(serializedRequest);
    } catch (BufferUnderflowException e) {
      throw new SerializationException("Invalid request. Not all the fields are passed");
    }
  }

  @Override
  public byte[] serialize() {
    final byte[] serializedDeviceType = serializeString(deviceType);
    final byte[] serializedDeviceName = serializeString(deviceName);
    final byte[] base = super.serialize();
    ByteBuffer bb = ByteBuffer.wrap(new byte[base.length + 4 + serializedDeviceType.length + serializedDeviceName.length]);
    bb.put(base);
    bb.putInt(state);
    bb.put(serializedDeviceType);
    bb.put(serializedDeviceName);
    return bb.array();
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public String getDeviceName() { return deviceName; }

  public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

  /* Auto-generated */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    StatusReply that = (StatusReply) o;

    if (state != that.state) return false;
    if (deviceType != null ? !deviceType.equals(that.deviceType) : that.deviceType != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + state;
    result = 31 * result + (deviceType != null ? deviceType.hashCode() : 0);
    return result;
  }
}
