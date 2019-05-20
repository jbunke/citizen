package com.redsquare.citizen.systems.politics;

import com.redsquare.citizen.util.Randoms;

import java.awt.*;

public class Race {

  private static final Color DARKEST = new Color(51, 19, 0);
  private static final Color LIGHTEST = new Color(206, 158, 107);

  private static final Color PLAT_BLONDE = new Color(234, 213, 136);
  private static final Color BLONDE = new Color(219, 170, 81);
  private static final Color GINGE = new Color(225, 120, 43);
  private static final Color LIGHT_BROWN = new Color(137, 78, 31);
  private static final Color BROWN = new Color(81, 40, 0);
  private static final Color AFRO_BROWN = new Color(31, 16, 0);
  private static final Color BLACK = new Color(0, 0, 0);

  private final double skew = Randoms.bounded(0.1, 0.9);

  private Race() { }

  public static Race generate() {
    return new Race();
  }

  public Color generateSkinColor() {
    double skew = Randoms.bounded(-0.1, 0.1) + this.skew;

    int r = DARKEST.getRed() + (int)(skew * DIFF().getRed());
    int g = DARKEST.getGreen() + (int)(skew * DIFF().getGreen());
    int b = DARKEST.getBlue() + (int)(skew * DIFF().getBlue());

    return new Color(r, g, b);
  }

  private static Color DIFF() {
    return new Color(LIGHTEST.getRed() - DARKEST.getRed(),
            LIGHTEST.getGreen() - DARKEST.getGreen(),
            LIGHTEST.getBlue() - DARKEST.getBlue());
  }

  public Color generateHairColor() {
    Color[] options;
    double[] probs;

    if (skew > 0.8) {
      options = new Color[] { PLAT_BLONDE, BLONDE, GINGE, LIGHT_BROWN, BROWN };
      probs = new double[] { 0.15, 0.45, 0.55, 0.8, 1.0 };
    } else if (skew > 0.7) {
      options = new Color[] { BLONDE, GINGE, LIGHT_BROWN, BROWN, BLACK };
      probs = new double[] { 0.1, 0.12, 0.5, 0.85, 1.0 };
    } else if (skew > 0.6) {
      options = new Color[] { LIGHT_BROWN, BROWN, BLACK };
      probs = new double[] { 0.3, 0.7, 1.0 };
    } else if (skew > 0.4) {
      options = new Color[] { LIGHT_BROWN, BROWN, AFRO_BROWN, BLACK };
      probs = new double[] { 0.1, 0.4, 0.7, 1.0 };
    } else {
      options = new Color[] { BROWN, AFRO_BROWN, BLACK };
      probs = new double[] { 0.05, 0.3, 1.0 };
    }

    return fromProbabilities(options, probs);
  }

  private Color fromProbabilities(Color[] options, double[] probs) {
    double prob = Math.random();
    for (int i = 0; i < options.length; i++) {
      if (prob < probs[i]) return options[i];
    }

    return new Color(0, 0, 0);
  }
}
