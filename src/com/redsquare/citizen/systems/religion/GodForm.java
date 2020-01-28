package com.redsquare.citizen.systems.religion;

import com.redsquare.citizen.systems.aesthetics.ColorPalettes;
import com.redsquare.citizen.util.Randoms;

import java.awt.*;

public abstract class GodForm {
  private final Color[] depictionPalette;

  GodForm(God.Attribute attribute) {
    this.depictionPalette = ColorPalettes.randomColorScheme();
  }

  static GodForm generate(God.Attribute attribute) {
    if (Randoms.random())
      return new BasicForm(attribute);
    else
      return new ChimeraForm(attribute);
  }
}
