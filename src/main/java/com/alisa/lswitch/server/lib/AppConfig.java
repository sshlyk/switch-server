package com.alisa.lswitch.server.lib;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {

  private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

  private final Map<String, Object> config;
  private final Flavor flavor;

  public AppConfig(final Map<String, Map<String, Object>> jsonConfig, final Flavor flavor) {
    final HashMap<String, Object> combinedConfig = new HashMap<String, Object>();
    combinedConfig.putAll(jsonConfig.get("common"));
    final Map<String, Object> flavorConfig;
    switch(flavor) {
      case DEVELOPMENT:
        flavorConfig = jsonConfig.get("development");
        break;
      case RELEASE:
        flavorConfig = jsonConfig.get("release");
        break;
      default:
        throw new RuntimeException("Unknown flavor " + flavor);
    }
    if (flavorConfig != null) {
      combinedConfig.putAll(flavorConfig);
    }

    this.config = Collections.unmodifiableMap(combinedConfig);
    this.flavor = flavor;
    log.debug("App config: {}", config);
  }

  public static enum Flavor {
    DEVELOPMENT, RELEASE
  }

  private <T> T getVal(String key, Class<T> klass) {
    Object val = config.get(key);
    if (val == null) { return null; }
    if (klass.isInstance(val)) {
      try {
        return (T) val;
      } catch (ClassCastException e) {
        log.debug("Failed to read key {}", key, e);
        return null;
      }
    } else {
      log.debug(
          "Failed to read config key: {}. Expecting type {}, got {}",
          key,
          klass,
          val.getClass());
      return null;
    }
  }

  public Boolean getBoolean(String key) {
    return getVal(key, Boolean.class);
  }

  public Integer getInt(String key) {
    return getVal(key, Integer.class);
  }

  public String getString(String key) {
    return getVal(key, String.class);
  }

  public Map getMap(String key) {
    return getVal(key, Map.class);
  }

  public List getList(String key) {
    return getVal(key, List.class);
  }

  public Flavor getFlavor() {
    return this.flavor;
  }
}
