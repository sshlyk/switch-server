package com.alisa.lswitch.client.model;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class StatusReply extends BaseModel {
  private UUID deviceId;
  private boolean status;

  public StatusReply() { }

  public StatusReply(ByteBuffer serializedRequest) {
    super(serializedRequest);
    try {
      deviceId = new UUID(serializedRequest.getLong(), serializedRequest.getLong());
      status = serializedRequest.get() != 0;
    } catch (BufferUnderflowException e) {
      throw new RuntimeException("Invalid request. Not all the fields are passed");
    }
  }

  @Override
  public byte[] serialize() {
    final byte[] base = super.serialize();
    ByteBuffer bb = ByteBuffer.wrap(new byte[base.length + 16 + 1]);
    bb.put(base);
    if (deviceId == null) {
      throw new RuntimeException("Device id is missing");
    }
    bb.putLong(deviceId.getMostSignificantBits());
    bb.putLong(deviceId.getLeastSignificantBits());
    bb.put((byte) (status ? 1 : 0));
    return bb.array();
  }
}
