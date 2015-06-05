package com.alisa.lswitch.server.io;

import com.pi4j.io.gpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Switch that controls RaspberryPi GIPO.
 * http://pi4j.com/example/control.html
 */
public class SingleSwitch implements SwitchController {

  private static final Logger log = LoggerFactory.getLogger(SingleSwitch.class);
  private final GpioPinDigitalOutput pin;

  public SingleSwitch(int switchGPIOPinNumber) {
    log.debug("Raspberry switch. PinNumber: {}", switchGPIOPinNumber);
    this.pin = GpioFactory.getInstance().provisionDigitalOutputPin(
        toPin(switchGPIOPinNumber),
        String.valueOf(switchGPIOPinNumber),
        PinState.HIGH // misleading, but this keeps relay open (off)
    );
    this.pin.setShutdownOptions(true, PinState.HIGH);
  }

  @Override
  public void turnOn() {
    pin.low(); //initial state is HIGH == off
  }

  @Override
  public void turnOff() {
    pin.high(); //initial state is HIGH == off
  }

  @Override
  public void pulse() {
    turnOn();
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      log.debug("Pulse operation has been interrupted", e);
    }
    turnOff();
  }

  private Pin toPin(int value) {
    switch(value) {
    case 0: return RaspiPin.GPIO_00;
    case 1: return RaspiPin.GPIO_01;
    case 2: return RaspiPin.GPIO_02;
    case 3: return RaspiPin.GPIO_03;
    case 4: return RaspiPin.GPIO_04;
    case 5: return RaspiPin.GPIO_05;
    case 6: return RaspiPin.GPIO_06;
    case 7: return RaspiPin.GPIO_07;
    case 8: return RaspiPin.GPIO_08;
    case 9: return RaspiPin.GPIO_09;
    default:
      throw new RuntimeException("Invalid pin number: " + value);
    }
  }
}
