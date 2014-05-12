package com.alisa.lswitch.client;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

import com.alisa.lswitch.client.model.BaseModel;

//TODO implement signature
public class Auth {

  private final byte[] secret;

  public Auth(final byte[] secret) {
    this.secret = secret;
  }

  public byte[] sign(final BaseModel request) {
    //TODO
    return new byte[1];
  }

  public boolean isValid(final BaseModel request, ByteBuffer signature) {
    //TODO
    return true;
  }
}
