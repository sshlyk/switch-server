package com.alisa.lswitch.server;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import com.alisa.lswitch.client.Auth;
import com.alisa.lswitch.client.model.StatusReply;
import com.alisa.lswitch.client.model.SwitchRequest;
import com.alisa.lswitch.server.io.SwitchController;

/**
 * Listens for request to operate switch and performs action.
 */
public class SwitchRequestListener extends BaseRequestListener<SwitchRequest, StatusReply> {

  public SwitchRequestListener(DeviceManager deviceManager, int port, Auth auth) {
    super(deviceManager, port, auth);
  }

  @Override
  protected StatusReply processRequest(SwitchRequest request, InetAddress ip, int port) {
    if (!getDeviceManager().getStatus().getSwitchId().equals(request.getDeviceId())) {
      log.debug("Ignoring request since device id is different");
      return null;
    }

    final SwitchRequest.Operation op = request.getOperation();
    final SwitchController controller = getDeviceManager().getController();
    if (op == null) { return null; }
    switch(op) {
      case SET_ON:
        controller.turnOn();
        break;
      case SET_OFF:
        controller.turnOff();
        break;
      case BLINK:
        controller.blink();
        break;
      default:
        log.debug("Unknown operation: {}", op);
        break;
    }

    return StatusRequestListener.toStatusReply(getDeviceManager());
  }

  @Override
  protected SwitchRequest deserializeRequest(ByteBuffer requestBytes) {
    return new SwitchRequest(requestBytes);
  }
}
