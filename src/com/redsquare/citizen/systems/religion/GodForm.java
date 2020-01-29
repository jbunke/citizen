package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.systems.aesthetics.ColorPalettes;
import com.redsquare.citizen.util.Randoms;

import java.awt.*;

public abstract class GodForm {
  private final Color[] depictionPalette;

  GodForm(God.Attribute attribute) {
    this.depictionPalette = ColorPalettes.randomColorScheme();
  }

  static GodForm generateBeastly(God.Attribute attribute) {
    if (Randoms.random())
      return new AnimalForm(attribute);
    else
      return new ChimeraForm(attribute);
  }

  static GodForm generateAnthropomorphic(God.Attribute attribute) {
    return new AnthropomorphicForm(attribute);
  }
}
