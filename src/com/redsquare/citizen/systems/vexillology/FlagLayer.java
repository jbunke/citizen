package com.redsquare.citizen.systems.vexillology;

import com.redsquare.citizen.systems.politics.Culture;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class FlagLayer {

  private final Type type;
  private final List<FlagPattern> patterns;

  private FlagLayer(Culture culture) {
    this.type = Type.random();
    this.patterns = generatePatterns(culture);
  }

  private FlagLayer(Culture culture, FlagLayer last) {
    this.type = Math.random() < 0.3 ? Type.MONO : last.type;
    this.patterns = generateSymbols(culture);
  }

  static FlagLayer generate(Culture culture) {
    return new FlagLayer(culture);
  }

  static FlagLayer generateSymbolLayer(Culture culture, FlagLayer last) {
    return new FlagLayer(culture, last);
  }

  public enum Type {
    MONO, QUARTERS, HALVES_TTB, HALVES_LTR,
    THREE_TTB, THREE_LTR;

    static Type random() {
      double r = Math.random();
      double denom = 6.;

      if (r < 1/denom) return MONO;
      else if (r < 2/denom) return QUARTERS;
      else if (r < 3/denom) return HALVES_LTR;
      else if (r < 4/denom) return HALVES_TTB;
      else if (r < 5/denom) return THREE_TTB;
      else return THREE_LTR;
    }

    int capacity() {
      switch (this) {
        case MONO:
          return 1;
        case HALVES_LTR:
        case HALVES_TTB:
          return 2;
        case THREE_LTR:
        case THREE_TTB:
          return 3;
        case QUARTERS:
        default:
          return 4;
      }
    }
  }

  private List<FlagPattern> generatePatterns(Culture culture) {
    List<FlagPattern> patterns = new ArrayList<>();
    int capacity = type.capacity();

    while (patterns.size() < capacity) {
      patterns.add(FlagPattern.generatePattern(culture));
    }

    return patterns;
  }

  private List<FlagPattern> generateSymbols(Culture culture) {
    List<FlagPattern> symbols = new ArrayList<>();
    int capacity = type.capacity();

    while (symbols.size() < capacity)
      symbols.add(FlagPattern.generateSymbol(culture, 1/(double)capacity));

    return symbols;
  }

  void draw(final int WIDTH, final int HEIGHT, final Graphics2D g) {
    switch (type) {
      case MONO:
        g.drawImage(patterns.get(0).draw(), 0, 0, WIDTH, HEIGHT, null);
        break;
      case QUARTERS:
        g.drawImage(patterns.get(0).draw(), 0, 0,
                WIDTH / 2, HEIGHT / 2, null);
        g.drawImage(patterns.get(1).draw(), WIDTH / 2, 0,
                WIDTH / 2, HEIGHT / 2, null);
        g.drawImage(patterns.get(2).draw(), 0, HEIGHT / 2,
                WIDTH / 2, HEIGHT / 2, null);
        g.drawImage(patterns.get(3).draw(), WIDTH / 2, HEIGHT / 2,
                WIDTH / 2, HEIGHT / 2, null);
        break;
      case HALVES_LTR:
        g.drawImage(patterns.get(0).draw(), 0, 0, WIDTH / 2, HEIGHT, null);
        g.drawImage(patterns.get(1).draw(), WIDTH / 2, 0, WIDTH / 2, HEIGHT, null);
        break;
      case HALVES_TTB:
        g.drawImage(patterns.get(0).draw(), 0, 0, WIDTH, HEIGHT / 2, null);
        g.drawImage(patterns.get(1).draw(), 0, HEIGHT / 2, WIDTH, HEIGHT / 2, null);
        break;
      case THREE_LTR:
        g.drawImage(patterns.get(0).draw(), 0, 0, WIDTH / 3, HEIGHT, null);
        g.drawImage(patterns.get(1).draw(), WIDTH / 3, 0, WIDTH / 3, HEIGHT, null);
        g.drawImage(patterns.get(2).draw(), 2 * (WIDTH / 3), 0, WIDTH / 3, HEIGHT, null);
        break;
      case THREE_TTB:
        g.drawImage(patterns.get(0).draw(), 0, 0, WIDTH, HEIGHT / 3, null);
        g.drawImage(patterns.get(1).draw(), 0, HEIGHT / 3, WIDTH, HEIGHT / 3, null);
        g.drawImage(patterns.get(2).draw(), 0, 2 * (HEIGHT / 3), WIDTH, HEIGHT / 3, null);
        break;
    }
  }
}
