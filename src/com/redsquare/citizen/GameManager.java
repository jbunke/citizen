package com.redsquare.citizen;

import com.redsquare.citizen.game_states.GameState;
import com.redsquare.citizen.game_states.MenuGameState;
import com.redsquare.citizen.game_states.PlayingGameState;

import java.awt.*;

public class GameManager {

  public static class WorldMaths {
    public static final int CELLS_IN_WORLD_CELL_DIM = 384;
    public static final double CELL_DIMENSION_LENGTH = 200.;
  }

  private static GameManager instance = new GameManager();

  private GameState[] states;

  private int current;

  public static final int PLAYING = 0;
  public static final int PAUSED = 1;
  public static final int MENU = 2;

  private GameManager() {
    current = MENU;

    states = new GameState[3];
    // states[PLAYING] = PlayingGameState.init();

    states[MENU] = MenuGameState.init();
    // TODO - PAUSE, MENU, & PERHAPS MORE
  }

  public static GameManager get() {
    return instance;
  }

  static GameManager init() {
    instance = new GameManager();
    return instance;
  }

  public void initPlaying() {
    states[PLAYING] = PlayingGameState.init();
  }

  void update() {
    states[current].update();
  }

  void render(Graphics2D g) {
    states[current].render(g);
  }

  void input(InputHandler inputHandler) {
    states[current].input(inputHandler);

    inputHandler.clearUnprocessedEvents();
  }

  public GameState getGameState() {
    return states[current];
  }

  public void setGameState(int state) {
    current = state;
  }
}
