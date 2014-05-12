package com.alisa.lswitch.client;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

//TODO implement signature
public class Auth {

  private final byte[] secret;

  public Auth(final byte[] secret) {
    this.secret = secret;
  }

  public byte[] sign(byte[] bytesToSign) {
    //TODO
    return bytesToSign;
  }

  public byte[] verify(byte[] bytesToVerify) {
    //TODO
    return bytesToVerify;
  }
}
