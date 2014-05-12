package com.alisa.lswitch.client;

import java.nio.ByteBuffer;

/**
 * Request to broadcast status.
 */
public class StatusRequest extends Request {

  public StatusRequest() { }
  public StatusRequest(final ByteBuffer serializedRequest) {
    super(serializedRequest);
  }

}
