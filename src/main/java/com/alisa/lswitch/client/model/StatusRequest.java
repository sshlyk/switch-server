package com.alisa.lswitch.client.model;

import java.nio.ByteBuffer;

/**
 * BaseRequest to broadcast status.
 */
public class StatusRequest extends BaseRequest {

  public StatusRequest() { super(STATUS_REQUEST); }
  public StatusRequest(final ByteBuffer serializedRequest) {
    super(serializedRequest);
  }
}
