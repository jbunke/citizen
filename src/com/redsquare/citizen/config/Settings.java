package com.redsquare.citizen.config;

public class Settings {
  public static int[] SCREEN_DIM = new int[] { 1280, 720 };
  public static ExecutionMode executionMode = ExecutionMode.TEST;

  // Dedicated threads
  /**
   * The SECONDARY thread is the main thread for delegation concurrent tasks
   * */
  public static Thread secondary = null;

  /**
   * The DEBUGGER thread is the dedicated thread for
   * processing runtime debugging commands
   * */
  public static Thread debugger = null;

  /**
   * The ARCHIVE thread is the dedicated thread for archiving
   * runtime information for each execution
   * */
  public static Thread archive = null;

  public enum ExecutionMode {
    GAME, TEST
  }
}
