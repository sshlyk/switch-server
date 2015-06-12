package com.alisa.lswitch.server;

import com.alisa.lswitch.client.model.SwitchRequest;
import com.alisa.lswitch.server.io.SwitchController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SwitchRequestProcessor implements Runnable {

  private final NetworkClient networkClient;
  private Map<UUID, DeviceManager> deviceManagers;
  protected final Logger log;
  protected ExecutorService executor;

  public SwitchRequestProcessor(final Map<UUID, DeviceManager> deviceManagers, final NetworkClient networkClient) {
    this.networkClient = networkClient;
    this.log = LoggerFactory.getLogger(this.getClass());
    this.deviceManagers = deviceManagers;
    this.executor = Executors.newFixedThreadPool(2);
  }

  @Override
  public void run() {
    log.debug(this.getClass().getSimpleName() + " processing incoming switch requests");
    while (!Thread.currentThread().isInterrupted()) {
      final NetworkClient.RequestWrapper<SwitchRequest> requestWrapper = networkClient.nextSwitchRequest();
      final SwitchRequest request = requestWrapper.request;
      final UUID deviceId = request.getDeviceId();
      if (deviceId == null) { continue; }
      final DeviceManager deviceManager = deviceManagers.get(deviceId);
      final SwitchRequest.Operation operation = request.getOperation();
      if (deviceManager != null && operation != null) {
        executor.submit(new Runnable() {
          @Override
          public void run() {
            operateDevice(deviceManager, operation);
          }
        });
      }
    }
  }

  private void operateDevice(DeviceManager deviceManager, SwitchRequest.Operation operation) {
    final SwitchController controller = deviceManager.getController();
    switch(operation) {
      case SET_ON:
        controller.turnOn();
        break;
      case SET_OFF:
        controller.turnOff();
        break;
      case PULSE:
        controller.pulse();
        break;
      default:
        log.debug("Unknown operation: {}", operation);
        break;
    }
  }
}
