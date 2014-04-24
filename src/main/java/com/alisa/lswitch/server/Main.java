package com.alisa.lswitch.server;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class  Main {

  public static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    //final GpioController gpio = GpioFactory.getInstance();
    log.info("Hello world");
  }


}

