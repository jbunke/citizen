package com.redsquare.citizen.game_states;

import com.redsquare.citizen.GameDebug;
import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.config.WorldConfig;
import com.redsquare.citizen.entity.Player;
import com.redsquare.citizen.worldgen.World;

import java.awt.*;

public final class PlayingGameState extends GameState {

  private final World world;
  private final Player player;

  private PlayingGameState() {
    int x = WorldConfig.getXDim();
    int y = (x * 9) / 16;
    world = World.safeCreate(x, y, WorldConfig.getPlateCount(), 20);
    player = Player.temp(world);
  }

  public static PlayingGameState init() {
    GameDebug.printDebug("Initialising \"playing\" game state...");
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
