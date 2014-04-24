package com.alisa.lswitch.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for requests and broadcasts switch status.
 */
public class StatusRequestListener implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(StatusRequestListener.class);

  public StatusRequestListener(final int port) {
    log.debug("Starting status request listener. Port: {}", port);
  }

  @Override
  public void run() {
    while(!Thread.interrupted()) {
      // TODO
    }
  }
}
