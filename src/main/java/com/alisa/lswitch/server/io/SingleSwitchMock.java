package com.alisa.lswitch.server.io;

import com.alisa.lswitch.server.exceptions.SwitchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Switch controller that can be used for development purposes and does not interact with GPIO.
 */
public class SingleSwitchMock implements SwitchController {

  private static final Logger log = LoggerFactory.getLogger(SingleSwitchMock.class);

  @Override
  public void turnOn() throws SwitchException {
    log.info("Switch is ON");
  }

  @Override
  public void turnOff() throws SwitchException {
    log.info("Switch is OFF");
  }

  @Override
  public void pulse() {
    log.info("Pulse");
  }
}
