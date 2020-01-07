package com.redsquare.citizen.systems.vexillography;

import com.redsquare.citizen.debug.GameDebug;
import com.redsquare.citizen.systems.politics.Culture;
import com.redsquare.citizen.util.Sets;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class FlagPattern {

  private static final BufferedImage BLANK_SYMBOL = new BufferedImage(30, 30,
          BufferedImage.TYPE_INT_ARGB);

  private static final Set<Color> colorPool = Set.of(
          new Color(200, 0, 0),
          new Color(0, 150, 0),
          new Color(200, 100, 0),
          new Color(255, 200, 0),
          new Color(0, 0, 200),
          new Color(0, 100, 200),
          new Color(0, 0, 0),
          new Color(200, 200, 200)
  );

  private static final Set<File> symbols = Set.of(
          new File("res/flag_pattern_templates/symbols/fpt_symbol_pillar.png"),
          new File("res/flag_pattern_templates/symbols/fpt_symbol_torch.png"),
          new File("res/flag_pattern_templates/symbols/fpt_symbol_sun.png"),
          new File("res/flag_pattern_templates/symbols/fpt_symbol_x_cross.png")
  );

  private static final Map<Type, File> referenceMap = Map.ofEntries(
          Map.entry(Type.VERT_STRIPES, new File("res/flag_pattern_templates/fpt_vert_stripes.png")),
          Map.entry(Type.HORZ_STRIPES, new File("res/flag_pattern_templates/fpt_horz_stripes.png")),
          Map.entry(Type.CHECKERS, new File("res/flag_pattern_templates/fpt_checkers.png")),
          Map.entry(Type.SALTIRE, new File("res/flag_pattern_templates/fpt_saltire.png"))
  );

  private final Color primary;
  private final Color secondary;
  private final BufferedImage reference;
  private final Type type;

  private FlagPattern(boolean noSymbols) {
    this.type = Type.random(noSymbols);
    this.primary = Sets.randomEntry(colorPool);
    this.secondary = generateSecondary();
    this.reference = BLANK_SYMBOL;
  }

  private FlagPattern(double odds) {
    this.type = Type.SYMBOL;
    this.primary = Sets.randomEntry(colorPool);
    this.secondary = generateSecondary();
    this.reference = Math.random() < odds ? pickSymbol() : BLANK_SYMBOL;
  }

  public static FlagPattern generatePattern() {
    return new FlagPattern(true);
  }

  static FlagPattern generatePattern(Culture culture) {
    if (Math.random() < 0.7) return Sets.randomEntry(culture.getPatterns());
    else return new FlagPattern(true);
  }

  public static FlagPattern generateSymbol() {
    return new FlagPattern(1.0);
  }

  static FlagPattern generateSymbol(Culture culture, double odds) {
    if (Math.random() < 0.7) return Sets.randomEntry(culture.getSymbols());
    else return new FlagPattern(odds);
  }

  public enum Type {
    VERT_STRIPES, HORZ_STRIPES, SALTIRE, MONO, CHECKERS, SYMBOL;

    static Type random(boolean noSymbols) {
      double r = Math.random();
      double denom = (double)(Type.values().length - 1) + (noSymbols ? 0. : 1.);

      return Type.values()[(int)(r * denom)];
    }
  }

  private Color generateSecondary() {
    Color secondary = Sets.randomEntry(colorPool);

    while (primary.equals(secondary))
      secondary = Sets.randomEntry(colorPool);

    return secondary;
  }

  private BufferedImage pickSymbol() {
    // TODO: Could change and remove symbol from static set
    File file = Sets.randomEntry(symbols);
    BufferedImage symbol = BLANK_SYMBOL;

    try {
      symbol = ImageIO.read(file);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return symbol;
  }

  BufferedImage draw() {
    BufferedImage pattern = new BufferedImage(60, 30, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) pattern.getGraphics();

    switch (type) {
      case MONO:
        g.setColor(primary);
        g.fillRect(0, 0, 60, 30);
        break;
      case CHECKERS:
      case HORZ_STRIPES:
      case VERT_STRIPES:
      case SALTIRE:
        try {
          BufferedImage reference = ImageIO.read(referenceMap.get(type));
          for (int x = 0; x < reference.getWidth(); x++) {
            for (int y = 0; y < reference.getHeight(); y++) {

              if (new Color(reference.getRGB(x, y)).equals(new Color(0, 0, 0)))
                g.setColor(primary);
              else g.setColor(secondary);
              g.fillRect(x, y, 1, 1);
            }
          }
        } catch (IOException e) {
          GameDebug.printMessage("Invalid resource path", GameDebug::printDebug);
          e.printStackTrace();
        }
        break;
      case SYMBOL:
        for (int x = 0; x < reference.getWidth(); x++) {
          for (int y = 0; y < reference.getHeight(); y++) {

            if (new Color(reference.getRGB(x, y), true).
                    equals(new Color(0, 0, 0))) {
              g.setColor(primary);
              g.fillRect(x + 15, y, 1, 1);
            } else if (new Color(reference.getRGB(x, y), true).getAlpha() == 255) {
              g.setColor(secondary);
              g.fillRect(x + 15, y, 1, 1);
            }
          }
        }
        break;
    }

    return pattern;
  }
}
