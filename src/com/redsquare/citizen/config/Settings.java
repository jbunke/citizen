package com.redsquare.citizen.config;

public class Settings {
  public static int[] SCREEN_DIM = new int[] {1280, 720 };
  public static ExecutionMode executionMode = ExecutionMode.TEST;

  // Dedicated threads
  public static Thread secondary = null;
  public static Thread debugger = null;

  public enum ExecutionMode {
    GAME, TEST
  }
}
