package com.redsquare.citizen.game_states.menu_elements;

import com.redsquare.citizen.systems.politics.Race;

import java.awt.*;
import java.util.Set;

public enum MenuStateCode {
  MAIN, WORLD_CONFIG, QUIT_ARE_YOU_SURE, QUIT,
  PLAYER_CONFIG,
  START_GAME,
  GENERATING_WORLD, PHYSICAL_GEOGRAPHY, STATES_SETTLEMENTS, WORLD_GENERATED;

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
                SliderMenuElement.worldSizeFunction(400),
                new Point(340, 150));
        TextMenuElement plateCount = TextMenuElement.temp("35",
                new Point(340, 250));

        return Set.of(
                TextMenuElement.temp("WORLD CONFIGURATION",
                        new Point(640, 50)),
                TextMenuElement.temp("BACK",
                        new Point(100, 50), MAIN),
                TextMenuElement.temp("World size (cell dimensions)",
                        new Point(240, 130)),
                SliderMenuElement.generate(dimensions, 200,
                        new Point(160, 150), 160, 640, 16,
                        SliderMenuElement::worldSizeFunction),
                dimensions,
                TextMenuElement.temp("Tectonic plate count",
                        new Point(240, 230)),
                SliderMenuElement.generate(plateCount, 200,
                        new Point(160, 250), 20, 50, 1,
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

                // TODO: temp
                TextMenuElement.temp("RANDOM",
                        new Point(1180, 50), START_GAME),
                TextMenuElement.temp("ADVANCE",
                        new Point(1180, 670), START_GAME)
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
      case START_GAME:
        return Set.of(
                BehavioralMenuElement.generate(
                        BehavioralMenuElement.Behaviour.START_GAME,
                        new Object[0])
        );
      case GENERATING_WORLD:
        String base = "Generating world";
        return Set.of(
                AnimatedTextMenuElement.temp(new String[] {
                        base + ".", base + "..", base + "..."
                        }, 15,
                        new Point(640, 360))
        );
      case PHYSICAL_GEOGRAPHY:
        base = "Adding physical geography";
        return Set.of(
                AnimatedTextMenuElement.temp(new String[] {
                                base + ".", base + "..", base + "..."
                        }, 15,
                        new Point(640, 360))
        );
      case STATES_SETTLEMENTS:
        base = "Adding states and settlements";
        return Set.of(
                AnimatedTextMenuElement.temp(new String[] {
                                base + ".", base + "..", base + "..."
                        }, 15,
                        new Point(640, 360))
        );
      case WORLD_GENERATED:
        base = "World generated";
        return Set.of(
                AnimatedTextMenuElement.temp(new String[] {
                                base + ".", base + "..", base + "..."
                        }, 15,
                        new Point(640, 360))
        );
      default:
        return Set.of();
    }
  }
}
