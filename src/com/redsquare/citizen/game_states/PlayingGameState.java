package com.redsquare.citizen.game_states;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.GameManager;
import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.config.WorldConfig;
import com.redsquare.citizen.entity.Entity;
import com.redsquare.citizen.entity.Person;
import com.redsquare.citizen.entity.Player;
import com.redsquare.citizen.entity.Sex;
import com.redsquare.citizen.entity.collision.CollisionManager;
import com.redsquare.citizen.game_states.playing_systems.Camera;
import com.redsquare.citizen.game_states.playing_systems.ControlScheme;
import com.redsquare.citizen.input_events.Event;
import com.redsquare.citizen.input_events.KeyPressEvent;
import com.redsquare.citizen.systems.time.GameDate;
import com.redsquare.citizen.worldgen.World;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
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

    for (int i = 0; i < 10; i++) {
      Person temp = Person.create(Sex.MALE, new GameDate(1, 1), player.getBirthplace(), world);
      citizens.add(temp);
    }
  }

  public static PlayingGameState init() {
    GameDebug.printMessage("Initialising \"playing\" game state...",
            GameDebug::printDebug);
    return new PlayingGameState();
  }

  @Override
  public void update() {
    // TODO - Macro/micro scope sorting
    for (Entity citizen : citizens) citizen.update();
    camera.update();

    for (Entity a : citizens) {
      for (Entity b : citizens) {
        if (!a.equals(b)) {
          CollisionManager.check(a, b);
        }
      }
    }
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

    List<Event> events = inputHandler.getUnprocessedEvents();
    for (int i = 0; i < events.size(); i++) {
      boolean processed = false;

      if (events.get(i) instanceof KeyPressEvent) {
        KeyPressEvent kpe = (KeyPressEvent) events.get(i);

        if (kpe.eventType == KeyPressEvent.EventType.RELEASED &&
                ControlScheme.get().getAction(kpe.key) == ControlScheme.Action.PAUSE) {
          /* PAUSE
           * Reset direction keys to avoid unpause glitch where player
           * is still moving because keys were never unpressed */
          player.resetDirectionKeys();
          // Actual pause
          GameManager.get().setGameState(GameManager.PAUSED);
          processed = true;
        }
      }

      if (processed) {
        events.remove(i);
        i--;
      }
    }
  }
}
