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
  public void turnOn() throws SwitchException;

  /**
   * Turn switch OFF.
   * @throws SwitchException
   */
  public void turnOff() throws SwitchException;

}
