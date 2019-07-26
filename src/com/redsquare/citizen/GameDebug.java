package com.redsquare.citizen;

import java.util.function.Consumer;

public class GameDebug {
  private static boolean active = true;

  public static boolean isActive() {
    return active;
  }

  public static void activate() {
    active = true;
  }

  public static void deactivate() {
    active = false;
  }

  public static void printMessage(String message,
                                  Consumer<String> function) {
    if (active)
      function.accept(message);
  }

  public static void printError(String message) {
    System.out.println("CITIZEN ERROR: [" + message + "]");
  }

  public static void printDebug(String message) {
    System.out.println("CITIZEN DEBUGGER: [" + message + "]");
  }
}
