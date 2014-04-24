package com.alisa.lswitch.server.io;

import com.alisa.lswitch.server.exceptions.SwitchException;

public interface Switch {

  public void turnOn() throws SwitchException;
  public void turnOff() throws SwitchException;

}
