package com.alisa.lswitch.server.io;

import com.alisa.lswitch.server.exceptions.SwitchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Switch that controls RaspberryPi GIPO.
 * http://pi4j.com/example/control.html
 */
public class RaspberrySwitch implements SwitchController {

  private static final Logger log = LoggerFactory.getLogger(RaspberrySwitch.class);

  public RaspberrySwitch(int switchGPIOPinNumber) {
    log.debug("Raspberry switch. PinNumber: {}", switchGPIOPinNumber);
  }

  @Override
  public void turnOn() throws SwitchException {
    //TODO
  }

  @Override
  public void turnOff() throws SwitchException {
    //TODO
  }
}
