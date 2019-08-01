package com.redsquare.citizen;

import com.redsquare.citizen.config.Settings;

public class GameLauncher {

  public static void main(String[] args) {
    gameConfiguration();
    new GameLauncher();
  }

  private static void gameConfiguration() {
    Settings.executionMode = Settings.ExecutionMode.GAME;
  }

  private GameLauncher() {
    new Window();
  }

}
