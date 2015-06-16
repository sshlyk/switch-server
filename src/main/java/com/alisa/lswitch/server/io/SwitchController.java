package com.alisa.lswitch.server.io;

import com.alisa.lswitch.server.exceptions.SwitchException;

/**
 * Interface for all switch operations.
 */
public interface SwitchController {

  /**
   * Turn switch ON.
   * @throws SwitchException
   */
  public void turnOn();

  /**
   * Turn switch OFF.
   * @throws SwitchException
   */
  public void turnOff();

  /**
   * Pulse the switch.
   */
  public void pulse();
}
