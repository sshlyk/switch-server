package com.alisa.lswitch.server;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import com.alisa.lswitch.client.Auth;
import com.alisa.lswitch.client.model.StatusReply;
import com.alisa.lswitch.client.model.StatusRequest;

/**
 * Listens for com.alisa.lswitch.client and broadcasts switch status.
 */
public class StatusRequestListener extends BaseRequestListener<StatusRequest, StatusReply> {

  public StatusRequestListener(DeviceManager deviceManager, int port, Auth auth) {
    super(deviceManager, port, auth);
  }

  @Override
  protected StatusReply processRequest(StatusRequest request, InetAddress ip, int port) {
    final DeviceManager deviceManager = getDeviceManager();
    log.info("Replying with the status to {}:{} Status: {}",
        ip, port, deviceManager.getStatus().toString());
    return toStatusReply(deviceManager);
  }

  @Override
  protected StatusRequest deserializeRequest(ByteBuffer requestBytes) {
    return new StatusRequest(requestBytes);
  }

  public static StatusReply toStatusReply(DeviceManager deviceManager) {
    final DeviceManager.Status status = deviceManager.getStatus();
    final StatusReply statusReply = new StatusReply();
    statusReply.setDeviceId(status.getSwitchId());
    statusReply.setState(status.getState());
    statusReply.setDeviceType(deviceManager.getDeviceType());
    return statusReply;
  }
}
