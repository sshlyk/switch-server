package com.alisa.lswitch.server;

import java.util.UUID;

import com.alisa.lswitch.server.exceptions.SwitchException;
import com.alisa.lswitch.server.io.SwitchController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages switch info and status. Thread safe.
 */
public class SwitchManager {

  private final Status status;
  private final SwitchController controller;
  private static Logger log = LoggerFactory.getLogger(SwitchManager.class);

  public SwitchManager(SwitchController switchController, UUID switchId) {
    log.debug("Device ID: " + switchId);

    this.status = new Status();
    this.controller = switchController;
    this.status.switchId = switchId;
  }

  /* returns snapshot of current status */
  public Status getStatus() {
    synchronized (controller) {
      return status;
    }
  }

  public SwitchController getController() {
    return new SwitchControllerWrapper();
  }

  /** Holds current status of the switch. */
  /* Make sure it is immutable. */
  public class Status {

    private UUID switchId;
    private boolean state;

    private Status() { }
    public UUID getSwitchId() { return switchId; }

    public boolean isOn() {
      synchronized (controller) {
        return state;
      }
    }

    @Override
    /* Auto-generated. Please, update if new variables are introduced */
    public String toString() {
      return "Status{" +
          "switchId=" + switchId +
          ", state=" + state +
          '}';
    }
  }

  /**
   * Wrapper for switch controller that automatically refreshes switch status.
   */
  private class SwitchControllerWrapper implements SwitchController {
    @Override
    public void turnOn() throws SwitchException {
      synchronized (controller) {
        controller.turnOn();
        status.state = true;
      }
    }

    @Override
    public void turnOff() throws SwitchException {
      synchronized (controller) {
        controller.turnOff();
        status.state = false;
      }
    }
  }
}
