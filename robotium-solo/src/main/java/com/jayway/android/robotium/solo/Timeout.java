package com.jayway.android.robotium.solo;

import java.lang.reflect.Method;

class Timeout {

  public static final int SMALLTIMEOUT;
  public static final int TIMEOUT;
  public static final int MINISLEEP;

  static {
    SMALLTIMEOUT = parseTimeout("SOLO_SMALLTIMEOUT", 10000);
    TIMEOUT = parseTimeout("SOLO_TIMEOUT", 20000);
    MINISLEEP = parseTimeout("SOLO_MINISLEEP", 50);
  }

  private static int parseTimeout(String property, int defaultValue) {
    try {
      Class clazz = Class.forName("android.os.SystemProperties");
      Method method = clazz.getDeclaredMethod("get", String.class);
      String value = (String) method.invoke(null, property);
      return Integer.parseInt(value);
    } catch (Exception e) {
      return defaultValue;
    }
  }

}
