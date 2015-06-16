package com.alisa.lswitch.server;

import com.alisa.lswitch.client.model.BaseRequest;
import com.alisa.lswitch.client.model.StatusRequest;
import com.alisa.lswitch.client.model.SwitchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class NetworkClient {
  private final BlockingQueue<RequestWrapper<BaseRequest>> replyQueue = new LinkedBlockingDeque<>(100);
  private final BlockingQueue<RequestWrapper<StatusRequest>> statusRequestQueue = new LinkedBlockingDeque<>(100);
  private final BlockingQueue<RequestWrapper<SwitchRequest>> switchRequestQueue = new LinkedBlockingDeque<>(100);
  protected final Logger log;
  private final byte[] secret;
  private final DatagramSocket socket;

  public NetworkClient(int port, byte[] secret) {
    log = LoggerFactory.getLogger(this.getClass());
    log.debug("Starting {}. Port: {}", this.getClass().getSimpleName(), port);
    this.secret = secret;
    try {
      this.socket = new DatagramSocket(port);
    } catch (SocketException e) {
      throw new RuntimeException("Failed to create socket. Port: " + port, e);
    }

    // receiver
    new Thread(new Receiver()).start();

    //sender
    new Thread(new Sender()).start();
  }

  public void send(BaseRequest reply, InetAddress inetAddress, int port) {
    replyQueue.add(new RequestWrapper<>(reply, inetAddress, port));
  }

  public RequestWrapper<SwitchRequest> nextSwitchRequest() {
    return next(switchRequestQueue);
  }

  public RequestWrapper<StatusRequest> nextStatusRequest() {
    return next(statusRequestQueue);
  }

  private <T extends BaseRequest> RequestWrapper<T> next(BlockingQueue<RequestWrapper<T>> queue) {
    try {
      return queue.take();
    } catch (InterruptedException e) {
      log.debug("Interrupted", e);
      return null;
    }
  }

  // Reply sender
  private class Sender implements Runnable {
    @Override
    public void run() {
      while(!Thread.interrupted()) {
        try {
          final RequestWrapper<BaseRequest> requestWrapper = replyQueue.take();
          send(requestWrapper.request, requestWrapper.inetAddress, requestWrapper.port);
        } catch (InterruptedException e) {
          log.debug("Sender has been interrupted", e);
          Thread.currentThread().interrupt();
        }
      }
    }

    private void send(final BaseRequest request, final InetAddress inetAddress, final int port) {
      try {
        final byte[] replyBytes = request.serialize();
        socket.send(new DatagramPacket(
            replyBytes,
            replyBytes.length,
            inetAddress,
            port
        ));
      } catch (IOException e) {
        log.warn("Failed to send reply due to IO exception", e);
      }
    }
  }

  // Requests listener
  private class Receiver implements Runnable {
    private final LinkedHashMap<UUID, Long> requestLookup = new LinkedHashMap<UUID, Long>() {
      @Override
      protected boolean removeEldestEntry(Map.Entry<UUID, Long> eldest) {
        return size() > 500;
      }
    };

    @Override
    public void run() {
      while (!Thread.currentThread().isInterrupted()) {
        receive();
      }
    }
    public void receive() {
      final int maxPacketSize = 1024;
      final DatagramPacket packet = new DatagramPacket(new byte[maxPacketSize], maxPacketSize);

      try {
        socket.receive(packet);
        final InetAddress inetAddress = packet.getAddress();
        final int port = packet.getPort();
        final ByteBuffer bb = ByteBuffer.wrap(packet.getData());
        final BaseRequest baseRequest = new BaseRequest(bb.asReadOnlyBuffer()) {
        };
        final byte requestType = baseRequest.getRequestType();

        switch (requestType) {
          case BaseRequest.STATUS_REQUEST:
            offer(statusRequestQueue, new StatusRequest(bb), inetAddress, port);
            break;
          case BaseRequest.SWITCH_REQUEST:
            offer(switchRequestQueue, new SwitchRequest(bb), inetAddress, port);
            break;
          default:
            log.debug("Unknown request type. Ignoring: " + requestType);
            break;
        }
      } catch (IOException e) {
        log.warn("Failed to process request due to IO exception", e);
      } catch (BaseRequest.SerializationException e) {
        log.debug("Failed to de-serialize request", e);
      } catch (Exception e) {
        log.warn("Failed to process request due to unknown exception", e);
      }
    }

    private <T extends BaseRequest> void offer(BlockingQueue<RequestWrapper<T>> queue,
                                               T request,
                                               InetAddress inetAddress,
                                               int port) {
      final UUID requestId = request.getRequestId();
      if (requestLookup.containsKey(requestId)) { return; }
      log.debug("Received request: {}", request);
      final boolean authorizedRequest = request.verifySignature(secret);
      if (!authorizedRequest) {
        log.info("Unauthorized request {}", request);
        return;
      }
      queue.offer(new RequestWrapper<>(request, inetAddress, port));
      requestLookup.put(requestId, System.currentTimeMillis());
    }
  }

  public static class RequestWrapper<R extends BaseRequest> {
    final R request;
    final InetAddress inetAddress;
    final int port;

    public RequestWrapper(R request, InetAddress inetAddress, int port) {
      this.request = request;
      this.inetAddress = inetAddress;
      this.port = port;
    }
  }
}
