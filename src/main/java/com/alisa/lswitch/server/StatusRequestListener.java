package com.alisa.lswitch.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

import com.alisa.lswitch.client.StatusRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for com.alisa.lswitch.client and broadcasts switch status.
 */
public class StatusRequestListener implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(StatusRequestListener.class);
  private final SwitchManager switchManager;
  private final DatagramSocket socket;

  public StatusRequestListener(final int port, final SwitchManager switchManager) {
    log.debug("Starting status request listener. Port: {}", port);
    try {
      this.socket = new DatagramSocket(port);
    } catch (SocketException e) {
      throw new RuntimeException("Failed to create datagram socket on port " + port);
    }
    this.switchManager = switchManager;
  }

  @Override
  public void run() {
    final int maxPacketSize = 1024;
    final DatagramPacket packet = new DatagramPacket(new byte[maxPacketSize], maxPacketSize);

    while(!Thread.interrupted()) {
      try {
        socket.receive(packet);
        StatusRequest request = new StatusRequest(ByteBuffer.wrap(packet.getData()));
        processRequest(request);
      } catch (IOException e) {
        //TODO
      }
    }
  }

  public void processRequest(StatusRequest request) {
    //TODO
    log.info(switchManager.getStatus().toString());
  }
}
