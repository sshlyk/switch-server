package com.alisa.lswitch.client;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.alisa.lswitch.client.model.StatusRequest;
import com.alisa.lswitch.client.model.BaseModel;
import com.alisa.lswitch.client.model.SwitchRequest;

/**
 * Proxy that abstracts client-server communications.
 */
public class SwitchProxy {

  private final Auth auth;

  public SwitchProxy(Auth auth) {
    this.auth = auth;
  }

  public void requestStatusBroadcast(final Wire wire) {
    final StatusRequest request = new StatusRequest();
    setBaseRequest(request);
    final byte[] signature = auth.sign(request);
    final byte[] serializedRequest = request.serialize();
    ByteBuffer packet = ByteBuffer.wrap(new byte[signature.length + serializedRequest.length]);
    packet.put(serializedRequest);
    packet.put(signature);
    wire.send(packet.array());
  }

  public void changeSwitchStatus(SwitchRequest.Operation op) {
    final SwitchRequest request = new SwitchRequest();
    setBaseRequest(request);
    request.setOperation(op);
  }

  private void setBaseRequest(final BaseModel request) {
    request.setRequestId(UUID.randomUUID());
    request.setTimestampMsec(System.currentTimeMillis());
  }

  public static interface Wire {
    void send(byte[] request) throws WireException;
  }

  public static class WireException extends RuntimeException {
    public WireException(String message, Throwable t) {
      super(message, t);
    }
  }
}
