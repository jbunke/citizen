package com.redsquare.citizen.game_states.playing_systems;

import java.util.Map;

public class ControlScheme {

  private static final char ESC_KEY = 27;

  // TODO: find ASCII for new keystrokes: drop item, drop stack

  private static final char W_KEY = 'w';
  private static final char A_KEY = 'a';
  private static final char D_KEY = 'd';
  private static final char S_KEY = 's';
  private static final char Z_KEY = 'z';
  private static final char X_KEY = 'x';
  private static final char O_KEY = 'o';
  private static final char P_KEY = 'p';

  private static final char _1_KEY = '1';
  private static final char _2_KEY = '2';
  private static final char _3_KEY = '3';
  private static final char _4_KEY = '4';
  private static final char _5_KEY = '5';

  private Map<Character, Action> controlScheme = Map.ofEntries(
          Map.entry(W_KEY, Action.UP),
          Map.entry(A_KEY, Action.LEFT),
          Map.entry(D_KEY, Action.RIGHT),
          Map.entry(S_KEY, Action.DOWN),
          Map.entry(Z_KEY, Action.ZOOM),
          Map.entry(ESC_KEY, Action.PAUSE),
          Map.entry(X_KEY, Action.TOGGLE_SPRINT),
          Map.entry(_1_KEY, Action.SELECT_SLOT_1),
          Map.entry(_2_KEY, Action.SELECT_SLOT_2),
          Map.entry(_3_KEY, Action.SELECT_SLOT_3),
          Map.entry(_4_KEY, Action.SELECT_SLOT_4),
          Map.entry(_5_KEY, Action.SELECT_SLOT_5),
          Map.entry(O_KEY, Action.DROP_SINGLE_ITEM),
          Map.entry(P_KEY, Action.DROP_ITEM_STACK)
  );

  public enum Action {
    DO_NOTHING,
    UP, LEFT, RIGHT, DOWN,
    TOGGLE_SPRINT,
    ZOOM,
    PAUSE,
    DROP_SINGLE_ITEM,
    DROP_ITEM_STACK,
    SELECT_SLOT_1,
    SELECT_SLOT_2,
    SELECT_SLOT_3,
    SELECT_SLOT_4,
    SELECT_SLOT_5
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
