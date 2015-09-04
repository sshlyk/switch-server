package com.alisa.lswitch.server;

import com.alisa.lswitch.server.exceptions.SwitchException;
import com.alisa.lswitch.server.io.SingleSwitch;
import com.alisa.lswitch.server.io.SingleSwitchMock;
import com.alisa.lswitch.server.io.SwitchController;
import com.alisa.lswitch.server.lib.AppConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.alisa.lswitch.server.lib.AppConfig.Flavor;

/**
 * Initialize switch and start switch.
 */
public class  Main {
  private static final Logger log = LoggerFactory.getLogger(Main.class);

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
      final Integer pulseDelay = (Integer) deviceConfig.get("buttonDelay");
      final DeviceManager deviceManager = initSwitchManager(deviceName, deviceType, pinNumber,
          pulseDelay != null && pulseDelay > 0 ? pulseDelay : 100, mockSwitch);
      deviceManagers.put(deviceManager.getStatus().getSwitchId(), deviceManager);
    }
    final NetworkClient networkClient = new NetworkClient(port, secret);
    new Thread(new StatusRequestProcessor(deviceManagers, networkClient)).start();
    new Thread(new SwitchRequestProcessor(deviceManagers, networkClient)).start();
  }

  /* Initialize switch manager that keeps track of switch status and has instance of controller */
  private static DeviceManager initSwitchManager(final String deviceName, final String deviceType,
                                                 final int pinNumber, final int pulseDelay,
                                                 final boolean mockSwitch) {
    final SwitchController switchController;
    final UUID switchId;
    if (mockSwitch) {
      log.debug("Using mocked switch");
      switchController = new SingleSwitchMock();
      switchId = UUID.randomUUID();
    } else {
      switchController = new SingleSwitch(pinNumber, pulseDelay);
      switchId = UUID.randomUUID();
    }
    return new DeviceManager(switchController, switchId, deviceName, deviceType);
  }

  /* Read application configuration from resources */
  private static AppConfig getAppConfig(final String[] args) {
    final Map<String, String> argsMap = parseArgs(args);
    final Flavor flavor = "dev".equals(argsMap.get("flavor")) ? Flavor.DEVELOPMENT : Flavor.RELEASE;
    final String configFilePath = argsMap.get("config");
    try {
      final Map<String, Map<String, Object>> jsonConfig =
          new ObjectMapper().readValue(
              getResource(configFilePath),
              Map.class
          );
      return new AppConfig(jsonConfig, flavor);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load config resource", e);
    }
  }

  private static InputStream getResource(String configPath) {
    if (configPath == null || configPath.isEmpty()) {
      log.info("You did provide path to a config json file (--config /path/to/config.json) using default.");
      return Thread.currentThread().getClass().getResourceAsStream("/switch-config.json");
    } else {
      try {
        final InputStream externalConfig = new FileInputStream(configPath);
        log.info("Found configuration file: " + configPath);
        return externalConfig;
      } catch (FileNotFoundException e) {
        throw new RuntimeException("Could not find config file: " + configPath);
      }
    }
  }

  private static Map<String, String> parseArgs(String[] args) {
    final Map<String, String> argsMap = new HashMap<>();
    if (args == null) {
      log.info("No arguments provided");
      return argsMap;
    }
    if (args.length % 2 != 0) {
      throw new RuntimeException("Invalid list of arguments. Expected format: \"--key value\"");
    }
    for (int i = 0; i < args.length; i += 2) {
      final String key = args[i];
      final String value = args[i+1];
      if (!key.startsWith("--") && key.length() < 3) {
        log.error("Invalid key: " + key + ". Value: " + value);
      }
      argsMap.put(key.substring(2), value);
    }
    return argsMap;
  }
}

