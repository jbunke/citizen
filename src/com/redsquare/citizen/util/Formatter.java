package com.redsquare.citizen.util;

public class Formatter {
  public static String properNoun(String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }
}
