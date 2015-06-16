package com.alisa.lswitch.server;

import com.alisa.lswitch.server.io.SingleSwitch;
import com.alisa.lswitch.server.io.SingleSwitchMock;
import com.alisa.lswitch.server.io.SwitchController;
import com.alisa.lswitch.server.lib.AppConfig;
import com.alisa.lswitch.server.lib.SwitchUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.alisa.lswitch.server.lib.AppConfig.Flavor;

/**
 * Initialize switch and start switch.
 */
public class  Main {
  private static final Logger log = LoggerFactory.getLogger(Main.class);
  private static final String configResourceName = "/config.json";

  public static void main(String[] args) {
    log.info("Starting switch server...");
    final AppConfig config = getAppConfig(args);
    final Map<String, Map> devices = (Map<String, Map>) config.getMap("devices");
    final int port = config.getInt("port");
    final byte[] secret = (config.getString("password")).getBytes(StandardCharsets.UTF_8);
    final boolean mockSwitch = Boolean.TRUE.equals(config.getBoolean("mockDevices"));

    final Map<UUID, DeviceManager> deviceManagers = new ConcurrentHashMap<>();
    for (Map.Entry<String, Map> device: devices.entrySet()) {
      final String deviceName = device.getKey();
      final Map deviceConfig = device.getValue();
      final String deviceType = (String) deviceConfig.get("deviceType");
      final int pinNumber = (int) deviceConfig.get("switchPinNumber");
      final DeviceManager deviceManager = initSwitchManager(deviceName, deviceType, pinNumber, mockSwitch);
      deviceManagers.put(deviceManager.getStatus().getSwitchId(), deviceManager);
    }
    final NetworkClient networkClient = new NetworkClient(port, secret);
    new Thread(new StatusRequestProcessor(deviceManagers, networkClient)).start();
    new Thread(new SwitchRequestProcessor(deviceManagers, networkClient)).start();
  }

  /* Initialize switch manager that keeps track of switch status and has instance of controller */
  private static DeviceManager initSwitchManager(final String deviceName, final String deviceType,
                                                 final int pinNumber, final boolean mockSwitch) {
    final SwitchController switchController;
    final UUID switchId;
    if (mockSwitch) {
      log.debug("Using mocked switch");
      switchController = new SingleSwitchMock();
      switchId = UUID.randomUUID();
    } else {
      switchController = new SingleSwitch(pinNumber);
      switchId = UUID.randomUUID();
    }
    return new DeviceManager(switchController, switchId, deviceName, deviceType);
  }

  /* Read application configuration from resources */
  private static AppConfig getAppConfig(final String[] args) {
    final Flavor flavor;
    if (args != null && args.length > 0 && "dev".equals(args[0])) {
      flavor = Flavor.DEVELOPMENT;
    } else {
      flavor = Flavor.RELEASE;
    }
    try {
      final Map<String, Map<String, Object>> jsonConfig =
          new ObjectMapper().readValue(
              getResource(configResourceName),
              Map.class
          );
      return new AppConfig(jsonConfig, flavor);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load config resource: " + configResourceName, e);
    }
  }

  private static InputStream getResource(String resourceName) {
      return Thread.currentThread().getClass().getResourceAsStream(resourceName);
  }
}

