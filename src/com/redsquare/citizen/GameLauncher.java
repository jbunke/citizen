package com.redsquare.citizen;

import com.redsquare.citizen.config.Settings;
import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.debug.shell.DebuggerShell;

import java.time.LocalDateTime;

public class GameLauncher {

  public static void main(String[] args) {
    for (String arg : args) processArg(arg);

    gameConfiguration();
    new GameLauncher();
  }

  private static void processArg(String arg) {
    switch (arg) {
      case "-d":
        // DEBUGGER
        GameDebug.activate();
        Settings.debugger = new Thread(DebuggerShell::launch);
        Settings.debugger.start();
        break;
      case "-a":
        // ARCHIVE
        StringBuilder timeRepresentation = new StringBuilder("archive/");
        LocalDateTime time = LocalDateTime.now();

        timeRepresentation.append(time.getYear()).append("_");
        timeRepresentation.append(time.getMonth().getValue()).append("_");
        timeRepresentation.append(time.getDayOfMonth()).append(" ");
        timeRepresentation.append(time.getHour()).append("_");
        timeRepresentation.append(time.getMinute()).append("_");
        timeRepresentation.append(time.getSecond()).append("/");

        GameDebug.setIsArchiving(timeRepresentation.toString());
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
