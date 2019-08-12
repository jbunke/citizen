package com.redsquare.citizen.game_states.playing_systems;

import java.util.Map;

public class ControlScheme {

  private static final char ESC_KEY = 27;

  private static final char W_KEY = 'w';
  private static final char A_KEY = 'a';
  private static final char D_KEY = 'd';
  private static final char S_KEY = 's';
  private static final char Z_KEY = 'z';
  private static final char X_KEY = 'x';

  private Map<Character, Action> controlScheme = Map.ofEntries(
          Map.entry(W_KEY, Action.UP),
          Map.entry(A_KEY, Action.LEFT),
          Map.entry(D_KEY, Action.RIGHT),
          Map.entry(S_KEY, Action.DOWN),
          Map.entry(Z_KEY, Action.ZOOM),
          Map.entry(ESC_KEY, Action.PAUSE),
          Map.entry(X_KEY, Action.TOGGLE_SPRINT)
  );

  public enum Action {
    DO_NOTHING,
    UP, LEFT, RIGHT, DOWN,
    TOGGLE_SPRINT,
    ZOOM,
    PAUSE
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
