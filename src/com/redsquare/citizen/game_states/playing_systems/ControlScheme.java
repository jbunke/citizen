package com.redsquare.citizen.game_states.playing_systems;

import java.util.Map;

public class ControlScheme {

  private Map<Action, Character> controlScheme = Map.ofEntries(
          Map.entry(Action.UP, 'W'),
          Map.entry(Action.LEFT, 'A'),
          Map.entry(Action.RIGHT, 'D'),
          Map.entry(Action.DOWN, 'S')
  );

  public enum Action {
    UP, LEFT, RIGHT, DOWN
  }

  private static ControlScheme instance = new ControlScheme();

  public static ControlScheme get() {
    return instance;
  }

  public boolean isAction(Action check, Character pressed) {
    return controlScheme.get(check).equals(pressed);
  }
}
