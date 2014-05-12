package com.alisa.lswitch.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import com.alisa.lswitch.client.Auth;
import com.alisa.lswitch.client.model.StatusReply;
import com.alisa.lswitch.client.model.StatusRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for com.alisa.lswitch.client and broadcasts switch status.
 */
public class StatusRequestListener implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(StatusRequestListener.class);
  private final DeviceManager deviceManager;
  private final DatagramSocket socket;
  private final Auth auth;

  public StatusRequestListener(final int port, final DeviceManager deviceManager, final Auth auth) {
    log.debug("Starting status request listener. Port: {}", port);
    try {
      this.socket = new DatagramSocket(port);
    } catch (SocketException e) {
      throw new RuntimeException("Failed to create datagram socket on port " + port);
    }
    this.deviceManager = deviceManager;
    this.auth = auth;
  }

  @Override
  public void run() {
    final int maxPacketSize = 1024;
    final DatagramPacket packet = new DatagramPacket(new byte[maxPacketSize], maxPacketSize);

    while(!Thread.interrupted()) {
      try {
        socket.receive(packet);
        ByteBuffer bb = ByteBuffer.wrap(packet.getData());
        StatusRequest request = new StatusRequest(bb);
        if (auth.isValid(request, bb)) {
          processRequest(request, packet.getAddress(), packet.getPort());
        } else {
          log.debug("Received unauthorized request. Dropping: {}", request);
        }
      } catch (IOException e) {
        log.info("Failed to process request", e);
        //TODO
      }
    }
  }

  public void processRequest(StatusRequest request, InetAddress ip, int port)
  throws IOException {
    log.info("Replying with the status to {}:{} Status: {}",
        ip, port, deviceManager.getStatus().toString());
    DeviceManager.Status status = deviceManager.getStatus();
    final StatusReply statusReply = new StatusReply();
    statusReply.setDeviceId(status.getSwitchId());
    statusReply.setState(status.getState());
    final byte[] statusReplyBytes = statusReply.serialize();
    DatagramPacket packet = new DatagramPacket(
        statusReplyBytes,
        statusReplyBytes.length,
        ip,
        port
    );
    socket.send(packet);
  }
}
