package com.alisa.lswitch.client;

//TODO implement signature
public class Auth {

  private final byte[] secret;

  public Auth(final byte[] secret) {
    this.secret = secret;
  }

  public byte[] sign(Request request) {
    //TODO
    return new byte[1];
  }

  public boolean verify(Request request, byte[] signature) {
    //TODO
    return true;
  }
}
