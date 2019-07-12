package com.redsquare.citizen.systems.language;

import com.redsquare.citizen.util.Sets;

import java.util.Set;
import java.util.function.BiFunction;

public class Fonts {
  static BiFunction<Double, Double, Double> randomXFunc() {
    return Sets.randomEntry(
            Set.of(Fonts::fontIdentityX, Fonts::fontItalicX,
                    Fonts::fontStretchX, Fonts::fontCompressX,
                    Fonts::fontItalicX2, Fonts::fontBulbX)
    );
  }

  static BiFunction<Double, Double, Double> randomYFunc() {
    return Sets.randomEntry(
            Set.of(Fonts::fontIdentityY, Fonts::fontStretchY,
                    Fonts::fontCompressY)
    );
  }

  static double fontBulbX(double x, double y) {
    double margin = (1d - (0.8 * Math.pow(Math.abs(y - 0.5) * 2.5, 1.5)));

    x = (x * margin) + ((1d - margin) / 2d);

    return x;
  }

  static double fontStretchX(double x, double y) {
    return (x * 1.2) - 0.1;
  }

  static double fontCompressX(double x, double y) {
    return (x * 0.8) + 0.1;
  }

  static double fontItalicX(double x, double y) {
    double newX = x * 0.75;
    newX += (1 - y) * 0.25;

    return newX;
  }

  static double fontItalicX2(double x, double y) {
    double newX = x * 0.75;
    newX += y * 0.25;

    return newX;
  }

  static double fontStretchY(double x, double y) {
    return (y * 1.15) - 0.075;
  }

  static double fontCompressY(double x, double y) {
    return (y * 0.85) + 0.075;
  }

  static double fontIdentityX(double x, double y) {
    return x;
  }

  static double fontIdentityY(double x, double y) {
    return y;
  }
}
