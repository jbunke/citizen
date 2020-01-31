package com.redsquare.citizen.game_states;

import com.redsquare.citizen.config.Settings;
import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.GameManager;
import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.config.WorldConfig;
import com.redsquare.citizen.entity.Player;
import com.redsquare.citizen.game_states.playing_systems.Camera;
import com.redsquare.citizen.game_states.playing_systems.ControlScheme;
import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.input_events.Event;
import com.redsquare.citizen.input_events.KeyPressEvent;
import com.redsquare.citizen.item.Item;
import com.redsquare.citizen.worldgen.World;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public final class PlayingGameState extends GameState {

  private final World world;
  private final Player player;
  private final Camera camera;

  // DEBUG
  private Point worldPosition = new Point(-1, -1);
  private BufferedImage miniMap = null;

  private PlayingGameState() {
    int x = WorldConfig.getXDim();
    int y = (x * 9) / 16;
    world = World.safeCreate(x, y, WorldConfig.getPlateCount(), 5);
    world.getWorldManager().startOfGameSimulation(WorldConfig.getSimulationYears());
    this.player = world.getWorldManager().getPlayer();
    camera = Camera.generate(player);

    if (GameDebug.isArchiving())
      GameDebug.prepArchiving(player, world);
  }

  public static PlayingGameState init() {
    GameDebug.printMessage("Initialising \"PLAYING\" game state...",
            GameDebug::printDebug);
    return new PlayingGameState();
  }

  @Override
  public void update() {
    // TODO - Macro/micro scope sorting
    world.getWorldManager().update();

    camera.update();
  }

  @Override
  public void render(Graphics2D g) {
    // TODO: filter micro-scope entity set

    camera.render(g, world);

    // HUD
    // Selected slot
    g.setColor(new Color(0, 0, 0));
    int selectedSlot = player.getSelectedInventorySlot();
    g.fillRect((Settings.SCREEN_DIM[0] / 2) + 50 + (selectedSlot * Item.ICON_DIMENSION), 78, 64, 5);

    // Items
    Item[] inventory = player.getInventory().getContents();
    for (int i = 0; i < player.getInventory().getContents().length; i++) {
      if (inventory[i] == null)
        continue;
      g.drawImage(inventory[i].getItemIcon(), (Settings.SCREEN_DIM[0] / 2) +
              50 + (i * Item.ICON_DIMENSION), 10, null);
    }
    // END HUD

    // DEBUG
    if (GameDebug.isActive()) {
      Point worldPosition = player.position().world();

      if (!this.worldPosition.equals(worldPosition) || miniMap == null) {
        this.worldPosition = worldPosition;
        miniMap = world.worldMiniMap(worldPosition);
      }

      g.drawImage(miniMap, Settings.SCREEN_DIM[0] -
              (miniMap.getWidth() + 10), 10, null);

      BufferedImage animState = Font.CLEAN.getText(player.getSpriteCode());
      BufferedImage position = Font.CLEAN.getText(player.position().toString());
      g.drawImage(animState, Settings.SCREEN_DIM[0] - animState.getWidth(),
              Settings.SCREEN_DIM[1] - animState.getHeight(), null);
      g.drawImage(position, Settings.SCREEN_DIM[0] - position.getWidth(),
              Settings.SCREEN_DIM[1] -
                      (animState.getHeight() + position.getHeight() + 10),
              null);
    }
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
