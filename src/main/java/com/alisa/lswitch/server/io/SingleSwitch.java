package com.alisa.lswitch.server.io;

import com.alisa.lswitch.server.exceptions.SwitchException;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Switch that controls RaspberryPi GIPO.
 * http://pi4j.com/example/control.html
 */
public class SingleSwitch implements SwitchController {

  private static final Logger log = LoggerFactory.getLogger(SingleSwitch.class);
  private final GpioController gpio;
  private final GpioPinDigitalOutput pin;

  public SingleSwitch(int switchGPIOPinNumber) {
    log.debug("Raspberry switch. PinNumber: {}", switchGPIOPinNumber);
    this.gpio = GpioFactory.getInstance();
    this.pin = gpio.provisionDigitalOutputPin(
        toPin(switchGPIOPinNumber),
        String.valueOf(switchGPIOPinNumber),
        PinState.HIGH
    );
  }

  @Override
  public void turnOn() {
    pin.low();
  }

  @Override
  public void turnOff() {
    pin.high();
  }

  @Override
  public void blink() {
    pin.blink(100);
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
