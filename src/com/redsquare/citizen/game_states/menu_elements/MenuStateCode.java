package com.redsquare.citizen.game_states.menu_elements;

import java.awt.*;
import java.util.Set;

public enum MenuStateCode {
  MAIN, PLAY_MENU, QUIT_ARE_YOU_SURE;

  public Set<MenuElement> generateElements(MenuStateCode sender) {
    // TODO

    switch (this) {
      case MAIN:
        return Set.of(
                TextMenuElement.temp("CITIZEN",
                        new Point(640, 50)),
                TextMenuElement.temp("PLAY",
                        new Point(640, 180), PLAY_MENU),
                TextMenuElement.temp("QUIT",
                        new Point(640, 220), QUIT_ARE_YOU_SURE, this)
        );
      case PLAY_MENU:
        TextMenuElement dimensions = TextMenuElement.temp(
                SliderMenuElement.worldSizeFunction(480),
                new Point(340, 150));
        TextMenuElement plateCount = TextMenuElement.temp("40",
                new Point(340, 250));

        return Set.of(
                TextMenuElement.temp("WORLD SETTINGS",
                        new Point(640, 50)),
                TextMenuElement.temp("BACK",
                        new Point(100, 50), MAIN),
                TextMenuElement.temp("World size (cell dimensions)",
                        new Point(240, 130)),
                SliderMenuElement.generate(dimensions, 200,
                        new Point(160, 150), 320, 640, 16,
                        SliderMenuElement::worldSizeFunction),
                dimensions,
                TextMenuElement.temp("Tectonic plate count",
                        new Point(240, 230)),
                SliderMenuElement.generate(plateCount, 200,
                        new Point(160, 250), 30, 50, 1,
                        String::valueOf),
                plateCount
        );
      case QUIT_ARE_YOU_SURE:
        return Set.of(
                TextMenuElement.temp("Are you sure that you want to quit?\n" +
                        "All unsaved progress will be lost.",
                        new Point(640, 330)),
                TextMenuElement.temp("NO, RETURN",
                        new Point(320, 390), sender),
                TextMenuElement.temp("YES", new Point(960, 390))
        );
      default:
        return Set.of();
    }
  }
}
