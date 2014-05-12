package com.alisa.lswitch.server;

import com.alisa.lswitch.client.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for com.alisa.lswitch.client and broadcasts switch status.
 */
public class StatusRequestListener implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(StatusRequestListener.class);
  private final SwitchManager switchManager;
  private final Serializer serializer;

  public StatusRequestListener(final int port, final SwitchManager switchManager,
                               final Serializer serializer) {
    log.debug("Starting status request listener. Port: {}", port);

    this.switchManager = switchManager;
    this.serializer = serializer;
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
