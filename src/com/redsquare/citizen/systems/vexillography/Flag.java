package com.redsquare.citizen.systems.vexillography;

import com.redsquare.citizen.systems.politics.Culture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Flag {
  private final static int DEFAULT_DRAW_HEIGHT = 30;

  private final List<FlagLayer> layers;
  private final AspectRatio aspectRatio;

  private Flag(Culture culture) {
    this.layers = generateLayers(culture);
    this.aspectRatio = AspectRatio.random();
  }

  public static Flag generate(Culture culture) {
    return new Flag(culture);
  }

  public enum AspectRatio {
    _1_2, _2_3, _3_5;

    private static AspectRatio random() {
      double r = Math.random();

      if (r < 1/2.) return _1_2;
      else if (r < 3/4.) return _2_3;
      else return _3_5;
    }

    int baseWidth() {
      switch (this) {
        case _1_2:
          return DEFAULT_DRAW_HEIGHT * 2;
        case _2_3:
          return (int)(DEFAULT_DRAW_HEIGHT * 1.5);
        case _3_5:
        default:
          return (int)(DEFAULT_DRAW_HEIGHT * (5/3.));
      }
    }
  }

  private List<FlagLayer> generateLayers(Culture culture) {
    List<FlagLayer> layers = new ArrayList<>();

    // Background layer
    FlagLayer background = FlagLayer.generate(culture);
    layers.add(background);

    // Symbol layer
    layers.add(FlagLayer.generateSymbolLayer(culture, background));

    return layers;
  }

  /** HEIGHT is 30, scale factor follows */
  public BufferedImage draw(final int SCALE) {
    int height = DEFAULT_DRAW_HEIGHT * SCALE;
    int width = aspectRatio.baseWidth() * SCALE;

    BufferedImage flag = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) flag.getGraphics();

    for (FlagLayer layer : layers) {
      layer.draw(width, height, g);
    }

    return flag;
  }
}
