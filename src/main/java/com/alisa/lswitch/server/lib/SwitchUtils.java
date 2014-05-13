package com.alisa.lswitch.server.lib;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import com.pi4j.system.SystemInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchUtils {

  /* pi4j SystemInfo is not thread safe */
  private static Object systemInfoLock = new Object();
  private static int serialNumberBitLength = 128;
  private static Logger log = LoggerFactory.getLogger(SwitchUtils.class);

  public static UUID getSerialNumber() {
    synchronized (systemInfoLock) {
      BigInteger bigInteger;
      try {
        String serial = SystemInfo.getSerial();
        bigInteger = new BigInteger(serial.trim(), 16);
      } catch (IOException|InterruptedException|NumberFormatException e) {
        throw new RuntimeException("Failed to retrieve raspberry serial number", e);
      }

      long leastSignificantBits = bigInteger.longValue();
      long mostSignificantBits = bigInteger.shiftRight(serialNumberBitLength / 2).longValue();

      return new UUID(mostSignificantBits, leastSignificantBits);
    }
  }
}
