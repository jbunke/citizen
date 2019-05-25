package com.redsquare.citizen.game_states.menu_elements;

import java.awt.*;
import java.util.Set;

public enum MenuStateCode {
  MAIN, PLAY_MENU, QUIT_ARE_YOU_SURE;

  public Set<MenuElement> generateElements() {
    // TODO

    switch (this) {
      case MAIN:
        return Set.of(
                TextMenuElement.temp("CITIZEN",
                        new Point(640, 50)),
                TextMenuElement.temp("PLAY",
                        new Point(640, 340), PLAY_MENU),
                TextMenuElement.temp("QUIT",
                        new Point(640, 380), QUIT_ARE_YOU_SURE, this)
        );
      case PLAY_MENU:
        return Set.of(
                TextMenuElement.temp("TEST",
                        new Point(640, 50))
        );
      default:
        return Set.of();
    }
  }
}
