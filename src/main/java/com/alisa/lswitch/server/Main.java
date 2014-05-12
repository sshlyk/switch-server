package com.alisa.lswitch.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import com.alisa.lswitch.client.Auth;
import com.alisa.lswitch.server.io.MockSwitch;
import com.alisa.lswitch.server.io.RaspberrySwitch;
import com.alisa.lswitch.server.io.SwitchController;
import com.alisa.lswitch.server.lib.AppConfig;
import com.alisa.lswitch.server.lib.SwitchUtils;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    final DeviceManager deviceManager = initSwitchManager(config);
    final Auth auth = new Auth(
        config.getString("defaultPassword").getBytes(StandardCharsets.UTF_8)
    );

    final StatusRequestListener statusRequestListener =
        initStatusRequestListener(config, deviceManager, auth);
    final SwitchRequestListener switchRequestListener =
        initSwitchRequestListener(config, deviceManager, auth);

    new Thread(statusRequestListener) {{
      setDaemon(true);
    }}.start();
    new Thread(switchRequestListener).start();
    log.info("Server is running.");
  }

  /* Initialize status request listener that broadcast switch status */
  private static StatusRequestListener initStatusRequestListener(
      final AppConfig appConfig, final DeviceManager deviceManager, final Auth auth) {
    final int port = appConfig.getInt("statusListenerPort");
    return new StatusRequestListener(port, deviceManager, auth);
  }

  /* Initialize switch request listener that operates GPIO pins */
  private static SwitchRequestListener initSwitchRequestListener(
      final AppConfig appConfig, final DeviceManager deviceManagers, final Auth auth) {
    final int port = appConfig.getInt("switchListenerPort");
    final SwitchController controller = deviceManagers.getController();
    return new SwitchRequestListener(controller, port, auth);
  }

  /* Initialize switch manager that keeps track of switch status and has instance of controller */
  private static DeviceManager initSwitchManager(AppConfig appConfig) {
    final SwitchController switchController;
    final UUID switchId;
    if (Boolean.TRUE.equals(appConfig.getBoolean("mockSwitch"))) {
      log.debug("Using mocked switch");
      switchController = new MockSwitch();
      switchId = UUID.randomUUID();
    } else {
      final int pinNumber = appConfig.getInt("switchPinNumber");
      switchController = new RaspberrySwitch(pinNumber);
      switchId = SwitchUtils.getSerialNumber();
    }
    return new DeviceManager(switchController, switchId);
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

