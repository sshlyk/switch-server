package com.alisa.lswitch.client.model;

import java.nio.ByteBuffer;

import com.alisa.lswitch.client.model.BaseModel;

/**
 * BaseModel to broadcast status.
 */
public class StatusRequest extends BaseModel {

  public StatusRequest() { }
  public StatusRequest(final ByteBuffer serializedRequest) {
    super(serializedRequest);
  }

}
