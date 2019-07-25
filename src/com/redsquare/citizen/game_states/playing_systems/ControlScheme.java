package com.redsquare.citizen.game_states.playing_systems;

import java.util.Map;

public class ControlScheme {

  private Map<Character, Action> controlScheme = Map.ofEntries(
          Map.entry('w', Action.UP),
          Map.entry('a', Action.LEFT),
          Map.entry('d', Action.RIGHT),
          Map.entry('s', Action.DOWN),
          Map.entry('z', Action.ZOOM)
  );

  public enum Action {
    DO_NOTHING,
    UP, LEFT, RIGHT, DOWN,
    ZOOM
  }

  private static ControlScheme instance = new ControlScheme();

  public static ControlScheme get() {
    return instance;
  }

  public Action getAction(Character pressed) {
    if (controlScheme.containsKey(pressed)) return controlScheme.get(pressed);

    return Action.DO_NOTHING;
  }
}
