package com.alisa.lswitch.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.alisa.lswitch.server.io.MockSwitch;
import com.alisa.lswitch.server.io.RaspberrySwitch;
import com.alisa.lswitch.server.io.SwitchController;
import com.alisa.lswitch.server.lib.AppConfig;

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

    final StatusRequestListener statusRequestListener = initStatusRequestListener(config);
    final SwitchRequestListener switchRequestListener = initSwitchRequestListener(config);

    new Thread(statusRequestListener).start();
    new Thread(switchRequestListener).start();
  }

  /* Initialize status request listener that broadcast switch status */
  private static StatusRequestListener initStatusRequestListener(AppConfig appConfig) {
    final int port = appConfig.getInt("statusListenerPort");
    return new StatusRequestListener(port);
  }

  /* Initialize switch request listener that operates GPIO pins */
  private static SwitchRequestListener initSwitchRequestListener(AppConfig appConfig) {
    final int port = appConfig.getInt("switchListenerPort");
    final SwitchController switchController;
    if (Boolean.TRUE.equals(appConfig.getBoolean("mockSwitch"))) {
      log.debug("Using mocked switch");
      switchController = new MockSwitch();
    } else {
      final int pinNumber = appConfig.getInt("switchPinNumber");
      switchController = new RaspberrySwitch(pinNumber);
    }
    return new SwitchRequestListener(switchController, port);
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

