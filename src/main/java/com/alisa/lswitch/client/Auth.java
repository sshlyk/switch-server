package com.alisa.lswitch.client;

import java.nio.ByteBuffer;

import com.alisa.lswitch.client.model.BaseRequest;

//TODO implement signature
public class Auth {

  private final byte[] secret;

  public Auth(final byte[] secret) {
    this.secret = secret;
  }

  public byte[] sign(final BaseRequest request) {
    //TODO
    return new byte[1];
  }

  public boolean isValid(final BaseRequest request, ByteBuffer signature) {
    //TODO
    return true;
  }
}
