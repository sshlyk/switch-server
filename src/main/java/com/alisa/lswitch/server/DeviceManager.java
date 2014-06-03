package com.alisa.lswitch.server;

import java.util.UUID;

import com.alisa.lswitch.server.exceptions.SwitchException;
import com.alisa.lswitch.server.io.SwitchController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages switch info and status. Thread safe.
 */
public class DeviceManager {

  private final String deviceType;
  private final Status status;
  private final SwitchController controller;
  private static Logger log = LoggerFactory.getLogger(DeviceManager.class);

  public DeviceManager(SwitchController switchController, UUID switchId, String deviceType) {
    log.debug("Device ID: " + switchId);

    this.status = new Status();
    this.controller = switchController;
    this.status.switchId = switchId;
    this.deviceType = deviceType;
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

  public String getDeviceType() {
    return deviceType;
  }

  /** Holds current status of the switch. */
  /* Make sure it is immutable. */
  public class Status {

    private UUID switchId;
    private int state;

    private Status() { }
    public UUID getSwitchId() { return switchId; }

    public int getState() {
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
        status.state = 1;
      }
    }

    @Override
    public void turnOff() throws SwitchException {
      synchronized (controller) {
        controller.turnOff();
        status.state = 0;
      }
    }

    @Override
    public void blink() {
      controller.blink();
    }
  }
}
