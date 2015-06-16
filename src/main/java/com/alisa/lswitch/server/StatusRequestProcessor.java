package com.alisa.lswitch.server;

import com.alisa.lswitch.client.model.StatusReply;
import com.alisa.lswitch.client.model.StatusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

public class StatusRequestProcessor implements Runnable {

  private final NetworkClient networkClient;
  private Map<UUID, DeviceManager> deviceManagers;
  protected final Logger log;

  public StatusRequestProcessor(final Map<UUID, DeviceManager> deviceManagers, final NetworkClient networkClient) {
    this.networkClient = networkClient;
    this.log = LoggerFactory.getLogger(this.getClass());
    this.deviceManagers = deviceManagers;
  }

  @Override
  public void run() {
    log.debug(this.getClass().getSimpleName() + " processing incoming status requests");
    while (!Thread.currentThread().isInterrupted()) {
      final NetworkClient.RequestWrapper<StatusRequest> requestWrapper = networkClient.nextStatusRequest();
      final StatusRequest statusRequest = requestWrapper.request; // at this point no addition info needed
      final InetAddress address = requestWrapper.inetAddress;
      final int port = requestWrapper.port;
      for (DeviceManager deviceManager: deviceManagers.values()) {
        log.info("Replying with the status to {}:{} Status: {}",
            address, port, deviceManager.getStatus().toString());
        sendStatus(deviceManager, address, port);
      }
    }
  }

  public void sendStatus(DeviceManager deviceManager, InetAddress address, int port) {
    final DeviceManager.Status status = deviceManager.getStatus();
    final StatusReply statusReply = new StatusReply();
    statusReply.setDeviceId(status.getSwitchId());
    statusReply.setState(status.getState());
    statusReply.setDeviceType(deviceManager.getDeviceType());
    statusReply.setDeviceName(deviceManager.getDeviceName());
    networkClient.send(statusReply, address, port);
  }
}
