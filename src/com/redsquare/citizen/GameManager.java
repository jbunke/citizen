package com.redsquare.citizen;

import com.redsquare.citizen.game_states.GameState;
import com.redsquare.citizen.game_states.PlayingGameState;

import java.awt.*;

public class GameManager {

  private GameState[] states;

  private int current;

  private static final int PLAYING = 0;
  private static final int PAUSED = 1;
  private static final int MENU = 2;

  private GameManager() {
    current = PLAYING;

    states = new GameState[3];
    states[0] = PlayingGameState.init();
    // TODO - PAUSE, MENU, & PERHAPS MORE
  }

  public static GameManager init() {
    return new GameManager();
  }

  void update() {

  }

  void render(Graphics2D g) {

  }

  void input() {

  }

  public GameState getGameState() {
    return states[current];
  }
}
