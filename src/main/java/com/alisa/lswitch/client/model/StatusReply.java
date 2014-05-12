package com.alisa.lswitch.client.model;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class StatusReply extends BaseModel {
  private UUID deviceId;
  private int state;

  public StatusReply() { }

  public StatusReply(ByteBuffer serializedRequest) {
    super(serializedRequest);
    try {
      deviceId = new UUID(serializedRequest.getLong(), serializedRequest.getLong());
      state = serializedRequest.getInt();
    } catch (BufferUnderflowException e) {
      throw new SerializationException("Invalid request. Not all the fields are passed");
    }
  }

  @Override
  public byte[] serialize() {
    final byte[] base = super.serialize();
    ByteBuffer bb = ByteBuffer.wrap(new byte[base.length + 16 + 4]);
    bb.put(base);
    if (deviceId == null) {
      throw new SerializationException("Device id is missing");
    }
    bb.putLong(deviceId.getMostSignificantBits());
    bb.putLong(deviceId.getLeastSignificantBits());
    bb.putInt(state);
    return bb.array();
  }

  public UUID getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(UUID deviceId) {
    this.deviceId = deviceId;
  }

  public int getState() {
    return state;
  }

  public void setState(int state) {
    this.state = state;
  }

  /* Auto-generated */

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    StatusReply that = (StatusReply) o;

    if (state != that.state) return false;
    if (deviceId != null ? !deviceId.equals(that.deviceId) : that.deviceId != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
    result = 31 * result + state;
    return result;
  }
}
