package com.redsquare.citizen.game_states;

import com.redsquare.citizen.GameDebug;
import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.config.WorldConfig;
import com.redsquare.citizen.entity.Entity;
import com.redsquare.citizen.entity.Person;
import com.redsquare.citizen.entity.Player;
import com.redsquare.citizen.entity.Sex;
import com.redsquare.citizen.game_states.playing_systems.Camera;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.worldgen.World;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public final class PlayingGameState extends GameState {

  private final World world;
  private final Player player;
  private final Camera camera;
  private final Set<Entity> citizens;

  private PlayingGameState() {
    int x = WorldConfig.getXDim();
    int y = (x * 9) / 16;
    world = World.safeCreate(x, y, WorldConfig.getPlateCount(), 20);
    player = Player.temp(world);
    camera = Camera.generate(player);

    citizens = new HashSet<>();
    citizens.add(player);

    Person temp = Person.create(Sex.MALE, new GameDate(1, 1), player.getBirthplace());
    citizens.add(temp);
  }

  public static PlayingGameState init() {
    GameDebug.printDebug("Initialising \"playing\" game state...");
    return new PlayingGameState();
  }

  @Override
  public void update() {
    // TODO - Macro/micro scope sorting
    for (Entity citizen : citizens) citizen.update();
    camera.update();
  }

  @Override
  public void render(Graphics2D g) {
    // TODO: filter micro-scope entity set

    camera.render(g, citizens, world);
  }

  @Override
  public void input(InputHandler inputHandler) {
    camera.input(inputHandler);
    camera.mouseToWorldLocation(inputHandler, player);
    player.input(inputHandler);
  }
}
