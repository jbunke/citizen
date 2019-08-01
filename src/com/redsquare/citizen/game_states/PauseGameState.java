package com.redsquare.citizen.game_states;

import com.redsquare.citizen.GameManager;
import com.redsquare.citizen.InputHandler;
import com.redsquare.citizen.config.Settings;
import com.redsquare.citizen.game_states.playing_systems.ControlScheme;
import com.redsquare.citizen.graphics.Font;
import com.redsquare.citizen.input_events.Event;
import com.redsquare.citizen.input_events.KeyPressEvent;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public final class PauseGameState extends GameState {

  private PauseGameState() {}

  public static PauseGameState init() {
    return new PauseGameState();
  }

  @Override
  public void update() {

  }

  @Override
  public void render(Graphics2D g) {
    // TODO - temp
    BufferedImage paused = Font.CLEAN.getText("PAUSED");

    int[] renderPoint = new int[] {
            (Settings.SCREEN_DIM[0] / 2) - (paused.getWidth() / 2),
            (Settings.SCREEN_DIM[1] / 2) - (paused.getHeight() / 2) };
    g.drawImage(paused, renderPoint[0], renderPoint[1], null);
  }

  @Override
  public void input(InputHandler inputHandler) {
    List<Event> events = inputHandler.getUnprocessedEvents();
    for (int i = 0; i < events.size(); i++) {
      boolean processed = false;

      if (events.get(i) instanceof KeyPressEvent) {
        KeyPressEvent kpe = (KeyPressEvent) events.get(i);

        if (kpe.eventType == KeyPressEvent.EventType.RELEASED &&
                ControlScheme.get().getAction(kpe.key) == ControlScheme.Action.PAUSE) {
          /* PAUSE / RESUME key is the same; RESUME event */
          GameManager.get().setGameState(GameManager.PLAYING);
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