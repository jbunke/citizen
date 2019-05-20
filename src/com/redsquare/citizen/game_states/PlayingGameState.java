package com.redsquare.citizen.game_states;

import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.entity.Player;
import com.redsquare.citizen.worldgen.World;

import java.awt.*;

public final class PlayingGameState extends GameState {

  private final World world;
  private final Player player;

  private PlayingGameState() {
    world = World.safeCreate(World.DEFAULT_WIDTH,
            World.DEFAULT_HEIGHT, 30, 20);
    player = Player.temp();
  }

  public static PlayingGameState init() {
    return new PlayingGameState();
  }

  @Override
  public void update() {

  }

  @Override
  public void render(Graphics2D g) {

  }

  @Override
  public void input(InputHandler inputHandler) {

  }
}
