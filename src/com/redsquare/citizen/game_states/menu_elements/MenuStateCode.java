package com.redsquare.citizen.game_states.menu_elements;

import com.redsquare.citizen.systems.politics.Race;

import java.awt.*;
import java.util.Set;

public enum MenuStateCode {
  MAIN, WORLD_CONFIG, QUIT_ARE_YOU_SURE, QUIT,
  PLAYER_CONFIG;

  public Set<MenuElement> generateElements(MenuStateCode sender) {
    // TODO

    switch (this) {
      case MAIN:
        return Set.of(
                TextMenuElement.temp("CITIZEN",
                        new Point(640, 50)),
                TextMenuElement.temp("PLAY",
                        new Point(640, 180), WORLD_CONFIG),
                TextMenuElement.temp("QUIT",
                        new Point(640, 220), QUIT_ARE_YOU_SURE, this)
        );
      case WORLD_CONFIG:
        TextMenuElement dimensions = TextMenuElement.temp(
                SliderMenuElement.worldSizeFunction(480),
                new Point(340, 150));
        TextMenuElement plateCount = TextMenuElement.temp("40",
                new Point(340, 250));

        return Set.of(
                TextMenuElement.temp("WORLD CONFIGURATION",
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
                        SliderMenuElement::plateCountFunction),
                plateCount,

                TextMenuElement.temp("ADVANCE",
                        new Point(1180, 670), PLAYER_CONFIG)
        );
      case PLAYER_CONFIG:
        TextMenuElement skinColor = TextMenuElement.temp(
                SliderMenuElement.skinColorFunction(25),
                new Point(340, 150));

        return Set.of(
                TextMenuElement.temp("PLAYER CONFIGURATION",
                        new Point(640, 50)),
                TextMenuElement.temp("BACK",
                        new Point(100, 50), WORLD_CONFIG),
                TextMenuElement.temp("Skin colour",
                        new Point(240, 130)),
                ColorSliderMenuElement.generate(skinColor, 200,
                        new Point(160, 150), 1, 50, 1,
                        SliderMenuElement::skinColorFunction,
                        Race.DARKEST, Race.LIGHTEST),
                skinColor,

                TextMenuElement.temp("RANDOM",
                        new Point(1180, 50), MAIN),
                TextMenuElement.temp("ADVANCE",
                        new Point(1180, 670), MAIN)
        );
      case QUIT_ARE_YOU_SURE:
        return Set.of(
                TextMenuElement.temp("Are you sure that you want to quit?\n" +
                        "All unsaved progress will be lost.",
                        new Point(640, 330)),
                TextMenuElement.temp("NO, RETURN",
                        new Point(320, 390), sender),
                TextMenuElement.temp("YES", new Point(960, 390), QUIT)
        );
      case QUIT:
        return Set.of(
                BehavioralMenuElement.generate(
                        BehavioralMenuElement.Behaviour.QUIT, new Object[0])
        );
      default:
        return Set.of();
    }
  }
}
