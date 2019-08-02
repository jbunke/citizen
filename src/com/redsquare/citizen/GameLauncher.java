package com.redsquare.citizen;

import com.redsquare.citizen.config.Settings;
import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.debug.shell.DebuggerShell;

public class GameLauncher {

  private static Thread debugger = new Thread(DebuggerShell::launch);

  public static void main(String[] args) {
    for (String arg : args) processArg(arg);

    gameConfiguration();
    new GameLauncher();
  }

  private static void processArg(String arg) {
    switch (arg) {
      case "-d":
        GameDebug.activate();
        debugger.start();
        break;
      default:
        break;
    }
  }

  private static void gameConfiguration() {
    Settings.executionMode = Settings.ExecutionMode.GAME;
  }

  private GameLauncher() {
    new Window();
  }

}
