package com.alisa.lswitch.server.io;

import com.alisa.lswitch.server.exceptions.SwitchException;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
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
        RaspiPin.GPIO_08, //TODO get real pin number
        String.valueOf(switchGPIOPinNumber),
        PinState.HIGH
    );
  }

  @Override
  public void turnOn() throws SwitchException {
    pin.low();
  }

  @Override
  public void turnOff() throws SwitchException {
    pin.high();
  }
}
