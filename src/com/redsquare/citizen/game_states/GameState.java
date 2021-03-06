package com.redsquare.citizen.game_states;

import com.redsquare.citizen.InputHandler;

import java.awt.*;

public abstract class GameState {

  public abstract void update();

  public abstract void render(Graphics2D g);

  public abstract void input(InputHandler inputHandler);
}
