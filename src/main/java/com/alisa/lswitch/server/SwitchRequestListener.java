package com.alisa.lswitch.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.UUID;

import com.alisa.lswitch.client.Auth;
import com.alisa.lswitch.client.model.BaseModel;
import com.alisa.lswitch.client.model.StatusReply;
import com.alisa.lswitch.client.model.SwitchRequest;
import com.alisa.lswitch.server.io.SwitchController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for request to operate switch and performs action.
 */
public class SwitchRequestListener implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(SwitchRequestListener.class);
  private final DeviceManager deviceManager;
  private final Auth auth;
  private final DatagramSocket socket;
  private final UUID deviceId;

  public SwitchRequestListener(final DeviceManager deviceManager,
                               final int port, final Auth auth) {
    log.debug("Starting switch request listener. Port: {}", port);
    this.deviceManager = deviceManager;
    this.auth = auth;
    try {
      this.socket = new DatagramSocket(port);
    } catch (SocketException e) {
      throw new RuntimeException("Failed to create socket. Port: " + port, e);
    }
    this.deviceId = deviceManager.getStatus().getSwitchId();
  }

  @Override
  public void run() {
    final int maxPacketSize = 1024;
    final DatagramPacket packet = new DatagramPacket(new byte[maxPacketSize], maxPacketSize);

    while(!Thread.interrupted()) {
      try {
        socket.receive(packet);
        ByteBuffer bb = ByteBuffer.wrap(packet.getData());
        SwitchRequest request = new SwitchRequest(bb);
        if (deviceId.equals(request.getDeviceId()) && auth.isValid(request, bb)) {
          process(request, packet.getAddress(), packet.getPort());
        } else {
          log.info("Unauthorized switch request {}", request);
        }
      } catch (IOException e) {
        log.warn("Failed to process request due to IO exception", e);
      } catch (BaseModel.SerializationException e) {
        log.debug("Failed to de-serialize request");
      }
    }
  }

  private void process(final SwitchRequest request, final InetAddress ip, final int port)
  throws IOException {
    final SwitchRequest.Operation op = request.getOperation();
    final SwitchController controller = deviceManager.getController();
    if (op == null) { return; }
    switch(op) {
    case SET_ON:
      controller.turnOn();
      break;
    case SET_OFF:
      controller.turnOff();
      break;
    default:
      log.debug("Unknown operation: {}", op);
      break;
    }

    final StatusReply statusReply = StatusRequestListener.toStatusReply(deviceManager.getStatus());
    final byte[] statusReplyBytes = statusReply.serialize();
    socket.send(new DatagramPacket(
        statusReplyBytes,
        statusReplyBytes.length,
        ip,
        port
    ));
  }
}
