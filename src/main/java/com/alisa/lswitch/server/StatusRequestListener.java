package com.alisa.lswitch.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for requests and broadcasts switch status.
 */
public class StatusRequestListener implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(StatusRequestListener.class);
  private final SwitchManager switchManager;

  public StatusRequestListener(final int port, final SwitchManager switchManager) {
    log.debug("Starting status request listener. Port: {}", port);

    this.switchManager = switchManager;
  }

  @Override
  public void run() {
    while(!Thread.interrupted()) {

      //TODO just a demo. Print status.
      try {
        Thread.sleep(500);
        log.info(switchManager.getStatus().toString());
      } catch (InterruptedException e) {
        log.warn("Interrupted", e);
      }

    }
  }
}
