package com.alisa.lswitch.client.model;

import java.nio.ByteBuffer;

/**
 * BaseRequest to broadcast status.
 */
public class StatusRequest extends BaseRequest {

  public StatusRequest() { }
  public StatusRequest(final ByteBuffer serializedRequest) {
    super(serializedRequest);
  }
}
