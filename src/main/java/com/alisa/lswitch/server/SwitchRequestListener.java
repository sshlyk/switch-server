package com.alisa.lswitch.server;

import com.alisa.lswitch.client.Auth;
import com.alisa.lswitch.server.io.SwitchController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for request to operate switch and performs action.
 */
public class SwitchRequestListener implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(SwitchRequestListener.class);
  private final SwitchController switchController;
  private final Auth auth;

  public SwitchRequestListener(final SwitchController switchController,
                               final int port, final Auth auth) {
    log.debug("Starting switch request listener. Port: {}", port);
    this.switchController = switchController;
    this.auth = auth;
  }

  @Override
  public void run() {
    while(!Thread.interrupted()) {

      //TODO just a demo
      try {
        switchController.turnOn();
        Thread.sleep(2000);
        switchController.turnOff();
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.warn("Interrupted", e); //while loop takes care of interruptions
      }

    }
  }
}
