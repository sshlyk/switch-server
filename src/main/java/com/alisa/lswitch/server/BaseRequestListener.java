package com.alisa.lswitch.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.alisa.lswitch.client.Auth;
import com.alisa.lswitch.client.model.BaseRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for request to operate switch and performs action.
 */
public abstract class BaseRequestListener<I extends BaseRequest, O extends BaseRequest> implements Runnable {

  protected final Logger log;
  private final DeviceManager deviceManager;
  private final Auth auth;
  private final DatagramSocket socket;
  private final UUID deviceId;

  private final LinkedHashMap<UUID, Long> requestLookup = new LinkedHashMap<UUID, Long>() {
    @Override
    protected boolean removeEldestEntry(Map.Entry<UUID, Long> eldest) {
      return size() > 500;
    }
  };

  public BaseRequestListener(final DeviceManager deviceManager,
                             final int port, final Auth auth) {
    log = LoggerFactory.getLogger(this.getClass());
    log.debug("Starting {}. Port: {}", this.getClass().getSimpleName(), port);
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
        I request = deserializeRequest(bb);
        if (requestLookup.containsKey(request.getRequestId())
            || !deviceId.equals(request.getDeviceId())) { continue; }

        O reply = null;
        if (auth.isValid(request, bb)) {
          reply = processRequest(request, packet.getAddress(), packet.getPort());
        } else {
          log.info("Unauthorized request {}", request);
        }
        if (reply != null) {
          final byte[] replyBytes = reply.serialize();
          getSocket().send(new DatagramPacket(
              replyBytes,
              replyBytes.length,
              packet.getAddress(),
              packet.getPort()
          ));
        }
        requestLookup.put(request.getRequestId(), System.currentTimeMillis());
      } catch (IOException e) {
        log.warn("Failed to process request due to IO exception", e);
      } catch (BaseRequest.SerializationException e) {
        log.debug("Failed to de-serialize request");
      } catch (Exception e) {
        log.warn("Failed to process request due to unknown exception", e);
      }
    }
  }

  protected DeviceManager getDeviceManager() {
    return deviceManager;
  }

  protected DatagramSocket getSocket() {
    return socket;
  }

  protected abstract O processRequest(final I request, final InetAddress ip, final int port);

  protected abstract I deserializeRequest(final ByteBuffer requestBytes);
}
