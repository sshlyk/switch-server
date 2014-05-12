package com.alisa.lswitch.client;

import java.util.UUID;

/**
 * Proxy that abstracts client-server communications.
 */
public class SwitchProxy {

  private Serializer serializer;

  public SwitchProxy(Serializer serializer) {
    this.serializer = serializer;
  }

  public void requestStatusBroadcast(final Wire wire) {
    final StatusRequest request = new StatusRequest();
    setBaseRequest(request);
    wire.send(serializer.serialize(request));
  }

  public void changeSwitchStatus(SwitchRequest.Operation op) {
    final SwitchRequest request = new SwitchRequest();
    setBaseRequest(request);
    request.setOperation(op);
  }

  private void setBaseRequest(final Request request) {
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
